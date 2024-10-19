package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BinaryAlgorithm extends TradingAlgorithm {

    public BinaryAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand
    ) {
        super(exchangeAccount, orderType, baseOrderAmount, orderAmount, orderAll, bandMinValue, bandMaxValue, inBand);
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(2);
    }

    @Override
    public Order placeOrder(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        Order order = null;
        double trendCoef = getTrendCoef(previousPeriods);
        if(addInfo){
            addInfo("binaryTrendCoef", Double.toString(trendCoef));
        }
        if (orderType.equals(Order.Type.SELL)) {
            if (compare(trendCoef)) {
                order = sell(Order.AlgorithmType.BINARY, timestamp);
            }
        }
        if (orderType.equals(Order.Type.BUY)) {
            if (compare(trendCoef)) {
                order = buy(Order.AlgorithmType.BINARY, timestamp);
            }
        }
        return order;
    }

    private double getTrendCoef(ArrayList<Period> previousPeriods) {
        List<Trade> trades = previousPeriods.get(0).getTrades();
        BigDecimal previousPrice = trades.get(1).getPrice();
        BigDecimal lastPrice = trades.get(0).getPrice();
        double trendCoef = previousPrice.divide(lastPrice, 12, RoundingMode.HALF_UP).doubleValue();
        return trendCoef;
    }

}
