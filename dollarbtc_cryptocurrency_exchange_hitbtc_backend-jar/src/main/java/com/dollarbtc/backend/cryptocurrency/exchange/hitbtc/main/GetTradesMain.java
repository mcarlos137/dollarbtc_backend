/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.GetTradesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetTradesMain {

    public static void main(String[] args) {
        GetTradesOperation hitBTCGetTradesOperation = new GetTradesOperation("ETHBTC", 1, "DESC", "id", 123);
    }

}
