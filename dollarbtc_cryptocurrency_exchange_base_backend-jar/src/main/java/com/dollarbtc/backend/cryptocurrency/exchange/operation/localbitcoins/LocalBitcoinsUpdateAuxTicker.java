/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LocalBitcoinsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LocalBitcoinsUpdateAuxTicker extends AbstractOperation<Void> {
    
    private final String[] baseCurrenciesAndTargetCurrencies;

    public LocalBitcoinsUpdateAuxTicker(String[] baseCurrenciesAndTargetCurrencies) {
        super(Void.class);
        this.baseCurrenciesAndTargetCurrencies = baseCurrenciesAndTargetCurrencies;
    }
    
    @Override
    protected void execute() {
        method(baseCurrenciesAndTargetCurrencies);
    }
    
    private synchronized void method(String[] baseCurrenciesAndTargetCurrencies) {
        String timestamp = DateUtil.getMinuteStartDate(null);
        for (String baseCurrencyAndTargetCurrency : baseCurrenciesAndTargetCurrencies) {
            String baseCurrency = baseCurrencyAndTargetCurrency.split("__")[0];
            String targetCurrency = baseCurrencyAndTargetCurrency.split("__")[1];
            String symbolAsset = "BTC";
            File baseTickersFolder = LocalBitcoinsFolderLocator.getTickersSymbolFolder(symbolAsset + baseCurrency);
            JsonNode baseTicker = null;
            for (File tickerFile : baseTickersFolder.listFiles()) {
                if (!tickerFile.isFile()) {
                    continue;
                }
                try {
                    baseTicker = mapper.readTree(tickerFile);
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(LocalBitcoinsUpdateAuxTicker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (baseTicker == null) {
                continue;
            }
            String baseTickerTimestamp = baseTicker.get("timestamp").textValue();
            ((ObjectNode) baseTicker).put("baseTimestamp", baseTickerTimestamp);
            File targetTickersFolder = LocalBitcoinsFolderLocator.getTickersSymbolFolder(symbolAsset + targetCurrency);
            File targetTickersOldFolder = FileUtil.createFolderIfNoExist(targetTickersFolder, "Old");
            boolean processed = false;
            for (File tickerFile : targetTickersFolder.listFiles()) {
                if (!tickerFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode ticker = mapper.readTree(tickerFile);
                    processed = true;
                    if (baseTickerTimestamp.equals(ticker.get("baseTimestamp").textValue())) {
                        break;
                    }
                    FileUtil.moveFileToFolder(tickerFile, targetTickersOldFolder);
                    ((ObjectNode) baseTicker).put("timestamp", timestamp);
                    FileUtil.createFile(baseTicker, new File(targetTickersFolder, DateUtil.getFileDate(timestamp) + ".json"));
                } catch (IOException ex) {
                    Logger.getLogger(LocalBitcoinsUpdateAuxTicker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!processed) {
                ((ObjectNode) baseTicker).put("timestamp", timestamp);
                FileUtil.createFile(baseTicker, new File(targetTickersFolder, DateUtil.getFileDate(timestamp) + ".json"));
            }
        }
    }
    
}
