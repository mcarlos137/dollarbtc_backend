/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashAddEscrowFromMCUserBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
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
public class CashAddEscrowFromMCUserBalance extends AbstractOperation<String> {

    private final CashAddEscrowFromMCUserBalanceRequest cashAddEscrowFromMCUserBalanceRequest;

    public CashAddEscrowFromMCUserBalance(CashAddEscrowFromMCUserBalanceRequest cashAddEscrowFromMCUserBalanceRequest) {
        super(String.class);
        this.cashAddEscrowFromMCUserBalanceRequest = cashAddEscrowFromMCUserBalanceRequest;
    }

    @Override
    public void execute() {
        boolean currencyAllowed = false;
        try {
            File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(cashAddEscrowFromMCUserBalanceRequest.getPlaceId());
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            Iterator<JsonNode> cashPlaceConfigCurrenciesIterator = cashPlaceConfig.get("currencies").iterator();
            while (cashPlaceConfigCurrenciesIterator.hasNext()) {
                JsonNode cashPlaceConfigCurrenciesIt = cashPlaceConfigCurrenciesIterator.next();
                if (cashPlaceConfigCurrenciesIt.textValue().equals(cashAddEscrowFromMCUserBalanceRequest.getCurrency())) {
                    currencyAllowed = true;
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CashAddEscrowFromMCUserBalance.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!currencyAllowed) {
            super.response = "CURRENCY NOT ALLOWED";
            return;
        }
        Double escrowPlaceLimit = new CashGetEscrowPlaceLimit(cashAddEscrowFromMCUserBalanceRequest.getPlaceId(), cashAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse();
        File cashPlaceEscrowBalanceFolder = CashFolderLocator.getPlaceEscrowBalanceFolder(cashAddEscrowFromMCUserBalanceRequest.getPlaceId());
        File cashPlaceEscrowFromToUserBalanceFolder = CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(cashAddEscrowFromMCUserBalanceRequest.getPlaceId());
        if (BaseOperation.isLocked(cashPlaceEscrowBalanceFolder)) {
            super.response = "IS LOCKED";
            return;
        }
        if (BaseOperation.isLocked(cashPlaceEscrowFromToUserBalanceFolder)) {
            super.response = "IS LOCKED";
            return;
        }
        try {
            BaseOperation.lock(cashPlaceEscrowBalanceFolder);
            BaseOperation.lock(cashPlaceEscrowFromToUserBalanceFolder);
            Logger.getLogger(CashAddEscrowFromMCUserBalance.class.getName()).log(Level.INFO, new CashGetEscrowBalance(cashAddEscrowFromMCUserBalanceRequest.getPlaceId(), cashAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse().toString());
            Logger.getLogger(CashAddEscrowFromMCUserBalance.class.getName()).log(Level.INFO, cashAddEscrowFromMCUserBalanceRequest.getAmount().toString());
            Logger.getLogger(CashAddEscrowFromMCUserBalance.class.getName()).log(Level.INFO, escrowPlaceLimit.toString());
            boolean inEscrowLimitBalance = false;
            if (new CashGetEscrowBalance(cashAddEscrowFromMCUserBalanceRequest.getPlaceId(), cashAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse() + cashAddEscrowFromMCUserBalanceRequest.getAmount() <= escrowPlaceLimit) {
                inEscrowLimitBalance = true;
            }
            System.out.println("" + new CashGetEscrowBalance(cashAddEscrowFromMCUserBalanceRequest.getPlaceId(), cashAddEscrowFromMCUserBalanceRequest.getCurrency()).getResponse());
            if (inEscrowLimitBalance) {
                String responsee = BaseOperation.substractToBalance(
                        UsersFolderLocator.getMCBalanceFolder(cashAddEscrowFromMCUserBalanceRequest.getUserName()),
                        cashAddEscrowFromMCUserBalanceRequest.getCurrency(),
                        cashAddEscrowFromMCUserBalanceRequest.getAmount(),
                        BalanceOperationType.CASH_ADD_ESCROW,
                        BalanceOperationStatus.OK,
                        "ADDED TO PLACE ESCROW " + cashAddEscrowFromMCUserBalanceRequest.getPlaceId(),
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
                        cashPlaceEscrowFromToUserBalanceFolder,
                        cashAddEscrowFromMCUserBalanceRequest.getCurrency(),
                        cashAddEscrowFromMCUserBalanceRequest.getAmount(),
                        BalanceOperationType.MC_ADD_ESCROW,
                        BalanceOperationStatus.OK,
                        "ADDED TO PLACE ESCROW " + cashAddEscrowFromMCUserBalanceRequest.getPlaceId(),
                        null,
                        null,
                        false,
                        null
                );
                return;
            }
            super.response = "DOES NOT IN LIMIT BALANCE";
        } finally {
            BaseOperation.unlock(cashPlaceEscrowBalanceFolder);
            BaseOperation.unlock(cashPlaceEscrowFromToUserBalanceFolder);
        }
    }

}
