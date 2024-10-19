/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewAddEscrow extends AbstractOperation<String> {

    private final String currency, retailId, description;
    private final Double amount;

    public MCRetailNewAddEscrow(String currency, Double amount, String retailId, String description) {
        super(String.class);
        this.currency = currency;
        this.retailId = retailId;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public void execute() {
        Double escrowRetailLimit = new MCRetailNewGetEscrowBalance(retailId, currency).getResponse();
        File moneyclickRetailEscrowBalanceFolder = MoneyclickFolderLocator.getRetailEscrowBalanceFolder(retailId);
        if (BaseOperation.isLocked(moneyclickRetailEscrowBalanceFolder)) {
            super.response = "IS LOCKED";
            return;
        }
        try {
            BaseOperation.lock(moneyclickRetailEscrowBalanceFolder);
            boolean inLimitBalance = false;
            Logger.getLogger(MCRetailNewAddEscrow.class.getName()).log(Level.INFO, new MCRetailNewGetEscrowBalance(retailId, currency).getResponse().toString());
            Logger.getLogger(MCRetailNewAddEscrow.class.getName()).log(Level.INFO, amount.toString());
            Logger.getLogger(MCRetailNewAddEscrow.class.getName()).log(Level.INFO, escrowRetailLimit.toString());
            if (new MCRetailNewGetEscrowBalance(retailId, currency).getResponse() + amount <= escrowRetailLimit) {
                inLimitBalance = true;
            }
            if (inLimitBalance) {
                String responsee = BaseOperation.addToBalance(
                        moneyclickRetailEscrowBalanceFolder,
                        currency,
                        amount,
                        BalanceOperationType.MC_ADD_ESCROW,
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
            BaseOperation.unlock(moneyclickRetailEscrowBalanceFolder);
        }
    }

}
