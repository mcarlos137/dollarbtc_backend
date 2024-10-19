package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;

public class MaxBuyQuantityAlgorithm extends InOutAlgorithm {

    public MaxBuyQuantityAlgorithm(
            ExchangeAccount exchangeAccount, 
            int quantity
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        exchangeAccount.setMaxBuyQuantity(quantity);
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
        if(exchangeAccount.getBuyCounter() > 0 && exchangeAccount.getBuyCounter() == exchangeAccount.getMaxBuyQuantity() && exchangeAccount.getBuy().getQuantity() == 0){
            if(addInfo){
                addInfo("outMaxBuyQuantity", "TRUE");
            }
            return Order.AlgorithmType.MAX_BUY_QUANTITY;
        }
        return null;
    }

}
