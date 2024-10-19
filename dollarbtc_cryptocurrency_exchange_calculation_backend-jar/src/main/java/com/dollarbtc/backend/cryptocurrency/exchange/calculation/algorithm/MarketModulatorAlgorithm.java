/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm;

import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.PrimaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.InactivateMarketModulatorAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

/**
 *
 * @author CarlosDaniel
 */
public class MarketModulatorAlgorithm {
    
    public final String name;
    public final InactivateMarketModulatorAlgorithm[] inactivates;
    public final ArrayList<Period> periods;

    public MarketModulatorAlgorithm(
            String name, 
            InactivateMarketModulatorAlgorithm[] inactivates,
            String exchangeId, 
            String symbol,
            String period
    )
    {
        this.name = name;
        this.inactivates = inactivates;
        this.periods = PrimaryOperation.getMarketPeriods(exchangeId, symbol, period);
    }
            
}
