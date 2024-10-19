/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetSpecialBalanceMovements extends AbstractOperation<ArrayNode> {

    private final String currency, type, initTimestamp;
    private final Double minAmount;

    public MCUserGetSpecialBalanceMovements(String currency, String type, Double minAmount, String initTimestamp) {
        super(ArrayNode.class);
        this.currency = currency;
        this.type = type;
        this.minAmount = minAmount;
        this.initTimestamp = initTimestamp;
    }

    @Override
    public void execute() {
        ArrayNode specialBalanceMovements = mapper.createArrayNode();
        String timestamp = DateUtil.getCurrentDate();
        File specialBalanceMovementsFile = MoneyclickFolderLocator.getSpecialBalanceMovementsFile(currency + "__" + type + "__" + minAmount + "__" + DateUtil.getFileDate(initTimestamp));
        if (specialBalanceMovementsFile.isFile()) {
            try {
                JsonNode result = mapper.readTree(specialBalanceMovementsFile);
                if (result.get("timestamp").textValue().compareTo(DateUtil.getDateMinutesBefore(timestamp, 15)) < 0) {
                    ((ObjectNode) result).put("timestamp", timestamp);
                    FileUtil.editFile(result, specialBalanceMovementsFile);
                    createThread(specialBalanceMovementsFile);
                }
                specialBalanceMovements.addAll((ArrayNode) result.get("data"));
            } catch (IOException ex) {
                Logger.getLogger(MCUserGetSpecialBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JsonNode result = mapper.createObjectNode();
            ((ObjectNode) result).put("timestamp", timestamp);
            FileUtil.createFile(result, specialBalanceMovementsFile);
            createThread(specialBalanceMovementsFile);
        }
        super.response = specialBalanceMovements;
    }

    private void createThread(File specialBalanceMovementsFile) {
        Thread createThread = new Thread(() -> {
            ArrayNode specialBalanceMovements = mapper.createArrayNode();
            File usersFolder = UsersFolderLocator.getFolder();
            for (String userFolderName : usersFolder.list()) {
                File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userFolderName);
                if (!userMCBalanceFolder.isDirectory()) {
                    continue;
                }
                JsonNode specialBalanceMovement = mapper.createObjectNode();
                ((ObjectNode) specialBalanceMovement).put("userName", userFolderName);
                ArrayNode balanceMovements = mapper.createArrayNode();
                for (File userMCBalanceFile : userMCBalanceFolder.listFiles()) {
                    if (!userMCBalanceFile.isFile()) {
                        continue;
                    }
                    if (!userMCBalanceFile.getName().contains(type)) {
                        continue;
                    }
                    String timestamp = DateUtil.getDate(userMCBalanceFile.getName().split("__")[1]);
                    if (timestamp.compareTo(initTimestamp) < 0) {
                        continue;
                    }
                    try {
                        JsonNode userMCBalance = mapper.readTree(userMCBalanceFile);
                        if (!userMCBalance.get(type + "edAmount").get("currency").textValue().equals(currency)) {
                            continue;
                        }
                        if (userMCBalance.get(type + "edAmount").get("amount").doubleValue() < minAmount) {
                            continue;
                        }
                        balanceMovements.add(userMCBalance);
                    } catch (IOException ex) {
                        Logger.getLogger(MCUserGetSpecialBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (balanceMovements.size() > 0) {
                    ((ObjectNode) specialBalanceMovement).putArray("specialBalanceMovements").addAll(balanceMovements);
                    specialBalanceMovements.add(specialBalanceMovement);
                }
            }
            try {
                JsonNode result = mapper.readTree(specialBalanceMovementsFile);
                ((ObjectNode) result).putArray("data").addAll(specialBalanceMovements);
                FileUtil.editFile(result, specialBalanceMovementsFile);
            } catch (IOException ex) {
                Logger.getLogger(MCUserGetSpecialBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        createThread.start();
    }

}
