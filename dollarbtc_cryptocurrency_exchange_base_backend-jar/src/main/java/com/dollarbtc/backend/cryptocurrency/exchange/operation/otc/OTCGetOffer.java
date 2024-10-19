/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

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
public class OTCGetOffer extends AbstractOperation<JsonNode> {

    private final String encryptedOfferKey;

    public OTCGetOffer(String encryptedOfferKey) {
        super(JsonNode.class);
        this.encryptedOfferKey = encryptedOfferKey;
    }

    @Override
    public void execute() {
        try {
            String[] offerParams = EncryptorBASE64.decryptURL(encryptedOfferKey).split("__");
            String currency = offerParams[0];
            OfferType offerType = OfferType.valueOf(offerParams[1]);
            String paymentId = offerParams[2];
            PaymentType paymentType = PaymentType.valueOf(offerParams[3]);
            JsonNode offers = new OTCGetOffers(currency, paymentId, offerType, paymentType, false).getResponse();
            if (offers.has(currency) && offers.get(currency).has(offerType.name() + "__" + paymentId + "__" + paymentType.name())) {
                JsonNode offer = offers.get(currency).get(offerType.name() + "__" + paymentId + "__" + paymentType.name());
                ((ObjectNode) offer).put("currency", currency);
                ((ObjectNode) offer).put("offerType", offerType.name());
                ((ObjectNode) offer).put("paymentId", paymentId);
                ((ObjectNode) offer).put("paymentType", paymentType.name());
                super.response = offer;
                return;
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OTCGetOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
