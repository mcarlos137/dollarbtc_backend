/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.main;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator.MarketModulatorModulator;

/**
 *
 * @author CarlosDaniel
 */
public class MarketModulatorMain {

    public static void main(String[] args) {
        MarketModulatorModulator marketModulatorModulator = new MarketModulatorModulator(args[0]);
        marketModulatorModulator.start(600000);
    }
    
}
