/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class TradeResponse implements Serializable {
    
    private final List<Trade> trades;   

    public TradeResponse(List<Trade> trades) {
        this.trades = trades;
    }
    
    public List<Trade> getTrades() {
        return trades;
    }
    
}
