/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.GetTickerOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetTickerMain {

    public static void main(String[] args) {
        GetTickerOperation getTickerOperation = new GetTickerOperation("TRXUSD");
        System.out.println("" + getTickerOperation.getResponse());
    }

}
