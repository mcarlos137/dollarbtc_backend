/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminTransferBetweenDollarBTCPaymentsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminTransferBetweenDollarBTCPayments extends AbstractOperation<String> {
    
    private final OTCAdminTransferBetweenDollarBTCPaymentsRequest otcAdminTransferBetweenDollarBTCPaymentsRequest;

    public OTCAdminTransferBetweenDollarBTCPayments(OTCAdminTransferBetweenDollarBTCPaymentsRequest otcAdminTransferBetweenDollarBTCPaymentsRequest) {
        super(String.class);
        this.otcAdminTransferBetweenDollarBTCPaymentsRequest = otcAdminTransferBetweenDollarBTCPaymentsRequest;
    }

    @Override
    protected void execute() {
        File baseOTCCurrencyPaymentBalanceFolder = new File(OTCFolderLocator.getCurrencyPaymentFolder(null, otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency(), otcAdminTransferBetweenDollarBTCPaymentsRequest.getBaseId()), "Balance");
        if (!baseOTCCurrencyPaymentBalanceFolder.isDirectory()) {
            super.response = "BASE DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        File targetOTCCurrencyPaymentBalanceFolder = new File(OTCFolderLocator.getCurrencyPaymentFolder(null, otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency(), otcAdminTransferBetweenDollarBTCPaymentsRequest.getTargetId()), "Balance");
        if (!targetOTCCurrencyPaymentBalanceFolder.isDirectory()) {
            super.response = "TARGET DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        String substractToBalance = BaseOperation.substractToBalance(
                baseOTCCurrencyPaymentBalanceFolder,
                otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency(),
                otcAdminTransferBetweenDollarBTCPaymentsRequest.getAmount(),
                BalanceOperationType.DEBIT,
                BalanceOperationStatus.OK,
                "TRANSFER TO DOLLARBTC PAYMENT " + otcAdminTransferBetweenDollarBTCPaymentsRequest.getTargetId() + " - " + otcAdminTransferBetweenDollarBTCPaymentsRequest.getAdditionalInfo(),
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
                otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency(),
                otcAdminTransferBetweenDollarBTCPaymentsRequest.getAmount(),
                BalanceOperationType.CREDIT,
                BalanceOperationStatus.OK,
                "TRANSFER FROM DOLLARBTC PAYMENT " + otcAdminTransferBetweenDollarBTCPaymentsRequest.getBaseId() + " - " + otcAdminTransferBetweenDollarBTCPaymentsRequest.getAdditionalInfo(),
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }
        
}
