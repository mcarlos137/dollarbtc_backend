/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetAccountInfoOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetAccountInfoMain {

    public static void main(String[] args) {
        GetAccountInfoOperation localBitcoinsGetAccountInfoOperation = new GetAccountInfoOperation("Indestructible2018");
        System.out.println("" + localBitcoinsGetAccountInfoOperation.getResponse());
    }

}
