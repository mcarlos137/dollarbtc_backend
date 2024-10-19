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
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author carlosmolina
 */
public class CashSubstractEscrow extends AbstractOperation<String> {
    
    private final String currency, placeId, description;
    private final Double amount;

    public CashSubstractEscrow(String currency, Double amount, String placeId, String description) {
        super(String.class);
        this.currency = currency;
        this.placeId = placeId;
        this.description = description;
        this.amount = amount;
    }
    
    @Override
    public void execute() {
        super.response = substractToBalanceEscrowReturnFileName(placeId, currency, amount, BalanceOperationStatus.OK, description, null);
    }

    private static String substractToBalanceEscrowReturnFileName(String placeId, String currency, Double amount, BalanceOperationStatus balanceOperationStatus, String additionalInfo, Double btcPrice) {
        if (BaseOperation.isLocked(CashFolderLocator.getPlaceEscrowBalanceFolder(placeId))) {
            return "IS LOCKED";
        }
        if (BaseOperation.isLocked(CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId))) {
            BaseOperation.unlock(CashFolderLocator.getPlaceEscrowBalanceFolder(placeId));
            return "IS LOCKED";
        }
        try {
            BaseOperation.lock(CashFolderLocator.getPlaceEscrowBalanceFolder(placeId));
            BaseOperation.lock(CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId));
            File[] cashPlaceEscrowBalanceFolders = new File[]{CashFolderLocator.getPlaceEscrowBalanceFolder(placeId), CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId)};
            Double availableAmount = 0.0;
            for (File cashPlaceEscrowBalanceFolder : cashPlaceEscrowBalanceFolders) {
                ArrayNode balance = BaseOperation.getBalance(cashPlaceEscrowBalanceFolder);
                Iterator<JsonNode> balanceIterator = balance.elements();
                while (balanceIterator.hasNext()) {
                    JsonNode balanceIt = balanceIterator.next();
                    if (balanceIt.get("currency").textValue().equals(currency)) {
                        availableAmount = availableAmount + balanceIt.get("amount").doubleValue();
                        break;
                    }
                }
            }
            boolean enoughBalance = false;
            if (availableAmount >= amount) {
                enoughBalance = true;
            }
            if (enoughBalance) {
                ObjectMapper mapper = new ObjectMapper();
                String timestamp = DateUtil.getFileDate(null);
                JsonNode substractToBalance = mapper.createObjectNode();
                ObjectNode amountNode = mapper.createObjectNode();
                amountNode.put("amount", amount);
                amountNode.put("currency", currency);
                ((ObjectNode) substractToBalance).put("timestamp", timestamp);
                ((ObjectNode) substractToBalance).set("substractedAmount", amountNode);
                ((ObjectNode) substractToBalance).put("balanceOperationType", BalanceOperationType.CASH_SUBSTRACT_ESCROW.name());
                ((ObjectNode) substractToBalance).put("balanceOperationStatus", balanceOperationStatus.toString());
                if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING)) {
                    ((ObjectNode) substractToBalance).put("balanceOperationProcessId", UUID.randomUUID().toString());
                }
                if (additionalInfo != null) {
                    ((ObjectNode) substractToBalance).put("additionalInfo", additionalInfo);
                }
                if (btcPrice != null) {
                    ((ObjectNode) substractToBalance).put("btcPrice", btcPrice);
                }
                String fileName = "substract__" + timestamp + "__1" + ".json";
                FileUtil.createFile(substractToBalance, CashFolderLocator.getPlaceEscrowBalanceFolder(placeId), fileName);
                return "OK____" + fileName;
            }
            return "DOES NOT HAVE ENOUGH BALANCE";
        } finally {
            BaseOperation.unlock(CashFolderLocator.getPlaceEscrowBalanceFolder(placeId));
            BaseOperation.unlock(CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId));
        }
    }
    
}