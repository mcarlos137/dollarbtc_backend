/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bigdecimal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
/**
 *
 * @author CarlosDaniel
 */
public class PriceBigDecimal extends BigDecimal {
    
    private static final MathContext context = new MathContext(8, RoundingMode.HALF_DOWN);
    
    public PriceBigDecimal(String val) {
        super(val, context);
    }
    
    
}
