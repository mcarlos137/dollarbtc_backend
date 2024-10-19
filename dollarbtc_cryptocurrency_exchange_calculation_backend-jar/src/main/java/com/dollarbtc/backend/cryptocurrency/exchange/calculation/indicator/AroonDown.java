package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Aroon down indicator.
 */
public class AroonDown implements Indicator<Double> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods)
     */
    public AroonDown(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
    }
    
    /**
     * @return the Aroon Down indicator (in percentage terms, i.e. between 0 and 100)
     */
    @Override
    public Double execute() {
        // Getting the number of periods since the low price
        Period lowPeriod = null;
        int nbPeriodsSinceLow = 0;
        int i = periods.size() - 1;
        while(i >= 0){
            Trade currentLowTrade = periods.get(i).getLow();
            if (currentLowTrade != null) {
                if (lowPeriod == null) {
                    lowPeriod = periods.get(i);
                } else {
                    BigDecimal lowPrice = lowPeriod.getLow().getPrice();
                    if (currentLowTrade.getPrice().compareTo(lowPrice) <= 0) {
                        // New low price
                        lowPeriod = periods.get(i);
                        nbPeriodsSinceLow = 0;
                    }
                }
            }
            nbPeriodsSinceLow++;
            i--;
        }
        return (double) (periods.size() - nbPeriodsSinceLow) / (double) periods.size() * 100.0;
    }
    
}