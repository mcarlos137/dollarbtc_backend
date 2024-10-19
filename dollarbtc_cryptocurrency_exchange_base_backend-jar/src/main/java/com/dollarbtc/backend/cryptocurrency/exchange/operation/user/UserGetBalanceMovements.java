/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class UserGetBalanceMovements extends AbstractOperation<JsonNode> {

    private final String userName, initTimestamp, endTimestamp;
    private final BalanceOperationType balanceOperationType;
    private final boolean isMoneyClick;

    public UserGetBalanceMovements(String userName, String initTimestamp, String endTimestamp, BalanceOperationType balanceOperationType, boolean isMoneyClick) {
        super(JsonNode.class);
        this.userName = userName;
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.balanceOperationType = balanceOperationType;
        this.isMoneyClick = isMoneyClick;
    }

    @Override
    protected void execute() {
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userName);
        if(isMoneyClick){
            userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userName);
        }
        Map<String, JsonNode> userBalanceMovements = new TreeMap<>();
        Map<String, JsonNode> finalUserBalanceMovements = new TreeMap<>();
        for (File userBalanceMovementFile : userBalanceFolder.listFiles()) {
            JsonNode userBalanceMovement;
            try {
                userBalanceMovement = mapper.readTree(userBalanceMovementFile);
                String timestamp = DateUtil.getDate(userBalanceMovementFile.getName().split("__")[1]);
                String position = userBalanceMovementFile.getName().split("__")[2].replace(".json", "");
                userBalanceMovements.put(timestamp + "__" + position, userBalanceMovement);
            } catch (IOException ex) {
                Logger.getLogger(UserGetBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (String key : userBalanceMovements.keySet()) {
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
                if (userBalanceMovements.get(key).has("balanceOperationType") && !BalanceOperationType
                        .valueOf(userBalanceMovements.get(key).get("balanceOperationType").textValue())
                        .equals(balanceOperationType)) {
                    continue;
                }
            }
            if (userBalanceMovements.get(key).has("addedAmount")
                    && userBalanceMovements.get(key).get("addedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            if (userBalanceMovements.get(key).has("substractedAmount")
                    && userBalanceMovements.get(key).get("substractedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            finalUserBalanceMovements.put(key, userBalanceMovements.get(key));
        }
        super.response = mapper.valueToTree(finalUserBalanceMovements);
    }

}
