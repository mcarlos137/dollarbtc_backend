package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.ROC;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class ROCAlgorithm extends TradingAlgorithm {

    private final int periods;

    public ROCAlgorithm(
            ExchangeAccount exchangeAccount, 
            Order.Type orderType, 
            double baseOrderAmount, 
            double orderAmount, 
            boolean orderAll,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int periods
    ) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, bandMinValue, bandMaxValue, inBand);
        this.periods = periods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public Order placeOrder(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order order = null;
        if (orderType.equals(Order.Type.SELL)) {
            previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastBidPrice());
            double bidRoc = new ROC(previousPeriods, periods).execute();
            if(addInfo){
                addInfo("bidRoc", Double.toString(bidRoc));
            }
            if(compare(bidRoc)){
                order = sell(Order.AlgorithmType.ROC, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastAskPrice());
            double askRoc = new ROC(previousPeriods, periods).execute();
            if(addInfo){
                addInfo("askRoc", Double.toString(askRoc));
            }
            if(compare(askRoc)){
                order = buy(Order.AlgorithmType.ROC, timestamp);
            }
        }
        return order;
    }

}
