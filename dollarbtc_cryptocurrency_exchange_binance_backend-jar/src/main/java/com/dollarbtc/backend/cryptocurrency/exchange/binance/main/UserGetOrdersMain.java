/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.user.UserGetOrdersOperation;

/**
 *
 * @author CarlosDaniel
 */
public class UserGetOrdersMain {

    public static void main(String[] args) {
        UserGetOrdersOperation hitBTCUserGetOrdersOperation = new UserGetOrdersOperation("modelName", 123);
    }

}
