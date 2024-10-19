/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LocalBitcoinsFolderLocator;
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
public class LocalBitcoinsGetTickersAndUSDPrice extends AbstractOperation<ArrayNode> {

    public LocalBitcoinsGetTickersAndUSDPrice() {
        super(ArrayNode.class);
    }

    @Override
    protected void execute() {
        File tickersFolder = LocalBitcoinsFolderLocator.getTickersFolder();
        ArrayNode localbitcoinResumeTickers = mapper.createArrayNode();
        double btcUSDPrice = 0;
        for (File tickersCurrencyFolder : tickersFolder.listFiles()) {
            if (!tickersCurrencyFolder.isDirectory()) {
                continue;
            }
            if (!tickersCurrencyFolder.getName().contains("USD")) {
                continue;
            }
            for (File tickerCurrencyFile : tickersCurrencyFolder.listFiles()) {
                if (!tickerCurrencyFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode tickerCurrency = mapper.readTree(tickerCurrencyFile);
                    double bidPrice = tickerCurrency.get("bid").get("average").get("price").doubleValue();
                    double askPrice = tickerCurrency.get("ask").get("average").get("price").doubleValue();
                    btcUSDPrice = (bidPrice + askPrice) / 2;
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(LocalBitcoinsGetTickersAndUSDPrice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (File tickersCurrencyFolder : tickersFolder.listFiles()) {
            if (!tickersCurrencyFolder.isDirectory()) {
                continue;
            }
            for (File tickerCurrencyFile : tickersCurrencyFolder.listFiles()) {
                if (!tickerCurrencyFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode tickerCurrency = mapper.readTree(tickerCurrencyFile);
                    double bidPrice = tickerCurrency.get("bid").get("average").get("price").doubleValue();
                    double askPrice = tickerCurrency.get("ask").get("average").get("price").doubleValue();
                    double price = (bidPrice + askPrice) / 2;
                    ((ObjectNode) tickerCurrency).put("currency", tickersCurrencyFolder.getName().replace("BTC", ""));
                    ((ObjectNode) tickerCurrency).put("price", price);
                    if (btcUSDPrice > 0) {
                        ((ObjectNode) tickerCurrency).put("usdPrice", price / btcUSDPrice);
                    }
                    localbitcoinResumeTickers.add(tickerCurrency);
                } catch (IOException ex) {
                    Logger.getLogger(LocalBitcoinsGetTickersAndUSDPrice.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.response = localbitcoinResumeTickers; 
    }

}
