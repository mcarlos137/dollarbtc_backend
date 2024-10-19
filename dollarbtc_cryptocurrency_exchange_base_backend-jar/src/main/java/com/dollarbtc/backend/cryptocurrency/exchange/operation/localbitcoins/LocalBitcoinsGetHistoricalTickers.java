/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PriceType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LocalBitcoinsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LocalBitcoinsGetHistoricalTickers extends AbstractOperation<Object> {

    private final String symbol;
    private final OfferType offerType;
    private final PriceType priceType;

    public LocalBitcoinsGetHistoricalTickers(String symbol, OfferType offerType, PriceType priceType) {
        super(Object.class);
        this.symbol = symbol;
        this.offerType = offerType;
        this.priceType = priceType;
    }
    
    @Override
    protected void execute() {
        if(offerType == null && priceType == null){
            super.response = method(symbol);
        } else {
            super.response = method(symbol, offerType, priceType);
        }
    }

    private Map<String, JsonNode> method(String symbol) {
        Map<String, JsonNode> localbitcoinHistoricalTickersMap = new TreeMap<>();
        File tickersOldFolder = LocalBitcoinsFolderLocator.getTickersSymbolOldFolder(symbol);
        if (!tickersOldFolder.isDirectory()) {
            return localbitcoinHistoricalTickersMap;
        }
        for (File tickerOldFile : tickersOldFolder.listFiles()) {
            if (!tickerOldFile.isFile()) {
                continue;
            }
            try {
                localbitcoinHistoricalTickersMap.put(tickerOldFile.getName().replace(".json", ""), mapper.readTree(tickerOldFile));
            } catch (IOException ex) {
                Logger.getLogger(LocalBitcoinsGetHistoricalTickers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return localbitcoinHistoricalTickersMap;
    }

    private Map<String, Double> method(String symbol, OfferType offerType, PriceType priceType) {
        Map<String, Double> localbitcoinHistoricalTickersMap = new TreeMap<>();
        File tickersOldFolder = LocalBitcoinsFolderLocator.getTickersSymbolOldFolder(symbol);
        if (!tickersOldFolder.isDirectory()) {
            return localbitcoinHistoricalTickersMap;
        }
        for (File tickerOldFile : tickersOldFolder.listFiles()) {
            if (!tickerOldFile.isFile()) {
                continue;
            }
            try {
                JsonNode tickerOld = mapper.readTree(tickerOldFile);
                if (!tickerOld.has(offerType.name().toLowerCase())) {
                    continue;
                }
                if (!tickerOld.get(offerType.name().toLowerCase()).has(priceType.name().toLowerCase())) {
                    continue;
                }
                tickerOld.get(offerType.name().toLowerCase()).get(priceType.name().toLowerCase()).get("price").doubleValue();
                localbitcoinHistoricalTickersMap.put(tickerOld.get("timestamp").textValue(), tickerOld.get(offerType.name().toLowerCase()).get(priceType.name().toLowerCase()).get("price").doubleValue());
            } catch (IOException ex) {
                Logger.getLogger(LocalBitcoinsGetHistoricalTickers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return localbitcoinHistoricalTickersMap;
    }

}
