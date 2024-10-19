package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.SMA;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.StandardDeviation;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class BollingerBandsAlgorithm extends InactivateMarketModulatorAlgorithm {

    private final BollingerBandsAlgorithm.Type bollingerBandsAlgorithmType;

    public BollingerBandsAlgorithm(
            String symbol,
            BollingerBandsAlgorithm.Type bollingerBandsAlgorithmType,
            Integer periods
    ) {
        super(symbol, periods, 0.0, 0.0, false);
        this.bollingerBandsAlgorithmType = bollingerBandsAlgorithmType;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }
    
    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        double middleBand = new SMA(previousPeriods, periods).execute().doubleValue();
        double standardDeviation = new StandardDeviation(previousPeriods, periods).execute();
        double upperBand = middleBand + (standardDeviation * 2);
        double lowerBand = middleBand - (standardDeviation * 2);
        switch (bollingerBandsAlgorithmType) {
            case SIMPLE:
                if (previousPeriods.get(0).getLast().getPrice().doubleValue() < lowerBand) {
                    return true;
                }
                break;
        }
        return false;
    }

    public static enum Type {

        SIMPLE;

    }

}
