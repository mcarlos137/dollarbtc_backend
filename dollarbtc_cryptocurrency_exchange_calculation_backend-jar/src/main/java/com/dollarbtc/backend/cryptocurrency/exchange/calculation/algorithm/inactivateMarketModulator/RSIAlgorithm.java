package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.RSI;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RSIAlgorithm extends InactivateMarketModulatorAlgorithm {

    public RSIAlgorithm(
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
        BigDecimal rsi = new RSI(previousPeriods, periods).execute();
        if(rsi == null){
            return false;
        }
        return compare(rsi.doubleValue());
    }

}
