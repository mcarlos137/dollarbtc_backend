/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.main;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator.ModelModulator;

/**
 *
 * @author CarlosDaniel
 */
public class ModelMain {

    public static void main(String[] args) {
//        args = new String[1];
//        args[0] = "Sampler1";
        ModelModulator modelModulator = new ModelModulator(args[0]);
        modelModulator.start(5000);
    }
    
}
