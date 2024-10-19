/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetPlacesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetPlacesMain {

    public static void main(String[] args) {
        GetPlacesOperation localBitcoinsGetPlacesOperation = new GetPlacesOperation(38898748, -77037684, null, null);
        localBitcoinsGetPlacesOperation.getResponse();
    }

}
