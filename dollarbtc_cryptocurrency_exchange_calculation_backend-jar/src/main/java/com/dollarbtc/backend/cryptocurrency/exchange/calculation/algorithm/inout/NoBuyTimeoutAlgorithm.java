package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import java.util.ArrayList;

public class NoBuyTimeoutAlgorithm extends InOutAlgorithm {

    private final int maxMinutes;

    public NoBuyTimeoutAlgorithm(
            ExchangeAccount exchangeAccount,
            int maxMinutes
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.maxMinutes = maxMinutes;
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        if (exchangeAccount.getBuy().getQuantity() > 0) {
            return null;
        }
        long startedTime;
        if (exchangeAccount.getLastBuyTimestamp() == null) {
            startedTime = DateUtil.parseDate(exchangeAccount.getStartedTimestamp()).getTime();
        } else {
            startedTime = DateUtil.parseDate(exchangeAccount.getLastBuyTimestamp()).getTime();
        }
        long currentTime = DateUtil.parseDate(timestamp).getTime();
        long diffInMinutes = (currentTime - startedTime) / 60000;
        if (diffInMinutes >= maxMinutes) {
            if (addInfo) {
                addInfo("outNoBuyTimeout", "TRUE");
            }
            return Order.AlgorithmType.NO_BUY_TIMEOUT;
        }
        return null;
    }

}
