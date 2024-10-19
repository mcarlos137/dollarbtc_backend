package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.EMA;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class EMAAlgorithm extends TradingAlgorithm {

    private final int shortTermEMAPeriods;
    private final int longTermEMAPeriods;

    public EMAAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand,
            int shortTermEMAPeriods,
            int longTermEMAPeriods) {
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
            double bidTrendCoef = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice());
            if(addInfo){
                addInfo("bidEMATrendCoef", Double.toString(bidTrendCoef));
            }
            if (compare(bidTrendCoef)) {
                order = sell(Order.AlgorithmType.EMA, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            double askTrendCoef = getTrendCoef(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice());
            if(addInfo){
                addInfo("askEMATrendCoef", Double.toString(askTrendCoef));
            }
            if (compare(askTrendCoef)) {
                order = buy(Order.AlgorithmType.EMA, timestamp);
            }
        }
        return order;
    }

    private double getTrendCoef(ArrayList<Period> previousPeriods, BigDecimal lastPrice) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        double movingAvgLong = new EMA(previousPeriods, longTermEMAPeriods).execute().doubleValue();
        double movingAvgShort = new EMA(previousPeriods, shortTermEMAPeriods).execute().doubleValue();
        return movingAvgShort / movingAvgLong;
    }

}
