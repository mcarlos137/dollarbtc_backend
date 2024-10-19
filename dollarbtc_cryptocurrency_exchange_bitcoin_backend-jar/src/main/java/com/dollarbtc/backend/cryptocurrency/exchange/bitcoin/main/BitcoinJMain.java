/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bitcoin.main;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.Context;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 *
 * @author CarlosDaniel
 */
public class BitcoinJMain {

    public static void main(String[] args) throws Exception {
        Context context = new Context(MainNetParams.get());
        if(args[0].equals("TEST")){
            context = new Context(TestNet3Params.get());
        } 
        Context.propagate(context);
        BitcoinJ bitcoinJ = new BitcoinJ(context);
//        System.out.println("-------------------------------0-------------------------------");
//        bitcoinJ.getBalance("myJSEXLNB43iXkYUhrny2tnYuQNNg13xyY");
//        System.out.println("-------------------------------1-------------------------------");
//        bitcoinJ.getBalance("mw5u5cspEbXKjukUJz5yfHkWdoACmGmneK");
//        System.out.println("-------------------------------2-------------------------------");
//        bitcoinJ.sendBitcoins(0.0015, "myJSEXLNB43iXkYUhrny2tnYuQNNg13xyY", "mw5u5cspEbXKjukUJz5yfHkWdoACmGmneK");
        transactionLoop(bitcoinJ);
        bitcoinJ.destroy();
        System.exit(0);
    }
    
    private static void transactionLoop(BitcoinJ bitcoinJ) {
        int totalMillisecs = 0;
        int millisecsPortion = 30000;
        while (true) {
            //look for new transactions
            bitcoinJ.checkProcessingOperations();
            if(bitcoinJ.checkForcedToExit()){
                break;
            }
            try {
                totalMillisecs = totalMillisecs + millisecsPortion;
                Thread.sleep(millisecsPortion);
            } catch (InterruptedException ex) {
                Logger.getLogger(BitcoinJMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(totalMillisecs < 600000){
                continue;
            }
            totalMillisecs = 0;
            //look for new addresses
            if(bitcoinJ.existNewPrivateKeys()){
                break;
            }
        }
    }

}
