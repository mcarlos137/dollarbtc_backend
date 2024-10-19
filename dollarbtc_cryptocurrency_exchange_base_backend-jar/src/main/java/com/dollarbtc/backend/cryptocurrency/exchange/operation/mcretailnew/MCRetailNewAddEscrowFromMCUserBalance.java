/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewAddEscrowFromMCUserBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewAddEscrowFromMCUserBalance extends AbstractOperation<String> {

    private final MCRetailNewAddEscrowFromMCUserBalanceRequest mcRetailNewAddEscrowFromMCUserBalanceRequest;

    public MCRetailNewAddEscrowFromMCUserBalance(MCRetailNewAddEscrowFromMCUserBalanceRequest mcRetailNewAddEscrowFromMCUserBalanceRequest) {
        super(String.class);
        this.mcRetailNewAddEscrowFromMCUserBalanceRequest = mcRetailNewAddEscrowFromMCUserBalanceRequest;
    }

    @Override
    public void execute() {
        boolean currencyAllowed = false;
        try {
            File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId());
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            Iterator<JsonNode> moneyclickRetailConfigCurrenciesIterator = moneyclickRetailConfig.get("currencies").iterator();
            while (moneyclickRetailConfigCurrenciesIterator.hasNext()) {
                JsonNode moneyclickRetailConfigCurrenciesIt = moneyclickRetailConfigCurrenciesIterator.next();
                if (moneyclickRetailConfigCurrenciesIt.textValue().equals(mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency())) {
                    currencyAllowed = true;
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewAddEscrowFromMCUserBalance.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!currencyAllowed) {
            super.response = "CURRENCY NOT ALLOWED";
            return;
        }
        Double escrowRetailLimit = new MCRetailNewGetEscrowRetailLimit(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId(), mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse();
        File moneyclickRetailEscrowBalanceFolder = MoneyclickFolderLocator.getRetailEscrowBalanceFolder(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId());
        File moneyclickRetailEscrowFromToUserBalanceFolder = MoneyclickFolderLocator.getRetailEscrowBalanceFromToUserFolder(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId());
        if (BaseOperation.isLocked(moneyclickRetailEscrowBalanceFolder)) {
            super.response = "IS LOCKED";
            return;
        }
        if (BaseOperation.isLocked(moneyclickRetailEscrowFromToUserBalanceFolder)) {
            super.response = "IS LOCKED";
            return;
        }
        try {
            BaseOperation.lock(moneyclickRetailEscrowBalanceFolder);
            BaseOperation.lock(moneyclickRetailEscrowFromToUserBalanceFolder);
            Logger.getLogger(MCRetailNewAddEscrowFromMCUserBalance.class.getName()).log(Level.INFO, new MCRetailNewGetEscrowBalance(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId(), mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse().toString());
            Logger.getLogger(MCRetailNewAddEscrowFromMCUserBalance.class.getName()).log(Level.INFO, mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount().toString());
            Logger.getLogger(MCRetailNewAddEscrowFromMCUserBalance.class.getName()).log(Level.INFO, escrowRetailLimit.toString());
            boolean inEscrowLimitBalance = false;
            if (new MCRetailNewGetEscrowBalance(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId(), mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse() + mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount() <= escrowRetailLimit) {
                inEscrowLimitBalance = true;
            }
            System.out.println("" + new MCRetailNewGetEscrowBalance(mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId(), mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse());
            if (inEscrowLimitBalance) {
                String responsee = BaseOperation.substractToBalance(
                        UsersFolderLocator.getMCBalanceFolder(mcRetailNewAddEscrowFromMCUserBalanceRequest.getUserName()),
                        mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency(),
                        mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount(),
                        BalanceOperationType.MC_ADD_ESCROW,
                        BalanceOperationStatus.OK,
                        "ADDED TO RETAIL ESCROW " + mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId(),
                        null,
                        false,
                        null,
                        false,
                        null
                );
                if (!responsee.equals("OK")) {
                    super.response = responsee;
                    return;
                }
                super.response = BaseOperation.addToBalance(
                        moneyclickRetailEscrowFromToUserBalanceFolder,
                        mcRetailNewAddEscrowFromMCUserBalanceRequest.getCurrency(),
                        mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount(),
                        BalanceOperationType.MC_ADD_ESCROW,
                        BalanceOperationStatus.OK,
                        "ADDED TO RETAIL ESCROW " + mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId(),
                        null,
                        null,
                        false,
                        null
                );
                return;
            }
            super.response = "DOES NOT IN LIMIT BALANCE";
        } finally {
            BaseOperation.unlock(moneyclickRetailEscrowBalanceFolder);
            BaseOperation.unlock(moneyclickRetailEscrowFromToUserBalanceFolder);
        }
    }

}
