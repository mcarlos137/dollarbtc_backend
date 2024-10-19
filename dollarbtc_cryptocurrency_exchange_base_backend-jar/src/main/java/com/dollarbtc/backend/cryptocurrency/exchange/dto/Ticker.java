
package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class Ticker {
        
    private final String symbol; // required
    private final BigDecimal bidPrice, askPrice; // required

    private Ticker(Builder builder) {
        this.symbol = builder.symbol;
        this.bidPrice = builder.bidPrice;
        this.askPrice = builder.askPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public BigDecimal getAskPrice() {
        return askPrice;
    }
    
    @Override
    public String toString() {
        return "Ticker{" 
                + ", symbol=" + symbol 
                + ", bidPrice=" + bidPrice
                + ", askPrice=" + askPrice 
                + '}';
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("symbol", symbol);
        if(this.bidPrice != null){
            ((ObjectNode) jsonNode).put("bidPrice", bidPrice);
        }
        if(this.askPrice != null){
            ((ObjectNode) jsonNode).put("askPrice", askPrice);
        }
        return jsonNode;
    }

    public static class Builder {

        private final String symbol; // required
        private final BigDecimal bidPrice, askPrice; // required

        public Builder(String symbol, BigDecimal bidPrice, BigDecimal askPrice) {
            this.symbol = symbol;
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
        }

        public Ticker build() {
            return new Ticker(this);
        }

    }

}
