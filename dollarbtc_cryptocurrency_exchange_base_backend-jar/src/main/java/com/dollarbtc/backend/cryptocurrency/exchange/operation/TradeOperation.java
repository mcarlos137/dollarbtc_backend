/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class TradeOperation {
    
    public static List<Trade> getAllTrades(String exchangeId, String symbol, String initDate, String endDate, CollectionOrderByDate collectionOrderByDate){
        List<Trade> trades = LocalData.getTrades(exchangeId, symbol, initDate, endDate, 2000, "NORMAL", true);
        if(collectionOrderByDate.equals(CollectionOrderByDate.ASC)){
            Collections.reverse(trades);
        }
        return trades;
    }
    
    public static List<Trade> getReducedTrades(String exchangeId, String symbol, String initDate, String endDate, CollectionOrderByDate collectionOrderByDate){
        List<Trade> trades = LocalData.getTrades(exchangeId, symbol, initDate, endDate, 1000, "1H", false);
        if(collectionOrderByDate.equals(CollectionOrderByDate.ASC)){
            Collections.reverse(trades);
        }
        return trades;
    }
    
}
