/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminSellFromDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminSellFromDollarBTCPayment extends AbstractOperation<String> {
    
    private final OTCAdminSellFromDollarBTCPaymentRequest otcSellFromDollarBTCPaymentRequest;

    public OTCAdminSellFromDollarBTCPayment(OTCAdminSellFromDollarBTCPaymentRequest otcSellFromDollarBTCPaymentRequest) {
        super(String.class);
        this.otcSellFromDollarBTCPaymentRequest = otcSellFromDollarBTCPaymentRequest;
    }

    @Override
    protected void execute() {
        File masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(otcSellFromDollarBTCPaymentRequest.getMasterAccountName());
        if (!masterAccountBalanceFolder.isDirectory()) {
            super.response = "MASTER ACCOUNT DOES NOT EXIST";
            return;
        }
        File otcCurrencyPaymentFolder = OTCFolderLocator.getCurrencyPaymentFolder(null, otcSellFromDollarBTCPaymentRequest.getCurrency(), otcSellFromDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFolder.isDirectory()) {
            super.response = "DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        String substractToBalance = BaseOperation.substractToBalance(
                masterAccountBalanceFolder,
                "BTC",
                otcSellFromDollarBTCPaymentRequest.getMasterAccountAmount(),
                BalanceOperationType.DEBIT,
                BalanceOperationStatus.OK,
                "SELL BTC FROM DOLLARBTC PAYMENT " + otcSellFromDollarBTCPaymentRequest.getId() + " - " + otcSellFromDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                false,
                null,
                false,
                null
        );
        if (!substractToBalance.equals("OK")) {
            super.response = substractToBalance;
            return;
        }
        BaseOperation.addToBalance(
                new File(otcCurrencyPaymentFolder, "Balance"),
                otcSellFromDollarBTCPaymentRequest.getCurrency(),
                otcSellFromDollarBTCPaymentRequest.getAmount(),
                BalanceOperationType.CREDIT,
                BalanceOperationStatus.OK,
                "SELL BTC FROM DOLLARBTC PAYMENT " + otcSellFromDollarBTCPaymentRequest.getId() + " - " + otcSellFromDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }
    
}
