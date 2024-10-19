package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.PPO;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PPOAlgorithm extends TradingAlgorithm {

    private final int shortTermEMAPeriods;
    private final int longTermEMAPeriods;

    public PPOAlgorithm(
            ExchangeAccount exchangeAccount, 
            Order.Type orderType, 
            double baseOrderAmount, 
            double orderAmount, 
            boolean orderAll,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermEMAPeriods,
            int longTermEMAPeriods
    ) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, bandMinValue, bandMaxValue, inBand);
        this.shortTermEMAPeriods = shortTermEMAPeriods;
        this.longTermEMAPeriods = longTermEMAPeriods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(longTermEMAPeriods);
    }

    @Override
    public Order placeOrder(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order order = null;
        if (orderType.equals(Order.Type.SELL)) {
            double bidPpo = getPPO(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice());
            if(addInfo){
                addInfo("bidPpo", Double.toString(bidPpo));
            }
            if(compare(bidPpo)){
                order = sell(Order.AlgorithmType.PPO, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            double askPpo = getPPO(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice());
            if(addInfo){
                addInfo("askPpo", Double.toString(askPpo));
            }
            if(compare(askPpo)){
                order = buy(Order.AlgorithmType.PPO, timestamp);
            }
        }
        return order;
    }

    private double getPPO(ArrayList<Period> previousPeriods, BigDecimal lastPrice) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        return new PPO(previousPeriods, shortTermEMAPeriods, longTermEMAPeriods).execute().doubleValue();
    }

}
