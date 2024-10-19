/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.user.UserNewOrderOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserNewOrderMain {

    public static void main(String[] args) {
        UserNewOrderOperation hitBTCUserNewOrderOperation = new UserNewOrderOperation("modelName", "ETHBTC", "57d5525562c945448e3cbd559bd068c4", "sell", "market", "0.059837", "0.015", 123);
    }

}
