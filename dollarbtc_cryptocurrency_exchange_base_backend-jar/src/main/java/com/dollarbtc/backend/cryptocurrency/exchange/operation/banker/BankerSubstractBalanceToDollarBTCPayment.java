/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerSubstractBalanceToDollarBTCPaymentRequest;
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
public class BankerSubstractBalanceToDollarBTCPayment extends AbstractOperation<String> {

    private final BankerSubstractBalanceToDollarBTCPaymentRequest bankerSubstractBalanceToDollarBTCPaymentRequest;

    public BankerSubstractBalanceToDollarBTCPayment(BankerSubstractBalanceToDollarBTCPaymentRequest bankerSubstractBalanceToDollarBTCPaymentRequest) {
        super(String.class);
        this.bankerSubstractBalanceToDollarBTCPaymentRequest = bankerSubstractBalanceToDollarBTCPaymentRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyPaymentFolder = BankersFolderLocator.getPaymentCurrencyFolder(bankerSubstractBalanceToDollarBTCPaymentRequest.getUserName(), bankerSubstractBalanceToDollarBTCPaymentRequest.getCurrency(), bankerSubstractBalanceToDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFolder.isDirectory()) {
            super.response = "PAYMENT ID DOES NOT EXIST";
            return;
        }
        String substractToBalance = BaseOperation.substractToBalance(
                new File(otcCurrencyPaymentFolder, "Balance"),
                bankerSubstractBalanceToDollarBTCPaymentRequest.getCurrency(),
                bankerSubstractBalanceToDollarBTCPaymentRequest.getAmount(),
                BalanceOperationType.DEBIT,
                BalanceOperationStatus.OK,
                bankerSubstractBalanceToDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                false,
                null,
                false,
                null
        );
        super.response = substractToBalance;
    }

}
