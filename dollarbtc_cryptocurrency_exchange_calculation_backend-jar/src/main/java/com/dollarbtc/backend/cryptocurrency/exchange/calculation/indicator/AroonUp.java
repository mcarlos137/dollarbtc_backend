package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;

/**
 * Aroon up indicator.
 */
public class AroonUp implements Indicator<Double> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods)
     */
    public AroonUp(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
    }
    
    /**
     * @return the Aroon Up indicator (in percentage terms, i.e. between 0 and 100)
     */
    @Override
    public Double execute() {
        // Getting the number of periods since the high price
        Period highPeriod = null;
        int nbPeriodsSinceHigh = 0;
        int i = periods.size() - 1;
        while(i >= 0){
            Trade currentHighTrade = periods.get(i).getHigh();
            if (currentHighTrade != null) {
                if (highPeriod == null) {
                    highPeriod = periods.get(i);
                } else {
                    BigDecimal highPrice = highPeriod.getHigh().getPrice();
                    if (currentHighTrade.getPrice().compareTo(highPrice) >= 0) {
                        // New high price
                        highPeriod = periods.get(i);
                        nbPeriodsSinceHigh = 0;
                    }
                }
            }
            nbPeriodsSinceHigh++;
            i--;
        }
        return (double) (periods.size() - nbPeriodsSinceHigh) / (double) periods.size() * 100.0;
    }
    
}