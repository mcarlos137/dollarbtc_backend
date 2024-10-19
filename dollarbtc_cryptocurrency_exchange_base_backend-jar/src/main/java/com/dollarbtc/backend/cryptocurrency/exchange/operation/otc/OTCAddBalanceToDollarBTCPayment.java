/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddBalanceToDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAddBalanceToDollarBTCPayment extends AbstractOperation<String> {

    private final OTCAddBalanceToDollarBTCPaymentRequest otcAddBalanceToDollarBTCPaymentRequest;

    public OTCAddBalanceToDollarBTCPayment(OTCAddBalanceToDollarBTCPaymentRequest otcAddBalanceToDollarBTCPaymentRequest) {
        super(String.class);
        this.otcAddBalanceToDollarBTCPaymentRequest = otcAddBalanceToDollarBTCPaymentRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyPaymentFolder = OTCFolderLocator.getCurrencyPaymentFolder(null, otcAddBalanceToDollarBTCPaymentRequest.getCurrency(), otcAddBalanceToDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFolder.isDirectory()) {
            super.response = "PAYMENT ID DOES NOT EXIST";
            return;
        }
        BaseOperation.addToBalance(
                new File(otcCurrencyPaymentFolder, "Balance"),
                otcAddBalanceToDollarBTCPaymentRequest.getCurrency(),
                otcAddBalanceToDollarBTCPaymentRequest.getAmount(),
                BalanceOperationType.CREDIT,
                BalanceOperationStatus.OK,
                otcAddBalanceToDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                null,
                false,
                null
        );
        if (otcAddBalanceToDollarBTCPaymentRequest.isCompensateMoneyclick()) {
            BaseOperation.substractToBalance(
                    MoneyclickFolderLocator.getBalanceFolder(OPERATOR_NAME),
                    otcAddBalanceToDollarBTCPaymentRequest.getCurrency(),
                    otcAddBalanceToDollarBTCPaymentRequest.getAmount(),
                    BalanceOperationType.DEBIT,
                    BalanceOperationStatus.OK,
                    otcAddBalanceToDollarBTCPaymentRequest.getAdditionalInfo(),
                    null,
                    true,
                    null,
                    false,
                    null
            );
        }
        super.response = "OK";
    }

}
