/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.binance.BinanceGetLastTrade;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTicker;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOperationBalanceParams;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author carlosmolina
 */
public class OTCChangeOperationPrice extends AbstractOperation<Void> {

    private final String currency;
    private final Double amount, operationPrice;
    private final String operationType;

    public OTCChangeOperationPrice(String currency, Double amount, String operationType, Double operationPrice) {
        super(Void.class);
        this.currency = currency;
        this.amount = amount;
        this.operationPrice = operationPrice;
        this.operationType = operationType;
    }

    @Override
    public void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, currency);
        File otcCurrencyOperationBalanceFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyFolder, "OperationBalance"));
        File otcCurrencyOperationBalanceOldFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyOperationBalanceFolder, "Old"));
        Double askPrice = null;
        Double bidPrice = null;
        Double lastBuyPrice = null;
        Double lastSellPrice = null;
        JsonNode otcCurrencyOperationBalance;
        Long otcCurrencyOperationBalanceFileId = null;
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            try {
                otcCurrencyOperationBalance = mapper.readTree(otcCurrencyOperationBalanceFile);
                askPrice = otcCurrencyOperationBalance.get("askPrice").doubleValue();
                bidPrice = otcCurrencyOperationBalance.get("bidPrice").doubleValue();
                if (otcCurrencyOperationBalance.has("lastBuyPrice")) {
                    lastBuyPrice = otcCurrencyOperationBalance.get("lastBuyPrice").doubleValue();
                }
                if (otcCurrencyOperationBalance.has("lastSellPrice")) {
                    lastSellPrice = otcCurrencyOperationBalance.get("lastSellPrice").doubleValue();
                }
                otcCurrencyOperationBalanceFileId = Long.parseLong(otcCurrencyOperationBalanceFile.getName().replace(".json", "")) - 1;
                FileUtil.moveFileToFolder(otcCurrencyOperationBalanceFile, otcCurrencyOperationBalanceOldFolder);
            } catch (IOException ex) {
                Logger.getLogger(OTCChangeOperationPrice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (askPrice == null || bidPrice == null) {
            if (currency.equals("USDT") || currency.equals("ETH")) {
                JsonNode lastTrade = new BinanceGetLastTrade("BTC" + currency).getResponse();
                askPrice = Double.parseDouble(lastTrade.get("price").textValue());
                bidPrice = Double.parseDouble(lastTrade.get("price").textValue());
            } else {
                JsonNode localBitcoinsTicker = new LocalBitcoinsGetTicker("BTC" + currency).getResponse();
                askPrice = localBitcoinsTicker.get("ask").get("average").get("price").doubleValue();
                bidPrice = localBitcoinsTicker.get("bid").get("average").get("price").doubleValue();
            }
            lastBuyPrice = askPrice;
            lastSellPrice = bidPrice;
        }
        Double newAskPrice = null;
        Double newBidPrice = null;
        JsonNode operationBalanceParams = new OTCAdminGetOperationBalanceParams(currency, false).getResponse();
        Double maxSpreadPercent = operationBalanceParams.get("maxSpreadPercent").doubleValue();
        Double changePercent = operationBalanceParams.get("changePercent").doubleValue();
        Double maxSpread = maxSpreadPercent / 100;
        Double spread;
        switch (operationType) {
            case "BUY":
                newAskPrice = askPrice + (amount * changePercent / 100);
                newBidPrice = bidPrice;
                spread = (newAskPrice - newBidPrice) / newAskPrice;
                if (spread > maxSpread) {
                    newAskPrice = newBidPrice / (1 - maxSpread);
                }
                break;
            case "SELL":
                newAskPrice = askPrice;
                newBidPrice = bidPrice - (amount * changePercent / 100);
                spread = (newAskPrice - newBidPrice) / newAskPrice;
                if (spread > maxSpread) {
                    newBidPrice = newAskPrice * (1 - maxSpread);
                }
                break;
            case "NONE":
                newAskPrice = askPrice;
                newBidPrice = bidPrice;
                break;
        }
        otcCurrencyOperationBalance = mapper.createObjectNode();
        ((ObjectNode) otcCurrencyOperationBalance).put("askPrice", newAskPrice);
        ((ObjectNode) otcCurrencyOperationBalance).put("bidPrice", newBidPrice);
        switch (operationType) {
            case "BUY":
                if (operationPrice == null) {
                    ((ObjectNode) otcCurrencyOperationBalance).put("lastBuyPrice", lastBuyPrice);
                } else {
                    ((ObjectNode) otcCurrencyOperationBalance).put("lastBuyPrice", operationPrice);
                }
                ((ObjectNode) otcCurrencyOperationBalance).put("lastSellPrice", lastSellPrice);
                break;
            case "SELL":
                if (operationPrice == null) {
                    ((ObjectNode) otcCurrencyOperationBalance).put("lastSellPrice", lastSellPrice);
                } else {
                    ((ObjectNode) otcCurrencyOperationBalance).put("lastSellPrice", operationPrice);
                }
                ((ObjectNode) otcCurrencyOperationBalance).put("lastBuyPrice", lastBuyPrice);
                break;
            case "NONE":
                ((ObjectNode) otcCurrencyOperationBalance).put("lastBuyPrice", lastBuyPrice);
                ((ObjectNode) otcCurrencyOperationBalance).put("lastSellPrice", lastSellPrice);
                break;
        }
        ((ObjectNode) otcCurrencyOperationBalance).put("amount", amount);
        ((ObjectNode) otcCurrencyOperationBalance).put("operationType", operationType);
        ((ObjectNode) otcCurrencyOperationBalance).put("timestamp", DateUtil.getCurrentDate());
        if (otcCurrencyOperationBalanceFileId == null) {
            otcCurrencyOperationBalanceFileId = getOTCCurrencyOperationBalanceOldFileId(currency) - 1;
        }
        FileUtil.createFile(otcCurrencyOperationBalance, new File(otcCurrencyOperationBalanceFolder, otcCurrencyOperationBalanceFileId + ".json"));
    }

    private static Long getOTCCurrencyOperationBalanceOldFileId(String currency) {
        File otcCurrencyOperationBalanceOldFolder = new File(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"), "Old");
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(otcCurrencyOperationBalanceOldFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o2, o1) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id2.compareTo(id1);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                File file = it.toFile();
                return Long.parseLong(file.getName().replace(".json", ""));
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCChangeOperationPrice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Long.MAX_VALUE;
    }

}
