/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
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
public class DebitCardGetBalanceMovements extends AbstractOperation<JsonNode> {
    
    private final String id, initTimestamp, endTimestamp;
    private final BalanceOperationType balanceOperationType;

    public DebitCardGetBalanceMovements(String id, String initTimestamp, String endTimestamp, BalanceOperationType balanceOperationType) {
        super(JsonNode.class);
        this.id = id;
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.balanceOperationType = balanceOperationType;
    }
        
    @Override
    public void execute() {
        
        File debitCardBalanceFolder = DebitCardsFolderLocator.getBalanceFolder(id);
        Map<String, JsonNode> debitCardBalanceMovements = new TreeMap<>();
        Map<String, JsonNode> finalDebitCardBalanceMovements = new TreeMap<>();
        for (File userBalanceMovementFile : debitCardBalanceFolder.listFiles()) {
            if(!userBalanceMovementFile.isFile()){
                continue;
            }
            JsonNode userBalanceMovement;
            try {
                userBalanceMovement = mapper.readTree(userBalanceMovementFile);
                String timestamp = DateUtil.getDate(userBalanceMovementFile.getName().split("__")[1]);
                String position = userBalanceMovementFile.getName().split("__")[2].replace(".json", "");
                debitCardBalanceMovements.put(timestamp + "__" + position, userBalanceMovement);
            } catch (IOException ex) {
                Logger.getLogger(DebitCardGetBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (String key : debitCardBalanceMovements.keySet()) {
            if (initTimestamp != null && !initTimestamp.equals("")) {
                if (DateUtil.parseDate(key.split("__")[0]).compareTo(DateUtil.parseDate(initTimestamp)) < 0) {
                    continue;
                }
            }
            if (endTimestamp != null && !endTimestamp.equals("")) {
                if (DateUtil.parseDate(key.split("__")[0]).compareTo(DateUtil.parseDate(endTimestamp)) > 0) {
                    continue;
                }
            }
            if (balanceOperationType != null) {
                if (debitCardBalanceMovements.get(key).has("balanceOperationType") && !BalanceOperationType
                        .valueOf(debitCardBalanceMovements.get(key).get("balanceOperationType").textValue())
                        .equals(balanceOperationType)) {
                    continue;
                }
            }
            if (debitCardBalanceMovements.get(key).has("addedAmount")
                    && debitCardBalanceMovements.get(key).get("addedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            if (debitCardBalanceMovements.get(key).has("substractedAmount")
                    && debitCardBalanceMovements.get(key).get("substractedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            finalDebitCardBalanceMovements.put(key, debitCardBalanceMovements.get(key));
        }
        super.response = mapper.valueToTree(finalDebitCardBalanceMovements);
    }
    
}
