package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Simple moving average (SMA) indicator.
 */
public class SMA implements Indicator<BigDecimal> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods)
     */
    public SMA(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
    }
    
    /**
     * @return the simple moving average price of trades
     */
    @Override
    public BigDecimal execute() {
        BigDecimal average = BigDecimal.ZERO;
        int nbPeriods = periods.size();
        for (Period period : periods) {
            Trade periodLastTrade = period.getLast();
            if (periodLastTrade == null) {
                // No trade in the period
                nbPeriods--;
            } else {
                average = average.add(periodLastTrade.getPrice());
            }
        }
        return average.divide(new BigDecimal(nbPeriods), RoundingMode.HALF_UP);
    }
    
}