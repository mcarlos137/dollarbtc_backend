/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashProcessOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashProcessOperation extends AbstractOperation<String> {

    private final CashProcessOperationRequest cashProcessOperationRequest;

    public CashProcessOperation(CashProcessOperationRequest cashProcessOperationRequest) {
        super(String.class);
        this.cashProcessOperationRequest = cashProcessOperationRequest;
    }

    @Override
    public void execute() {
        File cashOperationFile = new File(CashFolderLocator.getOperationsFolder(), cashProcessOperationRequest.getOperationId() + ".json");
        if (!cashOperationFile.isFile()) {
            super.response = "OPERATION ID DOES NOT EXIST";
            return;
        }
        File cashOperationsIndexFile = new File(new File(CashFolderLocator.getOperationsIndexFolder("Statuses"), CashOperationStatus.PROCESSING.name()), cashProcessOperationRequest.getOperationId() + ".json");
        if (!cashOperationsIndexFile.isFile()) {
            super.response = "OPERATION ID IS NOT IN PROCESSING STATUS";
            return;
        }
        File cashOperationsIndexesStatusFolder = FileUtil.createFolderIfNoExist(new File(CashFolderLocator.getOperationsIndexFolder("Statuses"), cashProcessOperationRequest.getCashOperationStatus().name()));
        try {
            JsonNode cashOperation = mapper.readTree(cashOperationFile);
            String placeId = cashOperation.get("placeId").textValue();
            String userName = cashOperation.get("userName").textValue();
            String currency = cashOperation.get("currency").textValue();
            Double amount = cashOperation.get("amount").doubleValue();
            CashOperationType cashOperationType = CashOperationType.valueOf(cashOperation.get("type").textValue());
            if (cashProcessOperationRequest.getCashOperationStatus().equals(CashOperationStatus.CANCELED)) {
                String escrowBalanceFileName = cashOperation.get("escrowBalanceFileName").textValue();
                BaseOperation.changeBalanceOperationStatus(new File(CashFolderLocator.getPlaceEscrowBalanceFolder(placeId), escrowBalanceFileName), BalanceOperationStatus.FAIL, null, null, cashProcessOperationRequest.getCanceledReason());
                switch (cashOperationType) {
                    case BUY:
                        String mcUserBalanceFileNameBuy = cashOperation.get("mcUserBalanceFileName").textValue();
                        BaseOperation.changeBalanceOperationStatus(new File(UsersFolderLocator.getMCBalanceFolder(userName), mcUserBalanceFileNameBuy), BalanceOperationStatus.FAIL, null, null, cashProcessOperationRequest.getCanceledReason());
                        break;
                    case SELL:
                        String mcUserBalanceFileNameSell = cashOperation.get("mcUserBalanceFileName").textValue();
                        BaseOperation.changeBalanceOperationStatus(new File(UsersFolderLocator.getMCBalanceFolder(userName), mcUserBalanceFileNameSell), BalanceOperationStatus.FAIL, null, null, cashProcessOperationRequest.getCanceledReason());
                        break;
                }
            }
            if (cashProcessOperationRequest.getCashOperationStatus().equals(CashOperationStatus.SUCCESS)) {
                switch (cashOperationType) {
                    case BUY:
                        File cashPlaceBalance;
                        if (cashProcessOperationRequest.isCash()) {
                            cashPlaceBalance = CashFolderLocator.getPlaceBalanceCashFolder(placeId);
                        } else {
                            cashPlaceBalance = CashFolderLocator.getPlaceBalanceNoCashFolder(placeId);
                        }
                        String result1 = BaseOperation.addToBalance(
                                cashPlaceBalance,
                                currency,
                                amount,
                                BalanceOperationType.CASH_BUY,
                                BalanceOperationStatus.OK,
                                "BUY CASH USER " + userName,
                                null,
                                null,
                                true,
                                null
                        );
                        if (!result1.contains("OK____")) {
                            super.response = result1;
                            return;
                        }
                        String cashPlaceBalanceFileName = result1.split("____")[1];
                        ((ObjectNode) cashOperation).put("cash", true);
                        ((ObjectNode) cashOperation).put("cashPlaceBalanceFileName", cashPlaceBalanceFileName);
                        String mcUserBalanceFileName = cashOperation.get("mcUserBalanceFileName").textValue();
                        BaseOperation.changeBalanceOperationStatus(new File(UsersFolderLocator.getMCBalanceFolder(userName), mcUserBalanceFileName), BalanceOperationStatus.OK, null, null, cashProcessOperationRequest.getCanceledReason());
                        break;
                    case SELL:
                        String result2 = BaseOperation.substractToBalance(
                                CashFolderLocator.getPlaceBalanceCashFolder(placeId),
                                currency,
                                amount,
                                BalanceOperationType.CASH_SELL,
                                BalanceOperationStatus.OK,
                                "SELL CASH USER " + userName,
                                null,
                                false,
                                null,
                                true,
                                null
                        );
                        if (!result2.contains("OK____")) {
                            super.response = result2;
                            return;
                        }
                        String cashPlaceBalanceCashFileName = result2.split("____")[1];
                        ((ObjectNode) cashOperation).put("cash", true);
                        ((ObjectNode) cashOperation).put("cashPlaceBalanceCashFileName", cashPlaceBalanceCashFileName);
                        break;
                }
            }
            ((ObjectNode) cashOperation).put("status", cashProcessOperationRequest.getCashOperationStatus().name());
            FileUtil.editFile(cashOperation, cashOperationFile);
            FileUtil.moveFileToFolder(cashOperationsIndexFile, cashOperationsIndexesStatusFolder);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(CashProcessOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
