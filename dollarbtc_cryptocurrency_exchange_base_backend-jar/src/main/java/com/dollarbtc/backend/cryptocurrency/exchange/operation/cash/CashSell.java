/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashSellRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class CashSell extends AbstractOperation<String> {

    private final CashSellRequest cashSellRequest;

    public CashSell(CashSellRequest cashSellRequest) {
        super(String.class);
        this.cashSellRequest = cashSellRequest;
    }

    @Override
    public void execute() {
        File userFolder = UsersFolderLocator.getFolder(cashSellRequest.getUserName());
        if (!userFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File cashPlaceFolder = CashFolderLocator.getPlaceFolder(cashSellRequest.getPlaceId());
        if (!cashPlaceFolder.isDirectory()) {
            super.response = "CASH PLACE DOES NOT EXIST";
            return;
        }
        String inLimits = BaseOperation.inLimits(cashSellRequest.getUserName(), cashSellRequest.getCurrency(), cashSellRequest.getAmount(), BalanceOperationType.CASH_SELL);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String response1 = new CashSubstractEscrow(
                cashSellRequest.getCurrency(),
                cashSellRequest.getAmount(),
                cashSellRequest.getPlaceId(),
                "SELL CASH USER " + cashSellRequest.getUserName()).getResponse();
        if (!response1.contains("OK____")) {
            super.response = response1;
            return;
        }
        String escrowBalanceFileName = response1.split("____")[1];
        String response2 = BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(cashSellRequest.getUserName()),
                cashSellRequest.getCurrency(),
                cashSellRequest.getAmount(),
                BalanceOperationType.CASH_SELL,
                BalanceOperationStatus.PROCESSING,
                "SELL CASH PLACE " + cashSellRequest.getPlaceId(),
                null,
                BaseOperation.getChargesNew(cashSellRequest.getCurrency(), cashSellRequest.getAmount(), BalanceOperationType.CASH_SELL, null, "CASH_PLACE__" + cashSellRequest.getPlaceId(), null, null),
                true,
                null
        );
        if (!response2.contains("OK____")) {
            BaseOperation.changeBalanceOperationStatus(
                    new File(CashFolderLocator.getPlaceEscrowBalanceFolder(cashSellRequest.getPlaceId()), escrowBalanceFileName),
                    BalanceOperationStatus.FAIL, null, null, null);
            super.response = response2;
            return;
        }
        String mcUserBalanceFileName = response2.split("____")[1];
        File cashOperationsFolder = CashFolderLocator.getOperationsFolder();
        JsonNode cashOperation = new ObjectMapper().createObjectNode();
        String id = BaseOperation.getId();
        ((ObjectNode) cashOperation).put("id", id);
        String[] securityImageUrlAndName = BaseOperation.getSecurityImageUrlAndName();
        ((ObjectNode) cashOperation).put("securityImageUrl", securityImageUrlAndName[0]);
        ((ObjectNode) cashOperation).put("securityImageNameES", securityImageUrlAndName[1]);
        ((ObjectNode) cashOperation).put("securityImageNameEN", securityImageUrlAndName[2]);
        ((ObjectNode) cashOperation).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) cashOperation).put("placeId", cashSellRequest.getPlaceId());
        ((ObjectNode) cashOperation).put("cashOperationStatus", CashOperationStatus.PROCESSING.name());
        ((ObjectNode) cashOperation).put("cashOperationType", CashOperationType.SELL.name());
        ((ObjectNode) cashOperation).put("escrowBalanceFileName", escrowBalanceFileName);
        ((ObjectNode) cashOperation).put("mcUserBalanceFileName", mcUserBalanceFileName);
        FileUtil.createFile(cashSellRequest.toJsonNode(cashOperation),
                new File(cashOperationsFolder, id + ".json"));
        addOperationIndexes(id, cashSellRequest.getUserName(), cashSellRequest.getCurrency(), cashSellRequest.getPlaceId(), CashOperationType.SELL);
        // add commisions to retail 2%
        super.response = "OK";
    }

    static void addOperationIndexes(String id, String userName, String currency, String placeId, CashOperationType cashOperationType) {
        JsonNode cashOperationIndex = new ObjectMapper().createObjectNode();
        ((ObjectNode) cashOperationIndex).put("id", id);
        ((ObjectNode) cashOperationIndex).put("timestamp", DateUtil.getCurrentDate());
        //UserNames
        File cashOperationIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(
                        new File(CashFolderLocator.getOperationsIndexesFolder(), "UserNames")),
                        userName));
        FileUtil.createFile(cashOperationIndex, new File(cashOperationIndexFolder, id + ".json"));
        //Currencies
        cashOperationIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(
                        new File(CashFolderLocator.getOperationsIndexesFolder(), "Currencies")),
                        currency));
        FileUtil.createFile(cashOperationIndex, new File(cashOperationIndexFolder, id + ".json"));
        //Places
        cashOperationIndexFolder = FileUtil.createFolderIfNoExist(new File(
                FileUtil.createFolderIfNoExist(
                        new File(CashFolderLocator.getOperationsIndexesFolder(), "Places")), placeId));
        FileUtil.createFile(cashOperationIndex, new File(cashOperationIndexFolder, id + ".json"));
        //Types
        cashOperationIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(
                        new File(CashFolderLocator.getOperationsIndexesFolder(), "Types")), cashOperationType.name()));
        FileUtil.createFile(cashOperationIndex, new File(cashOperationIndexFolder, id + ".json"));
        //Statuses
        cashOperationIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(
                        new File(CashFolderLocator.getOperationsIndexesFolder(), "Statuses")), CashOperationStatus.PROCESSING.name()));
        FileUtil.createFile(cashOperationIndex, new File(cashOperationIndexFolder, id + ".json"));
    }

}
