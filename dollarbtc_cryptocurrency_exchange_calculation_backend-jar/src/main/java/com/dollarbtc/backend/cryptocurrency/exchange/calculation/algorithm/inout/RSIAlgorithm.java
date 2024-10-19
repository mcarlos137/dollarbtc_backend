package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator.RSI;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RSIAlgorithm extends InOutAlgorithm {

    private final int periods;

    public RSIAlgorithm(
            ExchangeAccount exchangeAccount,
            double bandMinValue,
            double bandMaxValue, 
            boolean inBand,
            int periods
    ) {
        super(exchangeAccount, bandMinValue, bandMaxValue, inBand);
        this.periods = periods;
    }

    @Override
    public boolean isEnoughPeriods() {
        return ExchangeMarket.isEnoughPeriods(periods);
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastAskPrice());
        BigDecimal inRsi = new RSI(previousPeriods, periods).execute();
        if(inRsi == null){
            return null;
        }
        if(addInfo){
            addInfo("inRsi", inRsi.toString());
        }
        if(compare(inRsi.doubleValue())){
            return Order.AlgorithmType.RSI;
        }
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        previousPeriods.get(0).changeLastTradePrice(exchangeAccount.getAccountBase().getLastBidPrice());
        BigDecimal outRsi = new RSI(previousPeriods, periods).execute();
        if(outRsi == null){
            return null;
        }
        if(addInfo){
            addInfo("outRsi", outRsi.toString());
        }
        if(compare(outRsi.doubleValue())){
            return Order.AlgorithmType.RSI;
        }
        return null;
    }

}
