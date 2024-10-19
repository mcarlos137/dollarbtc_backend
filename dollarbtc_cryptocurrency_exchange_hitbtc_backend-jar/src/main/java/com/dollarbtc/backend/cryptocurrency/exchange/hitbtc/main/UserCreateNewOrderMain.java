/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user.UserCreateNewOrderOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserCreateNewOrderMain {

    public static void main(String[] args) {
        UserCreateNewOrderOperation hitBTCUserCreateNewOrderOperation = new UserCreateNewOrderOperation("HitBTC1", "ETHBTC", "57d5525562c945448e3cbd559bd068c4", "sell", "market", "0.059837", "0.015", "123");
        hitBTCUserCreateNewOrderOperation.getResponse();
    }

}
