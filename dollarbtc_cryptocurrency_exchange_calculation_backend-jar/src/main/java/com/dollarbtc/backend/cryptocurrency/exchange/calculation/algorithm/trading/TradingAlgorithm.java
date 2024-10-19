package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.Algorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.math.RoundingMode;

public abstract class TradingAlgorithm extends Algorithm {

    protected final Order.Type orderType;
    protected final double baseOrderAmount;
    protected final double orderAmount;
    protected final boolean orderAll;

    public TradingAlgorithm(
            ExchangeAccount exchangeAccount,
            Order.Type orderType,
            double baseOrderAmount,
            double orderAmount,
            boolean orderAll,
            double bandMinValue,
            double bandMaxValue,
            boolean inBand
    ) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
        this.orderType = orderType;
        this.baseOrderAmount = baseOrderAmount;
        this.orderAmount = orderAmount;
        this.orderAll = orderAll;
    }

    public abstract boolean isEnoughPeriods();

    public abstract Order placeOrder(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo);

    public Order sell(Order.AlgorithmType orderAlgorithmType, String timestamp) {
        if (!orderAll) {
            return new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.SELL, timestamp).tradableAmount(BigDecimal.valueOf(orderAmount)).algorithmType(orderAlgorithmType).build();
        } else {
            BigDecimal btcBalance = exchangeAccount.getAccountBase().getCurrentAssetBalance();
            if (btcBalance.compareTo(BigDecimal.ZERO) == 1) {
                return new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.SELL, timestamp).tradableAmount(btcBalance).algorithmType(orderAlgorithmType).build();
            }
        }
        return null;
    }

    public Order buy(Order.AlgorithmType orderAlgorithmType, String timestamp) {
        if (!orderAll) {
            return new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.BUY, timestamp).tradableAmount(BigDecimal.valueOf(orderAmount)).algorithmType(orderAlgorithmType).build();
        } else {
            BigDecimal assetToBeBought = exchangeAccount.getAccountBase().getCurrentBaseBalance().divide(exchangeAccount.getAccountBase().getLastAskPrice(), 8, RoundingMode.DOWN).multiply(BigDecimal.ONE.subtract(ExchangeUtil.FEE_TRANSACTION_FACTOR));
            assetToBeBought = assetToBeBought.divide(BigDecimal.valueOf(baseOrderAmount), 0, RoundingMode.DOWN).multiply(BigDecimal.valueOf(baseOrderAmount));
            if (assetToBeBought.compareTo(BigDecimal.ZERO) == 1) {
                return new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.BUY, timestamp).tradableAmount(assetToBeBought).algorithmType(orderAlgorithmType).build();
            }
        }
        return null;
    }

}
