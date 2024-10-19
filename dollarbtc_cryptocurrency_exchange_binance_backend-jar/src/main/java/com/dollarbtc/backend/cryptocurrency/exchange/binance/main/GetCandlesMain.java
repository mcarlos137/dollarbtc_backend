/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.GetCandlesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetCandlesMain {

    public static void main(String[] args) {
        GetCandlesOperation hitBTCGetCandlesOperation = new GetCandlesOperation("BTCUSDT", "M5");
        hitBTCGetCandlesOperation.getResponse();
    }

}
