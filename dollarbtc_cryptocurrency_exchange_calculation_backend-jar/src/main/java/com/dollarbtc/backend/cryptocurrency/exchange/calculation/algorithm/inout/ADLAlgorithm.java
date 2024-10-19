package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.ADL;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ADLAlgorithm extends InOutAlgorithm {

    private final int periods;

    public ADLAlgorithm(
            ExchangeAccount exchangeAccount,
            int periods
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.periods = periods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastAskPrice());
        BigDecimal inAdl = new ADL(previousPeriods, periods).execute();
        if(addInfo){
            addInfo("inAdl", inAdl.toString());
        }
        if(inAdl.compareTo(BigDecimal.ZERO) < 0){
            return Order.AlgorithmType.ADL;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastBidPrice());
        BigDecimal outAdl = new ADL(previousPeriods, periods).execute();
        if(addInfo){
            addInfo("outAdl", outAdl.toString());
        }        
        if (outAdl.compareTo(BigDecimal.ZERO) > 0) {
            return Order.AlgorithmType.ADL;
        }
        return null;
    }

}
