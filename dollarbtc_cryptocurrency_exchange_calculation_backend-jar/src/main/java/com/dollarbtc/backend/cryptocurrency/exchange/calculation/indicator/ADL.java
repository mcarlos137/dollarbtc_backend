package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Accumulation distribution line (ADL) indicator.
 */
public class ADL implements Indicator<BigDecimal> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods)
     */
    public ADL(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
    }
    
    /**
     * @return the ADL indicator
     */
    @Override
    public BigDecimal execute() {
        BigDecimal adl = BigDecimal.ZERO;
        int i = periods.size() - 1;
        while(i >= 0){
            // Getting high, low and close prices
            BigDecimal highPrice = periods.get(i).getHigh().getPrice();
            BigDecimal lowPrice = periods.get(i).getLow().getPrice();
            BigDecimal closePrice = periods.get(i).getLast().getPrice();
            // Calculating the money flow multiplier
            BigDecimal moneyFlowMultiplier;
            if(highPrice.equals(lowPrice)){
                moneyFlowMultiplier = BigDecimal.ZERO; 
            } else {
                moneyFlowMultiplier = closePrice.subtract(lowPrice).subtract(highPrice.subtract(closePrice)).divide(highPrice.subtract(lowPrice), RoundingMode.HALF_UP);
            }
            // Calculating the money flow volume
            BigDecimal moneyFlowVolume = moneyFlowMultiplier.multiply(new Volume(periods.get(i)).execute());
            // Calculating the ADL
            adl = adl.add(moneyFlowVolume);
            i--;
        }
        return adl;
    }
    
}