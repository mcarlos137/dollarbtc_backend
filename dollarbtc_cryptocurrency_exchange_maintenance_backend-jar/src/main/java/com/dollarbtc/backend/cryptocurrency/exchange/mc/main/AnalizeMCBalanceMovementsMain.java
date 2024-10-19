/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class AnalizeMCBalanceMovementsMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> blackListedUsers = new HashSet<>();
        blackListedUsers.add("584245522788");
        blackListedUsers.add("12019896074");
        blackListedUsers.add("sinep77@gmail.com");
        blackListedUsers.add("15512214091");
        int usersWithMCBalanceMovements = 0;
        int usersWithMCBalanceMovementsOK = 0;
        int usersVerificated = 0;
        Map<String, Integer> usersMCBalanceMovementsLastTimestamps = new TreeMap<>();
        Map<String, Set<String>> usersMCBalanceMovementsLastTimestampsIds = new TreeMap<>();
        Map<String, String> usersWithNoMCBalanceMovementsAndVerificated = new TreeMap<>();
        Map<String, Integer> usersMCBalanceMovementsDepositsQuantityByCurrency = new TreeMap<>();
        Map<String, Double> usersMCBalanceMovementsDepositsAmountByCurrency = new TreeMap<>();
        Map<String, Integer> usersMCBalanceMovementsTransfersQuantityByCurrency = new TreeMap<>();
        Map<String, Double> usersMCBalanceMovementsTransfersAmountByCurrency = new TreeMap<>();
        Map<String, Integer> usersMCBalanceMovementsChangesQuantityByCurrency = new TreeMap<>();
        Map<String, Double> usersMCBalanceMovementsChangesAmountByCurrency = new TreeMap<>();
        Map<String, String> usersWithNoVerificationAndEmail = new HashMap<>();
        Set<String> usersMCBalanceMovementsAndWithNoDeposits = new HashSet<>();
        Set<String> usersWithReadedNotifications = new HashSet<>();
        Map<String, Double> usersWithMCBalanceOf200OrMore = new TreeMap<>();
        int usersWithMCBalanceBetween50And200 = 0;
        System.out.println("Starting AnalizeMCBalanceMovementsMain");
        for (File userFolder : UsersFolderLocator.getFolder().listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (blackListedUsers.contains(userFolder.getName())) {
                continue;
            }
            if (userFolder.getName().contains("@mailinator.com")) {
                continue;
            }
            System.out.println("---------------------------------------------------------");
            System.out.println("user: " + userFolder.getName());
            boolean verificated = false;
            String verificationTimestamp = null;
            String email = null;
            try {
                JsonNode user = mapper.readTree(UsersFolderLocator.getConfigFile(userFolder.getName()));
                if (user.has("verification")
                        && user.get("verification").has("E")
                        && user.get("verification").get("E").has("userVerificationStatus")
                        && user.get("verification").get("E").get("userVerificationStatus").textValue().equals("OK")) {
                    verificationTimestamp = user.get("verification").get("E").get("timestamp").textValue();
                    verificated = true;
                }
                if (user.has("verification")
                        && user.get("verification").has("C")
                        && user.get("verification").get("C").has("userVerificationStatus")
                        && user.get("verification").get("C").get("userVerificationStatus").textValue().equals("OK")) {
                    verificationTimestamp = user.get("verification").get("C").get("timestamp").textValue();
                    verificated = true;
                }
                if (user.has("email") && !user.get("email").textValue().equals("")) {
                    email = user.get("email").textValue();
                }
                if (verificated) {
                    usersVerificated++;
                }
            } catch (IOException ex) {
                Logger.getLogger(AnalizeMCBalanceMovementsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userFolder.getName());
            String userMCBalanceMovementLastTimestamp = null;
            if (userMCBalanceFolder.listFiles().length > 0) {
                usersWithMCBalanceMovements++;
            } else {
                if (verificationTimestamp != null) {
                    usersWithNoMCBalanceMovementsAndVerificated.put(verificationTimestamp, userFolder.getName());
                }
                if (verificationTimestamp == null && email != null) {
                    usersWithNoVerificationAndEmail.put(userFolder.getName(), email);
                }
                continue;
            }
            boolean mcBalanceMovementOK = false;
            boolean mcBalanceMovementDepositOK = false;
            for (File userMCBalanceMovementFile : userMCBalanceFolder.listFiles()) {
                if (!userMCBalanceMovementFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userMCBalanceMovement = mapper.readTree(userMCBalanceMovementFile);
                    String timestamp = userMCBalanceMovement.get("timestamp").textValue();
                    String balanceOperationStatus = userMCBalanceMovement.get("balanceOperationStatus").textValue();
                    String balanceOperationType = userMCBalanceMovement.get("balanceOperationType").textValue();
                    String currency = null;
                    Double amount = null;
                    if (userMCBalanceMovement.has("addedAmount")) {
                        amount = userMCBalanceMovement.get("addedAmount").get("amount").doubleValue();
                        currency = userMCBalanceMovement.get("addedAmount").get("currency").textValue();
                    }
                    if (userMCBalanceMovement.has("substractedAmount")) {
                        amount = userMCBalanceMovement.get("substractedAmount").get("amount").doubleValue();
                        currency = userMCBalanceMovement.get("substractedAmount").get("currency").textValue();
                    }
                    if (balanceOperationStatus.equals("OK")) {
                        mcBalanceMovementOK = true;
                    }
                    if (userMCBalanceMovementLastTimestamp == null || timestamp.compareTo(userMCBalanceMovementLastTimestamp) > 0) {
                        userMCBalanceMovementLastTimestamp = timestamp;
                    }
                    if (userMCBalanceMovement.has("addedAmount") && balanceOperationStatus.equals("OK") && (balanceOperationType.equals("RECEIVE_OUT") || balanceOperationType.equals("MC_BUY_BALANCE"))) {
                        if (!usersMCBalanceMovementsDepositsQuantityByCurrency.containsKey(currency)) {
                            usersMCBalanceMovementsDepositsQuantityByCurrency.put(currency, 0);
                        }
                        usersMCBalanceMovementsDepositsQuantityByCurrency.put(currency, usersMCBalanceMovementsDepositsQuantityByCurrency.get(currency) + 1);
                        if (!usersMCBalanceMovementsDepositsAmountByCurrency.containsKey(currency)) {
                            usersMCBalanceMovementsDepositsAmountByCurrency.put(currency, 0.0);
                        }
                        usersMCBalanceMovementsDepositsAmountByCurrency.put(currency, usersMCBalanceMovementsDepositsAmountByCurrency.get(currency) + amount);
                        if (currency.equals("BTC") && amount > 1) {
                            System.out.println("IN " + amount + " BTC");
                        }
                        mcBalanceMovementDepositOK = true;
                    }
                    if (userMCBalanceMovement.has("substractedAmount") && balanceOperationStatus.equals("OK") && (balanceOperationType.equals("SEND_TO_PAYMENT") || balanceOperationType.equals("SEND_OUT"))) {
                        if (!usersMCBalanceMovementsTransfersQuantityByCurrency.containsKey(currency)) {
                            usersMCBalanceMovementsTransfersQuantityByCurrency.put(currency, 0);
                        }
                        usersMCBalanceMovementsTransfersQuantityByCurrency.put(currency, usersMCBalanceMovementsTransfersQuantityByCurrency.get(currency) + 1);
                        if (!usersMCBalanceMovementsTransfersAmountByCurrency.containsKey(currency)) {
                            usersMCBalanceMovementsTransfersAmountByCurrency.put(currency, 0.0);
                        }
                        usersMCBalanceMovementsTransfersAmountByCurrency.put(currency, usersMCBalanceMovementsTransfersAmountByCurrency.get(currency) + amount);
                    }
                    if (userMCBalanceMovement.has("addedAmount") && balanceOperationStatus.equals("OK") && (balanceOperationType.equals("MC_FAST_CHANGE") || balanceOperationType.equals("FAST_CHANGE") || balanceOperationType.equals("MC_BUY_CRYPTO") || balanceOperationType.equals("MC_SELL_CRYPTO"))) {
                        if (!usersMCBalanceMovementsChangesQuantityByCurrency.containsKey(currency)) {
                            usersMCBalanceMovementsChangesQuantityByCurrency.put(currency, 0);
                        }
                        usersMCBalanceMovementsChangesQuantityByCurrency.put(currency, usersMCBalanceMovementsChangesQuantityByCurrency.get(currency) + 1);
                        if (!usersMCBalanceMovementsChangesAmountByCurrency.containsKey(currency)) {
                            usersMCBalanceMovementsChangesAmountByCurrency.put(currency, 0.0);
                        }
                        usersMCBalanceMovementsChangesAmountByCurrency.put(currency, usersMCBalanceMovementsChangesAmountByCurrency.get(currency) + amount);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AnalizeMCBalanceMovementsMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (mcBalanceMovementOK) {
                usersWithMCBalanceMovementsOK++;
                if (!mcBalanceMovementDepositOK) {
                    usersMCBalanceMovementsAndWithNoDeposits.add(userFolder.getName());
                }
            }
            System.out.println("userMCBalanceMovementLastTimestamp: " + userMCBalanceMovementLastTimestamp);
            String userMCBalanceMovementLastTimestampMonth = DateUtil.getMonthStartDate(DateUtil.getDate(userMCBalanceMovementLastTimestamp));
            if (!usersMCBalanceMovementsLastTimestamps.containsKey(userMCBalanceMovementLastTimestampMonth)) {
                usersMCBalanceMovementsLastTimestamps.put(userMCBalanceMovementLastTimestampMonth, 0);
                usersMCBalanceMovementsLastTimestampsIds.put(userMCBalanceMovementLastTimestampMonth, new HashSet<>());
            }
            usersMCBalanceMovementsLastTimestamps.put(userMCBalanceMovementLastTimestampMonth, usersMCBalanceMovementsLastTimestamps.get(userMCBalanceMovementLastTimestampMonth) + 1);
            usersMCBalanceMovementsLastTimestampsIds.get(userMCBalanceMovementLastTimestampMonth).add(userFolder.getName());

            File userNotificationFolder = UsersFolderLocator.getNotificationsFolder(userFolder.getName());
            for (File userNotificationFile : userNotificationFolder.listFiles()) {
                if (!userNotificationFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userNotification = mapper.readTree(userNotificationFile);
                    if (userNotification.has("readed") && userNotification.get("readed").booleanValue()) {
                        usersWithReadedNotifications.add(userFolder.getName());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AnalizeMCBalanceMovementsMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            JsonNode userMCBalance = new MCUserGetBalance(userFolder.getName(), true, true).getResponse();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(AnalizeMCBalanceMovementsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            Double usdEstimatedBalance = userMCBalance.get("usdEstimatedBalance").doubleValue();
            if(usdEstimatedBalance >= 200){
                usersWithMCBalanceOf200OrMore.put(userFolder.getName(), usdEstimatedBalance);
            } else {
                usersWithMCBalanceBetween50And200++;
            }
            System.out.println("---------------------------------------------------------");
        }
        System.out.println("---------------------------------------------------------");
        System.out.println("usersWithMCBalanceMovements: " + usersWithMCBalanceMovements);
        System.out.println("usersWithMCBalanceMovementsOK: " + usersWithMCBalanceMovementsOK);
        System.out.println("usersVerificated: " + usersVerificated);
        System.out.println("usersMCBalanceMovementsLastTimestamps: ");
        for (String key : usersMCBalanceMovementsLastTimestamps.keySet()) {
            System.out.println("month: " + key + " " + usersMCBalanceMovementsLastTimestamps.get(key));
        }
        System.out.println("usersMCBalanceMovementsDepositsQuantityByCurrency: ");
        for (String key : usersMCBalanceMovementsDepositsQuantityByCurrency.keySet()) {
            System.out.println("currency: " + key + " " + usersMCBalanceMovementsDepositsQuantityByCurrency.get(key));
        }
        System.out.println("usersMCBalanceMovementsDepositsAmountByCurrency: ");
        for (String key : usersMCBalanceMovementsDepositsAmountByCurrency.keySet()) {
            System.out.println("currency: " + key + " " + usersMCBalanceMovementsDepositsAmountByCurrency.get(key));
        }
        System.out.println("usersMCBalanceMovementsTransfersQuantityByCurrency: ");
        for (String key : usersMCBalanceMovementsTransfersQuantityByCurrency.keySet()) {
            System.out.println("currency: " + key + " " + usersMCBalanceMovementsTransfersQuantityByCurrency.get(key));
        }
        System.out.println("usersMCBalanceMovementsTransfersAmountByCurrency: ");
        for (String key : usersMCBalanceMovementsTransfersAmountByCurrency.keySet()) {
            System.out.println("currency: " + key + " " + usersMCBalanceMovementsTransfersAmountByCurrency.get(key));
        }
        System.out.println("usersMCBalanceMovementsChangesQuantityByCurrency: ");
        for (String key : usersMCBalanceMovementsChangesQuantityByCurrency.keySet()) {
            System.out.println("currency: " + key + " " + usersMCBalanceMovementsChangesQuantityByCurrency.get(key));
        }
        System.out.println("usersMCBalanceMovementsChangesAmountByCurrency: ");
        for (String key : usersMCBalanceMovementsChangesAmountByCurrency.keySet()) {
            System.out.println("currency: " + key + " " + usersMCBalanceMovementsChangesAmountByCurrency.get(key));
        }
        System.out.println("usersMCBalanceMovementsLastTimestampsIds: ");
        for (String key : usersMCBalanceMovementsLastTimestampsIds.keySet()) {
            System.out.println("month: " + key + " " + usersMCBalanceMovementsLastTimestampsIds.get(key).size() + " " + usersMCBalanceMovementsLastTimestampsIds.get(key));
        }
        System.out.println("usersWithNoMCBalanceMovementsAndVerificated: " + usersWithNoMCBalanceMovementsAndVerificated.size() + " " + usersWithNoMCBalanceMovementsAndVerificated);
        System.out.println("usersWithNoVerificationAndEmail: " + usersWithNoVerificationAndEmail.size() + " " + usersWithNoVerificationAndEmail);
        System.out.println("usersMCBalanceMovementsAndWithNoDeposits: " + usersMCBalanceMovementsAndWithNoDeposits.size() + " " + usersMCBalanceMovementsAndWithNoDeposits);
        System.out.println("usersWithReadedNotifications: " + usersWithReadedNotifications.size() + " " + usersWithReadedNotifications);
        System.out.println("usersWithMCBalanceOf200OrMore: " + usersWithMCBalanceOf200OrMore.size() + " " + usersWithMCBalanceOf200OrMore);
        System.out.println("usersWithMCBalanceBetween50And200-200: " + usersWithMCBalanceBetween50And200);
        System.out.println("---------------------------------------------------------");
        System.out.println("Finishing AnalizeMCBalanceMovementsMain");
    }

}
