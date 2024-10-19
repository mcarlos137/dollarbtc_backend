package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.Algorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public abstract class InactivateMarketModulatorAlgorithm extends Algorithm {
        
    protected final String symbol;
    protected final Integer periods;
    
    public InactivateMarketModulatorAlgorithm(
            String symbol,
            Integer periods,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand) 
    {
        super(null, bandMinValue, bandMaxValue, inBand);
        this.symbol = symbol;
        this.periods = periods;
    }
        
    protected abstract boolean isEnoughPeriods();
    
    public abstract boolean inactivate(ArrayList<Period> previousPeriods);
    
}
