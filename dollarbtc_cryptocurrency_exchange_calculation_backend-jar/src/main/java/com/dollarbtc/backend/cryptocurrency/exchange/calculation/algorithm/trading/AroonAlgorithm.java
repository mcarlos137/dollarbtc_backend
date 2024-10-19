package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.AroonDown;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.AroonUp;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class AroonAlgorithm extends TradingAlgorithm {

    private final int periods;

    public AroonAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll,
            int periods) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, 0.0, 0.0, false);
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
            if (isStrongUp(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice(), exchangeAccount.getAccountBase().getLastAskPrice(), addInfo)) {
                order = sell(Order.AlgorithmType.AROON, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            if (isStrongDown(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice(), exchangeAccount.getAccountBase().getLastAskPrice(), addInfo)) {
                order = buy(Order.AlgorithmType.AROON, timestamp);
            }
        }
        return order;
    }

    private boolean isStrongUp(ArrayList<Period> previousPeriods, BigDecimal lastBidPrice, BigDecimal lastAskPrice, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(lastBidPrice);
        double aroonUp = new AroonUp(previousPeriods, periods).execute();
        previousPeriods.get(0).changeLastTradePrice(lastAskPrice);
        double aroonDown = new AroonDown(previousPeriods, periods).execute();
        if (addInfo) {
            addInfo("aroonUp", Double.toString(aroonUp));
            addInfo("aroonDown", Double.toString(aroonDown));
        }
        return (aroonUp > 80 && aroonDown < 20);
    }

    private boolean isStrongDown(ArrayList<Period> previousPeriods, BigDecimal lastBidPrice, BigDecimal lastAskPrice, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(lastBidPrice);
        double aroonUp = new AroonUp(previousPeriods, periods).execute();
        previousPeriods.get(0).changeLastTradePrice(lastAskPrice);
        double aroonDown = new AroonDown(previousPeriods, periods).execute();
        if (addInfo) {
            addInfo("aroonUp", Double.toString(aroonUp));
            addInfo("aroonDown", Double.toString(aroonDown));
        }
        return (aroonDown > 80 && aroonUp < 20);
    }

}
