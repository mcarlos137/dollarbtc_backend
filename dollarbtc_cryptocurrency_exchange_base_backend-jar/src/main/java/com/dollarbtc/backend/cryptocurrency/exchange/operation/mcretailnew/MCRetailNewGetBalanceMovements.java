/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailBalanceType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailNewGetBalanceMovements extends AbstractOperation<JsonNode> {

    private final String retailId, initTimestamp, endTimestamp;
    private final BalanceOperationType balanceOperationType;
    private final MCRetailBalanceType mcRetailBalanceType;

    public MCRetailNewGetBalanceMovements(String retailId, String initTimestamp, String endTimestamp, BalanceOperationType balanceOperationType, MCRetailBalanceType mcRetailBalanceType) {
        super(JsonNode.class);
        this.retailId = retailId;
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.balanceOperationType = balanceOperationType;
        this.mcRetailBalanceType = mcRetailBalanceType;
    }

    @Override
    public void execute() {
        File balanceFolder = null;
        switch (mcRetailBalanceType) {
            case NO_CASH:
                balanceFolder = MoneyclickFolderLocator.getRetailBalanceNoCashFolder(retailId);
                break;
            case CASH:
                balanceFolder = MoneyclickFolderLocator.getRetailBalanceCashFolder(retailId);
                break;
            case ESCROW:
                balanceFolder = MoneyclickFolderLocator.getRetailEscrowBalanceFolder(retailId);
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
                Logger.getLogger(MCRetailNewGetBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (mcRetailBalanceType.equals(MCRetailBalanceType.ESCROW)) {
            balanceFolder = MoneyclickFolderLocator.getRetailEscrowBalanceFromToUserFolder(retailId);
            for (File balanceMovementFile : balanceFolder.listFiles()) {
                JsonNode balanceMovement;
                try {
                    balanceMovement = mapper.readTree(balanceMovementFile);
                    String timestamp = DateUtil.getDate(balanceMovementFile.getName().split("__")[1]);
                    String position = balanceMovementFile.getName().split("__")[2].replace(".json", "");
                    balanceMovements.put(timestamp + "__" + position, balanceMovement);
                } catch (IOException ex) {
                    Logger.getLogger(MCRetailNewGetBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
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
