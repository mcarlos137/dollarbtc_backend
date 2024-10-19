/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BrokerGetOfferParams extends AbstractOperation<JsonNode> {

    private final String currency;

    public BrokerGetOfferParams(String currency) {
        super(JsonNode.class);
        this.currency = currency;
    }

    @Override
    protected void execute() {
        File brokersOfferFile = BrokersFolderLocator.getOfferFile();
        try {
            JsonNode brokersOffer = mapper.readTree(brokersOfferFile);
            if (brokersOffer.has(currency)) {
                JsonNode brokersOfferPrices = mapper.createObjectNode();
                JsonNode brokersOfferMarginPercents = mapper.createObjectNode();
                JsonNode brokersOfferSpreadPercents = mapper.createObjectNode();
                JsonNode askOffers = new OTCGetOffers(currency, null, OfferType.ASK, null, false).getResponse().get(currency);
                JsonNode bidOffers = new OTCGetOffers(currency, null, OfferType.BID, null, false).getResponse().get(currency);
                Double askMinPrice = null;
                Double bidMaxPrice = null;
                Double askMinMarginPercent = 0.0;
                Double bidMinMarginPercent = 0.0;
                Double askMinSpreadPercent = 0.0;
                Double bidMinSpreadPercent = 0.0;
                Iterator<JsonNode> askOffersIterator = askOffers.iterator();
                while (askOffersIterator.hasNext()) {
                    JsonNode askOffersIt = askOffersIterator.next();
                    if (askMinPrice == null || askOffersIt.get("price").doubleValue() > askMinPrice) {
                        askMinPrice = askOffersIt.get("price").doubleValue();
                    }
                    if (askOffersIt.has("marginPercent") && askOffersIt.get("marginPercent").doubleValue() > askMinMarginPercent) {
                        askMinMarginPercent = askOffersIt.get("marginPercent").doubleValue();
                    }
                    if (askOffersIt.has("spreadPercent") && askOffersIt.get("spreadPercent").doubleValue() > askMinSpreadPercent) {
                        askMinSpreadPercent = askOffersIt.get("spreadPercent").doubleValue();
                    }
                }
                Iterator<JsonNode> bidOffersIterator = bidOffers.iterator();
                while (bidOffersIterator.hasNext()) {
                    JsonNode bidOffersIt = bidOffersIterator.next();
                    if (bidMaxPrice == null || bidOffersIt.get("price").doubleValue() < bidMaxPrice) {
                        bidMaxPrice = bidOffersIt.get("price").doubleValue();
                    }
                    if (bidOffersIt.has("marginPercent") && bidOffersIt.get("marginPercent").doubleValue() > bidMinMarginPercent) {
                        bidMinMarginPercent = bidOffersIt.get("marginPercent").doubleValue();
                    }
                    if (bidOffersIt.has("spreadPercent") && bidOffersIt.get("spreadPercent").doubleValue() > bidMinSpreadPercent) {
                        bidMinSpreadPercent = bidOffersIt.get("spreadPercent").doubleValue();
                    }
                }
                ((ObjectNode) brokersOfferPrices).put("askMin", askMinPrice);
                ((ObjectNode) brokersOfferPrices).put("bidMax", bidMaxPrice);
                ((ObjectNode) brokersOffer.get(currency)).put("prices", brokersOfferPrices);
                ((ObjectNode) brokersOfferMarginPercents).put("askMin", askMinMarginPercent);
                ((ObjectNode) brokersOfferMarginPercents).put("bidMin", bidMinMarginPercent);
                ((ObjectNode) brokersOffer.get(currency)).put("marginPercents", brokersOfferMarginPercents);
                ((ObjectNode) brokersOfferSpreadPercents).put("askMin", askMinSpreadPercent);
                ((ObjectNode) brokersOfferSpreadPercents).put("bidMin", bidMinSpreadPercent);
                ((ObjectNode) brokersOffer.get(currency)).put("spreadPercents", brokersOfferSpreadPercents);
                super.response = brokersOffer.get(currency);
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(BrokerGetOfferParams.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
