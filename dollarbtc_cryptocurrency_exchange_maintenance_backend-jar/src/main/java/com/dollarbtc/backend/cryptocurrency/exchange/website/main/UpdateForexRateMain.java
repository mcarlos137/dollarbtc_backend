/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.website.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.forex.ForexUpdateAuxRate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.forex.ForexUpdateRate;

/**
 *
 * @author CarlosDaniel
 */
public class UpdateForexRateMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        args = new String[]{
//            "USDUSD",
//            "USDVES",
//            "USDCOP",
//            "USDEUR",
//            "USDCLP",
//            "USDPEN",
//            "USDBRL",
//            "USDARS",
//            "USDMXN",
//            "USDCNY",
//            "USDRUB",
//            "USDINR",
//            "USDCAD",
//            "USDDOP",
//            "USDJPY",
//            "USDCHF",
//            "USDPAB",
//            "USDCRC"
//        };
        new ForexUpdateRate(args).getResponse();
        args = new String[]{
            "USDUSD__USDPA_USD"
        };
        new ForexUpdateAuxRate(args).getResponse();
    }

}
