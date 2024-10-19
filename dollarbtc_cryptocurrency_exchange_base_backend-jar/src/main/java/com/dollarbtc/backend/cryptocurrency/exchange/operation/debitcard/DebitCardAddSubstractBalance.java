/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardAddSubstractBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class DebitCardAddSubstractBalance extends AbstractOperation<String> {

    private final DebitCardAddSubstractBalanceRequest debitCardAddSubstractBalanceRequest;

    public DebitCardAddSubstractBalance(DebitCardAddSubstractBalanceRequest debitCardAddSubstractBalanceRequest) {
        super(String.class);
        this.debitCardAddSubstractBalanceRequest = debitCardAddSubstractBalanceRequest;
    }

    @Override
    protected void execute() {
        File substractFolder = UsersFolderLocator.getMCBalanceFolder(debitCardAddSubstractBalanceRequest.getUserName());
        if (!substractFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File addFolder = DebitCardsFolderLocator.getBalanceFolder(debitCardAddSubstractBalanceRequest.getId());
        if (!addFolder.isDirectory()) {
            super.response = "DEBIT CARD DOES NOT EXIST";
            return;
        }
        BalanceOperationType balanceOperationType = BalanceOperationType.DEBIT_CARD_ADD_BALANCE;
        if (debitCardAddSubstractBalanceRequest.getOperation().equals("SUBSTRACT")) {
            substractFolder = DebitCardsFolderLocator.getBalanceFolder(debitCardAddSubstractBalanceRequest.getId());
            addFolder = UsersFolderLocator.getMCBalanceFolder(debitCardAddSubstractBalanceRequest.getUserName());
            balanceOperationType = BalanceOperationType.DEBIT_CARD_SUBSTRACT_BALANCE;
        }
        String currency = "USD";
        String type = "MONEYCLICK";
        File debitCardConfigFile = DebitCardsFolderLocator.getConfigFile(debitCardAddSubstractBalanceRequest.getId());
        if (!debitCardConfigFile.isFile()) {
            super.response = "DEBIT CARD ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
            if (!debitCardConfig.has("number")) {
                super.response = "DEBIT CARD DOES NOT HAVE NUMBER";
                return;
            }
            if (debitCardConfig.has("currency")) {
                currency = debitCardConfig.get("currency").textValue();
            }
            if (debitCardConfig.has("type")) {
                type = debitCardConfig.get("type").textValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(DebitCardAddNumber.class.getName()).log(Level.SEVERE, null, ex);
        }
        BalanceOperationStatus substractBalanceOperationStatus = BalanceOperationStatus.OK;
        if (!type.equals("MONEYCLICK")) {
            substractBalanceOperationStatus = BalanceOperationStatus.PROCESSING;
        }
        String substract = BaseOperation.substractToBalance(
                substractFolder,
                currency,
                debitCardAddSubstractBalanceRequest.getAmount(),
                balanceOperationType,
                substractBalanceOperationStatus,
                null,
                null,
                false,
                null,
                false,
                null
        );
        if (!substract.equals("OK")) {
            super.response = substract;
            return;
        }
        if (!type.equals("MONEYCLICK")) {
            super.response = "OK";
            return;
        }
        BaseOperation.addToBalance(
                addFolder,
                currency,
                debitCardAddSubstractBalanceRequest.getAmount(),
                balanceOperationType,
                BalanceOperationStatus.OK,
                null,
                null,
                BaseOperation.getChargesNew(currency, debitCardAddSubstractBalanceRequest.getAmount(), balanceOperationType, null, "MONEYCLICK", null, null),
                false,
                null
        );
        super.response = "OK";
    }

}
