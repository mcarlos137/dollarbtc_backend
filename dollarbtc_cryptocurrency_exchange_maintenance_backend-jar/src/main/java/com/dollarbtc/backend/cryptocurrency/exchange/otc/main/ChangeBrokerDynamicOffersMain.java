/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTicker;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
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
public class ChangeBrokerDynamicOffersMain {

    public static void main(String[] args) {
        File brokersFolder = BrokersFolderLocator.getFolder();
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        for (File brokerFolder : brokersFolder.listFiles()) {
            if (!brokerFolder.isDirectory()) {
                continue;
            }
            System.out.println("----------------------------------------------");
            System.out.println("starting broker " + brokerFolder.getName());
            System.out.println("----------------------------------------------");
            File brokerOffersFolder = new File(brokerFolder, "Offers");
            if(!brokerOffersFolder.isDirectory()){
                continue;
            }
            Double minAskPrice = 0.0;
            for (File brokerOfferFolder : brokerOffersFolder.listFiles()) {
                if (!brokerOfferFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting offer " + brokerOfferFolder.getName());
                System.out.println("----------------------------------------------");
                String currency = brokerOfferFolder.getName().split("__")[0];
                OfferType offerType = OfferType.valueOf(brokerOfferFolder.getName().split("__")[1]);
                if (!offerType.equals(OfferType.ASK)) {
                    continue;
                }
                for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
                    if (!brokerOfferFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode brokerOffer = new ObjectMapper().readTree(brokerOfferFile);
                        Double offerAskPrice = 0.0;
                        if (brokerOffer.has("source")
                                && brokerOffer.has("limitPrice")
                                && brokerOffer.has("marginPercent")
                                && brokerOffer.has("spreadPercent")) {
                            System.out.println("----------------------------------------------");
                            System.out.println("starting change offer process");
                            System.out.println("----------------------------------------------");
                            String brokerOfferSource = brokerOffer.get("source").textValue();
                            Double brokerOfferLimitPrice = brokerOffer.get("limitPrice").doubleValue();
                            Double brokerOfferMarginPercent = brokerOffer.get("marginPercent").doubleValue();
                            Double sourceAskPrice = getSourcePrice(currency, brokerOfferSource, offerType);
                            offerAskPrice = getAskPrice(sourceAskPrice, brokerOfferLimitPrice, brokerOfferMarginPercent);
                            if (brokerOffer.has("useChangePriceByOperationBalance") && brokerOffer.get("useChangePriceByOperationBalance").booleanValue()) {
                                offerAskPrice = getPriceByOperationBalance(currency, offerAskPrice, offerType);
                            }
                            System.out.println("offerAskPrice: " + offerAskPrice);
                            ((ObjectNode) brokerOffer).put("price", offerAskPrice);
                            FileUtil.editFile(brokerOffer, brokerOfferFile);
                            System.out.println("----------------------------------------------");
                            System.out.println("finishing change offer process");
                            System.out.println("----------------------------------------------");
                        } else {
                            offerAskPrice = brokerOffer.get("price").doubleValue();
                        }
                        if (minAskPrice == null || offerAskPrice < minAskPrice) {
                            minAskPrice = offerAskPrice;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ChangeBrokerDynamicOffersMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("----------------------------------------------");
                System.out.println("finishing offer " + brokerOfferFolder.getName());
                System.out.println("----------------------------------------------");
            }
            for (File brokerOfferFolder : brokerOffersFolder.listFiles()) {
                if (!brokerOfferFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting offer " + brokerOfferFolder.getName());
                System.out.println("----------------------------------------------");
                String currency = brokerOfferFolder.getName().split("__")[0];
                OfferType offerType = OfferType.valueOf(brokerOfferFolder.getName().split("__")[1]);
                if (!offerType.equals(OfferType.BID)) {
                    continue;
                }
                for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
                    if (!brokerOfferFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode brokerOffer = new ObjectMapper().readTree(brokerOfferFile);
                        if (brokerOffer.has("source")
                                && brokerOffer.has("limitPrice")
                                && brokerOffer.has("marginPercent")
                                && brokerOffer.has("spreadPercent")) {
                            System.out.println("----------------------------------------------");
                            System.out.println("starting change offer process");
                            System.out.println("----------------------------------------------");
                            String brokerOfferSource = brokerOffer.get("source").textValue();
                            Double brokerOfferLimitPrice = brokerOffer.get("limitPrice").doubleValue();
                            Double brokerOfferMarginPercent = brokerOffer.get("marginPercent").doubleValue();
                            Double brokerOfferSpreadPercent = brokerOffer.get("spreadPercent").doubleValue();
                            Double sourceBidPrice = getSourcePrice(currency, brokerOfferSource, offerType);
                            Double offerBidPrice = getBidPrice(sourceBidPrice, brokerOfferLimitPrice, brokerOfferMarginPercent, minAskPrice, brokerOfferSpreadPercent);
                            if (brokerOffer.has("useChangePriceByOperationBalance") && brokerOffer.get("useChangePriceByOperationBalance").booleanValue()) {
                                offerBidPrice = getPriceByOperationBalance(currency, offerBidPrice, offerType);
                            }
                            System.out.println("offerBidPrice: " + offerBidPrice);
                            ((ObjectNode) brokerOffer).put("price", offerBidPrice);
                            FileUtil.editFile(brokerOffer, brokerOfferFile);
                            System.out.println("----------------------------------------------");
                            System.out.println("finishing change offer process");
                            System.out.println("----------------------------------------------");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ChangeBrokerDynamicOffersMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("----------------------------------------------");
                System.out.println("finishing offer " + brokerOfferFolder.getName());
                System.out.println("----------------------------------------------");
            }
            System.out.println("----------------------------------------------");
            System.out.println("finishing broker " + brokerFolder.getName());
            System.out.println("----------------------------------------------");
        }
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
    }

    private static Double getSourcePrice(String currency, String source, OfferType offerType) {
        switch (source) {
            case "LocalBitcoins":
                JsonNode ticker = new LocalBitcoinsGetTicker("BTC" + currency).getResponse();
                return ticker.get(offerType.name().toLowerCase()).get("average").get("price").doubleValue();
        }
        return null;
    }

    private static Double getAskPrice(Double sourcePrice, Double offerLimitPrice, Double offerMarginPercent) {
        Double askPrice = sourcePrice * (100 + offerMarginPercent) / 100;
        if (offerLimitPrice > askPrice) {
            askPrice = offerLimitPrice;
        }
        return askPrice;
    }
    
    private static Double getBidPrice(Double sourcePrice, Double offerLimitPrice, Double offerMarginPercent, Double askPrice, Double offerSpreadPercent) {
        Double bidPrice = sourcePrice * (100 - offerMarginPercent) / 100;
        if (offerLimitPrice < bidPrice) {
            bidPrice = offerLimitPrice;
        }
        if (askPrice != 0.0) {
            Double bidSpreadPrice = askPrice - ((offerSpreadPercent / 100) * askPrice);
            if (bidSpreadPrice < bidPrice) {
                bidPrice = bidSpreadPrice;
            }
        }
        return bidPrice;
    }
    
    private static Double getPriceByOperationBalance(String currency, Double price, OfferType offerType){
        File otcCurrencyOperationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode otcCurrencyOperationBalance = new ObjectMapper().readTree(otcCurrencyOperationBalanceFile);
                switch(offerType){
                    case ASK:
                        Double askPrice = otcCurrencyOperationBalance.get("askPrice").doubleValue();
                        if(askPrice > price){
                            price = askPrice;
                        }
                        break;
                    case BID:
                        Double bidPrice = otcCurrencyOperationBalance.get("bidPrice").doubleValue();
                        if(bidPrice < price){
                            price = bidPrice;
                        }
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(ChangeBrokerDynamicOffersMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return price;
    }

}
