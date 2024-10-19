package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import java.util.ArrayList;

public class TimeoutAlgorithm extends InOutAlgorithm {

    private final int maxMinutes;
    private final int maxUnderZeroMinutes;

    public TimeoutAlgorithm(
            ExchangeAccount exchangeAccount, 
            int maxMinutes, 
            int maxUnderZeroMinutes
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.maxMinutes = maxMinutes;
        this.maxUnderZeroMinutes = maxUnderZeroMinutes;
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
        long currentTime = DateUtil.parseDate(timestamp).getTime();
        long startedTime = DateUtil.parseDate(exchangeAccount.getStartedTimestamp()).getTime();
        long diffInMinutes = (currentTime - startedTime) / 60000;
        if(diffInMinutes >= maxMinutes){
            if (addInfo) {
                addInfo("outTimeout", "TRUE");
            }
            return Order.AlgorithmType.TIMEOUT;
        }
        return null;
    }

}
