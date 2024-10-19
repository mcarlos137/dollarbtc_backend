package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout;

import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.PrimaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;
import java.math.BigDecimal;

public class LastSellPriceVariationAlgorithm extends InOutAlgorithm {

    private final double lastSellPrice, priceVariationPercent;
    private final LastSellPriceVariationAlgorithm.Type lastSellPriceVariationAlgorithmType;
    
    public LastSellPriceVariationAlgorithm(
            ExchangeAccount exchangeAccount,
            double lastSellPrice,
            double priceVariationPercent,
            LastSellPriceVariationAlgorithm.Type lastSellPriceVariationAlgorithmType
    ) {
        super(exchangeAccount, 0.0, 0.0, false);
        this.lastSellPrice = lastSellPrice;
        this.priceVariationPercent = priceVariationPercent;
        this.lastSellPriceVariationAlgorithmType = lastSellPriceVariationAlgorithmType;
    }

    @Override
    public boolean isEnoughPeriods() {
        return true;
    }

    @Override
    public Order.AlgorithmType in(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        BigDecimal[] ticker = PrimaryOperation.getTickerPrices(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol());
        if (ticker == null || ticker.length != 2) {
            return null;
        }
        double percent = (lastSellPrice - ticker[1].doubleValue()) / lastSellPrice * 100;
        switch(lastSellPriceVariationAlgorithmType){
            case DOWN:
                if(percent > 0 && percent > priceVariationPercent){
                    return Order.AlgorithmType.LAST_SELL_PRICE_VARIATION;
                }
                break;
            case UP:
                if(percent < 0 && -percent > priceVariationPercent){
                    return Order.AlgorithmType.LAST_SELL_PRICE_VARIATION;
                }
                break;
        }
        
        return null;
    }

    @Override
    public Order.AlgorithmType out(ArrayList<Period> previousPeriods, String timestamp, boolean addInfo) {
        return null;
    }
    
    public static enum Type {

        DOWN, UP;

    }

}
