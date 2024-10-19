package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.EMA;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class EMAAlgorithm extends InOutAlgorithm {

    private final int shortTermEMAPeriods;
    private final int longTermEMAPeriods;

    public EMAAlgorithm(
            ExchangeAccount exchangeAccount, 
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermEMAPeriods,
            int longTermEMAPeriods
    ) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
        this.shortTermEMAPeriods = shortTermEMAPeriods;
        this.longTermEMAPeriods = longTermEMAPeriods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(longTermEMAPeriods);
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        double inEMA = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice());
        if(addInfo){
            addInfo("inEMA", Double.toString(inEMA));
        }
        if(compare(inEMA)){
            return Order.AlgorithmType.EMA;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        double outEMA = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice());
        if(addInfo){
            addInfo("outEMA", Double.toString(outEMA));
        }
        if(compare(outEMA)){
            return Order.AlgorithmType.EMA;
        }
        return null;
    }
    
    private double getTrendCoef(ArrayList<Period> previousPeriods, BigDecimal lastPrice) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        double movingAvgLong = new EMA(previousPeriods, longTermEMAPeriods).execute().doubleValue();
        double movingAvgShort = new EMA(previousPeriods, shortTermEMAPeriods).execute().doubleValue();
        return movingAvgShort / movingAvgLong;
    }
    
}
