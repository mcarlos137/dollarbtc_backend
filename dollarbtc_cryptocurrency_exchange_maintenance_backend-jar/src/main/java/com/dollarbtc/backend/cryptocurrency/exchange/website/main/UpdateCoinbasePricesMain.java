/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.website.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.coinbase.CoinbaseUpdateAuxPrices;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.coinbase.CoinbaseUpdatePrices;

/**
 *
 * @author CarlosDaniel
 */
public class UpdateCoinbasePricesMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CoinbaseUpdatePrices(args).getResponse();
        args = new String[]{
            "USD__PA_USD",
        };
        new CoinbaseUpdateAuxPrices(args).getResponse();
    }

}
