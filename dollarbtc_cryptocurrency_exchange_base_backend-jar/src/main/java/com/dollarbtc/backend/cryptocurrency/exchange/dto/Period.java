package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * A period of time.
 */
public class Period {

    private String startTimestamp, endTimestamp;
    private ArrayList<Trade> trades = new ArrayList<>();

    public Period(Trade trade, int durationInSeconds) {
        this(trade.getTimestamp(), durationInSeconds);
        trades.add(trade);
    }

    public Period(String endTimestamp, int durationInSeconds) {
        this(DateUtil.getDate(DateUtil.parseDate(endTimestamp).getTime() - durationInSeconds * 1000), endTimestamp);
    }

    public Period(String startTimestamp, String endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public boolean inPeriod(String timestamp) {
        if (timestamp == null || timestamp.equals("")) {
            return false;
        }
        return DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endTimestamp)) <= 0
                && DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(startTimestamp)) >= 0;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public ArrayList<Trade> getTrades() {
        return trades;
    }

    public Trade getHigh() {
        Trade highTrade = null;
        if (trades != null) {
            for (Trade trade : trades) {
                if (highTrade == null || highTrade.getPrice().compareTo(trade.getPrice()) < 0) {
                    highTrade = trade;
                }
            }
        }
        return highTrade;
    }

    public Trade getLow() {
        Trade lowTrade = null;
        if (trades != null) {
            for (Trade trade : trades) {
                if (lowTrade == null || lowTrade.getPrice().compareTo(trade.getPrice()) > 0) {
                    lowTrade = trade;
                }
            }
        }
        return lowTrade;
    }

    public Trade getLast() {
        return ((trades == null) || trades.isEmpty()) ? null : trades.get(0);
    }

    public BigDecimal getTypicalPrice() {
        BigDecimal high = getHigh().getPrice();
        BigDecimal low = getLow().getPrice();
        BigDecimal close = getLast().getPrice();
        return high.multiply(low).multiply(close).divide(new BigDecimal(3), RoundingMode.UP);
    }

    public void changeLastTradePrice(BigDecimal newPrice) {
        if (!trades.isEmpty()) {
            trades.get(0).setPrice(newPrice);
        }
    }

    @Override
    public String toString() {
        return "Period [" + "startTimestamp=" + startTimestamp + ", endTimestamp=" + endTimestamp + ", trades=" + trades.size() + ']';
    }
}
