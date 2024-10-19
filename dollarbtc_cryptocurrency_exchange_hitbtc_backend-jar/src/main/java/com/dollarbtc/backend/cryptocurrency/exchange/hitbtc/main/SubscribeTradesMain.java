/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.SubscribeTradesOperation;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeTradesMain {

    public static void main(String[] args) {
        //TRXUSD PRGUSD TNTUSD XRPUSDT STXUSD BNTUSD DCNUSD NEOUSD EOSUSD BTCUSD TRXBTC EOSBTC
        SubscribeTradesOperation hitBTCSubscribeTradesOperation = new SubscribeTradesOperation(args[0], 15230775);
        hitBTCSubscribeTradesOperation.start();
    }

}
