/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.ExchangeOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.ModelOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserCurrencyChange;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author CarlosDaniel
 */
public class SecundaryOperation {

    public static String currencyChangeUser(String userName, String baseCurrency, String targetCurrency, BigDecimal requestedAmount) {
        System.out.println("changeCurrency from baseCurrency "
                + baseCurrency
                + " to targetCurrency "
                + targetCurrency
                + " requestedAmount "
                + requestedAmount);
        BigDecimal price = getLocalMarketPrice(baseCurrency, targetCurrency);
        String symbol = ExchangeOperation.getSymbol(baseCurrency, targetCurrency);
        Order.Type orderType;
        BigDecimal newRequestedAmount;
        if (symbol.equals(baseCurrency + targetCurrency)) {
            newRequestedAmount = requestedAmount.divide(BigDecimal.ONE, 2, RoundingMode.HALF_DOWN);
            orderType = Order.Type.SELL;
        } else {
            newRequestedAmount = requestedAmount.multiply(price).divide(BigDecimal.ONE, 2, RoundingMode.HALF_DOWN);
            orderType = Order.Type.BUY;
        }
        String currencyChangeResponse = new UserCurrencyChange(userName, baseCurrency, targetCurrency, price.setScale(8, RoundingMode.UP), requestedAmount, true).getResponse();
        if (currencyChangeResponse.equals("OK")) {
            Order order = new Order.Builder(null, symbol, orderType, DateUtil.getCurrentDate()).build();
            order.setTradableAmount(newRequestedAmount);
            order.setPrice(price);
            System.out.println("order: " + order);
            if (PrimaryOperation.userNewOrder(null, userName, symbol, order)) {
                new UserCurrencyChange(userName, baseCurrency, targetCurrency, price.setScale(8, RoundingMode.UP), requestedAmount, false).getResponse();
                return "OK";
            }
            return "THERE IS A PROBLEM WITH EXCHANGE ACCOUNT";
        }
        return currencyChangeResponse;
    }

    public static String currencyChangeModel(String modelName, String baseCurrency, String targetCurrency, BigDecimal amount) {
        System.out.println("changeCurrency from baseCurrency "
                + baseCurrency
                + " to targetCurrency "
                + targetCurrency
                + " amount "
                + amount);
        if (!baseCurrency.equals("BTC") && !baseCurrency.equals("USDT") && !baseCurrency.equals("ETH")) {
            return "OK";
        }
        BigDecimal price = getLocalMarketPrice(baseCurrency, targetCurrency);
        String symbol = ExchangeOperation.getSymbol(baseCurrency, targetCurrency);
        Order.Type orderType;
        BigDecimal newAmount;
        if (symbol.equals(baseCurrency + targetCurrency)) {
            newAmount = amount.divide(BigDecimal.ONE, 2, RoundingMode.HALF_DOWN);
            orderType = Order.Type.SELL;
        } else {
            newAmount = amount.multiply(price).divide(BigDecimal.ONE, 2, RoundingMode.HALF_DOWN);
            orderType = Order.Type.BUY;
        }
        Order order = new Order.Builder(null, symbol, orderType, DateUtil.getCurrentDate()).build();
        order.setTradableAmount(newAmount);
        order.setPrice(price);
        System.out.println("order: " + order);
        if (PrimaryOperation.userNewOrder(null, modelName.split("__")[0], symbol, order)) {
            ModelOperation.currencyChange(modelName, baseCurrency, targetCurrency, price.setScale(8, RoundingMode.UP), amount);
            return "OK";
        }
        return "THERE IS A PROBLEM WITH EXCHANGE ACCOUNT";
    }

    public static BigDecimal getLocalMarketPrice(String baseCurrency, String targetCurrency) {
        String symbol = ExchangeOperation.getSymbol(baseCurrency, targetCurrency);
        BigDecimal marketPrice, newMarketPrice;
        marketPrice = PrimaryOperation.getTickerPrices("HitBTC", symbol)[1];
        newMarketPrice = PrimaryOperation.getTickerPrices("Binance", symbol)[1];
        if (marketPrice.compareTo(newMarketPrice) < 0) {
            marketPrice = newMarketPrice;
        }
        if (!symbol.equals(baseCurrency + targetCurrency)) {
            marketPrice = BigDecimal.ONE.divide(marketPrice, 8, RoundingMode.UP);
        }
        return marketPrice.multiply(BigDecimal.valueOf(0.997));
    }

}
