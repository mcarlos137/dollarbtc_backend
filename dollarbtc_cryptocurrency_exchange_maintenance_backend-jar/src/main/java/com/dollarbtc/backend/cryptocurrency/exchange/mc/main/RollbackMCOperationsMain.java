/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserDeleteRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserDelete;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
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
public class RollbackMCOperationsMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting RollbackMCOperationsMain");
        System.out.println("" + DateUtil.getCurrentDate());
        ObjectMapper mapper = new ObjectMapper();
        File usersFolder = new File(OPERATOR_PATH, "Users");
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            try {
                JsonNode userConfig = mapper.readTree(new File(userFolder, "config.json"));
                if (!userConfig.has("active") || userConfig.get("active").booleanValue()) {
                    continue;
                }
                System.out.println("INACTIVE USER: " + userFolder.getName());
                if (!userConfig.has("createdFromMCSend") || !userConfig.get("createdFromMCSend").booleanValue()) {
                    continue;
                }
                System.out.println("CREATED FROM MC SEND USER: " + userFolder.getName());
                File userMCBalanceFolder = new File(userFolder, "MCBalance");
                if (!userMCBalanceFolder.isDirectory()) {
                    continue;
                }
                boolean delete = false;
                for (File userMCBalanceFile : userMCBalanceFolder.listFiles()) {
                    if (!userMCBalanceFile.isFile()) {
                        continue;
                    }
                    if (!userMCBalanceFile.getName().contains("add")) {
                        continue;
                    }
                    System.out.println("ADD OPERATION FILE: " + userMCBalanceFile.getName());
                    try {
                        JsonNode userMCBalance = mapper.readTree(userMCBalanceFile);
                        String[] timestamps = userMCBalance.get("timestamp").textValue().split("T");
                        String timestamp = timestamps[0] + "T" + timestamps[1].replace("--", ".").replace("-", ":");
                        if(DateUtil.parseDate(timestamp).after(DateUtil.parseDate(DateUtil.getDateHoursBefore(DateUtil.getCurrentDate(), 6)))){
                            continue;
                        }
                        delete = true;
                        System.out.println("MARKING ADD OPERATION FILE AS FAILED");
                        ((ObjectNode) userMCBalance).put("balanceOperationStatus", BalanceOperationStatus.FAIL.name());
                        FileUtil.editFile(userMCBalance, userMCBalanceFile);
                        System.out.println("");
                        String senderUserName = userMCBalance.get("senderUserName").textValue();
                        Double addedAmountAmount = userMCBalance.get("addedAmount").get("amount").doubleValue();
                        String addedAmountCurrency = userMCBalance.get("addedAmount").get("currency").textValue();
                        if(userMCBalance.has("senderBalanceFile")){
                            File senderBalanceFile = new File(UsersFolderLocator.getMCBalanceFolder(senderUserName), userMCBalance.get("senderBalanceFile").textValue());
                            JsonNode senderBalance = mapper.readTree(senderBalanceFile);
                            addedAmountAmount = senderBalance.get("substractedAmount").get("amount").doubleValue();
                            addedAmountCurrency = senderBalance.get("substractedAmount").get("currency").textValue();
                        } 
                        System.out.println("RETURNING MONEY TO: " + senderUserName);
                        System.out.println("CURRENCY: " + addedAmountCurrency);
                        System.out.println("AMOUNT: " + addedAmountAmount);
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(senderUserName), 
                                addedAmountCurrency, 
                                addedAmountAmount, 
                                BalanceOperationType.CREDIT, 
                                BalanceOperationStatus.OK, 
                                "ROLLBACK FROM " + userFolder.getName(), 
                                null, 
                                null,
                                false,
                                null
                        );
                    } catch (IOException ex) {
                        Logger.getLogger(RollbackMCOperationsMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(delete){
                    System.out.println("DELETE USER " + userFolder.getName());
                    new UserDelete(new UserDeleteRequest(userFolder.getName())).getResponse();
                }
            } catch (IOException ex) {
                Logger.getLogger(RollbackMCOperationsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Finishing RollbackMCOperationsMain");
    }

}
