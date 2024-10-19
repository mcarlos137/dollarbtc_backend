/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.SubscribeTickerOperation;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeTickerMain {

    public static void main(String[] args) {
        SubscribeTickerOperation hitBTCSubscribeTickerOperation = new SubscribeTickerOperation("TRXUSD", 123);
        hitBTCSubscribeTickerOperation.start();
    }

}
