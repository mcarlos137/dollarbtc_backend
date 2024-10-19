/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetBalance extends AbstractOperation<JsonNode> {

    private final String userName;
    private final boolean onlyAvailable, withGlobalEstimatedBalance;

    public MCUserGetBalance(String userName, boolean onlyAvailable, boolean withGlobalEstimatedBalance) {
        super(JsonNode.class);
        this.userName = userName;
        this.onlyAvailable = onlyAvailable;
        this.withGlobalEstimatedBalance = withGlobalEstimatedBalance;
    }

    @Override
    public void execute() {
        JsonNode balance = mapper.createObjectNode();
        Double usdEstimatedBalance = 0.0;
        Double btcAvailableAmount = 0.0;
        Double btcDeferredAmount = null;
        JsonNode btcBalance = mapper.createObjectNode();
        Iterator<JsonNode> balanceIterator = BaseOperation.getBalance(UsersFolderLocator.getMCBalanceFolder(userName)).iterator();
        while (balanceIterator.hasNext()) {
            JsonNode balanceIt = balanceIterator.next();
            String balanceItCurrency = balanceIt.get("currency").textValue();
            if (!balanceItCurrency.equals("BTC")) {
                continue;
            }
            btcAvailableAmount = balanceIt.get("amount").doubleValue();
            if (balanceIt.has("deferredAmount") && balanceIt.get("deferredAmount").doubleValue() > 0.0) {
                btcDeferredAmount = balanceIt.get("deferredAmount").doubleValue();
            }
        }
        JsonNode usdSellOffers = new OTCGetOffers("USD", "MONEYCLICK", OfferType.BID, PaymentType.MAIN, false).getResponse();
        if (!usdSellOffers.has("USD") || !usdSellOffers.get("USD").has("BID__MONEYCLICK__MAIN")) {
            super.response = balance;
            return;
        }
        Double usdSellPrice = usdSellOffers.get("USD").get("BID__MONEYCLICK__MAIN").get("price").doubleValue();
        ((ObjectNode) btcBalance).put("availableBalance", btcAvailableAmount);
        if(btcDeferredAmount != null){
            ((ObjectNode) btcBalance).put("deferredBalance", btcDeferredAmount);
        }
        ((ObjectNode) btcBalance).put("usdEstimatedBalance", btcAvailableAmount * usdSellPrice);
        usdEstimatedBalance = usdEstimatedBalance + btcAvailableAmount * usdSellPrice;
        ((ObjectNode) balance).set("BTC", btcBalance);
        ArrayNode mcBalance = BaseOperation.getBalance(UsersFolderLocator.getMCBalanceFolder(userName));
        File otcFolder = OTCFolderLocator.getFolder("MAIN");
        for (File otcCurrencyFolder : otcFolder.listFiles()) {
            if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                continue;
            }
            String otcCurrency = otcCurrencyFolder.getName();
            JsonNode currencyBalance = mapper.createObjectNode();
            Double mcAvailableAmount = 0.0;
            Double mcDeferredAmount = null;
            Iterator<JsonNode> mcBalanceIterator = mcBalance.iterator();
            while (mcBalanceIterator.hasNext()) {
                JsonNode mcBalanceIt = mcBalanceIterator.next();
                if (mcBalanceIt.get("currency").textValue().equals(otcCurrency)) {
                    mcAvailableAmount = mcBalanceIt.get("amount").doubleValue();
                    if (mcBalanceIt.has("deferredAmount")  && mcBalanceIt.get("deferredAmount").doubleValue() > 0.0) {
                        mcDeferredAmount = mcBalanceIt.get("deferredAmount").doubleValue();
                    }
                    break;
                }
            }
            Double btcBuyPrice = null;
            Double btcSellPrice = null;
            Double btcBuyMinAmount = null;
            Double btcBuyMaxAmount = null;
            Double btcSellMinAmount = null;
            Double btcSellMaxAmount = null;
            JsonNode buyOffers = new OTCGetOffers(otcCurrency, "MONEYCLICK", OfferType.ASK, PaymentType.MAIN, false).getResponse();
            JsonNode sellOffers = new OTCGetOffers(otcCurrency, "MONEYCLICK", OfferType.BID, PaymentType.MAIN, false).getResponse();
            if (buyOffers.has(otcCurrency)) {
                Iterator<JsonNode> offersIterator = buyOffers.get(otcCurrency).iterator();
                while (offersIterator.hasNext()) {
                    JsonNode offersIt = offersIterator.next();
                    btcBuyPrice = offersIt.get("price").doubleValue();
                    btcBuyMinAmount = offersIt.get("minPerOperationAmount").doubleValue();
                    btcBuyMaxAmount = offersIt.get("maxPerOperationAmount").doubleValue();
                    break;
                }
            } else {
                continue;
            }
            if (sellOffers.has(otcCurrency)) {
                Iterator<JsonNode> offersIterator = sellOffers.get(otcCurrency).iterator();
                while (offersIterator.hasNext()) {
                    JsonNode offersIt = offersIterator.next();
                    btcSellPrice = offersIt.get("price").doubleValue();
                    btcSellMinAmount = offersIt.get("minPerOperationAmount").doubleValue();
                    btcSellMaxAmount = offersIt.get("maxPerOperationAmount").doubleValue();
                    break;
                }
            } else {
                continue;
            }
            if (btcBuyPrice == null || btcSellPrice == null) {
                continue;
            }
            ((ObjectNode) currencyBalance).put("availableBalance", mcAvailableAmount);
            if(mcDeferredAmount != null){
                ((ObjectNode) currencyBalance).put("deferredBalance", mcDeferredAmount);
            }
            if (!otcCurrency.equals("USD")) {
                ((ObjectNode) currencyBalance).put("usdEstimatedBalance", mcAvailableAmount / btcBuyPrice * usdSellPrice);
                usdEstimatedBalance = usdEstimatedBalance + mcAvailableAmount / btcBuyPrice * usdSellPrice;
            } else {
                ((ObjectNode) currencyBalance).put("usdEstimatedBalance", mcAvailableAmount);
                usdEstimatedBalance = usdEstimatedBalance + mcAvailableAmount;
            }
            ((ObjectNode) currencyBalance).put("btcBuyPrice", btcBuyPrice);
            ((ObjectNode) currencyBalance).put("btcSellPrice", btcSellPrice);
            ((ObjectNode) currencyBalance).put("btcBuyMinAmount", btcBuyMinAmount);
            ((ObjectNode) currencyBalance).put("btcBuyMaxAmount", btcBuyMaxAmount);
            ((ObjectNode) currencyBalance).put("btcSellMinAmount", btcSellMinAmount);
            ((ObjectNode) currencyBalance).put("btcSellMaxAmount", btcSellMaxAmount);
            ((ObjectNode) balance).set(otcCurrency, currencyBalance);
        }
        if (withGlobalEstimatedBalance) {
            ((ObjectNode) balance).put("usdEstimatedBalance", usdEstimatedBalance);
            ((ObjectNode) balance).put("btcEstimatedBalance", usdEstimatedBalance / usdSellPrice);
        }
        Logger.getLogger(MCUserGetBalance.class.getName()).log(Level.INFO, "USER {0} GETTING BALANCE", userName);
        super.response = balance;
    }

}
