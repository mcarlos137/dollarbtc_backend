/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;

/**
 *
 * @author ricardo torres
 */
public class ExchangesFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Exchanges"));
    }
    
    public static File getExchangeFolder(String exchangeId) {
        return new File(getFolder(), exchangeId);
    }
    
    public static File getExchangeSymbolFolder(String exchangeId, String symbol) {
        return new File(getExchangeFolder(exchangeId), symbol);
    }
    
    public static File getExchangeSymbolTradesFolder(String exchangeId, String symbol) {
        return new File(getExchangeSymbolFolder(exchangeId, symbol), "Trades");
    }
    
    public static File getDollarBTCCurrenciesFile() {
        return new File(new File(getFolder(), "DollarBTC"), "currencies.json");
    }
    
    public static File getDollarBTCSymbolsFile() {
        return new File(new File(getFolder(), "DollarBTC"), "symbols.json");
    }
    
    public static File getDollarBTCSymbolTickerFolder(String symbol) {
        return new File(new File(new File(getFolder(), "DollarBTC"), symbol), "Ticker");
    }
    
    public static File getDollarBTCSymbolOrderbookFolder(String symbol) {
        return new File(new File(new File(getFolder(), "DollarBTC"), symbol), "Orderbook");
    }
    
    public static File getDollarBTCSymbolTradesFolder(String symbol) {
        return new File(new File(new File(getFolder(), "DollarBTC"), symbol), "Trades");
    }
    
    public static File getDollarBTCSymbolTradesOldFolder(String symbol) {
        return new File(getDollarBTCSymbolTradesFolder(symbol), "Old");
    }
    
    public static File getDollarBTCSymbolCandlesPeriodFolder(String symbol, String period) {
        return new File(new File(new File(new File(getFolder(), "DollarBTC"), symbol), "Candles"), period);
    }
            
    public static File getDollarBTCSymbolCandlesPeriodOldFolder(String symbol, String period) {
        return new File(getDollarBTCSymbolCandlesPeriodFolder(symbol, period), "Old");
    }
    
}
