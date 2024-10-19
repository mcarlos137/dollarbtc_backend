
package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class Order {

    private final String exchangeId, symbol, timestamp, id; // required
    private final Type type; // required
    private BigDecimal tradableAmount, price; // optional
    private final PriceType priceType; // optional
    private AlgorithmType algorithmType; // optional
    private TradingType tradingType; // optional
    private List<MarketTrade> marketTrades; // optional

    private Order(Builder builder) {
        this.exchangeId = builder.exchangeId;
        this.symbol = builder.symbol;
        this.type = builder.type;
        this.timestamp = builder.timestamp;
        this.tradableAmount = builder.tradableAmount;
        this.price = builder.price;
        this.priceType = builder.priceType;
        this.algorithmType = builder.algorithmType;
        this.tradingType = builder.tradingType;
        this.id = builder.id;
        this.marketTrades = builder.marketTrades;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Type getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public BigDecimal getTradableAmount() {
        return tradableAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public TradingType getTradingType() {
        return tradingType;
    }

    public String getId() {
        return id;
    }

    public List<MarketTrade> getMarketTrades() {
        if(this.marketTrades == null){
            this.marketTrades = new ArrayList<>();
        }
        return marketTrades;
    }
    
    public void setTradableAmount(BigDecimal tradableAmount) {
        this.tradableAmount = tradableAmount;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setIndicatorType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public void setTradingType(TradingType tradingType) {
        this.tradingType = tradingType;
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("exchangeId", this.exchangeId);
        ((ObjectNode) jsonNode).put("symbol", this.symbol);
        if (this.type != null) {
            ((ObjectNode) jsonNode).put("type", this.type.toString());
        }
        ((ObjectNode) jsonNode).put("timestamp", this.timestamp);
        ((ObjectNode) jsonNode).put("tradableAmount", this.tradableAmount);
        ((ObjectNode) jsonNode).put("price", this.price);
        if (this.priceType != null) {
            ((ObjectNode) jsonNode).put("priceType", this.priceType.toString());
        }
        if (this.algorithmType != null) {
            ((ObjectNode) jsonNode).put("algorithmType", this.algorithmType.toString());
        }
        if (this.tradingType != null) {
            ((ObjectNode) jsonNode).put("tradingType", this.tradingType.toString());
        }
        ((ObjectNode) jsonNode).put("id", this.id);
        if(this.marketTrades != null){
            ((ObjectNode) jsonNode).put("marketTrades", this.marketTrades.toString());
        }
        return jsonNode;
    }
    
    @Override
    public String toString() {
        return "Order{"
                + "exchangeId=" + this.exchangeId
                + ", symbol=" + this.symbol
                + ", type=" + this.type
                + ", timestamp=" + this.timestamp
                + ", tradableAmount=" + this.tradableAmount
                + ", price=" + this.price
                + ", priceType=" + this.priceType
                + ", algorithmType=" + this.algorithmType
                + ", tradingType=" + this.tradingType
                + ", id=" + this.id
                + ", marketTrades=" + this.marketTrades
                + '}';
    }

    public static class Builder {

        private final String exchangeId, symbol, timestamp; // required
        private final Type type; // required
        private BigDecimal tradableAmount, price; // optional
        private PriceType priceType; // optional
        private AlgorithmType algorithmType; // optional
        private TradingType tradingType; // optional
        private String id; // optional
        private List<MarketTrade> marketTrades; // optional

        public Builder(String exchangeId, String symbol, Type type, String timestamp) {
            this.exchangeId = exchangeId;
            this.symbol = symbol;
            this.type = type;
            this.timestamp = timestamp;
        }
        
        public Builder tradableAmount(BigDecimal tradableAmount) {
            this.tradableAmount = tradableAmount;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder priceType(PriceType priceType) {
            this.priceType = priceType;
            return this;
        }

        public Builder algorithmType(AlgorithmType algorithmType) {
            this.algorithmType = algorithmType;
            return this;
        }

        public Builder tradingType(TradingType tradingType) {
            this.tradingType = tradingType;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder marketExecutions(List<MarketTrade> marketTrades) {
            this.marketTrades = marketTrades;
            return this;
        }
        
        public Order build() {
            return new Order(this);
        }

    }
    
    public static class MarketTrade {

        private BigDecimal tradableAmount, price, feeAmount;

        public MarketTrade(BigDecimal tradableAmount, BigDecimal price, BigDecimal feeAmount) {
            this.tradableAmount = tradableAmount;
            this.price = price;
            this.feeAmount = feeAmount;
        }
        
        public BigDecimal getTradableAmount() {
            return tradableAmount;
        }

        public void setTradableAmount(BigDecimal tradableAmount) {
            this.tradableAmount = tradableAmount;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getFeeAmount() {
            return feeAmount;
        }

        public void setFeeAmount(BigDecimal feeAmount) {
            this.feeAmount = feeAmount;
        }

        @Override
        public String toString() {
            return "MarketExecution{" 
                    + "tradableAmount=" + tradableAmount 
                    + ", price=" + price 
                    + ", feeAmount=" + feeAmount 
                    + '}';
        }
              
    }

    public enum Type {

        BUY, SELL;

    }
    
    public enum PriceType {

        MARKET, LIMIT;

    }

    public enum AlgorithmType {

        NONE("NO RESTRICTION TO BUY OR SELL"),
        BOLLINGER_BANDS("BUY AT BOTTON BAND, SELL AT TOP BAND"),
        ADL("ADL ALGORITHM"),
        BINARY("BINARY ALGORITHM"),
        EMA("EMA ALGORITHM"),
        SMA("SMA ALGORITHM"),
        OPPOSITE_ORDER("IF LAST OPERATION BUY, IT SELLS OR VICE VERSA"),
        PPO("PPO ALGORITHM"),
        AROON("AROON ALGORITHM"),
        ORDER_QUANTITY("BUY OR SELL IF A SPECIFIC NUMBER OF ORDERS IS REACHED"),
        STOP_LOSS("DOWN PERCENT FROM BUY PRICE TO SELL"),
        MAX_LOSS_EARNING("IF BALANCE IS BELOW THAN % CONFIGURED, IT SELLS AND GOES OUT. BEFORE THAT, IT HAS A RESERVE BALANCE GREATER THAN LAST LOSS BALANCE"),
        MAX_LOSS_RESERVING("IF BALANCE IS BELOW THAN % CONFIGURED, IT SELLS AND GOES OUT. BEFORE THAT, IT HAS A RESERVE BALANCE LOWER OR EQUAL TO LAST LOSS BALANCE"),
        MAX_LOSS_LOSSING("IF BALANCE IS BELOW THAN % CONFIGURED, IT SELLS AND GOES OUT"),
        RESERVE("IF BALANCE IS UPPER THAN % CONFIGURED, IT SELLS AND GOES OUT. BALANCE GOES TO RESERVE BALANCE"),
        SPREAD("SPREAD"),
        TIMEOUT("TIME STARTED WHEN ALGORITHMS MARK TO IN"),
        NO_BUY_TIMEOUT("TIME WITH NO BUY"),
        ROC("ROC ALGORITHM"),
        RSI("RSI ALGORITHM"), 
        BAND_PRICE("BAND PRICE"),
        LAST_SELL_PRICE_VARIATION("VARIATION OF LAST SELL PRICE WITH A MAX TIME"),
        MAX_BUY_QUANTITY("IT CAN BE MORE BUYS THAN CONFIGURED IN A PERIOD"),
        MARKET_INACTIVATION("MARKET INACTIVATION"),
        END_HOLDING_PERIOD_INACTIVATION("END HOLDING PERIOD INACTIVATION"),
        CLIENT_INACTIVATION("CLIENT INACTIVATION");
        
        private AlgorithmType(String description){
            this.description = description;
        }
        
        private final String description;

        public String getDescription() {
            return description;
        }
        
    }

    public enum TradingType {

        NORMAL,
        BUY_TO_SELL,
        SELL_TO_BUY,
        SELL_BUY_TO_SELL,
        BUY_SELL_TO_BUY;

    }

}
