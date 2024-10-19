/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.SubscribeCandlesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeCandlesMain {

    public static void main(String[] args) {
        SubscribeCandlesOperation hitBTCSubscribeCandlesOperation = new SubscribeCandlesOperation("STXUSD", "30M", 123);
        hitBTCSubscribeCandlesOperation.start();
    }
    
}
