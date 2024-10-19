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
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class ConsolidateDollarBTCPaymentBalanceMovementsByMonthMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String initialTimestamp = DateUtil.getDateDaysBefore(null, 92);
        System.out.println("Starting ConsolidateDollarBTCPaymentBalanceMovementsByMonthMain");
        for (File currencyFolder : OTCFolderLocator.getFolder(null).listFiles()) {
            if (!currencyFolder.isDirectory()) {
                continue;
            }
            if (!currencyFolder.getName().equals("USD")) {
                continue;
            }
            System.out.println("currency: " + currencyFolder.getName());
            File currencyPaymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, currencyFolder.getName());
            for (File currencyPaymentFolder : currencyPaymentsFolder.listFiles()) {
                if (!currencyPaymentFolder.isDirectory()) {
                    continue;
                }
                String id = currencyPaymentFolder.getName();
                System.out.println("id: " + id);
                File currencyPaymentBalanceFolder = OTCFolderLocator.getCurrencyPaymentBalanceFolder(null, currencyFolder.getName(), id);
                for (File currencyPaymentBalanceMovementFile : currencyPaymentBalanceFolder.listFiles()) {
                    if (!currencyPaymentBalanceMovementFile.isFile()) {
                        continue;
                    }
                    System.out.println("currencyPaymentBalanceMovementFile: " + currencyPaymentBalanceMovementFile.getName());
                    try {
                        JsonNode currencyPaymentBalanceMovement = mapper.readTree(currencyPaymentBalanceMovementFile);
                        String timestamp = currencyPaymentBalanceMovement.get("timestamp").textValue();
                        if (timestamp.compareTo(initialTimestamp) >= 0) {
                            continue;
                        }
                        System.out.println("timestamp: " + timestamp);
                        String monthTimestamp = DateUtil.getMonthStartDate(timestamp);
                        System.out.println("monthTimestamp: " + monthTimestamp);
                        File monthFolder = new File(currencyPaymentBalanceFolder, DateUtil.getFileDate(monthTimestamp));
                        if (!monthFolder.isDirectory()) {
                            FileUtil.createFolderIfNoExist(monthFolder);
                            System.out.println("create folder: " + monthFolder);
                        }
                        System.out.println("move file: " + currencyPaymentBalanceMovementFile);
                        FileUtil.moveFileToFolder(currencyPaymentBalanceMovementFile, monthFolder);
                        BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.valueOf(currencyPaymentBalanceMovement.get("balanceOperationStatus").textValue());
                        if (balanceOperationStatus.equals(BalanceOperationStatus.FAIL)) {
                            continue;
                        }
                        System.out.println("balanceOperationStatus: " + balanceOperationStatus);
                        Double amount = null;
                        String currency = null;
                        if (currencyPaymentBalanceMovement.has("addedAmount")) {
                            amount = currencyPaymentBalanceMovement.get("addedAmount").get("amount").doubleValue();
                            currency = currencyPaymentBalanceMovement.get("addedAmount").get("currency").textValue();
                        }
                        if (currencyPaymentBalanceMovement.has("substractedAmount")) {
                            amount = -1 * currencyPaymentBalanceMovement.get("substractedAmount").get("amount").doubleValue();
                            currency = currencyPaymentBalanceMovement.get("substractedAmount").get("currency").textValue();
                        }
                        File monthCurrencyFile = new File(currencyPaymentBalanceFolder, "add__" + DateUtil.getFileDate(monthTimestamp) + "__" + currency + "__" + balanceOperationStatus.name() + ".json");
                        if (monthCurrencyFile.isFile()) {
                            JsonNode monthCurrency = mapper.readTree(monthCurrencyFile);
                            Double currentAmount = monthCurrency.get("addedAmount").get("amount").doubleValue();
                            ((ObjectNode) monthCurrency.get("addedAmount")).put("amount", currentAmount + amount);
                            System.out.println("edit faile: " + monthCurrencyFile);
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
                            if (currencyPaymentBalanceMovement.has("reconverted20210930")) {
                                monthCurrency.put("reconverted20210930", currencyPaymentBalanceMovement.get("reconverted20210930").booleanValue());
                            }
                            System.out.println("create file: " + monthCurrencyFile);
                            FileUtil.createFile(monthCurrency, monthCurrencyFile);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ConsolidateDollarBTCPaymentBalanceMovementsByMonthMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.out.println("Finishing ConsolidateDollarBTCPaymentBalanceMovementsByMonthMain");
    }

}
