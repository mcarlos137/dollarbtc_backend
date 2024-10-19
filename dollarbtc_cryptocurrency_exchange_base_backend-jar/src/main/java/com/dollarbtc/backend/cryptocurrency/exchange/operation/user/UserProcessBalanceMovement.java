/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.address.AddressCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserProcessBalanceMovementRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.address.AddressCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersAddressesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserProcessBalanceMovement extends AbstractOperation<String> {

    private String userName, balanceOperationProcessId;
    private BalanceOperationStatus balanceOperationStatus;
    private String message;
    private UserProcessBalanceMovementRequest userProcessBalanceMovementRequest;

    public UserProcessBalanceMovement(String userName, String balanceOperationProcessId, BalanceOperationStatus balanceOperationStatus, String message) {
        super(String.class);
        this.userName = userName;
        this.balanceOperationProcessId = balanceOperationProcessId;
        this.balanceOperationStatus = balanceOperationStatus;
        this.message = message;
    }

    public UserProcessBalanceMovement(UserProcessBalanceMovementRequest userProcessBalanceMovementRequest) {
        super(String.class);
        this.userProcessBalanceMovementRequest = userProcessBalanceMovementRequest;
    }

    @Override
    protected void execute() {
        if (userProcessBalanceMovementRequest != null) {
            super.response = method(userProcessBalanceMovementRequest);
        } else {
            super.response = method(userName, balanceOperationProcessId, balanceOperationStatus, message);
        }
    }

    private String method(String userName, String balanceOperationProcessId, BalanceOperationStatus balanceOperationStatus, String message) {
        File[] userBalancesFolders = new File[]{UsersFolderLocator.getBalanceFolder(userName), UsersFolderLocator.getMCBalanceFolder(userName)};
        for (File userBalanceFolder : userBalancesFolders) {
            if (!userBalanceFolder.isDirectory()) {
                continue;
            }
            for (File userBalanceFile : userBalanceFolder.listFiles()) {
                if (!userBalanceFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userBalance = mapper.readTree(userBalanceFile);
                    if (!userBalance.has("balanceOperationProcessId")) {
                        continue;
                    }
                    if (userBalance.get("balanceOperationProcessId").textValue().equals(balanceOperationProcessId)) {
                        String currentDate = DateUtil.getCurrentDate();
                        ((ObjectNode) userBalance).put("balanceOperationProcessTimestamp", currentDate);
                        ((ObjectNode) userBalance).put("balanceOperationStatus", balanceOperationStatus.name());
                        ((ObjectNode) userBalance).put("message", message);
                        FileUtil.editFile(userBalance, userBalanceFile);
                        FileUtil.deleteFile(new File(BaseFilesLocator.getProcessingBalanceFolder(), userBalanceFile.getName()));
                        if (balanceOperationStatus.equals(BalanceOperationStatus.OK)
                                && userBalance.get("substractedAmount").get("currency").textValue().equals("BTC")
                                && BalanceOperationType.valueOf(userBalance.get("balanceOperationType").textValue())
                                        .equals(BalanceOperationType.SEND_OUT)) {
                            String currency = userBalance.get("substractedAmount").get("currency").textValue();
                            double amount = userBalance.get("substractedAmount").get("initialAmount").doubleValue();
                            File addressesCurrencyOperationsFolder = AddressesFolderLocator.getCurrencyOperationsFolder(currency, "PROCESSING");
                            JsonNode addressOperation = mapper.createObjectNode();
                            ((ObjectNode) addressOperation).put("id", balanceOperationProcessId);
                            ((ObjectNode) addressOperation).put("timestamp", userBalance.get("timestamp").textValue());
                            ((ObjectNode) addressOperation).put("processTimestamp", currentDate);
                            ((ObjectNode) addressOperation).put("baseAddress", message);
                            ((ObjectNode) addressOperation).put("targetAddress", userBalance.get("targetAddress").textValue());
                            ((ObjectNode) addressOperation).put("operation", BalanceOperationType.SEND_OUT.name());
                            ((ObjectNode) addressOperation).put("amount", amount);
                            FileUtil.createFile(addressOperation, addressesCurrencyOperationsFolder, DateUtil.getFileDate(currentDate) + ".json");
                        }
                        return "OK";
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UserProcessBalanceMovement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return "THERE IS NO MATCH FOR THIS BALANCE OPERATION PROCESS ID";
    }

    private String method(UserProcessBalanceMovementRequest userProcessBalanceMovementRequest) {
        File[] userBalanceFolders = new File[]{UsersFolderLocator.getBalanceFolder(userProcessBalanceMovementRequest.getUserName()), UsersFolderLocator.getMCBalanceFolder(userProcessBalanceMovementRequest.getUserName())};
        for (File userBalanceFolder : userBalanceFolders) {
            if (!userBalanceFolder.isDirectory()) {
                continue;
            }
            for (File userBalanceFile : userBalanceFolder.listFiles()) {
                if (!userBalanceFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userBalance = mapper.readTree(userBalanceFile);
                    if (!userBalance.has("balanceOperationProcessId")) {
                        continue;
                    }
                    if (userBalance.get("balanceOperationProcessId").textValue()
                            .equals(userProcessBalanceMovementRequest.getBalanceOperationProcessId())) {
                        String currentDate = DateUtil.getCurrentDate();
                        ((ObjectNode) userBalance).put("balanceOperationProcessTimestamp", currentDate);
                        ((ObjectNode) userBalance).put("balanceOperationStatus",
                                userProcessBalanceMovementRequest.getBalanceOperationStatus().name());
                        ((ObjectNode) userBalance).put("baseAddress", userProcessBalanceMovementRequest.getBaseAddress());
                        if (userProcessBalanceMovementRequest.getAdminMessage() != null
                                && !userProcessBalanceMovementRequest.getAdminMessage().equals("")) {
                            ((ObjectNode) userBalance).put("adminMessage",
                                    userProcessBalanceMovementRequest.getAdminMessage());
                        }
                        FileUtil.editFile(userBalance, userBalanceFile);
                        FileUtil.deleteFile(new File(BaseFilesLocator.getProcessingBalanceFolder(), userBalanceFile.getName()));
                        if (userProcessBalanceMovementRequest.getBalanceOperationStatus().equals(BalanceOperationStatus.OK)
                                && BalanceOperationType.valueOf(userBalance.get("balanceOperationType").textValue())
                                        .equals(BalanceOperationType.SEND_OUT)) {
                            if (userProcessBalanceMovementRequest.getTxId() != null
                                    && !userProcessBalanceMovementRequest.getTxId().equals("")) {
                                ((ObjectNode) userBalance).put("txId", userProcessBalanceMovementRequest.getTxId());
                                FileUtil.editFile(userBalance, userBalanceFile);
                            } else {
                                String currency = userBalance.get("substractedAmount").get("currency").textValue();
                                double amount = userBalance.get("substractedAmount").get("initialAmount").doubleValue();
                                File addressesCurrencyOperationsFolder = AddressesFolderLocator.getCurrencyOperationsFolder(currency, "PROCESSING");
                                JsonNode addressOperation = mapper.createObjectNode();
                                ((ObjectNode) addressOperation).put("id", userProcessBalanceMovementRequest.getBalanceOperationProcessId());
                                ((ObjectNode) addressOperation).put("userName", userProcessBalanceMovementRequest.getUserName());
                                ((ObjectNode) addressOperation).put("timestamp", userBalance.get("timestamp").textValue());
                                ((ObjectNode) addressOperation).put("processTimestamp", currentDate);
                                if(userProcessBalanceMovementRequest.getBaseAddress() != null){
                                    ((ObjectNode) addressOperation).put("baseAddress", userProcessBalanceMovementRequest.getBaseAddress());
                                }
                                ((ObjectNode) addressOperation).put("targetAddress", userBalance.get("targetAddress").textValue());
                                ((ObjectNode) addressOperation).put("operation", BalanceOperationType.SEND_OUT.name());
                                ((ObjectNode) addressOperation).put("amount", amount);
                                if (userProcessBalanceMovementRequest.getBaseAddress() != null) {
                                    File usersAddressFile = UsersAddressesFolderLocator.getAddressFile(userProcessBalanceMovementRequest.getBaseAddress());
                                    JsonNode userAddress = mapper.readTree(usersAddressFile);
                                    String wif = null;
                                    if (userAddress.has("wif")) {
                                        wif = userAddress.get("wif").textValue();
                                    }
                                    if (wif == null) {
                                        addressesCurrencyOperationsFolder = AddressesFolderLocator.getCurrencyOperationsFolder(currency, "MANUAL");
                                    }
                                    if (!new File(AddressesFolderLocator.getCurrencyFolder("BTC"), usersAddressFile.getName().replace(".json", "")).isDirectory()) {
                                        String privateKey = "";
                                        if (userAddress.has("privateKey")) {
                                            privateKey = userAddress.get("privateKey").textValue();
                                        }
                                        if (userAddress.has("private")) {
                                            privateKey = userAddress.get("private").textValue();
                                        }
                                        new AddressCreate(new AddressCreateRequest("BTC", userProcessBalanceMovementRequest.getBaseAddress(), privateKey, null, wif)).getResponse();
                                    }
                                } else {
                                    addressesCurrencyOperationsFolder = AddressesFolderLocator.getCurrencyOperationsFolder(currency, "PROCESSING");
                                }
                                FileUtil.createFile(addressOperation, addressesCurrencyOperationsFolder, DateUtil.getFileDate(currentDate) + ".json");
                            }
                        }
                        return "OK";
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UserProcessBalanceMovement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return "THERE IS NO MATCH FOR THIS BALANCE OPERATION PROCESS ID";
    }

}
