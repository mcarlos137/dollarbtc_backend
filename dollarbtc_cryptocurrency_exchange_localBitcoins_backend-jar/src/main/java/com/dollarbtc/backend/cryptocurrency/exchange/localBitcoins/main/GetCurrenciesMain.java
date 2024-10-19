/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetCurrenciesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetCurrenciesMain {

    public static void main(String[] args) {
        GetCurrenciesOperation localBitcoinsGetCurrenciesOperation = new GetCurrenciesOperation();
        localBitcoinsGetCurrenciesOperation.getResponse();
    }

}
