
package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class Trade {

    private final String exchageId, symbol, timestamp, id; // required
    private final Side side; // required
    private final BigDecimal tradableAmount; // optional
    private BigDecimal price; // optional

    private Trade(Builder builder) {
        this.exchageId = builder.exchageId;
        this.symbol = builder.symbol;
        this.side = builder.side;
        this.timestamp = builder.timestamp;
        this.tradableAmount = builder.tradableAmount;
        this.price = builder.price;
        this.id = builder.id;
    }

    public String getExchageId() {
        return exchageId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
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

    public String getId() {
        return id;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("exchangeId", this.exchageId);
        ((ObjectNode) jsonNode).put("symbol", this.symbol);
        if (this.side != null) {
            ((ObjectNode) jsonNode).put("side", this.side.toString());
        }
        ((ObjectNode) jsonNode).put("timestamp", this.timestamp);
        if (this.tradableAmount != null) {
            ((ObjectNode) jsonNode).put("tradableAmount", this.tradableAmount.toPlainString());
        }
        if (this.price != null) {
            ((ObjectNode) jsonNode).put("price", this.price.toPlainString());
        }
        ((ObjectNode) jsonNode).put("id", this.id);
        return jsonNode;
    }

    public static class Builder {

        private final String exchageId, symbol, timestamp; // required
        private final Side side; // required
        private BigDecimal tradableAmount, price; // optional
        private String id; // optional

        public Builder(String exchageId, String symbol, Side side, BigDecimal tradableAmount, BigDecimal price, String timestamp) {
            this.exchageId = exchageId;
            this.symbol = symbol;
            this.side = side;
            this.tradableAmount = tradableAmount;
            this.price = price;
            this.timestamp = timestamp;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Trade build() {
            return new Trade(this);
        }

    }

    public enum Side {

        BUY, SELL;

    }

}
