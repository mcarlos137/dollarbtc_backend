/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetDollarBTCPaymentBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOffers extends AbstractOperation<JsonNode> {
    
    private final String currency, paymentId; 
    private final OfferType offerType; 
    private final PaymentType paymentType; 
    private final boolean old;

    public OTCGetOffers(String currency, String paymentId, OfferType offerType, PaymentType paymentType, boolean old) {
        super(JsonNode.class);
        this.currency = currency;
        this.paymentId = paymentId;
        this.offerType = offerType;
        this.paymentType = paymentType;
        this.old = old;
    }
        
    @Override
    public void execute() {
        JsonNode offers = mapper.createObjectNode();
        Map<String, String[]> offersAdded = new HashMap<>();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            for (File otcCurrencyFolder : OTCFolderLocator.getFolder(operator).listFiles()) {
                if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                    continue;
                }
                if (currency != null && !otcCurrencyFolder.getName().equals(currency)) {
                    continue;
                }
                File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
                if (!otcCurrencyOffersFolder.isDirectory()) {
                    continue;
                }
                JsonNode otcCurrencyOffer = mapper.createObjectNode();
                for (File otcCurrencyOffersTypeFolder : otcCurrencyOffersFolder.listFiles()) {
                    if (!otcCurrencyOffersTypeFolder.isDirectory()) {
                        continue;
                    }
                    String[] otcCurrencyOffersTypeFolderNames = otcCurrencyOffersTypeFolder.getName().split("__");
                    if (otcCurrencyOffersTypeFolderNames.length != 3) {
                        continue;
                    }
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
                            JsonNode otcCurrencyOfferType = mapper.readTree(otcCurrencyOfferTypeFile);
                            if (!(paymentIdd.equals("MONEYCLICK") || paymentIdd.equals("DOLLARBTC"))) {
                                Double dollarBTCPaymentBalanceLeft = null;
                                JsonNode dollarBTCPaymentBalance = new OTCGetDollarBTCPaymentBalance(new OTCGetDollarBTCPaymentBalanceRequest("dollarBTC", otcCurrencyFolder.getName(), new String[]{paymentIdd})).getResponse();
                                if (dollarBTCPaymentBalance.has(paymentIdd)) {
                                    Iterator<JsonNode> dollarBTCPaymentBalanceIterator = dollarBTCPaymentBalance.get(paymentIdd).iterator();
                                    while (dollarBTCPaymentBalanceIterator.hasNext()) {
                                        JsonNode dollarBTCPaymentBalanceIt = dollarBTCPaymentBalanceIterator.next();
                                        if (dollarBTCPaymentBalanceIt.get("currency").textValue().equals(otcCurrencyFolder.getName())) {
                                            dollarBTCPaymentBalanceLeft = dollarBTCPaymentBalanceIt.get("amount").doubleValue();
                                            break;
                                        }
                                    }
                                }
                                if (dollarBTCPaymentBalanceLeft == null) {
                                    continue;
                                }
                                if (dollarBTCPaymentBalanceLeft <= otcCurrencyOfferType.get("minPerOperationAmount").doubleValue() && offerTypee.equals(OfferType.BID)) {
                                    continue;
                                }
                            }
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
                            ((ObjectNode) otcCurrencyOfferType).put("id", otcCurrencyOfferTypeId);
                            ((ObjectNode) otcCurrencyOfferType).put("accumulatedAmount", accumulatedAmount);
                            ((ObjectNode) otcCurrencyOfferType).put("offerType", offerTypee.name());
                            ((ObjectNode) otcCurrencyOfferType).put("paymentId", paymentIdd);
                            ((ObjectNode) otcCurrencyOfferType).put("paymentType", paymentTypee.name());
                            otcCurrencyOfferTypes.add(otcCurrencyOfferType);
                            if (!old) {
                                if (paymentIdd.equals("MONEYCLICK")) {
                                    ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                                } else {
                                    File otcCurrencyPaymentConfigFile = new File(new File(new File(new File(OTCFolderLocator.getFolder(operator), currency), "Payments"), paymentIdd), "config.json");
                                    if (!otcCurrencyPaymentConfigFile.isFile()) {
                                        continue;
                                    }
                                    String bank = mapper.readTree(otcCurrencyPaymentConfigFile).get("bank").textValue();
                                    Double price = otcCurrencyOfferType.get("price").doubleValue();
                                    if (operator.equals(OPERATOR_NAME)) {
                                        if (offersAdded.containsKey(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())) {
                                            ((ObjectNode) offers.get(otcCurrencyFolder.getName())).remove(offersAdded.get(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())[0]);
                                        }
                                        offersAdded.put(offerTypee.name() + "__" + bank + "__" + paymentTypee.name(), new String[]{otcCurrencyOffersTypeFolder.getName(), Double.toString(price)});
                                        ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                                    } else {
                                        if (offerTypee.equals(OfferType.BID)) {
                                            if (!offersAdded.containsKey(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())) {
                                                offersAdded.put(offerTypee.name() + "__" + bank + "__" + paymentTypee.name(), new String[]{otcCurrencyOffersTypeFolder.getName(), Double.toString(price)});
                                                ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                                            } else {
                                                Double pricee = Double.parseDouble(offersAdded.get(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())[1]);
                                                if (price > pricee) {
                                                    ((ObjectNode) offers.get(otcCurrencyFolder.getName())).remove(offersAdded.get(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())[0]);
                                                    offersAdded.put(offerTypee.name() + "__" + bank + "__" + paymentTypee.name(), new String[]{otcCurrencyOffersTypeFolder.getName(), Double.toString(price)});
                                                    ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                                                }
                                            }
                                        } else if (offerTypee.equals(OfferType.ASK)) {
                                            if (!offersAdded.containsKey(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())) {
                                                offersAdded.put(offerTypee.name() + "__" + bank + "__" + paymentTypee.name(), new String[]{otcCurrencyOffersTypeFolder.getName(), Double.toString(price)});
                                                ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                                            } else {
                                                Double pricee = Double.parseDouble(offersAdded.get(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())[1]);
                                                if (price < pricee) {
                                                    ((ObjectNode) offers.get(otcCurrencyFolder.getName())).remove(offersAdded.get(offerTypee.name() + "__" + bank + "__" + paymentTypee.name())[0]);
                                                    offersAdded.put(offerTypee.name() + "__" + bank + "__" + paymentTypee.name(), new String[]{otcCurrencyOffersTypeFolder.getName(), Double.toString(price)});
                                                    ((ObjectNode) otcCurrencyOffer).set(otcCurrencyOffersTypeFolder.getName(), otcCurrencyOfferType);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(OTCGetOffers.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (old) {
                        if (operator.equals(OPERATOR_NAME)) {
                            ((ObjectNode) otcCurrencyOffer).putArray(offerTypee.name() + "__" + paymentIdd + "__" + paymentTypee.name()).addAll(otcCurrencyOfferTypes);
                        }
                    }
                }
                if (offers.has(otcCurrencyFolder.getName())) {
                    ((ObjectNode) offers.get(otcCurrencyFolder.getName())).setAll((ObjectNode) otcCurrencyOffer);
                } else {
                    ((ObjectNode) offers).set(otcCurrencyFolder.getName(), otcCurrencyOffer);
                }
            }
        }
        super.response = offers;
    }
    
}
