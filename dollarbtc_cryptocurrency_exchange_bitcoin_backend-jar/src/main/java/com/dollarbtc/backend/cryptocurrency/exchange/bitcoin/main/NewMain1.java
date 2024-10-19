/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bitcoin.main;

import java.security.NoSuchAlgorithmException;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.params.TestNet3Params;

/**
 *
 * @author CarlosDaniel
 */
public class NewMain1 {

    public static void main(String[] args) throws NoSuchAlgorithmException, InsufficientMoneyException {        
        final Context context = new Context(TestNet3Params.get());
        Context.propagate(context);
        Kit kit = new Kit(context, "myJSEXLNB43iXkYUhrny2tnYuQNNg13xyY");
        kit.importKey("91t2GpmXBLQ1iPqzc573xwgtVYRtjtNjU6BTth6f7yEZ1RqGDKF");
        kit.sendBitcoins(0.0015, "myJSEXLNB43iXkYUhrny2tnYuQNNg13xyY", "mw5u5cspEbXKjukUJz5yfHkWdoACmGmneK");
    }
    
}
