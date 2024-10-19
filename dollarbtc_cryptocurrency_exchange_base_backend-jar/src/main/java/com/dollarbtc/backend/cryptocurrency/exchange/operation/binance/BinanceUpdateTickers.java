/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.binance;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BinanceFolderLocator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BinanceUpdateTickers extends AbstractOperation<Void> {

    private final String[] symbolBases;

    public BinanceUpdateTickers(String[] symbolBases) {
        super(Void.class);
        this.symbolBases = symbolBases;
    }

    @Override
    protected void execute() {
        method(symbolBases);
    }

    private synchronized void method(String[] symbolBases) {
        int pagesQuantity = 1;
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        JsonNode ticker = mapper.createObjectNode();
        String timestamp = DateUtil.getMinuteStartDate(null);
        String symbolAsset = "BTC";
        ((ObjectNode) ticker).put("timestamp", timestamp);
        String[] operations = new String[2];
        operations[0] = "buy";
        operations[1] = "sell";
        for (String symbolBase : symbolBases) {
            System.out.println("symbolBase: " + symbolBase);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BinanceUpdateTickers.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean fail = false;
            for (String operation : operations) {
                List<Double> prices = new ArrayList<>();
                int i = 1;
                while (i <= pagesQuantity) {
                    try {
                        prices.addAll(getPricesNew(new BinanceGetPrices(symbolBase, operation).getResponse()));
                    } catch (IOException ex) {
                        Logger.getLogger(BinanceUpdateTickers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    i++;
                }
                switch (operation) {
                    case "buy":
                        operation = "ask";
                        break;
                    case "sell":
                        operation = "bid";
                        break;
                }
                double[] pricesData = getPricesData(prices, 30);
                if (pricesData[0] == 0 || pricesData[1] == 0 || pricesData[2] == 0) {
                    fail = true;
                    break;
                }
                JsonNode tickerOperation = mapper.createObjectNode();
                JsonNode tickerOperationAverage = mapper.createObjectNode();
                ((ObjectNode) tickerOperationAverage).put("price", pricesData[0]);
                ((ObjectNode) tickerOperationAverage).put("6H%", "---");
                ((ObjectNode) tickerOperationAverage).put("24H%", "---");
                ((ObjectNode) tickerOperation).set("average", tickerOperationAverage);
                JsonNode tickerOperationLow = mapper.createObjectNode();
                ((ObjectNode) tickerOperationLow).put("price", pricesData[1]);
                ((ObjectNode) tickerOperationLow).put("6H%", "---");
                ((ObjectNode) tickerOperationLow).put("24H%", "---");
                ((ObjectNode) tickerOperation).set("low", tickerOperationLow);
                JsonNode tickerOperationHigh = mapper.createObjectNode();
                ((ObjectNode) tickerOperationHigh).put("price", pricesData[2]);
                ((ObjectNode) tickerOperationHigh).put("6H%", "---");
                ((ObjectNode) tickerOperationHigh).put("24H%", "---");
                ((ObjectNode) tickerOperation).set("high", tickerOperationHigh);
                ((ObjectNode) ticker).set(operation, tickerOperation);
            }
            if (fail) {
                return;
            }
            File tickersFolder = BinanceFolderLocator.getTickersSymbolFolder(symbolAsset + symbolBase);
            File tickersOldFolder = FileUtil.createFolderIfNoExist(tickersFolder, "Old");
            replaceMinutesBeforeValues(mapper, ticker, "6H%", 360, tickersOldFolder);
            replaceMinutesBeforeValues(mapper, ticker, "24H%", 1440, tickersOldFolder);
            for (File tickerFile : tickersFolder.listFiles()) {
                if (!tickerFile.isFile()) {
                    continue;
                }
                FileUtil.moveFileToFolder(tickerFile, tickersOldFolder);
            }
            FileUtil.createFile(ticker, new File(tickersFolder, DateUtil.getFileDate(timestamp) + ".json"));
        }
    }

    private static void replaceMinutesBeforeValues(ObjectMapper mapper, JsonNode ticker, String tag, int minutesBefore, File tickersOldFolder) {
        String timestamp = ticker.get("timestamp").textValue();
        int i = 0;
        JsonNode tickerOld = null;
        while (i < 30) {
            minutesBefore = minutesBefore + i;
            i++;
            String timestamp6H = DateUtil.getDateMinutesBefore(timestamp, minutesBefore);
            File tickersOld6HFile = new File(tickersOldFolder, DateUtil.getFileDate(timestamp6H) + ".json");
            if (!tickersOld6HFile.exists()) {
                continue;
            }
            try {
                tickerOld = mapper.readTree(tickersOld6HFile);
            } catch (IOException ex) {
                Logger.getLogger(BinanceUpdateTickers.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        }
        if (tickerOld == null) {
            return;
        }
        String[] operationTypes = new String[2];
        operationTypes[0] = "bid";
        operationTypes[1] = "ask";
        String[] priceTypes = new String[3];
        priceTypes[0] = "average";
        priceTypes[1] = "low";
        priceTypes[2] = "high";
        for (String operationType : operationTypes) {
            for (String priceType : priceTypes) {
                Double priceold = tickerOld.get(operationType).get(priceType).get("price").doubleValue();
                if (priceold.isNaN() || priceold.isInfinite()) {
                    continue;
                }
                double priceNew = ticker.get(operationType).get(priceType).get("price").doubleValue();
                double priceBTCVEF6HPercent = (priceNew - priceold) / priceold * 100;
                ((ObjectNode) ticker.get(operationType).get(priceType)).put(tag, priceBTCVEF6HPercent);
            }
        }
    }

    private static double[] getPricesData(List<Double> prices, double maxDeviationPercent) {
        double[] pricesData = new double[3];
        if (prices.isEmpty()) {
            pricesData[0] = 0.0;
            pricesData[1] = 0.0;
            pricesData[2] = 0.0;
            return pricesData;
        }
        double pricesSum = 0.0;
        int excludedCounter = 0;
        double low = 0.0;
        double high = 0.0;
        double basePrice = 0;
        int i = 0;
        int newBasePriceCounter = 0;
        int roundsNumber = prices.size() / 4;
        while (i < prices.size() / 4) {
            double newBasePrice = prices.get(i);
            double basePriceDeviationPercent = (newBasePrice - basePrice) / newBasePrice * 100;
            if (basePriceDeviationPercent < maxDeviationPercent && basePriceDeviationPercent > -maxDeviationPercent) {
                newBasePriceCounter = 0;
            } else {
                newBasePriceCounter++;
            }
            if (newBasePriceCounter >= roundsNumber) {
                basePrice = newBasePrice;
            }
            i++;
        }
        for (double price : prices) {
            double priceDeviationPercent = (basePrice - price) / basePrice * 100;
            if (priceDeviationPercent < maxDeviationPercent && priceDeviationPercent > -maxDeviationPercent) {
                pricesSum = pricesSum + price;
                if (low == 0.0 || price < low) {
                    low = price;
                }
                if (high == 0.0 || price > high) {
                    high = price;
                }
            } else {
                excludedCounter++;
            }
        }
        pricesData[0] = pricesSum / (prices.size() - excludedCounter);
        pricesData[1] = low;
        pricesData[2] = high;
        return pricesData;
    }

    private static List<Double> getPricesNew(JsonNode result) throws IOException {
        List<Double> results = new ArrayList<>();
        Iterator<JsonNode> resultsIterator = result.get("data").iterator();
        while (resultsIterator.hasNext()) {
            JsonNode next = resultsIterator.next();
            results.add(Double.parseDouble(next.get("adv").get("price").textValue()));
        }
        System.out.println(results);        
       return results;
    }

}
