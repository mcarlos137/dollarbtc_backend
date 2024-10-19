/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class ConsolidateMCBalanceMovementsByMonthMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String initialTimestamp = DateUtil.getDateDaysBefore(null, 92);
        System.out.println("Starting ConsolidateMCBalanceMovementsByMonthMain");
        for (File userFolder : UsersFolderLocator.getFolder().listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (args.length > 0 && !containsUserNameInArgs(args, userFolder.getName()))  {            
                continue;
            }
            System.out.println("user: " + userFolder.getName());
            File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userFolder.getName());
            for (File userMCBalanceMovementFile : userMCBalanceFolder.listFiles()) {
                if (!userMCBalanceMovementFile.isFile()) {
                    continue;
                }
                System.out.println("userMCBalanceMovementFile: " + userMCBalanceMovementFile.getName());
                try {
                    JsonNode userMCBalanceMovement = mapper.readTree(userMCBalanceMovementFile);
                    String timestamp = userMCBalanceMovement.get("timestamp").textValue();
                    if (timestamp.compareTo(initialTimestamp) >= 0) {
                        continue;
                    }
                    System.out.println("timestamp: " + timestamp);
                    String monthTimestamp = DateUtil.getMonthStartDate(timestamp);
                    System.out.println("monthTimestamp: " + monthTimestamp);
                    File monthFolder = new File(userMCBalanceFolder, DateUtil.getFileDate(monthTimestamp));
                    if (!monthFolder.isDirectory()) {
                        FileUtil.createFolderIfNoExist(monthFolder);
                    }
                    FileUtil.moveFileToFolder(userMCBalanceMovementFile, monthFolder);
                    BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.valueOf(userMCBalanceMovement.get("balanceOperationStatus").textValue());
                    if (balanceOperationStatus.equals(BalanceOperationStatus.FAIL)) {
                        continue;
                    }
                    System.out.println("balanceOperationStatus: " + balanceOperationStatus);
                    Double amount = null;
                    String currency = null;
                    if (userMCBalanceMovement.has("addedAmount")) {
                        amount = userMCBalanceMovement.get("addedAmount").get("amount").doubleValue();
                        currency = userMCBalanceMovement.get("addedAmount").get("currency").textValue();
                    }
                    if (userMCBalanceMovement.has("substractedAmount")) {
                        amount = -1 * userMCBalanceMovement.get("substractedAmount").get("amount").doubleValue();
                        currency = userMCBalanceMovement.get("substractedAmount").get("currency").textValue();
                    }
                    File monthCurrencyFile = new File(userMCBalanceFolder, "add__" + DateUtil.getFileDate(monthTimestamp) + "__" + currency + "__" + balanceOperationStatus.name() + ".json");
                    if (monthCurrencyFile.isFile()) {
                        JsonNode monthCurrency = mapper.readTree(monthCurrencyFile);
                        Double currentAmount = monthCurrency.get("addedAmount").get("amount").doubleValue();
                        ((ObjectNode) monthCurrency.get("addedAmount")).put("amount", currentAmount + amount);
                        FileUtil.editFile(monthCurrency, monthCurrencyFile);
                    } else {
                        ObjectNode monthCurrency = mapper.createObjectNode();
                        monthCurrency.put("timestamp", monthTimestamp);
                        ObjectNode addedAmount = mapper.createObjectNode();
                        addedAmount.put("amount", amount);
                        addedAmount.put("currency", currency);
                        monthCurrency.set("addedAmount", addedAmount);
                        monthCurrency.put("balanceOperationType", BalanceOperationType.INITIAL_MOVEMENT.name());
                        monthCurrency.put("balanceOperationStatus", BalanceOperationStatus.OK.name());
                        if (userMCBalanceMovement.has("reconverted20210930")) {
                            monthCurrency.put("reconverted20210930", userMCBalanceMovement.get("reconverted20210930").booleanValue());
                        }
                        FileUtil.createFile(monthCurrency, monthCurrencyFile);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ConsolidateMCBalanceMovementsByMonthMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("Finishing ConsolidateMCBalanceMovementsByMonthMain");
    }

    private static boolean containsUserNameInArgs(String[] args, String userName) {
        for (String arg : args) {
            if (arg.equals(userName)) {
                return true;
            }
        }
        return false;
    }

}
