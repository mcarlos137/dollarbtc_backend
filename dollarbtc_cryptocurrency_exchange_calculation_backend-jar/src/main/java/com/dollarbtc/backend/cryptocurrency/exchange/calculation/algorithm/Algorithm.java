package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;

public abstract class Algorithm {
    
    protected final ExchangeAccount exchangeAccount;
    protected final double bandMinValue, bandMaxValue;
    protected final boolean inBand;
    
    public Algorithm(
            ExchangeAccount exchangeAccount, 
            double bandMinValue,
            double bandMaxValue,
            boolean inBand
    ){
        this.exchangeAccount = exchangeAccount;
        this.bandMinValue = bandMinValue;
        this.bandMaxValue = bandMaxValue;
        this.inBand = inBand;
    }
    
    protected boolean compare(double currentValue) {
        if (inBand) {
            if(currentValue <= bandMaxValue && currentValue >= bandMinValue){
                return true;
            }
        } else {
            if(currentValue > bandMaxValue || currentValue < bandMinValue){
                return true;
            }
        }
        return false;
    }
    
    protected void addInfo(String type, String info){
        if(exchangeAccount.getAccountBase().getInfo() == null){
            exchangeAccount.getAccountBase().setInfo("");
        }
        StringBuilder stringBuilder = new StringBuilder(exchangeAccount.getAccountBase().getInfo());
        if(stringBuilder.length() != 0){
            stringBuilder.append("____");
        }
        stringBuilder.append(type);
        stringBuilder.append("__");
        stringBuilder.append(info);
        exchangeAccount.getAccountBase().setInfo(stringBuilder.toString());
    }
                
}
