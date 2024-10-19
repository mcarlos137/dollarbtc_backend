/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminBuyFromDollarBTCPaymentRequest;
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
public class OTCAdminBuyFromDollarBTCPayment extends AbstractOperation<String> {

    private final OTCAdminBuyFromDollarBTCPaymentRequest otcBuyFromDollarBTCPaymentRequest;

    public OTCAdminBuyFromDollarBTCPayment(OTCAdminBuyFromDollarBTCPaymentRequest otcBuyFromDollarBTCPaymentRequest) {
        super(String.class);
        this.otcBuyFromDollarBTCPaymentRequest = otcBuyFromDollarBTCPaymentRequest;
    }

    @Override
    protected void execute() {
        File masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(otcBuyFromDollarBTCPaymentRequest.getMasterAccountName());
        if (!masterAccountBalanceFolder.isDirectory()) {
            super.response = "MASTER ACCOUNT DOES NOT EXIST";
            return;
        }
        File otcCurrencyPaymentFolder = OTCFolderLocator.getCurrencyPaymentFolder(null, otcBuyFromDollarBTCPaymentRequest.getCurrency(), otcBuyFromDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFolder.isDirectory()) {
            super.response = "DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        String substractBalanceToDollarBTCPayment = BaseOperation.substractToBalance(
                new File(otcCurrencyPaymentFolder, "Balance"),
                otcBuyFromDollarBTCPaymentRequest.getCurrency(),
                otcBuyFromDollarBTCPaymentRequest.getAmount(),
                BalanceOperationType.DEBIT,
                BalanceOperationStatus.OK,
                "BUY BTC FROM DOLLARBTC PAYMENT " + otcBuyFromDollarBTCPaymentRequest.getId() + " - " + otcBuyFromDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                false,
                null,
                false,
                null
        );
        if (!substractBalanceToDollarBTCPayment.equals("OK")) {
            super.response = substractBalanceToDollarBTCPayment;
            return;
        }
        BaseOperation.addToBalance(
                masterAccountBalanceFolder,
                "BTC",
                otcBuyFromDollarBTCPaymentRequest.getMasterAccountAmount(),
                BalanceOperationType.CREDIT,
                BalanceOperationStatus.OK,
                "BUY BTC FROM DOLLARBTC PAYMENT " + otcBuyFromDollarBTCPaymentRequest.getId() + " - " + otcBuyFromDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }

}
