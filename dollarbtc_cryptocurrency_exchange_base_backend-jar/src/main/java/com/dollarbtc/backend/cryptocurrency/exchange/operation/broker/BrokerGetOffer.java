/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.EncryptorBASE64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BrokerGetOffer extends AbstractOperation<JsonNode> {

    private final String encryptedOfferKey;

    public BrokerGetOffer(String encryptedOfferKey) {
        super(JsonNode.class);
        this.encryptedOfferKey = encryptedOfferKey;
    }

    @Override
    protected void execute() {
        try {
            String[] offerParams = EncryptorBASE64.decryptURL(encryptedOfferKey).split("__");
            String userName = offerParams[0];
            String currency = offerParams[1];
            OfferType offerType = OfferType.valueOf(offerParams[2]);
            String paymentId = offerParams[3];
            PaymentType paymentType = PaymentType.valueOf(offerParams[4]);
            JsonNode offers = new BrokerGetOffers(userName, currency, paymentId, offerType, paymentType, false).getResponse();
            if (offers.has(currency + "__" + offerType.name() + "__" + paymentId + "__" + paymentType.name())) {
                JsonNode offer = offers.get(currency + "__" + offerType.name() + "__" + paymentId + "__" + paymentType.name());
                ((ObjectNode) offer).put("userName", userName);
                ((ObjectNode) offer).put("currency", currency);
                ((ObjectNode) offer).put("offerType", offerType.name());
                ((ObjectNode) offer).put("paymentId", paymentId);
                ((ObjectNode) offer).put("paymentType", paymentType.name());
                super.response = offer;
                return;
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BrokerGetOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
