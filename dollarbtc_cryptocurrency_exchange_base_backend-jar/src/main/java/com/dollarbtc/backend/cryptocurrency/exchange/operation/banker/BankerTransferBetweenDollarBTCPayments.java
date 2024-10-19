/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerTransferBetweenDollarBTCPaymentsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerTransferBetweenDollarBTCPayments extends AbstractOperation<String> {
    
    private final BankerTransferBetweenDollarBTCPaymentsRequest bankerTransferBetweenDollarBTCPaymentsRequest;

    public BankerTransferBetweenDollarBTCPayments(BankerTransferBetweenDollarBTCPaymentsRequest bankerTransferBetweenDollarBTCPaymentsRequest) {
        super(String.class);
        this.bankerTransferBetweenDollarBTCPaymentsRequest = bankerTransferBetweenDollarBTCPaymentsRequest;
    }

    @Override
    protected void execute() {
        File baseOTCCurrencyPaymentBalanceFolder = new File(BankersFolderLocator.getPaymentCurrencyFolder(bankerTransferBetweenDollarBTCPaymentsRequest.getUserName(), bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency(), bankerTransferBetweenDollarBTCPaymentsRequest.getBaseId()), "Balance");
        if (!baseOTCCurrencyPaymentBalanceFolder.isDirectory()) {
            super.response = "BASE DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        File targetOTCCurrencyPaymentBalanceFolder = new File(BankersFolderLocator.getPaymentCurrencyFolder(bankerTransferBetweenDollarBTCPaymentsRequest.getUserName(), bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency(), bankerTransferBetweenDollarBTCPaymentsRequest.getTargetId()), "Balance");
        if (!targetOTCCurrencyPaymentBalanceFolder.isDirectory()) {
            super.response = "TARGET DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        String substractToBalance = BaseOperation.substractToBalance(
                baseOTCCurrencyPaymentBalanceFolder,
                bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency(),
                bankerTransferBetweenDollarBTCPaymentsRequest.getAmount(),
                BalanceOperationType.DEBIT,
                BalanceOperationStatus.OK,
                "TRANSFER TO DOLLARBTC PAYMENT " + bankerTransferBetweenDollarBTCPaymentsRequest.getTargetId() + " - " + bankerTransferBetweenDollarBTCPaymentsRequest.getAdditionalInfo(),
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
                targetOTCCurrencyPaymentBalanceFolder,
                bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency(),
                bankerTransferBetweenDollarBTCPaymentsRequest.getAmount(),
                BalanceOperationType.CREDIT,
                BalanceOperationStatus.OK,
                "TRANSFER FROM DOLLARBTC PAYMENT " + bankerTransferBetweenDollarBTCPaymentsRequest.getBaseId() + " - " + bankerTransferBetweenDollarBTCPaymentsRequest.getAdditionalInfo(),
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }
        
}
