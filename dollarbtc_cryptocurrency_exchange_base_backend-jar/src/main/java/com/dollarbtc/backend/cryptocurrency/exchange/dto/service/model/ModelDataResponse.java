/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.OrderInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.AccountOverviewResponse;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author CarlosDaniel
 */
public class ModelDataResponse implements Serializable {

    private final List<Order> orders;
    private final List<OrderInterval> orderIntervals;
    private List<AccountOverviewResponse.ExchangeIdSymbol> exchangeIdSymbols;
    private List<Trade> trades;

    public ModelDataResponse(List<Order> orders, List<OrderInterval> orderIntervals) {
        this.orders = orders;
        this.orderIntervals = orderIntervals;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<OrderInterval> getOrderIntervals() {
        return orderIntervals;
    }
    
    public List<AccountOverviewResponse.ExchangeIdSymbol> getExchangeIdSymbols() {
        if(exchangeIdSymbols == null){
            exchangeIdSymbols = new ArrayList<>();
        }
        return exchangeIdSymbols;
    }

    public List<Trade> getTrades() {
        if(trades == null){
            trades = new ArrayList<>();
        }
        return trades;
    }
    
}
