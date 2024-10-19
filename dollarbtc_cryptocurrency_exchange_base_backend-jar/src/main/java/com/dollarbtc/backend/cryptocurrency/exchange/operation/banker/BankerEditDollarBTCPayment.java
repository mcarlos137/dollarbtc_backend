/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerEditDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerEditDollarBTCPayment extends AbstractOperation<String> {

    private final BankerEditDollarBTCPaymentRequest bankerEditDollarBTCPaymentRequest;

    public BankerEditDollarBTCPayment(BankerEditDollarBTCPaymentRequest bankerEditDollarBTCPaymentRequest) {
        super(String.class);
        this.bankerEditDollarBTCPaymentRequest = bankerEditDollarBTCPaymentRequest;
    }

    @Override
    public void execute() {
        File bankerCurrencyPaymentFile = BankersFolderLocator.getPaymentCurrencyFile(bankerEditDollarBTCPaymentRequest.getUserName(), bankerEditDollarBTCPaymentRequest.getCurrency(), bankerEditDollarBTCPaymentRequest.getId());
        if (!bankerCurrencyPaymentFile.isFile()) {
            super.response = "PAYMENT DOES NOT EXIST";
            return;
        }
        FileUtil.editFile(bankerEditDollarBTCPaymentRequest.getPayment(), bankerCurrencyPaymentFile);
        super.response = "OK";
    }

}
