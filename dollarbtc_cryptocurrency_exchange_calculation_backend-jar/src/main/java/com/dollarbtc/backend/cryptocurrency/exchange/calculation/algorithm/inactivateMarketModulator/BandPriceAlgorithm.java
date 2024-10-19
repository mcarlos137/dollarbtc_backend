package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.PrimaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class BandPriceAlgorithm extends InactivateMarketModulatorAlgorithm {

    protected final String exchangeId;
    
    public BandPriceAlgorithm(
            String exchangeId,
            String symbol,
            double bandMinPrice, 
            double bandMaxPrice, 
            boolean inBand
    ) {
        super(symbol, null, bandMinPrice, bandMaxPrice, inBand);
        this.exchangeId = exchangeId;
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        BigDecimal[] ticker = PrimaryOperation.getTickerPrices(exchangeId, symbol);
        if (ticker == null) {
            return false;
        }
        return compare(ticker[0].doubleValue());
    }

}
