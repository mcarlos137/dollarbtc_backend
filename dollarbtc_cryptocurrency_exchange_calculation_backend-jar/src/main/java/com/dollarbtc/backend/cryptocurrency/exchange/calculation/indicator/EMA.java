package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Exponential moving average (EMA) indicator.
 */
public class EMA implements Indicator<BigDecimal> {

    private ArrayList<Period> periods;

    private BigDecimal multiplier;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods)
     */
    public EMA(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
        // Weighting multiplier
        multiplier = new BigDecimal((double) (2 / ((double) this.periods.size() + 1)));
    }
    
    /**
     * @return the exponential moving average price of trades
     */
    @Override
    public BigDecimal execute() {
        // Computing EMAs
        int nbPeriods = periods.size();
        // An exponential moving average (EMA) has to start somewhere
        // so a simple moving average is used as the previous period's EMA in the first calculation.
        BigDecimal ema = new SMA(periods, nbPeriods).execute();
        int i = nbPeriods - 1;
        while(i >= 0){
            Trade periodLastTrade = periods.get(i).getLast();
            if (periodLastTrade != null) {
                BigDecimal closePrice = periodLastTrade.getPrice();
                ema = closePrice.subtract(ema).multiply(multiplier).add(ema);
            }
            i--;
        }
        return ema;
    }
    
}