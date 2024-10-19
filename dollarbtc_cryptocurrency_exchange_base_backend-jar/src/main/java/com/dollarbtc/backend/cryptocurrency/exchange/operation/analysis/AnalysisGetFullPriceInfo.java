/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.analysis;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.forex.ForexGetRate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTickersAndUSDPrice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class AnalysisGetFullPriceInfo extends AbstractOperation<JsonNode> {

    public AnalysisGetFullPriceInfo() {
        super(JsonNode.class);
    }

    @Override
    protected void execute() {
        JsonNode fullPriceInfo = mapper.createObjectNode();
        ArrayNode localBitcoinsTickersAndUSDPrice = new LocalBitcoinsGetTickersAndUSDPrice().getResponse();
        Iterator<JsonNode> localBitcoinsTickersAndUSDPriceIterator = localBitcoinsTickersAndUSDPrice.iterator();
        while (localBitcoinsTickersAndUSDPriceIterator.hasNext()) {
            JsonNode localBitcoinsTickersAndUSDPriceIt = localBitcoinsTickersAndUSDPriceIterator.next();
            ((ObjectNode) localBitcoinsTickersAndUSDPriceIt).remove("source");
            String currency = localBitcoinsTickersAndUSDPriceIt.get("currency").textValue();
            ((ObjectNode) localBitcoinsTickersAndUSDPriceIt).remove("currency");
            double price = localBitcoinsTickersAndUSDPriceIt.get("price").doubleValue();
            ((ObjectNode) localBitcoinsTickersAndUSDPriceIt).remove("price");
            ((ObjectNode) localBitcoinsTickersAndUSDPriceIt).put("btcPrice", price);
            JsonNode forexRate = new ForexGetRate("USD" + currency).getResponse();
            if (!fullPriceInfo.has(currency)) {
                ((ObjectNode) fullPriceInfo).set(currency, mapper.createObjectNode());
            }
            ((ObjectNode) fullPriceInfo.get(currency)).set("localBitcoins", localBitcoinsTickersAndUSDPriceIt);
            if (forexRate.has("rate")) {
                double rate = forexRate.get("rate").doubleValue();
                ((ObjectNode) forexRate).put("usdRate", rate);
                ((ObjectNode) forexRate).remove("rate");
            }
            ((ObjectNode) fullPriceInfo.get(currency)).set("forex", forexRate);
        }
        super.response = fullPriceInfo;
    }

}
