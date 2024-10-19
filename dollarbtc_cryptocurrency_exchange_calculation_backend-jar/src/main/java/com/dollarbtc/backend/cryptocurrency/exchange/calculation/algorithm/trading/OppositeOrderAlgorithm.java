package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class OppositeOrderAlgorithm extends TradingAlgorithm {

    public OppositeOrderAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll
    ) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, 0.0, 0.0, false);
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public Order placeOrder(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order order = null;
        Order buyLastOrder = exchangeAccount.getBuy().getLastOrder();
        if (buyLastOrder != null && orderType.equals(Order.Type.SELL)) {
            order = sell(Order.AlgorithmType.OPPOSITE_ORDER, timestamp);
        } else  {
            if(orderType.equals(Order.Type.BUY)){
                order = buy(Order.AlgorithmType.OPPOSITE_ORDER, timestamp);
            }
        }
        return order;
    }

}
