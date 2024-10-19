/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserGetTradesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserGetTradesMain {

    public static void main(String[] args) {
        UserGetTradesOperation userGetTradesOperation = new UserGetTradesOperation("HitBTC1", "EOSBTC", 10);
        System.out.println("response: " + userGetTradesOperation.getResponse());
    }

}
