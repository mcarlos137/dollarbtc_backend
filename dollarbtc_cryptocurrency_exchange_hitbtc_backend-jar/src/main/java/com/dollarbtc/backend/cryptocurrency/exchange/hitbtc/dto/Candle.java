package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto;

import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public class Candle {
    
    private String exchageId, symbol, timestamp;
    private BigDecimal open, close, min, max, volume, volumeQuote;

    public String getExchageId() {
        return exchageId;
    }

    public void setExchageId(String exchageId) {
        this.exchageId = exchageId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getVolumeQuote() {
        return volumeQuote;
    }

    public void setVolumeQuote(BigDecimal volumeQuote) {
        this.volumeQuote = volumeQuote;
    }    

}
