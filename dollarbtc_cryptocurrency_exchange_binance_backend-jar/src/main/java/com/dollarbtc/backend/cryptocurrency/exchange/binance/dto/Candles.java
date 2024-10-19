package com.dollarbtc.backend.cryptocurrency.exchange.binance.dto;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author CarlosDaniel
 */
public class Candles {
    
    private final List<Candle> candles = new ArrayList<>();

    public Candles(JsonNode jsonNode) {
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        Iterator<JsonNode> arrayNodeIterator = arrayNode.elements();
        while (arrayNodeIterator.hasNext()) {
            ArrayNode arrayNodeIt = (ArrayNode) arrayNodeIterator.next();
            Candle candle = new Candle(DateUtil.getDate(arrayNodeIt.get(6).longValue()), 
                    new BigDecimal(arrayNodeIt.get(1).textValue()), 
                    new BigDecimal(arrayNodeIt.get(4).textValue()), 
                    new BigDecimal(arrayNodeIt.get(3).textValue()), 
                    new BigDecimal(arrayNodeIt.get(2).textValue()), 
                    new BigDecimal(arrayNodeIt.get(5).textValue()), 
                    new BigDecimal(arrayNodeIt.get(7).textValue())
            );
            candles.add(candle);
        }
        
    }

    public List<Candle> getCandles() {
        return candles;
    }
        
    public static class Candle {

        private final String endTimestamp;
        private final BigDecimal open, close, low, high, volume, volumeQuote;

        public Candle(String endTimestamp, BigDecimal open, BigDecimal close, BigDecimal low, BigDecimal high, BigDecimal volume, BigDecimal volumeQuote) {
            this.endTimestamp = endTimestamp;
            this.open = open;
            this.close = close;
            this.low = low;
            this.high = high;
            this.volume = volume;
            this.volumeQuote = volumeQuote;
        }
        
        public String getEndTimestamp() {
            return endTimestamp;
        }

        public BigDecimal getOpen() {
            return open;
        }

        public BigDecimal getClose() {
            return close;
        }

        public BigDecimal getLow() {
            return low;
        }

        public BigDecimal getHigh() {
            return high;
        }

        public BigDecimal getVolume() {
            return volume;
        }

        public BigDecimal getVolumeQuote() {
            return volumeQuote;
        }

        @Override
        public String toString() {
            return "Candle{" + "endTimestamp=" + endTimestamp + ", open=" + open + ", close=" + close + ", low=" + low + ", high=" + high + ", volume=" + volume + ", volumeQuote=" + volumeQuote + '}';
        }
                
    }

}
