package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SpreadAlgorithm extends InOutAlgorithm {

    public SpreadAlgorithm(
            ExchangeAccount exchangeAccount,
            double bandMinValue,
            double bandMaxValue, 
            boolean inBand) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        if(compare(getSpread(addInfo).doubleValue())){
            if (addInfo) {
                addInfo("inSpread", "TRUE");
            }
            return Order.AlgorithmType.SPREAD;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        if(compare(getSpread(addInfo).doubleValue())){
            if (addInfo) {
                addInfo("outSpread", "TRUE");
            }
            return Order.AlgorithmType.SPREAD;
        }
        return null;
    }
    
    private BigDecimal getSpread(boolean addInfo){
        if (exchangeAccount.getAccountBase().getLastBidPrice() == null || exchangeAccount.getAccountBase().getLastAskPrice() == null) {
            return null;
        }
        BigDecimal spread = (exchangeAccount.getAccountBase().getLastAskPrice().subtract(exchangeAccount.getAccountBase().getLastBidPrice())).divide(exchangeAccount.getAccountBase().getLastAskPrice(), 8, RoundingMode.UP).multiply(new BigDecimal(100));
        if(spread == null){
            return null;
        }
        if(addInfo){
            addInfo("spread", spread.toString());
        }
        return spread;
    }

}
