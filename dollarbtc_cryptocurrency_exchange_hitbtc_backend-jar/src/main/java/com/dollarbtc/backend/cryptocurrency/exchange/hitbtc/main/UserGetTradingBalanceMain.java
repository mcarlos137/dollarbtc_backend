/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserGetTradingBalanceOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserGetTradingBalanceMain {

    public static void main(String[] args) {
        UserGetTradingBalanceOperation hitBTCUserGetTradingBalanceOperation = new UserGetTradingBalanceOperation("modelName", 123);
    }

}
