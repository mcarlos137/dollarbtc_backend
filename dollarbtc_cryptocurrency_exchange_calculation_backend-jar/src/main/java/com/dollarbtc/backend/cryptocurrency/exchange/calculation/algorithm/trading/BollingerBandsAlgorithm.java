package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.SMA;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.StandardDeviation;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class BollingerBandsAlgorithm extends TradingAlgorithm {

    private final BollingerBandsAlgorithm.Type bollingerBandsAlgorithmType;
    private final int periods;

    public BollingerBandsAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll,
            BollingerBandsAlgorithm.Type bollingerBandsAlgorithmType,
            int periods
    ) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, 0.0, 0.0, false);
        this.bollingerBandsAlgorithmType = bollingerBandsAlgorithmType;
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
            double middleBand = new SMA(previousPeriods, periods).execute().doubleValue();
            double standardDeviation = new StandardDeviation(previousPeriods, periods).execute();
            double upperBand = middleBand + (standardDeviation * 2);
            if (addInfo) {
                addInfo("bollingerUpperBand", Double.toString(upperBand));
                addInfo("bollingerMiddleBand", Double.toString(middleBand));
            }
            switch (bollingerBandsAlgorithmType) {
                case SIMPLE:
                    if (exchangeAccount.getAccountBase().getLastBidPrice().doubleValue() > upperBand) {
                        order = sell(Order.AlgorithmType.BOLLINGER_BANDS, timestamp);
                    }
                    break;
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastAskPrice());
            double middleBand = new SMA(previousPeriods, periods).execute().doubleValue();
            double standardDeviation = new StandardDeviation(previousPeriods, periods).execute();
            double lowerBand = middleBand - (standardDeviation * 2);
            if (addInfo) {
                addInfo("bollingerMiddleBand", Double.toString(middleBand));
                addInfo("bollingerLowerBand", Double.toString(lowerBand));
            }
            switch (bollingerBandsAlgorithmType) {
                case SIMPLE:
                    if (exchangeAccount.getAccountBase().getLastAskPrice().doubleValue() < lowerBand) {
                        order = buy(Order.AlgorithmType.BOLLINGER_BANDS, timestamp);
                    }
                    break;
            }
        }
        return order;
    }

    public static enum Type {

        SIMPLE;

    }

}
