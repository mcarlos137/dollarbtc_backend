/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminSendDollarBTCPaymentCommissionsToMoneyClick extends AbstractOperation<String> {

    private final OTCAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest;

    public OTCAdminSendDollarBTCPaymentCommissionsToMoneyClick(OTCAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest) {
        super(String.class);
        this.otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest = otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest;
    }

    @Override
    protected void execute() {
        File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getTargetUserName());
        if (!userMCBalanceFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File currencyPaymentCommissionsBalanceFolder = OTCFolderLocator.getCurrencyPaymentCommissionsBalanceFolder(OPERATOR_NAME, otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getCurrency(), otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getId());
        if (!currencyPaymentCommissionsBalanceFolder.isDirectory()) {
            super.response = "PAYMENT DOES NOT EXIST";
            return;
        }
        String subtractBalance = BaseOperation.substractToBalance(
                currencyPaymentCommissionsBalanceFolder, 
                otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getCurrency(), 
                otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getAmount(), 
                BalanceOperationType.DOLLARBTC_PAYMENT_COMMISSIONS_TO_MONEYCLICK, 
                BalanceOperationStatus.OK, 
                OPERATOR_NAME, 
                Double.NaN, 
                false, 
                null, 
                true, 
                null
        );
        if(!subtractBalance.equals("OK")){
            super.response = subtractBalance;
            return;
        }
        BaseOperation.addToBalance(
                userMCBalanceFolder, 
                otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getCurrency(), 
                otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getAmount(), 
                BalanceOperationType.DOLLARBTC_PAYMENT_COMMISSIONS_TO_MONEYCLICK, 
                BalanceOperationStatus.OK, 
                null, 
                null, 
                null, 
                false, 
                null
        );
        super.response = "OK";
    }

}
