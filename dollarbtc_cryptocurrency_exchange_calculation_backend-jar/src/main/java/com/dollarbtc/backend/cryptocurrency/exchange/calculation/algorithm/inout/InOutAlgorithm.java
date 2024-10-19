package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.Algorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public abstract class InOutAlgorithm extends Algorithm {

    public InOutAlgorithm(
            ExchangeAccount exchangeAccount,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand
    ) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
    }
    
    public abstract boolean isEnoughPeriods();

    public abstract Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo);

    public abstract Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo);
    
}
