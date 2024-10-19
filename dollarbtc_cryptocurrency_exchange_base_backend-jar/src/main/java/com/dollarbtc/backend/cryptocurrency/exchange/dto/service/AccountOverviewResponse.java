/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author CarlosDaniel
 */
public class AccountOverviewResponse implements Serializable {

    private final String modelName, startTimestamp, endTimestamp;
    private List<ExchangeIdSymbol> exchangeIdSymbols;
    private List<ExchangeIdSymbolAlgorithmIntervalName> exchangeIdSymbolAlgorithmIntervalNames;
    private List<ExchangeIdSymbolAlgorithmIntervalNameDetailed> exchangeIdSymbolAlgorithmIntervalNameDetaileds;

    public AccountOverviewResponse(String modelName, String startTimestamp, String endTimestamp) {
        this.modelName = modelName;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public String getModelName() {
        return modelName;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public List<ExchangeIdSymbol> getExchangeIdSymbols() {
        if (exchangeIdSymbols == null) {
            exchangeIdSymbols = new ArrayList<>();
        }
        return exchangeIdSymbols;
    }

    public List<ExchangeIdSymbolAlgorithmIntervalName> getExchangeIdSymbolAlgorithmIntervalNames() {
        if (exchangeIdSymbolAlgorithmIntervalNames == null) {
            exchangeIdSymbolAlgorithmIntervalNames = new ArrayList<>();
        }
        return exchangeIdSymbolAlgorithmIntervalNames;
    }

    public List<ExchangeIdSymbolAlgorithmIntervalNameDetailed> getExchangeIdSymbolAlgorithmIntervalNameDetaileds() {
        if (exchangeIdSymbolAlgorithmIntervalNameDetaileds == null) {
            exchangeIdSymbolAlgorithmIntervalNameDetaileds = new ArrayList<>();
        }
        return exchangeIdSymbolAlgorithmIntervalNameDetaileds;
    }

    public static class ExchangeIdSymbol {

        private final String exchangeId, symbol;
        private double balance;

        public ExchangeIdSymbol(String exchangeId, String symbol) {
            this.exchangeId = exchangeId;
            this.symbol = symbol;
        }

        public String getExchangeId() {
            return exchangeId;
        }

        public String getSymbol() {
            return symbol;
        }

        public double getBalance() {
            return balance;
        }

        public void addToBalance(double balanceToAdd) {
            balance = balance + balanceToAdd;
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
            final ExchangeIdSymbol other = (ExchangeIdSymbol) obj;
            if (!Objects.equals(this.exchangeId, other.exchangeId)) {
                return false;
            }
            return Objects.equals(this.symbol, other.symbol);
        }

    }

    public static class ExchangeIdSymbolAlgorithmIntervalName extends ExchangeIdSymbol {

        private final String algorithmIntervalName;

        public ExchangeIdSymbolAlgorithmIntervalName(String exchangeId, String symbol, String algorithmIntervalName) {
            super(exchangeId, symbol);
            this.algorithmIntervalName = algorithmIntervalName;
        }

        public String getAlgorithmIntervalName() {
            return algorithmIntervalName;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExchangeIdSymbolAlgorithmIntervalName other = (ExchangeIdSymbolAlgorithmIntervalName) obj;
            return Objects.equals(this.algorithmIntervalName, other.algorithmIntervalName);
        }

    }

    public static class ExchangeIdSymbolAlgorithmIntervalNameDetailed extends ExchangeIdSymbolAlgorithmIntervalName {

        private final String startTimestamp, endTimestamp;
        private double initLowestPrice, endLowestPrice, bottomLowestPrice, topLowestPrice;

        public ExchangeIdSymbolAlgorithmIntervalNameDetailed(String exchangeId, String symbol, String algorithmIntervalName, String startTimestamp, String endTimestamp) {
            super(exchangeId, symbol, algorithmIntervalName);
            this.startTimestamp = startTimestamp;
            this.endTimestamp = endTimestamp;
        }

        public String getStartTimestamp() {
            return startTimestamp;
        }

        public String getEndTimestamp() {
            return endTimestamp;
        }

        public double getInitLowestPrice() {
            return initLowestPrice;
        }

        public void setInitLowestPrice(double initLowestPrice) {
            this.initLowestPrice = initLowestPrice;
        }

        public double getEndLowestPrice() {
            return endLowestPrice;
        }

        public void setEndLowestPrice(double endLowestPrice) {
            this.endLowestPrice = endLowestPrice;
        }

        public double getBottomLowestPrice() {
            return bottomLowestPrice;
        }

        public void setBottomLowestPrice(double bottomLowestPrice) {
            this.bottomLowestPrice = bottomLowestPrice;
        }

        public double getTopLowestPrice() {
            return topLowestPrice;
        }

        public void setTopLowestPrice(double topLowestPrice) {
            this.topLowestPrice = topLowestPrice;
        }

    }

}
