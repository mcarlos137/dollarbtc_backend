package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class StopLossAlgorithm extends InOutAlgorithm {

    private final double basePercent;
    private final int inARowLimitQuantity;

    public StopLossAlgorithm(
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
        if(exchangeAccount.getBuy().getLastOrder() == null){
            return null;
        }
        BigDecimal lastBuyPrice = exchangeAccount.getBuy().getLastOrder().getPrice();
        BigDecimal lastAskPrice = exchangeAccount.getAccountBase().getLastAskPrice();
        if (lastBuyPrice.multiply(BigDecimal.valueOf(1 - basePercent / 100)).compareTo(lastAskPrice) > 0) {
            exchangeAccount.setStopLossInARowCounter(exchangeAccount.getStopLossInARowCounter() + 1);
        } else {
            exchangeAccount.setStopLossInARowCounter(0);
        }
        if (exchangeAccount.getStopLossInARowCounter() >= inARowLimitQuantity) {
            if (addInfo) {
                addInfo("outStopLossAlgorithm", "TRUE");
            }
            return Order.AlgorithmType.STOP_LOSS;
        }
        return null;
    }

}
