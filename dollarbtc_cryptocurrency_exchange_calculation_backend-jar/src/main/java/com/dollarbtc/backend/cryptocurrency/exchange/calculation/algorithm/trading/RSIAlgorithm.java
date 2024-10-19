package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.RSI;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RSIAlgorithm extends TradingAlgorithm {

    private final int periods;

    public RSIAlgorithm(
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
            BigDecimal bidRsi = new RSI(previousPeriods, periods).execute();
            if (addInfo) {
                addInfo("bidRsi", bidRsi.toString());
            }
            if (compare(bidRsi.doubleValue())) {
                order = sell(Order.AlgorithmType.RSI, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastAskPrice());
            BigDecimal askRsi = new RSI(previousPeriods, periods).execute();
            if (addInfo) {
                addInfo("askRsi", askRsi.toString());
            }
            if (compare(askRsi.doubleValue())) {
                order = buy(Order.AlgorithmType.RSI, timestamp);
            }
        }
        return order;
    }

}
