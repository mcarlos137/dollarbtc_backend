/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.SubscribeOrderBookOperation;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeOrderBookMain {

    public static void main(String[] args) {
        SubscribeOrderBookOperation hitBTCSubscribeOrderBookOperation = new SubscribeOrderBookOperation(args[0], 123);
        hitBTCSubscribeOrderBookOperation.start();
    }

}
