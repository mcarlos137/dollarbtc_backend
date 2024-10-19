package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.ADL;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ADLAlgorithm extends InactivateMarketModulatorAlgorithm {

    public ADLAlgorithm(
            String symbol,
            Integer periods
    ) {
        super(symbol, periods, 0.0, 0.0, false);
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        BigDecimal outAdl = new ADL(previousPeriods, periods).execute();
        return outAdl.compareTo(BigDecimal.ZERO) > 0;
    }

}
