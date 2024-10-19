/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.user.UserCancelOrderOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserCancelOrderMain {

    public static void main(String[] args) {
        UserCancelOrderOperation hitBTCUserCancelOrderOperation = new UserCancelOrderOperation("modelName", "57d5525562c945448e3cbd559bd068c4", 123);
    }

}
