/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.Collections;

/**
 *
 * @author CarlosDaniel
 */
public class PrimaryOperation {

    public static BigDecimal[] getTickerPrices(String exchangeId, String symbol) {
        BigDecimal[] tickerPrices = new BigDecimal[2];
        switch (exchangeId) {
            case "HitBTC":
                com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Ticker hitBTCTicker = new com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.GetTickerOperation(symbol).getResponse();
                if (hitBTCTicker == null || hitBTCTicker.getAsk() == null || hitBTCTicker.getBid() == null) {
                    return null;
                }
                tickerPrices[0] = hitBTCTicker.getBid();
                tickerPrices[1] = hitBTCTicker.getAsk();
                break;
            case "Binance":
                com.dollarbtc.backend.cryptocurrency.exchange.binance.dto.Ticker binanceTicker = new com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.GetTickerOperation(symbol).getResponse();
                if (binanceTicker == null || binanceTicker.getAskPrice() == null || binanceTicker.getBidPrice() == null) {
                    System.out.println("ticker fail to retrieve price " + symbol);
                    return null;
                }
                tickerPrices[0] = binanceTicker.getBidPrice();
                tickerPrices[1] = binanceTicker.getAskPrice();
                break;
        }
        if (tickerPrices.length == 0) {
            return null;
        }
        return tickerPrices;
    }

    public static boolean userNewOrder(String exchangeId, String userName, String symbol, Order order) {
        File userFile = new File(new File(new File(OPERATOR_PATH, "Users"), userName), "config.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(userFile);
        } catch (IOException ex) {
            Logger.getLogger(PrimaryOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (jsonNode == null) {
            return false;
        }
        if (!jsonNode.get("active").booleanValue()) {
            return false;
        }
        if (!UserEnvironment.valueOf(jsonNode.get("environment").textValue()).equals(UserEnvironment.PRODUCTION)) {
            return true;
        }
        if(exchangeId == null){
            exchangeId = "HitBTC";
        }
        if(!jsonNode.has(exchangeId + "_loginAccount")){
            exchangeId = "Binance";
        }
        if(!jsonNode.has(exchangeId + "_loginAccount")){
            return true;
        }
        String loginAccount = jsonNode.get(exchangeId + "_loginAccount").textValue();
        String side;
        if (order.getType().equals(Order.Type.BUY)) {
            side = "buy";
        } else {
            side = "sell";
        }
        switch (exchangeId) {
            case "HitBTC":
                String clientOrderId = UUID.randomUUID().toString().replace("-", "");
                com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserCreateNewOrderOperation userCreateNewOrderOperation = new com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserCreateNewOrderOperation(loginAccount, symbol, clientOrderId, side, "market", order.getPrice().toString(), order.getTradableAmount().toString(), "15230777");
                com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Order newOrder = userCreateNewOrderOperation.getResponse();
                if (newOrder.getError() != null) {
                    System.out.println("exchange error: " + newOrder.getError());
                    return false;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PrimaryOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
                com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserGetTradesOperation userGetTradesOperation = new com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserGetTradesOperation(loginAccount, symbol, 10);
                com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.MarketTrade[] marketTrades = userGetTradesOperation.getResponse();
                for (com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.MarketTrade marketTrade : marketTrades) {
                    if (!marketTrade.getClientOrderId().equals(clientOrderId)) {
                        continue;
                    }
                    order.getMarketTrades().add(new Order.MarketTrade(marketTrade.getQuantity(), marketTrade.getPrice(), marketTrade.getFee()));
                }
                return true;
        }
        return false;
    }

    public static ArrayList<Period> getMarketPeriods(String exchangeId, String symbol, String period) {
        ArrayList<Period> periods = new ArrayList<>();
        String startTimestamp = null;
        switch (exchangeId) {
            case "HitBTC":
                com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Candle[] hitBTCCandles = new com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.GetCandlesOperation(symbol, period).getResponse();
                for (com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Candle hitBTCCandle : hitBTCCandles) {
                    if (startTimestamp == null) {
                        startTimestamp = hitBTCCandle.getTimestamp();
                        continue;
                    }
                    Period hitBTCPeriod = new Period(startTimestamp, hitBTCCandle.getTimestamp());
                    hitBTCPeriod.addTrade(new Trade.Builder(exchangeId, symbol, null, null, hitBTCCandle.getClose(), hitBTCCandle.getTimestamp()).build());
                    periods.add(hitBTCPeriod);
                    startTimestamp = hitBTCCandle.getTimestamp();
                }
                break;
            case "Binance":
                com.dollarbtc.backend.cryptocurrency.exchange.binance.dto.Candles binanceCandles = new com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.GetCandlesOperation(symbol, period).getResponse();
                for (com.dollarbtc.backend.cryptocurrency.exchange.binance.dto.Candles.Candle binanceCandle : binanceCandles.getCandles()) {
                    if (startTimestamp == null) {
                        startTimestamp = binanceCandle.getEndTimestamp();
                        continue;
                    }
                    Period binancePeriod = new Period(startTimestamp, binanceCandle.getEndTimestamp());
                    binancePeriod.addTrade(new Trade.Builder(exchangeId, symbol, null, null, binanceCandle.getClose(), binanceCandle.getEndTimestamp()).build());
                    periods.add(binancePeriod);
                    startTimestamp = binanceCandle.getEndTimestamp();
                }
                break;
        }
        Collections.reverse(periods);
        return periods;
    }
    
}
