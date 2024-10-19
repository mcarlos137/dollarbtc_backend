/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetContactMessagesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetContactMessagesMain {

    public static void main(String[] args) {
        GetContactMessagesOperation localBitcoinsGetContactMessagesOperation = new GetContactMessagesOperation("34050157", null, null);
        System.out.println("" + localBitcoinsGetContactMessagesOperation.getResponse());
    }

}
