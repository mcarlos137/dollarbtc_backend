package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.EMA;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class EMAAlgorithm extends InactivateMarketModulatorAlgorithm {

    private final int shortTermEMAPeriods;
    private final int longTermEMAPeriods;

    public EMAAlgorithm(
            String symbol,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermEMAPeriods,
            int longTermEMAPeriods
    ) {
        super(symbol, null, bandMinValue, bandMaxValue, inBand);
        this.shortTermEMAPeriods = shortTermEMAPeriods;
        this.longTermEMAPeriods = longTermEMAPeriods;
    }
    
    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(longTermEMAPeriods);
    }

    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        double outEMA = getTrendCoef(previousPeriods);
        return compare(outEMA);
    }
    
    private double getTrendCoef(ArrayList<Period> previousPeriods) {
        double movingAvgLong = new EMA(previousPeriods, longTermEMAPeriods).execute().doubleValue();
        double movingAvgShort = new EMA(previousPeriods, shortTermEMAPeriods).execute().doubleValue();
        return movingAvgShort / movingAvgLong;
    }
    
}
