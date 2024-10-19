/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminGetOffers extends AbstractOperation<JsonNode> {

    private final String userName, currency, paymentId;
    private final OfferType offerType;
    private final PaymentType paymentType;
    private final boolean old;

    public OTCAdminGetOffers(String userName, String currency, String paymentId, OfferType offerType, PaymentType paymentType, boolean old) {
        super(JsonNode.class);
        this.userName = userName;
        this.currency = currency;
        this.paymentId = paymentId;
        this.offerType = offerType;
        this.paymentType = paymentType;
        this.old = old;
    }

    @Override
    protected void execute() {
        JsonNode offers = mapper.createObjectNode();
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        userCurrencies.stream().map((userCurrency) -> OTCFolderLocator.getCurrencyFolder(null, userCurrency)).filter((otcCurrencyFolder) -> !(currency != null && !otcCurrencyFolder.getName().equals(currency))).forEach((otcCurrencyFolder) -> {
            File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
            if (!(!otcCurrencyOffersFolder.isDirectory())) {
                JsonNode otcCurrencyOffer = mapper.createObjectNode();
                for (File otcCurrencyOffersTypeFolder : otcCurrencyOffersFolder.listFiles()) {
                    if (!otcCurrencyOffersTypeFolder.isDirectory()) {
                        continue;
                    }
                    String[] otcCurrencyOffersTypeFolderNames = otcCurrencyOffersTypeFolder.getName().split("__");
                    OfferType offerTypee = OfferType.valueOf(otcCurrencyOffersTypeFolderNames[0]);
                    String paymentIdd = otcCurrencyOffersTypeFolderNames[1];
                    PaymentType paymentTypee = PaymentType.valueOf(otcCurrencyOffersTypeFolderNames[2]);
                    if (offerType != null && !offerTypee.equals(offerType)) {
                        continue;
                    }
                    if (paymentId != null && !paymentId.equals("") && !paymentIdd.equals(paymentId)) {
                        continue;
                    }
                    if (paymentType != null && !paymentTypee.equals(paymentType)) {
                        continue;
                    }
                    if (old) {
                        otcCurrencyOffersTypeFolder = new File(otcCurrencyOffersTypeFolder, "Old");
                    }
                    ArrayNode otcCurrencyOfferTypes = mapper.createArrayNode();
                    for (File otcCurrencyOfferTypeFile : otcCurrencyOffersTypeFolder.listFiles()) {
                        if (!otcCurrencyOfferTypeFile.isFile()) {
                            continue;
                        }
                        try {
                            String otcCurrencyOfferTypeId = otcCurrencyOfferTypeFile.getName().replace(".json", "");
                            File otcCurrencyOffersTypeOperationsOfferIdFolder = OTCFolderLocator.getCurrencyOffersTypeOperationsOfferIdFolder(null, otcCurrencyFolder.getName(), offerTypee, paymentIdd, paymentTypee, otcCurrencyOfferTypeId);
                            Double accumulatedAmount = 0.0;
                            if (otcCurrencyOffersTypeOperationsOfferIdFolder.isDirectory()) {
                                for (File otcCurrencyOffersTypeOperationsOfferIdFile : otcCurrencyOffersTypeOperationsOfferIdFolder.listFiles()) {
                                    if (!otcCurrencyOffersTypeOperationsOfferIdFile.isFile()) {
                                        continue;
                                    }
                                    JsonNode otcCurrencyOffersTypeOperationsOfferId = mapper.readTree(otcCurrencyOffersTypeOperationsOfferIdFile);
                                    accumulatedAmount = accumulatedAmount + otcCurrencyOffersTypeOperationsOfferId.get("amount").doubleValue();
                                }
                            }
                            JsonNode otcCurrencyOfferType = mapper.readTree(otcCurrencyOfferTypeFile);
                            ((ObjectNode) otcCurrencyOfferType).put("id", otcCurrencyOfferTypeId);
                            ((ObjectNode) otcCurrencyOfferType).put("accumulatedAmount", accumulatedAmount);
                            otcCurrencyOfferTypes.add(otcCurrencyOfferType);
                            if (!old) {
                                ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(OTCAdminGetOffers.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (old) {
                        ((ObjectNode) otcCurrencyOffer).putArray(offerTypee.name() + "__" + paymentIdd + "__" + paymentTypee.name()).addAll(otcCurrencyOfferTypes);
                    }
                }
                ((ObjectNode) offers).set(otcCurrencyFolder.getName(), otcCurrencyOffer);
            }
        });
        super.response = offers;
    }

}
