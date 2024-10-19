package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class AccountBase {

    private BigDecimal reservedBaseBalance,
            initialBaseBalance,
            currentBaseBalance,
            initialAssetBalance,
            currentAssetBalance,
            lastAskPrice,
            lastBidPrice; // required
    private String exchangeId,
            symbol,
            modelName,
            timestamp,
            info; // required

    public AccountBase() {
    }

    private AccountBase(Builder builder) {
        this.reservedBaseBalance = builder.reservedBaseBalance;
        this.initialBaseBalance = builder.initialBaseBalance;
        this.currentBaseBalance = builder.currentBaseBalance;
        this.initialAssetBalance = builder.initialAssetBalance;
        this.currentAssetBalance = builder.currentAssetBalance;
        this.lastAskPrice = builder.lastAskPrice;
        this.lastBidPrice = builder.lastBidPrice;
        this.exchangeId = builder.exchangeId;
        this.symbol = builder.symbol;
        this.modelName = builder.modelName;
        this.timestamp = builder.timestamp;
        this.info = builder.info;
    }

    public BigDecimal getReservedBaseBalance() {
        return reservedBaseBalance;
    }

    public void setReservedBaseBalance(BigDecimal reservedBaseBalance) {
        this.reservedBaseBalance = reservedBaseBalance;
    }

    public BigDecimal getInitialBaseBalance() {
        return initialBaseBalance;
    }

    public void setInitialBaseBalance(BigDecimal initialBaseBalance) {
        this.initialBaseBalance = initialBaseBalance;
    }

    public BigDecimal getCurrentBaseBalance() {
        return currentBaseBalance;
    }

    public void setCurrentBaseBalance(BigDecimal currentBaseBalance) {
        this.currentBaseBalance = currentBaseBalance;
    }

    public BigDecimal getInitialAssetBalance() {
        return initialAssetBalance;
    }

    public void setInitialAssetBalance(BigDecimal initialAssetBalance) {
        this.initialAssetBalance = initialAssetBalance;
    }

    public BigDecimal getCurrentAssetBalance() {
        return currentAssetBalance;
    }

    public void setCurrentAssetBalance(BigDecimal currentAssetBalance) {
        this.currentAssetBalance = currentAssetBalance;
    }

    public BigDecimal getLastAskPrice() {
        return lastAskPrice;
    }

    public void setLastAskPrice(BigDecimal lastAskPrice) {
        this.lastAskPrice = lastAskPrice;
    }

    public BigDecimal getLastBidPrice() {
        return lastBidPrice;
    }

    public void setLastBidPrice(BigDecimal lastBidPrice) {
        this.lastBidPrice = lastBidPrice;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSymbolBase() {
        return getSymbolBase(symbol);
    }

    public static String getSymbolBase(String symbol) {
        if (symbol != null) {
            if (symbol.contains("USD") && !symbol.startsWith("USD")) {
                return "USDT";
            }
            if (symbol.contains("BTC") && !symbol.startsWith("BTC")) {
                return "BTC";
            }
            if (symbol.contains("ETH") && !symbol.startsWith("ETH")) {
                return "ETH";
            }
        }
        return null;
    }
    
    public String getSymbolAsset() {
        return getSymbolAsset(symbol);
    }

    public static String getSymbolAsset(String symbol) {
        if (symbol != null) {
            return symbol.substring(0, symbol.indexOf(getSymbolBase(symbol)));
        }
        return null;
    }

    @Override
    public String toString() {
        return "AccountBase{"
                + "reservedBaseBalance=" + reservedBaseBalance
                + ", initialBaseBalance=" + initialBaseBalance
                + ", currentBaseBalance=" + currentBaseBalance
                + ", initialAssetBalance=" + initialAssetBalance
                + ", currentAssetBalance=" + currentAssetBalance
                + ", lastAskPrice=" + lastAskPrice
                + ", lastBidPrice=" + lastBidPrice
                + ", exchangeId=" + exchangeId
                + ", symbol=" + symbol
                + ", modelName=" + modelName
                + ", timestamp=" + timestamp
                + ", info=" + info
                + '}';
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("reservedBaseBalance", this.reservedBaseBalance);
        ((ObjectNode) jsonNode).put("initialBaseBalance", this.initialBaseBalance);
        ((ObjectNode) jsonNode).put("currentBaseBalance", this.currentBaseBalance);
        ((ObjectNode) jsonNode).put("initialAssetBalance", this.initialAssetBalance);
        ((ObjectNode) jsonNode).put("currentAssetBalance", this.currentAssetBalance);
        ((ObjectNode) jsonNode).put("lastAskPrice", this.lastAskPrice);
        ((ObjectNode) jsonNode).put("lastBidPrice", this.lastBidPrice);
        ((ObjectNode) jsonNode).put("exchangeId", this.exchangeId);
        ((ObjectNode) jsonNode).put("symbol", this.symbol);
        ((ObjectNode) jsonNode).put("modelName", this.modelName);
        ((ObjectNode) jsonNode).put("timestamp", this.timestamp);
        ((ObjectNode) jsonNode).put("info", this.info);
        return jsonNode;
    }
    
    public JsonNode infoToJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("timestamp", this.timestamp);
        ((ObjectNode) jsonNode).put("info", this.info);
        return jsonNode;
    }

    public static class Builder {

        private final BigDecimal reservedBaseBalance,
                initialBaseBalance,
                currentBaseBalance,
                initialAssetBalance,
                currentAssetBalance;
        private BigDecimal lastAskPrice,
                lastBidPrice;
        private final String exchangeId,
                symbol,
                modelName,
                timestamp;
        private String info;

        public Builder(
                BigDecimal reservedBaseBalance,
                BigDecimal initialBaseBalance,
                BigDecimal currentBaseBalance,
                BigDecimal initialAssetBalance,
                BigDecimal currentAssetBalance,
                String exchangeId,
                String symbol,
                String modelName,
                String timestamp
        ) {
            this.reservedBaseBalance = reservedBaseBalance;
            this.initialBaseBalance = initialBaseBalance;
            this.currentBaseBalance = currentBaseBalance;
            this.initialAssetBalance = initialAssetBalance;
            this.currentAssetBalance = currentAssetBalance;
            this.exchangeId = exchangeId;
            this.symbol = symbol;
            this.modelName = modelName;
            this.timestamp = timestamp;
        }

        public Builder lastAskPrice(BigDecimal lastAskPrice) {
            this.lastAskPrice = lastAskPrice;
            return this;
        }

        public Builder lastBidPrice(BigDecimal lastBidPrice) {
            this.lastBidPrice = lastBidPrice;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }

        public AccountBase build() {
            return new AccountBase(this);
        }

    }
    
    public static enum Retrieve {
        
        ALL, ONLY_FIRST, ONLY_LAST;
        
    }

}
