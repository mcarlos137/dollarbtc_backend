package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.Validate;

/**
 * Relative strength index (RSI) indicator.
 */
public class RSI implements Indicator<BigDecimal> {

    private ArrayList<Period> periods;

    /**
     * @param periods the list of periods
     * @param lastPeriods the number of periods to use (i.e. the n last periods) (e.g. 14)
     */
    public RSI(final List<Period> periods, int lastPeriods) {
        Validate.noNullElements(periods, "List of periods is null or contains null periods");
        final int nbPeriods = periods.size();
        if (lastPeriods > nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }
        this.periods = new ArrayList<>(periods.subList(0, lastPeriods));
    }
    
    /**
     * @return the relative strength index
     */
    @Override
    public BigDecimal execute() {
        int nbPeriods = periods.size();
        // Computing gains and losses
        ArrayList<BigDecimal> gains = new ArrayList<>();
        gains.add(BigDecimal.ZERO);
        ArrayList<BigDecimal> losses = new ArrayList<>();
        losses.add(BigDecimal.ZERO);
        for (int i = 1; i < nbPeriods; i++) {
            Period previousPeriod  = periods.get(nbPeriods - i);
            Period currentPeriod  = periods.get(nbPeriods - i - 1);
            BigDecimal previousClosePrice = previousPeriod.getLast().getPrice();
            BigDecimal currentClosePrice = currentPeriod.getLast().getPrice();
            if (previousClosePrice.compareTo(currentClosePrice) < 0) {
                // Gain
                gains.add(currentClosePrice.subtract(previousClosePrice));
                losses.add(BigDecimal.ZERO);
            } else if (previousClosePrice.compareTo(currentClosePrice) > 0) {
                // Loss
                gains.add(BigDecimal.ZERO);
                losses.add(previousClosePrice.subtract(currentClosePrice));
            } else {
                // Neither gain nor loss
                gains.add(BigDecimal.ZERO);
                losses.add(BigDecimal.ZERO);
            }
        }
        // Sums of gains and losses
        BigDecimal sumOfGains = BigDecimal.ZERO;
        BigDecimal sumOfLosses = BigDecimal.ZERO;
        for (int i = 0; i < gains.size(); i++) {
            sumOfGains = sumOfGains.add(gains.get(i));
            sumOfLosses = sumOfLosses.add(losses.get(i));
        }
        // Computing average gains and average losses
        ArrayList<BigDecimal> averageGains = new ArrayList<>();
        ArrayList<BigDecimal> averageLosses = new ArrayList<>();
        BigDecimal nbPeriodsDivider = new BigDecimal(nbPeriods);
        BigDecimal nbPeriodsMinusOne = nbPeriodsDivider.subtract(BigDecimal.ONE);
        // First average gain and first average loss
        averageGains.add(sumOfGains.divide(nbPeriodsDivider, 10, RoundingMode.HALF_UP));
        averageLosses.add(sumOfLosses.divide(nbPeriodsDivider, 10, RoundingMode.HALF_UP));
        // Subsequent "average gain" and "average loss" values
        for (int i = 1; i < nbPeriods; i++) {
            BigDecimal previousAverageGain = averageGains.get(i - 1);
            BigDecimal previousAverageLoss = averageLosses.get(i - 1);
            averageGains.add(previousAverageGain.multiply(nbPeriodsMinusOne).add(gains.get(i)).divide(nbPeriodsDivider, 10, RoundingMode.HALF_UP));
            averageLosses.add(previousAverageLoss.multiply(nbPeriodsMinusOne).add(losses.get(i)).divide(nbPeriodsDivider, 10, RoundingMode.HALF_UP));
        }
        // Relative strength
        BigDecimal relativeStrength = null;
        try {
            relativeStrength = averageGains.get(nbPeriods - 1).divide(averageLosses.get(nbPeriods - 1), 10, RoundingMode.HALF_UP);
        } catch(java.lang.ArithmeticException ex ){
            Logger.getLogger(RSI.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(relativeStrength == null){
            return BigDecimal.valueOf(50);
        }
        // Relative strength index
        BigDecimal rsi = IndicatorUtils.HUNDRED.subtract(IndicatorUtils.HUNDRED.divide(relativeStrength.add(BigDecimal.ONE), 10, RoundingMode.HALF_UP));
        return rsi;
    }
    
}