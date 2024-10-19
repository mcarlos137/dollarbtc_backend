package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.PPO;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class PPOAlgorithm extends InactivateMarketModulatorAlgorithm {

    private final int shortTermEMAPeriods;
    private final int longTermEMAPeriods;

    public PPOAlgorithm(
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
        return compare(new PPO(previousPeriods, shortTermEMAPeriods, longTermEMAPeriods).execute().doubleValue());
    }

}
