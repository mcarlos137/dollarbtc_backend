package com.dollarbtc.backend.cryptocurrency.exchange.calculation.indicator;

/**
 * A technical indicator.
 * @param <T> the data type of the indicator
 */
public interface Indicator<T> {

  
    T execute();
    
}
