package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ReserveAlgorithm extends InOutAlgorithm {

    private final double basePercent;
    private final double baseLimitPercent;

    public ReserveAlgorithm(
            ExchangeAccount exchangeAccount,
            double basePercent,
            double baseLimitPercent
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.basePercent = basePercent;
        this.baseLimitPercent = baseLimitPercent;
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
        BigDecimal baseAmount = exchangeAccount.getAccountBase().getInitialBaseBalance().multiply(new BigDecimal(basePercent)).divide(new BigDecimal(100));
        BigDecimal baseLimitAmount = exchangeAccount.getAccountBase().getInitialBaseBalance().multiply(new BigDecimal(baseLimitPercent)).divide(new BigDecimal(100));
        if (exchangeAccount.addToReservedBaseBalance(exchangeAccount.getAccountBase().getLastAskPrice(), baseAmount, baseLimitAmount)) {
            if (addInfo) {
                addInfo("outReserve", "TRUE");
            }
            return Order.AlgorithmType.RESERVE;
        }
        return null;
    }

}
