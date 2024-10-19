/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetAdsOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetAdsMain {

    public static void main(String[] args) {
        GetAdsOperation localBitcoinsGetAdsOperation = new GetAdsOperation(null, null);
        System.out.println("" + localBitcoinsGetAdsOperation.getResponse());
    }

}
