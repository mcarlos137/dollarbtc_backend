package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.PrimaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SpreadAlgorithm extends InactivateMarketModulatorAlgorithm {

    protected final String exchangeId;
    
    public SpreadAlgorithm(
            String exchangeId,
            String symbol,
            double bandMinValue,
            double bandMaxValue, 
            boolean inBand) {
        super(symbol, null, bandMinValue, bandMaxValue, inBand);
        this.exchangeId = exchangeId;
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public boolean inactivate(ArrayList<Period> previousPeriods) {
        return compare(getSpread(exchangeId, symbol).doubleValue());
    }
    
    private BigDecimal getSpread(String exchangeId, String symbol){
        BigDecimal[] ticker = PrimaryOperation.getTickerPrices(exchangeId, symbol);
        if (ticker == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal spread = (ticker[1].subtract(ticker[0])).divide(ticker[1], 8, RoundingMode.UP).multiply(new BigDecimal(100));
        if(spread == null){
            return BigDecimal.ZERO;
        }
        return spread;
    }

}
