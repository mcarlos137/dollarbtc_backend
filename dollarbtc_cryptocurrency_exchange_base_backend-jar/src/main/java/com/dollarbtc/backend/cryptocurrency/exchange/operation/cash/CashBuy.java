/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashBuyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashSell.addOperationIndexes;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class CashBuy extends AbstractOperation<String> {
    
    private final CashBuyRequest cashBuyRequest;

    public CashBuy(CashBuyRequest cashBuyRequest) {
        super(String.class);
        this.cashBuyRequest = cashBuyRequest;
    }
    
    @Override
    public void execute() {
        File userFolder = UsersFolderLocator.getFolder(cashBuyRequest.getUserName());
        if (!userFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File cashPlaceFolder = CashFolderLocator.getPlaceFolder(cashBuyRequest.getPlaceId());
        if (!cashPlaceFolder.isDirectory()) {
            super.response = "CASH PLACE DOES NOT EXIST";
            return;
        }
        String inLimits = BaseOperation.inLimits(cashBuyRequest.getUserName(), cashBuyRequest.getCurrency(), cashBuyRequest.getAmount(), BalanceOperationType.CASH_BUY);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String response1 = new CashAddEscrow(cashBuyRequest.getCurrency(),
                cashBuyRequest.getAmount(), cashBuyRequest.getPlaceId(),
                "BUY CASH USER " + cashBuyRequest.getUserName()).getResponse();
        if (!response1.contains("OK____")) {
            super.response = response1;
            return;
        }
        String escrowBalanceFileName = response1.split("____")[1];
        JsonNode charges = BaseOperation.getChargesNew(cashBuyRequest.getCurrency(), cashBuyRequest.getAmount(), BalanceOperationType.CASH_BUY, null, "CASH_PLACE__" + cashBuyRequest.getPlaceId(), null, null);
        Double amount = cashBuyRequest.getAmount();
        Iterator<JsonNode> chargesIterator = charges.iterator();
        while (chargesIterator.hasNext()) {
            JsonNode chargesIt = chargesIterator.next();
            if (cashBuyRequest.getCurrency().equals(chargesIt.get("currency").textValue())) {
                amount = amount + chargesIt.get("amount").doubleValue();
            }
        }
        String response2 = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(cashBuyRequest.getUserName()),
                cashBuyRequest.getCurrency(),
                amount,
                BalanceOperationType.CASH_BUY, BalanceOperationStatus.OK,
                "BUY CASH PLACE " + cashBuyRequest.getPlaceId(),
                null,
                false,
                charges,
                true,
                null
        );
        if (!response2.contains("OK____")) {
            BaseOperation.changeBalanceOperationStatus(
                    new File(CashFolderLocator.getPlaceEscrowBalanceFolder(cashBuyRequest.getPlaceId()),
                            escrowBalanceFileName),
                    BalanceOperationStatus.FAIL, null, null, null);
            super.response = response2;
            return;
        }
        String mcUserBalanceFileName = response2.split("____")[1];
        File cashOperationsFolder = CashFolderLocator.getOperationsFolder();
        JsonNode cashOperation = new ObjectMapper().createObjectNode();
        String id = BaseOperation.getId();
        ((ObjectNode) cashOperation).put("id", id);
        ((ObjectNode) cashOperation).put("securityPin", ((int) (Math.random() * 9000) + 1000));
        String[] securityImageUrlAndName = BaseOperation.getSecurityImageUrlAndName();
        ((ObjectNode) cashOperation).put("securityImageUrl", securityImageUrlAndName[0]);
        ((ObjectNode) cashOperation).put("securityImageNameES", securityImageUrlAndName[1]);
        ((ObjectNode) cashOperation).put("securityImageNameEN", securityImageUrlAndName[2]);
        ((ObjectNode) cashOperation).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) cashOperation).put("placeId", cashBuyRequest.getPlaceId());
        ((ObjectNode) cashOperation).put("status", CashOperationStatus.PROCESSING.name());
        ((ObjectNode) cashOperation).put("type", CashOperationType.BUY.name());
        ((ObjectNode) cashOperation).put("escrowBalanceFileName", escrowBalanceFileName);
        ((ObjectNode) cashOperation).put("mcUserBalanceFileName", mcUserBalanceFileName);
        ((ObjectNode) cashOperation).set("charges", charges);
        FileUtil.createFile(cashBuyRequest.toJsonNode(cashOperation), new File(cashOperationsFolder, id + ".json"));
        addOperationIndexes(id, cashBuyRequest.getUserName(), cashBuyRequest.getCurrency(), cashBuyRequest.getPlaceId(), CashOperationType.BUY);
        // add commisions to retail 3%
        super.response = "OK";
    }
        
}
