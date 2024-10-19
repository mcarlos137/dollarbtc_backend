/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BankerGetDollarBTCPayment extends AbstractOperation<Object> {

    private final String userName, currency, id;

    public BankerGetDollarBTCPayment(String userName, String currency, String id) {
        super(Object.class);
        this.userName = userName;
        this.currency = currency;
        this.id = id;
    }

    @Override
    protected void execute() {
        File bankerCurrencyPaymentFile = BankersFolderLocator.getPaymentCurrencyFile(userName, currency, id);
        if (!bankerCurrencyPaymentFile.isFile()) {
            super.response = "PAYMENT DOES NOT EXIST";
            return;
        }
        try {
            super.response = mapper.readTree(bankerCurrencyPaymentFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(BankerGetDollarBTCPayment.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
