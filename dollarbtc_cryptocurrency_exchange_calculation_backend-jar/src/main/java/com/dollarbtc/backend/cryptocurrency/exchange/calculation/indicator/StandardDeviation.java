package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Standard deviation indicator.
 */
public class StandardDeviation implements Indicator<Double> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods) (e.g. 10)
     */
    public StandardDeviation(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
    }
    
    /**
     * @return the standard deviation (volatility)
     */
    @Override
    public Double execute() {
        // Getting the average close price
        int nbPeriods = periods.size();
        BigDecimal averageClosePrice = new SMA(periods, nbPeriods).execute();
        double sumOfSquaredDeviations = 0;
        int i = periods.size() - 1;
        while(i >= 0){
            BigDecimal closePrice = periods.get(i).getLast().getPrice();
            sumOfSquaredDeviations += closePrice.subtract(averageClosePrice).pow(2).doubleValue();
            i--;
        }
        return Math.sqrt(sumOfSquaredDeviations / nbPeriods);
    }
    
}