/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.coinbase;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CoinbaseFolderLocator;
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
public class CoinbaseUpdateAuxPrices extends AbstractOperation<Void> {
    
    private final String[] baseCurrenciesAndTargetCurrencies;

    public CoinbaseUpdateAuxPrices(String[] baseCurrenciesAndTargetCurrencies) {
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
            File basePricesFolder = CoinbaseFolderLocator.getPricesSymbolFolder(symbolAsset + baseCurrency);
            JsonNode basePrice = null;
            for (File priceFile : basePricesFolder.listFiles()) {
                if (!priceFile.isFile()) {
                    continue;
                }
                try {
                    basePrice = mapper.readTree(priceFile);
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(CoinbaseUpdateAuxPrices.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (basePrice == null) {
                continue;
            }
            String basePriceTimestamp = basePrice.get("timestamp").textValue();
            ((ObjectNode) basePrice).put("baseTimestamp", basePriceTimestamp);
            File targetPricesFolder = CoinbaseFolderLocator.getPricesSymbolFolder(symbolAsset + targetCurrency);
            File targetPricesOldFolder = FileUtil.createFolderIfNoExist(targetPricesFolder, "Old");
            boolean processed = false;
            for (File priceFile : targetPricesFolder.listFiles()) {
                if (!priceFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode price = mapper.readTree(priceFile);
                    processed = true;
                    if (basePriceTimestamp.equals(price.get("baseTimestamp").textValue())) {
                        break;
                    }
                    FileUtil.moveFileToFolder(priceFile, targetPricesOldFolder);
                    ((ObjectNode) basePrice).put("timestamp", timestamp);
                    FileUtil.createFile(basePrice, new File(targetPricesFolder, DateUtil.getFileDate(timestamp) + ".json"));
                } catch (IOException ex) {
                    Logger.getLogger(CoinbaseUpdateAuxPrices.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!processed) {
                ((ObjectNode) basePrice).put("timestamp", timestamp);
                FileUtil.createFile(basePrice, new File(targetPricesFolder, DateUtil.getFileDate(timestamp) + ".json"));
            }
        }
    }
    
}
