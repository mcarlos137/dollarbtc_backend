package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.PPO;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PPOAlgorithm extends InOutAlgorithm {

    private final int shortTermEMAPeriods;
    private final int longTermEMAPeriods;

    public PPOAlgorithm(
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
        double inPpo = getPPO(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice());
        if(addInfo){
            addInfo("inPpo", Double.toString(inPpo));
        }
        if(compare(inPpo)){
            return Order.AlgorithmType.PPO;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        double outPpo = getPPO(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice());
        if(addInfo){
            addInfo("outPpo", Double.toString(outPpo));
        }
        if(compare(outPpo)){
            return Order.AlgorithmType.PPO;
        }
        return null;
    }
    
    private double getPPO(ArrayList<Period> previousPeriods, BigDecimal lastPrice) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        return new PPO(previousPeriods, shortTermEMAPeriods, longTermEMAPeriods).execute().doubleValue();
    }

}
