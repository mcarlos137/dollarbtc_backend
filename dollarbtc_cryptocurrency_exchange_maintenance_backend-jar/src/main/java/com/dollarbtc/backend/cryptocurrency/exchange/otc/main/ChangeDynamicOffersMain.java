/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.binance.BinanceGetLastTrade;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.binance.BinanceGetTicker;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.coinbase.CoinbaseGetUpdatedPrice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.hitbtc.HitBTCGetLastTrade;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class ChangeDynamicOffersMain {

    public static void main(String[] args) {
        File otcFolder = OTCFolderLocator.getFolder(null);
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        for (File otcCurrencyFolder : otcFolder.listFiles()) {
            if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                continue;
            }
            System.out.println("----------------------------------------------");
            System.out.println("starting currency " + otcCurrencyFolder.getName());
            System.out.println("----------------------------------------------");
            File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
            Double maxBidPrice = null;
            for (File otcCurrencyOfferFolder : otcCurrencyOffersFolder.listFiles()) {
                if (!otcCurrencyOfferFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting offer " + otcCurrencyOfferFolder.getName());
                System.out.println("----------------------------------------------");
                OfferType offerType = OfferType.valueOf(otcCurrencyOfferFolder.getName().split("__")[0]);
                if (!offerType.equals(OfferType.BID)) {
                    continue;
                }
                for (File otcCurrencyOfferFile : otcCurrencyOfferFolder.listFiles()) {
                    if (!otcCurrencyOfferFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode otcCurrencyOffer = new ObjectMapper().readTree(otcCurrencyOfferFile);
                        Double offerBidPrice = 0.0;
                        if (otcCurrencyOffer.has("source")
                                && otcCurrencyOffer.has("limitPrice")
                                && otcCurrencyOffer.has("marginPercent")
                                && otcCurrencyOffer.has("spreadPercent")) {
                            System.out.println("----------------------------------------------");
                            System.out.println("starting change offer process");
                            System.out.println("----------------------------------------------");
                            String otcCurrencyOfferSource = otcCurrencyOffer.get("source").textValue();
                            Double otcCurrencyOfferLimitPrice = otcCurrencyOffer.get("limitPrice").doubleValue();
                            Double otcCurrencyOfferMarginPercent = otcCurrencyOffer.get("marginPercent").doubleValue();
                            Double otcCurrencyOfferSpreadPercent = otcCurrencyOffer.get("spreadPercent").doubleValue();
                            Double sourceBidPrice = getSourcePrice(otcCurrencyFolder.getName(), otcCurrencyOfferSource, offerType);
                            if (sourceBidPrice == null) {
                                continue;
                            }
                            offerBidPrice = getBidPrice(sourceBidPrice, otcCurrencyOfferLimitPrice, otcCurrencyOfferMarginPercent);
                            if (otcCurrencyOffer.has("useChangePriceByOperationBalance") && otcCurrencyOffer.get("useChangePriceByOperationBalance").booleanValue()) {
                                offerBidPrice = getPriceByOperationBalance(otcCurrencyFolder.getName(), offerBidPrice, offerType);
                            }
                            System.out.println("offerBidPrice: " + offerBidPrice);
                            ((ObjectNode) otcCurrencyOffer).put("price", offerBidPrice);
                            FileUtil.editFile(otcCurrencyOffer, otcCurrencyOfferFile);
                            System.out.println("----------------------------------------------");
                            System.out.println("finishing change offer process");
                            System.out.println("----------------------------------------------");
                        } else {
                            offerBidPrice = otcCurrencyOffer.get("price").doubleValue();
                        }
                        if (maxBidPrice == null || offerBidPrice > maxBidPrice) {
                            maxBidPrice = offerBidPrice;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ChangeDynamicOffersMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("----------------------------------------------");
                System.out.println("finishing offer " + otcCurrencyOfferFolder.getName());
                System.out.println("----------------------------------------------");
            }
            for (File otcCurrencyOfferFolder : otcCurrencyOffersFolder.listFiles()) {
                if (!otcCurrencyOfferFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting offer " + otcCurrencyOfferFolder.getName());
                System.out.println("----------------------------------------------");
                OfferType offerType = OfferType.valueOf(otcCurrencyOfferFolder.getName().split("__")[0]);
                if (!offerType.equals(OfferType.ASK)) {
                    continue;
                }
                for (File otcCurrencyOfferFile : otcCurrencyOfferFolder.listFiles()) {
                    if (!otcCurrencyOfferFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode otcCurrencyOffer = new ObjectMapper().readTree(otcCurrencyOfferFile);
                        Double offerAskPrice = 0.0;
                        if (otcCurrencyOffer.has("source")
                                && otcCurrencyOffer.has("limitPrice")
                                && otcCurrencyOffer.has("marginPercent")
                                && otcCurrencyOffer.has("spreadPercent")) {
                            System.out.println("----------------------------------------------");
                            System.out.println("starting change offer process");
                            System.out.println("----------------------------------------------");
                            String otcCurrencyOfferSource = otcCurrencyOffer.get("source").textValue();
                            Double otcCurrencyOfferLimitPrice = otcCurrencyOffer.get("limitPrice").doubleValue();
                            Double otcCurrencyOfferMarginPercent = otcCurrencyOffer.get("marginPercent").doubleValue();
                            Double otcCurrencyOfferSpreadPercent = otcCurrencyOffer.get("spreadPercent").doubleValue();
                            Double sourceAskPrice = getSourcePrice(otcCurrencyFolder.getName(), otcCurrencyOfferSource, offerType);
                            if (sourceAskPrice == null) {
                                continue;
                            }
                            offerAskPrice = getAskPrice(sourceAskPrice, otcCurrencyOfferLimitPrice, otcCurrencyOfferMarginPercent, maxBidPrice, otcCurrencyOfferSpreadPercent);
                            if (otcCurrencyOffer.has("useChangePriceByOperationBalance") && otcCurrencyOffer.get("useChangePriceByOperationBalance").booleanValue()) {
                                offerAskPrice = getPriceByOperationBalance(otcCurrencyFolder.getName(), offerAskPrice, offerType);
                            }
                            System.out.println("offerAskPrice: " + offerAskPrice);
                            ((ObjectNode) otcCurrencyOffer).put("price", offerAskPrice);
                            FileUtil.editFile(otcCurrencyOffer, otcCurrencyOfferFile);
                            System.out.println("----------------------------------------------");
                            System.out.println("finishing change offer process");
                            System.out.println("----------------------------------------------");
                        } else {
                            offerAskPrice = otcCurrencyOffer.get("price").doubleValue();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ChangeDynamicOffersMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("----------------------------------------------");
                System.out.println("finishing offer " + otcCurrencyOfferFolder.getName());
                System.out.println("----------------------------------------------");
            }
            System.out.println("----------------------------------------------");
            System.out.println("finishing currency " + otcCurrencyFolder.getName());
            System.out.println("----------------------------------------------");
        }
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
    }

    private static Double getSourcePrice(String currency, String source, OfferType offerType) {
//        if (!currency.equals("USD") && source.equals("Coinbase")) {
//            source = "LocalBitcoins";
//        }
        if (currency.equals("USDT")) {
            source = "HitBTC";
        }
        if (currency.equals("ETH")) {
            source = "HitBTC";
        }
//        if (currency.equals("USD")) {
//            source = "Binance";
//        }
        switch (source) {
            case "LocalBitcoins":
                JsonNode ticker = new BinanceGetTicker("BTC" + currency).getResponse();
                return ticker.get(offerType.name().toLowerCase()).get("average").get("price").doubleValue();
            case "Coinbase":
                JsonNode price = new CoinbaseGetUpdatedPrice("BTC" + currency).getResponse();
                return price.get("price").doubleValue();
            case "Binance":
                if (currency.equals("USDT")) {
                    JsonNode lastTrade = new BinanceGetLastTrade("BTC" + currency).getResponse();
                    System.out.println("lastTrade: " + lastTrade);
                    if (lastTrade.has("price")) {
                        return Double.parseDouble(lastTrade.get("price").textValue());
                    }
                } //                else if(currency.equals("USD")) {
                //                    JsonNode lastTrade = new BinanceGetLastTrade("BTCUSDT").getResponse();
                //                    System.out.println("lastTrade: " + lastTrade);
                //                    if (lastTrade.has("price")) {
                //                        return Double.parseDouble(lastTrade.get("price").textValue());
                //                    }
                //                } 
                else {
                    JsonNode lastTrade = new BinanceGetLastTrade(currency + "BTC").getResponse();
                    System.out.println("lastTrade: " + lastTrade);
                    if (lastTrade.has("price")) {
                        return 1 / Double.parseDouble(lastTrade.get("price").textValue());
                    }
                }
            case "HitBTC":
                if (currency.equals("USDT")) {
                    JsonNode lastTrade = new HitBTCGetLastTrade("BTC" + currency).getResponse();
                    System.out.println("lastTrade: " + lastTrade);
                    if (lastTrade.has("price")) {
                        return Double.parseDouble(lastTrade.get("price").textValue());
                    }
                } else {
                    JsonNode lastTrade = new HitBTCGetLastTrade(currency + "BTC").getResponse();
                    System.out.println("lastTrade: " + lastTrade);
                    if (lastTrade.has("price")) {
                        return 1 / Double.parseDouble(lastTrade.get("price").textValue());
                    }
                }
        }
        return null;
    }

    private static Double getAskPrice(Double sourcePrice, Double offerLimitPrice, Double offerMarginPercent, Double bidPrice, Double offerSpreadPercent) {
        Double askPrice = sourcePrice * (100 + offerMarginPercent) / 100;
        if (offerLimitPrice > askPrice) {
            askPrice = offerLimitPrice;
        }
        System.out.println("bidPrice: " + bidPrice);
        if (bidPrice != 0.0) {
            Double askSpreadPrice = bidPrice + ((offerSpreadPercent / 100) * bidPrice);
            System.out.println("askSpreadPrice: " + askSpreadPrice);
            if (askSpreadPrice > askPrice) {
                askPrice = askSpreadPrice;
            }
        }
        System.out.println("askPrice: " + askPrice);
        return askPrice;
    }

    private static Double getBidPrice(Double sourcePrice, Double offerLimitPrice, Double offerMarginPercent) {
        Double bidPrice = sourcePrice * (100 - offerMarginPercent) / 100;
        if (offerLimitPrice < bidPrice) {
            bidPrice = offerLimitPrice;
        }
        System.out.println("bidPrice: " + bidPrice);
        return bidPrice;
    }

    private static Double getPriceByOperationBalance(String currency, Double price, OfferType offerType) {
        File otcCurrencyOperationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode otcCurrencyOperationBalance = new ObjectMapper().readTree(otcCurrencyOperationBalanceFile);
                switch (offerType) {
                    case ASK:
                        Double askPrice = otcCurrencyOperationBalance.get("askPrice").doubleValue();
                        if (askPrice > price) {
                            price = askPrice;
                        }
                        break;
                    case BID:
                        Double bidPrice = otcCurrencyOperationBalance.get("bidPrice").doubleValue();
                        if (bidPrice < price) {
                            price = bidPrice;
                        }
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(ChangeDynamicOffersMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return price;
    }

}
