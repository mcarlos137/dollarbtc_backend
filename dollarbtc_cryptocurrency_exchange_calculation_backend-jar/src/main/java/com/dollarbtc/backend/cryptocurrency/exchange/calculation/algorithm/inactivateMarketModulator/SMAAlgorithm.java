package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.SMA;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class SMAAlgorithm extends InactivateMarketModulatorAlgorithm {

    private final int shortTermSMAPeriods;
    private final int longTermSMAPeriods;

    public SMAAlgorithm(
            String symbol,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermSMAPeriods,
            int longTermSMAPeriods
    ) {
        super(symbol, null, bandMinValue, bandMaxValue, inBand);
        this.shortTermSMAPeriods = shortTermSMAPeriods;
        this.longTermSMAPeriods = longTermSMAPeriods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(longTermSMAPeriods);
    }
    
    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        return compare(getTrendCoef(previousPeriods));
    }
    
    private double getTrendCoef(ArrayList<Period> previousPeriods) {
        double movingAvgLong = new SMA(previousPeriods, longTermSMAPeriods).execute().doubleValue();
        double movingAvgShort = new SMA(previousPeriods, shortTermSMAPeriods).execute().doubleValue();
        return movingAvgShort / movingAvgLong;
    }
    
}
