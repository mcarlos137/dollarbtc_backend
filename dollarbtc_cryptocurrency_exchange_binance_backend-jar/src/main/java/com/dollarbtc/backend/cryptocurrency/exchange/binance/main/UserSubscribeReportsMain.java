/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.user.UserSubscribeReportsOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserSubscribeReportsMain {

    public static void main(String[] args) {
        UserSubscribeReportsOperation hitBTCUserSubscribeReportsOperation = new UserSubscribeReportsOperation("modelName", 123);
        hitBTCUserSubscribeReportsOperation.start();
    }

}
