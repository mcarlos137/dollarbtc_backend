/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCSubstractBalanceToDollarBTCPaymentRequest;
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
public class OTCSubstractBalanceToDollarBTCPayment extends AbstractOperation<String> {
    
    private final OTCSubstractBalanceToDollarBTCPaymentRequest otcSubstractBalanceToDollarBTCPaymentRequest;

    public OTCSubstractBalanceToDollarBTCPayment(OTCSubstractBalanceToDollarBTCPaymentRequest otcSubstractBalanceToDollarBTCPaymentRequest) {
        super(String.class);
        this.otcSubstractBalanceToDollarBTCPaymentRequest = otcSubstractBalanceToDollarBTCPaymentRequest;
    }
        
    @Override
    public void execute() {
        File otcCurrencyPaymentFolder = OTCFolderLocator.getCurrencyPaymentFolder(null, otcSubstractBalanceToDollarBTCPaymentRequest.getCurrency(), otcSubstractBalanceToDollarBTCPaymentRequest.getId());
        if (!otcCurrencyPaymentFolder.isDirectory()) {
            super.response = "PAYMENT ID DOES NOT EXIST";
            return;
        }
        String substractToBalance = BaseOperation.substractToBalance(
                new File(otcCurrencyPaymentFolder, "Balance"),
                otcSubstractBalanceToDollarBTCPaymentRequest.getCurrency(),
                otcSubstractBalanceToDollarBTCPaymentRequest.getAmount(),
                BalanceOperationType.DEBIT,
                BalanceOperationStatus.OK,
                otcSubstractBalanceToDollarBTCPaymentRequest.getAdditionalInfo(),
                null,
                false,
                null,
                false,
                null
        );
        if (substractToBalance.equals("OK")) {
            if (otcSubstractBalanceToDollarBTCPaymentRequest.isCompensateMoneyclick()) {
                BaseOperation.addToBalance(
                        MoneyclickFolderLocator.getBalanceFolder(OPERATOR_NAME),
                        otcSubstractBalanceToDollarBTCPaymentRequest.getCurrency(),
                        otcSubstractBalanceToDollarBTCPaymentRequest.getAmount(),
                        BalanceOperationType.CREDIT,
                        BalanceOperationStatus.OK,
                        otcSubstractBalanceToDollarBTCPaymentRequest.getAdditionalInfo(),
                        null,
                        null,
                        false,
                        null
                );
            }
            super.response = "OK";
        } else {
            super.response = substractToBalance;
        }
    }
    
}
