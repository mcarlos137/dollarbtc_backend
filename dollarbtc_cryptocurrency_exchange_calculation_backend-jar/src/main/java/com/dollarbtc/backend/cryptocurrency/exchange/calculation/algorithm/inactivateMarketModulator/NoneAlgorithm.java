package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class NoneAlgorithm extends InactivateMarketModulatorAlgorithm {

    public NoneAlgorithm() {
        super(null, null, 0.0, 0.0, false);
    }
    
    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public boolean inactivate(ArrayList<Period> periods) {
        return false;
    }

}
