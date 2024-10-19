/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCEditDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCEditDollarBTCPayment extends AbstractOperation<String> {

    private final OTCEditDollarBTCPaymentRequest otcEditDollarBTCPaymentRequest;

    public OTCEditDollarBTCPayment(OTCEditDollarBTCPaymentRequest otcEditDollarBTCPaymentRequest) {
        super(String.class);
        this.otcEditDollarBTCPaymentRequest = otcEditDollarBTCPaymentRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyPaymentFile = OTCFolderLocator.getCurrencyPaymentFile(null, otcEditDollarBTCPaymentRequest.getCurrency(), otcEditDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFile.isFile()) {
            super.response = "PAYMENT DOES NOT EXIST";
            return;
        }
        FileUtil.editFile(otcEditDollarBTCPaymentRequest.getPayment(), otcCurrencyPaymentFile);
        super.response = "OK";
    }

}
