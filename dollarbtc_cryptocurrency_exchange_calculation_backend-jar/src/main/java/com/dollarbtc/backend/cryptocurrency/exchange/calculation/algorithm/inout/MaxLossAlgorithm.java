package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MaxLossAlgorithm extends InOutAlgorithm {

    private final double basePercent;
    private final int inARowLimitQuantity;

    public MaxLossAlgorithm(
            ExchangeAccount exchangeAccount,
            double basePercent,
            int inARowLimitQuantity
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.basePercent = basePercent;
        this.inARowLimitQuantity = inARowLimitQuantity;
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        double baseAmount = exchangeAccount.getAccountBase().getInitialBaseBalance().doubleValue() * basePercent / 100;
        double overallEarnings = exchangeAccount.getOverallEarnings(exchangeAccount.getAccountBase().getLastAskPrice());
        if (overallEarnings <= baseAmount * -1) {
            exchangeAccount.setMaxLossInARowCounter(exchangeAccount.getMaxLossInARowCounter() + 1);
        } else {
            exchangeAccount.setMaxLossInARowCounter(0);
        }
        if (exchangeAccount.getMaxLossInARowCounter() >= inARowLimitQuantity) {
            if (!exchangeAccount.getAccountBase().getReservedBaseBalance().equals(BigDecimal.ZERO)) {
                if (exchangeAccount.getAccountBase().getReservedBaseBalance().compareTo(BigDecimal.valueOf(overallEarnings * -1)) > 0) {
                    if (addInfo) {
                        addInfo("outMaxLossAlgorithm", "TRUE");
                    }
                    return Order.AlgorithmType.MAX_LOSS_EARNING;
                } else {
                    if (addInfo) {
                        addInfo("outMaxLossAlgorithm", "TRUE");
                    }
                    return Order.AlgorithmType.MAX_LOSS_RESERVING;
                }
            } else {
                if (addInfo) {
                    addInfo("outMaxLoss", "TRUE");
                }
                return Order.AlgorithmType.MAX_LOSS_LOSSING;
            }
        }
        return null;
    }

}
