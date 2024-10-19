/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashBalanceType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashGetPlaceBalanceMovements extends AbstractOperation<JsonNode> {

    private final String placeId, initTimestamp, endTimestamp;
    private final BalanceOperationType balanceOperationType;
    private final CashBalanceType cashBalanceType;

    public CashGetPlaceBalanceMovements(String placeId, String initTimestamp, String endTimestamp, BalanceOperationType balanceOperationType, CashBalanceType cashBalanceType) {
        super(JsonNode.class);
        this.placeId = placeId;
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.balanceOperationType = balanceOperationType;
        this.cashBalanceType = cashBalanceType;
    }

    @Override
    public void execute() {
        File balanceFolder = null;
        switch (cashBalanceType) {
            case NO_CASH:
                balanceFolder = CashFolderLocator.getPlaceBalanceNoCashFolder(placeId);
                break;
            case CASH:
                balanceFolder = CashFolderLocator.getPlaceBalanceCashFolder(placeId);
                break;
            case ESCROW:
                balanceFolder = CashFolderLocator.getPlaceEscrowBalanceFolder(placeId);
                break;
        }
        if (balanceFolder == null) {
            super.response = mapper.createObjectNode();
            return;
        }
        Map<String, JsonNode> balanceMovements = new TreeMap<>();
        Map<String, JsonNode> finalBalanceMovements = new TreeMap<>();
        for (File balanceMovementFile : balanceFolder.listFiles()) {
            JsonNode balanceMovement;
            try {
                balanceMovement = mapper.readTree(balanceMovementFile);
                String timestamp = DateUtil.getDate(balanceMovementFile.getName().split("__")[1]);
                String position = balanceMovementFile.getName().split("__")[2].replace(".json", "");
                balanceMovements.put(timestamp + "__" + position, balanceMovement);
            } catch (IOException ex) {
                Logger.getLogger(CashGetPlaceBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (cashBalanceType.equals(CashBalanceType.ESCROW)) {
            balanceFolder = CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId);
            for (File balanceMovementFile : balanceFolder.listFiles()) {
                JsonNode balanceMovement;
                try {
                    balanceMovement = mapper.readTree(balanceMovementFile);
                    String timestamp = DateUtil.getDate(balanceMovementFile.getName().split("__")[1]);
                    String position = balanceMovementFile.getName().split("__")[2].replace(".json", "");
                    balanceMovements.put(timestamp + "__" + position, balanceMovement);
                } catch (IOException ex) {
                    Logger.getLogger(CashGetPlaceBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (String key : balanceMovements.keySet()) {
            if (initTimestamp != null && !initTimestamp.equals("") && !initTimestamp.equals("NONE")) {
                if (DateUtil.parseDate(key.split("__")[0]).compareTo(DateUtil.parseDate(initTimestamp)) < 0) {
                    continue;
                }
            }
            if (endTimestamp != null && !endTimestamp.equals("") && !endTimestamp.equals("NONE")) {
                if (DateUtil.parseDate(key.split("__")[0]).compareTo(DateUtil.parseDate(endTimestamp)) > 0) {
                    continue;
                }
            }
            if (balanceOperationType != null) {
                if (balanceMovements.get(key).has("balanceOperationType") && !BalanceOperationType.valueOf(balanceMovements.get(key).get("balanceOperationType").textValue()).equals(balanceOperationType)) {
                    continue;
                }
            }
            if (balanceMovements.get(key).has("addedAmount") && balanceMovements.get(key).get("addedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            if (balanceMovements.get(key).has("substractedAmount") && balanceMovements.get(key).get("substractedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            finalBalanceMovements.put(key, balanceMovements.get(key));
        }
        super.response = mapper.valueToTree(finalBalanceMovements);
    }

}
