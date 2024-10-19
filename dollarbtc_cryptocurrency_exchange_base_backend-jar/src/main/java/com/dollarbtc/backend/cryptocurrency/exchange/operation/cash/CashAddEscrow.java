/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashAddEscrow extends AbstractOperation<String> {

    private final String currency, placeId, description;
    private final Double amount;

    public CashAddEscrow(String currency, Double amount, String placeId, String description) {
        super(String.class);
        this.currency = currency;
        this.placeId = placeId;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public void execute() {
        Double escrowPlaceLimit = new CashGetEscrowBalance(placeId, currency).getResponse();
        File cashPlaceEscrowBalanceFolder = CashFolderLocator.getPlaceEscrowBalanceFolder(placeId);
        if (BaseOperation.isLocked(cashPlaceEscrowBalanceFolder)) {
            super.response = "IS LOCKED";
            return;
        }
        try {
            BaseOperation.lock(cashPlaceEscrowBalanceFolder);
            boolean inLimitBalance = false;
            Logger.getLogger(CashAddEscrow.class.getName()).log(Level.INFO, new CashGetEscrowBalance(placeId, currency).getResponse().toString());
            Logger.getLogger(CashAddEscrow.class.getName()).log(Level.INFO, amount.toString());
            Logger.getLogger(CashAddEscrow.class.getName()).log(Level.INFO, escrowPlaceLimit.toString());
            if (new CashGetEscrowBalance(placeId, currency).getResponse() + amount <= escrowPlaceLimit) {
                inLimitBalance = true;
            }
            if (inLimitBalance) {
                String responsee = BaseOperation.addToBalance(
                        cashPlaceEscrowBalanceFolder,
                        currency,
                        amount,
                        BalanceOperationType.CASH_ADD_ESCROW,
                        BalanceOperationStatus.OK,
                        description,
                        null,
                        null,
                        true,
                        null
                );
                if (!responsee.contains("OK____")) {
                    super.response = responsee;
                    return;
                }
                super.response = responsee;
                return;
            }
            super.response = "DOES NOT IN LIMIT BALANCE";
        } finally {
            BaseOperation.unlock(cashPlaceEscrowBalanceFolder);
        }
    }

}
