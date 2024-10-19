/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerAddBalanceToDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerAddBalanceToDollarBTCPayment extends AbstractOperation<String> {

    private final BankerAddBalanceToDollarBTCPaymentRequest bankerAddBalanceToDollarBTCPaymentRequest;

    public BankerAddBalanceToDollarBTCPayment(BankerAddBalanceToDollarBTCPaymentRequest bankerAddBalanceToDollarBTCPaymentRequest) {
        super(String.class);
        this.bankerAddBalanceToDollarBTCPaymentRequest = bankerAddBalanceToDollarBTCPaymentRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyPaymentFolder = BankersFolderLocator.getPaymentCurrencyFolder(bankerAddBalanceToDollarBTCPaymentRequest.getUserName(), bankerAddBalanceToDollarBTCPaymentRequest.getCurrency(), bankerAddBalanceToDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFolder.isDirectory()) {
            super.response = "PAYMENT ID DOES NOT EXIST";
            return;
        }
        BaseOperation.addToBalance(
                new File(otcCurrencyPaymentFolder, "Balance"),
                bankerAddBalanceToDollarBTCPaymentRequest.getCurrency(),
                bankerAddBalanceToDollarBTCPaymentRequest.getAmount(),
                BalanceOperationType.CREDIT,
                BalanceOperationStatus.OK,
                bankerAddBalanceToDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }

}
