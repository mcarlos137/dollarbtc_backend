/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BrokerGetOffers extends AbstractOperation<JsonNode> {
    
    private final String userName, currency, paymentId; 
    private final OfferType offerType; 
    private final PaymentType paymentType;
    private final boolean old;

    public BrokerGetOffers(String userName, String currency, String paymentId, OfferType offerType, PaymentType paymentType, boolean old) {
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
        File brokerOffersFolder = BrokersFolderLocator.getOffersFolder(userName);
        for (File brokerOfferFolder : brokerOffersFolder.listFiles()) {
            if (!brokerOfferFolder.isDirectory()) {
                continue;
            }
            String[] brokerOfferFolderParams = brokerOfferFolder.getName().split("__");
            if (brokerOfferFolderParams.length != 4) {
                continue;
            }
            String currencyy = brokerOfferFolderParams[0];
            OfferType offerTypee = OfferType.valueOf(brokerOfferFolderParams[1]);
            String paymentIdd = brokerOfferFolderParams[2];
            PaymentType paymentTypee = PaymentType.valueOf(brokerOfferFolderParams[3]);
            if (currency != null && !currencyy.equals(currency)) {
                continue;
            }
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
                brokerOfferFolder = new File(brokerOfferFolder, "Old");
            }
            ArrayNode brokerOffers = mapper.createArrayNode();
            for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
                if (!brokerOfferFile.isFile()) {
                    continue;
                }
                try {
                    String brokerOfferId = brokerOfferFile.getName().replace(".json", "");
                    File brokerOfferOperationsOfferIdFolder = BrokersFolderLocator.getOfferOperationsOfferIdFolder(userName, currencyy, offerTypee, paymentIdd, paymentTypee, brokerOfferId);
                    Double accumulatedAmount = 0.0;
                    if (brokerOfferOperationsOfferIdFolder.isDirectory()) {
                        for (File brokerOfferOperationsOfferIdFile : brokerOfferOperationsOfferIdFolder.listFiles()) {
                            if (!brokerOfferOperationsOfferIdFile.isFile()) {
                                continue;
                            }
                            JsonNode brokerOfferOperationsOfferId = mapper.readTree(brokerOfferOperationsOfferIdFile);
                            accumulatedAmount = accumulatedAmount + brokerOfferOperationsOfferId.get("amount").doubleValue();
                        }
                    }
                    JsonNode brokerOffer = mapper.readTree(brokerOfferFile);
                    ((ObjectNode) brokerOffer).put("id", brokerOfferId);
                    ((ObjectNode) brokerOffer).put("accumulatedAmount", accumulatedAmount);
                    ((ObjectNode) brokerOffer).put("offerType", offerTypee.name());
                    ((ObjectNode) brokerOffer).put("paymentId", paymentIdd);
                    ((ObjectNode) brokerOffer).put("paymentType", paymentTypee.name());
                    brokerOffers.add(brokerOffer);
                    if (!old) {
                        ((ObjectNode) offers).set(brokerOfferFolder.getName(), brokerOffer);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BrokerGetOffers.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (old) {
                ((ObjectNode) offers).putArray(currencyy + "__" + offerTypee.name() + "__" + paymentIdd + "__" + paymentTypee.name()).addAll(brokerOffers);
            }
        }
        super.response = offers;
    }
        
}
