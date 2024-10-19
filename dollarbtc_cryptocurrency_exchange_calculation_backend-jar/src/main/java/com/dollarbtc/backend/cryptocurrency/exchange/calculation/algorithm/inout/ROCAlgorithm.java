package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.ROC;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class ROCAlgorithm extends InOutAlgorithm {

    private final int periods;

    public ROCAlgorithm(
            ExchangeAccount exchangeAccount,
            double bandMinValue, 
            double bandMaxValue, 
            boolean inBand,
            int periods
    ) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
        this.periods = periods;
    }
    
    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastAskPrice());
        double inRoc = new ROC(previousPeriods, periods).execute();
        if(addInfo){
            addInfo("inRoc", Double.toString(inRoc));
        }
        if(compare(inRoc)){
            return Order.AlgorithmType.ROC;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastBidPrice());
        double outRoc = new ROC(previousPeriods, periods).execute();
        if(addInfo){
            addInfo("outRoc", Double.toString(outRoc));
        }
        if(compare(outRoc)){
            return Order.AlgorithmType.ROC;
        }
        return null;
    }

}
