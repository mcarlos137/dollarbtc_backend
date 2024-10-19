package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.SMA;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.StandardDeviation;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class BollingerBandsAlgorithm extends InOutAlgorithm {

    private final BollingerBandsAlgorithm.Type bollingerBandsAlgorithmType;
    private final int periods;

    public BollingerBandsAlgorithm(
            ExchangeAccount exchangeAccount,
            BollingerBandsAlgorithm.Type bollingerBandsAlgorithmType,
            int periods
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.bollingerBandsAlgorithmType = bollingerBandsAlgorithmType;
        this.periods = periods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order.Type orderType = getOrderType(previousPeriods, exchangeAccount.getAccountBase().getLastAskPrice(), addInfo);
        if (orderType != null && orderType.equals(Order.Type.BUY)) {
            return Order.AlgorithmType.BOLLINGER_BANDS;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order.Type orderType = getOrderType(previousPeriods, exchangeAccount.getAccountBase().getLastBidPrice(), addInfo);
        if (orderType != null && orderType.equals(Order.Type.SELL)) {
            return Order.AlgorithmType.BOLLINGER_BANDS;
        }
        return null;
    }

    private Order.Type getOrderType(ArrayList<Period> previousPeriods, BigDecimal lastPrice, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(lastPrice);
        double middleBand = new SMA(previousPeriods, periods).execute().doubleValue();
        double standardDeviation = new StandardDeviation(previousPeriods, periods).execute();
        double upperBand = middleBand + (standardDeviation * 2);
        double lowerBand = middleBand - (standardDeviation * 2);
        if (addInfo) {
            addInfo("bollingerUpperBand", Double.toString(upperBand));
            addInfo("bollingerMiddleBand", Double.toString(middleBand));
            addInfo("bollingerLowerBand", Double.toString(lowerBand));
        }
        switch (bollingerBandsAlgorithmType) {
            case SIMPLE:
                if (lastPrice.doubleValue() > upperBand) {
                    return Order.Type.SELL;
                } else if (lastPrice.doubleValue() < lowerBand) {
                    return Order.Type.BUY;
                }
                break;
        }
        return null;
    }

    public static enum Type {

        SIMPLE;

    }

}
