package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Rate of change (ROC) indicator.
 * Aka. Momentum
 * The ROC calculation compares the current price with the price "n" periods ago.
 */
public class ROC implements Indicator<Double> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param n the current price will be compared with the price "n" (e.g. 12) periods ago
     */
    public ROC(final List<Period> periods, int n) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (n > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, n));
    }
    
    /**
     * @return the rate of change
     */
    @Override
    public Double execute() {
        try {
            BigDecimal nPeriodsAgoClosePrice = periods.get(periods.size() - 1).getLast().getPrice();
            BigDecimal currentClosePrice = periods.get(0).getLast().getPrice();
            return currentClosePrice.subtract(nPeriodsAgoClosePrice).divide(nPeriodsAgoClosePrice, 2, RoundingMode.HALF_UP).multiply(IndicatorUtils.HUNDRED).doubleValue();
        } catch (RuntimeException re) {
            return Double.NaN;
        }
    }
    
}