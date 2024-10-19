/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetContactInfoOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetContactInfoMain {

    public static void main(String[] args) {
        GetContactInfoOperation localBitcoinsGetContactInfoOperation = new GetContactInfoOperation("34050157", null, null);
        System.out.println("" + localBitcoinsGetContactInfoOperation.getResponse());
    }

}
