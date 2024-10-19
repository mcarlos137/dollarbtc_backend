package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import org.apache.commons.lang3.Validate;

/**
 * True range indicator.
 */
public class TrueRange implements Indicator<Double> {

    private Period previousPeriod;
    private Period currentPeriod;

    /**
     * @param period the period for which we want the true range
     */
    public TrueRange(Period period) {
        this(null, period);
    }

    /**
     * @param previousPeriod the previous period
     * @param currentPeriod the current period for which we want the true range
     */
    public TrueRange(Period previousPeriod, Period currentPeriod) {
        Validate.notNull(currentPeriod, "Current period can't be null");
        this.previousPeriod = previousPeriod;
        this.currentPeriod = currentPeriod;
    }
    
    /**
     * @return the true range for the current period
     */
    @Override
    public Double execute() {
        //  Current extrema prices
        BigDecimal currentHighPrice = currentPeriod.getHigh().getPrice();
        BigDecimal currentLowPrice = currentPeriod.getLow().getPrice();
        double trueRange;
        if (previousPeriod == null) {
            // No previous period
            trueRange = currentHighPrice.subtract(currentLowPrice).doubleValue();
        } else {
            // Using the previous close price
            BigDecimal previousClosePrice = previousPeriod.getLast().getPrice();
            BigDecimal trueRangeMethod1 = currentHighPrice.subtract(currentLowPrice);
            BigDecimal trueRangeMethod2 = currentHighPrice.subtract(previousClosePrice).abs();
            BigDecimal trueRangeMethod3 = currentLowPrice.subtract(previousClosePrice).abs();
            trueRange = trueRangeMethod1.max(trueRangeMethod2).max(trueRangeMethod3).doubleValue();
        }
        return trueRange;
    }
    
}