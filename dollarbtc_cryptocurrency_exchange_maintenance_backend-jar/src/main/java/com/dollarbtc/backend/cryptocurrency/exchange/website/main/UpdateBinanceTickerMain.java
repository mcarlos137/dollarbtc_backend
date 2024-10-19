/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.website.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.binance.BinanceUpdateAuxTicker;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.binance.BinanceUpdateTickers;

/**
 *
 * @author CarlosDaniel
 */
public class UpdateBinanceTickerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        args = new String[]{
            "USD",
            "VES",
            "COP", 
            "EUR", 
            "CLP", 
            "PEN", 
            "BRL", 
            "ARS", 
            "MXN", 
            "CNY", 
            "RUB", 
            "INR", 
            "CAD", 
            "DOP", 
            "JPY", 
            "CHF", 
            "CRC", 
            "PAB"
        };
        new BinanceUpdateTickers(args).getResponse();
        args = new String[]{
            "USD__PA_USD",
        };
        new BinanceUpdateAuxTicker(args).getResponse();

    }

}
