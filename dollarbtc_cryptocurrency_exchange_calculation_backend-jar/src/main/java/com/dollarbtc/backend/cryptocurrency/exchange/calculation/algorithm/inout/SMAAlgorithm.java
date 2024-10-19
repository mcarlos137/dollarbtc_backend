package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.SMA;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class SMAAlgorithm extends InOutAlgorithm {

    private final int shortTermSMAPeriods;
    private final int longTermSMAPeriods;

    public SMAAlgorithm(
            ExchangeAccount exchangeAccount, 
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermSMAPeriods,
            int longTermSMAPeriods
    ) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
        this.shortTermSMAPeriods = shortTermSMAPeriods;
        this.longTermSMAPeriods = longTermSMAPeriods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(longTermSMAPeriods);
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        double inSMA = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice());
        if(addInfo){
            addInfo("inSMA", Double.toString(inSMA));
        }
        if(compare(inSMA)){
            return Order.AlgorithmType.SMA;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        double outSMA = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice());
        if(addInfo){
            addInfo("outSMA", Double.toString(outSMA));
        }
        if(compare(outSMA)){
            return Order.AlgorithmType.SMA;
        }
        return null;
    }
    
    private double getTrendCoef(ArrayList<Period> previousPeriods, BigDecimal lastPrice) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        double movingAvgLong = new SMA(previousPeriods, longTermSMAPeriods).execute().doubleValue();
        double movingAvgShort = new SMA(previousPeriods, shortTermSMAPeriods).execute().doubleValue();
        return movingAvgShort / movingAvgLong;
    }
    
}
