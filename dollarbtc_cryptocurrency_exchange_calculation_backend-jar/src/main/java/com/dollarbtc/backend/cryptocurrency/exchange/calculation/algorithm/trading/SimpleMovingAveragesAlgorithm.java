package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.SMA;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class SimpleMovingAveragesAlgorithm extends TradingAlgorithm {

    private final int shortTermSMAPeriods;
    private final int longTermSMAPeriods;

    public SimpleMovingAveragesAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermSMAPeriods,
            int longTermSMAPeriods
    ) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, bandMinValue, bandMaxValue, inBand);
        this.shortTermSMAPeriods = shortTermSMAPeriods;
        this.longTermSMAPeriods = longTermSMAPeriods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(longTermSMAPeriods);
    }

    @Override
    public Order placeOrder(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order order = null;
        if (orderType.equals(Order.Type.SELL)) {
            double bidTrendCoef = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice());
            if(addInfo){
                addInfo("bidSMATrendCoef", Double.toString(bidTrendCoef));
            }
            if (compare(bidTrendCoef)) {
                order = sell(Order.AlgorithmType.SMA, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            double askTrendCoef = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice());
            if(addInfo){
                addInfo("askSMATrendCoef", Double.toString(askTrendCoef));
            }
            if (compare(askTrendCoef)) {
                order = buy(Order.AlgorithmType.SMA, timestamp);
            }
        }
        return order;
    }

    private double getTrendCoef(ArrayList<Period> previousPeriods, BigDecimal lastPrice) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        double bidMovingAvgHigh = new SMA(previousPeriods, longTermSMAPeriods).execute().doubleValue();
        double bidMovingAvgLow = new SMA(previousPeriods, shortTermSMAPeriods).execute().doubleValue();
        return bidMovingAvgLow / bidMovingAvgHigh;
    }

}
