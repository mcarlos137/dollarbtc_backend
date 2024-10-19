/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetFastChangeFactor;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.PricesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UpdatePricesFastChangeMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> currencies = new HashSet<>();
        currencies.add("USD");
        currencies.add("BTC");
        currencies.add("EUR");
        currencies.add("VES");
        currencies.add("COP");
        currencies.add("CLP");
        currencies.add("ARS");
        currencies.add("PEN");
        currencies.add("MXN");
        currencies.add("CHF");
        currencies.add("PA_USD");
        currencies.add("DOP");
        String timestamp = DateUtil.getMinuteStartDate(null);
        String timestamp24h = DateUtil.getDateHoursBefore(timestamp, 24);
        for (String currency : currencies) {
            File pricesFastChangeCurrencyFolder = PricesFolderLocator.getFastChangeFolder(currency);
            File pricesFastChangeCurrencyOldFolder = PricesFolderLocator.getFastChangeOldFolder(currency);
            for (File priceFile : pricesFastChangeCurrencyFolder.listFiles()) {
                if (!priceFile.isFile()) {
                    continue;
                }
                FileUtil.moveFileToFolder(priceFile, pricesFastChangeCurrencyOldFolder);
            }
            File pricesFastChangeCurrencyOld24hFile = null;
            JsonNode price24h = null;
            int i = 0;
            while (i < 5) {
                pricesFastChangeCurrencyOld24hFile = new File(pricesFastChangeCurrencyOldFolder, DateUtil.getFileDate(timestamp24h) + ".json");
                if(!pricesFastChangeCurrencyOld24hFile.isFile()){
                    timestamp24h = DateUtil.getDateMinutesBefore(timestamp24h, 1);
                } else {
                    try {
                        price24h = mapper.readTree(pricesFastChangeCurrencyOld24hFile);
                    } catch (IOException ex) {
                        Logger.getLogger(UpdatePricesFastChangeMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                i++;
            }
            ObjectNode price = mapper.createObjectNode();
            for (String currencyy : currencies) {
                if (currency.equals(currencyy)) {
                    continue;
                }
                ObjectNode priceCurrency = mapper.createObjectNode();
                Double bidPrice = null;
                Double askPrice = null;
                //SELL
                JsonNode fastChangeFactor = new MCUserGetFastChangeFactor(currency, currencyy).getResponse();
                if (fastChangeFactor.has("factor")) {
                    bidPrice = fastChangeFactor.get("factor").doubleValue();
                }
                //BUY
                fastChangeFactor = new MCUserGetFastChangeFactor(currencyy, currency).getResponse();
                if (fastChangeFactor.has("factor")) {
                    askPrice = 1 / fastChangeFactor.get("factor").doubleValue();
                }
                if (bidPrice != null) {
                    priceCurrency.put("bid", bidPrice);
                }
                if (askPrice != null) {
                    priceCurrency.put("ask", askPrice);
                }
                if (bidPrice != null && askPrice != null) {
                    priceCurrency.put("average", (bidPrice + askPrice) / 2);
                }
                if (bidPrice != null && askPrice != null && price24h != null && price24h.has(currencyy) && price24h.get(currencyy).has("average")) {
                    priceCurrency.put("24h%", ((((bidPrice + askPrice) / 2) / price24h.get(currencyy).get("average").doubleValue()) - 1) * 100);
                }
                price.set(currencyy, priceCurrency);
            }
            FileUtil.editFile(price, new File(pricesFastChangeCurrencyFolder, DateUtil.getFileDate(timestamp) + ".json"));
        }
    }

}
