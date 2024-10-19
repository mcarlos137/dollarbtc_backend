/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.OrderInterval;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author CarlosDaniel
 */
public class ModelGetDataResponse implements Serializable {

    private final String title, startTimestamp, endTimestamp;
    private List<ExchangeIdSymbolData> exchangeIdSymbolDatas;
    private double balancePercent;
    private Map<String, Double> cutOffBalancePercents;

    public ModelGetDataResponse(
            String title,
            String startTimestamp,
            String endTimestamp
    ) {
        this.title = title;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public List<ExchangeIdSymbolData> getExchangeIdSymbolDatas() {
        if (exchangeIdSymbolDatas == null) {
            exchangeIdSymbolDatas = new ArrayList<>();
        }
        return exchangeIdSymbolDatas;
    }

    public double getBalancePercent() {
        return balancePercent;
    }

    public void setBalancePercent(double balancePercent) {
        this.balancePercent = balancePercent;
    }

    public Map<String, Double> getCutOffBalancePercents() {
        if(this.cutOffBalancePercents == null){
            this.cutOffBalancePercents = new HashMap<>();
        }
        return cutOffBalancePercents;
    }

    public void setCutOffBalancePercents(Map<String, Double> cutOffBalancePercents) {
        this.cutOffBalancePercents = cutOffBalancePercents;
    }
    
    public static class ExchangeIdSymbolData {

        private final String exchangeId, symbol;
        private final List<Order> orders;
        private final List<OrderInterval> orderIntervals;

        public ExchangeIdSymbolData(String exchangeId, String symbol, List<Order> orders, List<OrderInterval> orderIntervals) {
            this.exchangeId = exchangeId;
            this.symbol = symbol;
            this.orders = orders;
            this.orderIntervals = orderIntervals;
        }

        public String getExchangeId() {
            return exchangeId;
        }

        public String getSymbol() {
            return symbol;
        }

        public List<Order> getOrders() {
            return orders;
        }

        public List<OrderInterval> getOrderIntervals() {
            return orderIntervals;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExchangeIdSymbolData other = (ExchangeIdSymbolData) obj;
            if (!Objects.equals(this.exchangeId, other.exchangeId)) {
                return false;
            }
            return Objects.equals(this.symbol, other.symbol);
        }

    }

}
