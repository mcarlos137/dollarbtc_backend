/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.main;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator.ModelTestModulator;

/**
 *
 * @author CarlosDaniel
 */
public class ModelTestMain {

    public static void main(String[] args) {
        ModelTestModulator modelTestModulator = new ModelTestModulator(args[0]);
        modelTestModulator.start(null);
    }
    
}
