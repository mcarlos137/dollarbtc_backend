/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.main;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator.MasterAccountModulator;

/**
 *
 * @author CarlosDaniel
 */
public class MasterAccountMain {

    public static void main(String[] args) {
        MasterAccountModulator masterAccountModulator = new MasterAccountModulator(args[0]);
        masterAccountModulator.start(6000);
    }
    
}