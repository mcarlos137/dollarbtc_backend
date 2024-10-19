package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.ROC;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class ROCAlgorithm extends InactivateMarketModulatorAlgorithm {

    public ROCAlgorithm(
            String symbol,
            double bandMinValue, 
            double bandMaxValue, 
            boolean inBand,
            Integer periods
    ) {
        super(symbol, periods, bandMinValue, bandMaxValue, inBand);
    }
    
    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        return compare(new ROC(previousPeriods, periods).execute());
    }

}
