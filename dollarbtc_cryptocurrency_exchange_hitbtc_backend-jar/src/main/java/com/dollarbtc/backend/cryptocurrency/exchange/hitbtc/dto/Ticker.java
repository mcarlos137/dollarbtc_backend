package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto;

import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public class Ticker {

    private String exchageId, symbol, timestamp;
    private BigDecimal open, last, bid, ask, high, low, volume, volumeQuote;

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

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
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
