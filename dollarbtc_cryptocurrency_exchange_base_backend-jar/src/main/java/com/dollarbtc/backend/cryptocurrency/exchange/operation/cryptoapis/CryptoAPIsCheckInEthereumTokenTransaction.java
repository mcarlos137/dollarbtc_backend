/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
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
public class CryptoAPIsCheckInEthereumTokenTransaction extends AbstractOperation<Boolean> {

    private final String userName, address, transactionId, currency;

    public CryptoAPIsCheckInEthereumTokenTransaction(String userName, String address, String transactionId, String currency) {
        super(Boolean.class);
        this.userName = userName;
        this.address = address;
        this.transactionId = transactionId;
        this.currency = currency;
    }

    @Override
    protected void execute() {
        Logger.getLogger(CryptoAPIsCheckInEthereumTokenTransaction.class.getName()).log(Level.INFO, address);
        super.response = true;
        String blockchain = null;
        Integer confirms = null;
        String walletsTag = null;
        switch (currency) {
            case "USDT":
                blockchain = "ethereum";
                confirms = 20;
                walletsTag = "mcWalletsEthereum";
                break;
        }
        if (blockchain == null || confirms == null) {
            super.response = false;
            return;
        }
        Integer blockchainHeight = null;
        boolean updateBlockchainHeight = false;
        File addressesCurrencyTransactionsTypeDataFile = new File(AddressesFolderLocator.getCurrencyTransactionsTypeFolder(currency, "IN"), "data.json");
        JsonNode addressesCurrencyTransactionsTypeData = mapper.createObjectNode();
        if (addressesCurrencyTransactionsTypeDataFile.isFile()) {
            try {
                addressesCurrencyTransactionsTypeData = mapper.readTree(addressesCurrencyTransactionsTypeDataFile);
                if (addressesCurrencyTransactionsTypeData.has("blockchainHeight")) {
                    Integer value = addressesCurrencyTransactionsTypeData.get("blockchainHeight").get("value").intValue();
                    String timestamp = addressesCurrencyTransactionsTypeData.get("blockchainHeight").get("timestamp").textValue();
                    if (DateUtil.getDateMinutesBefore(null, 10).compareTo(timestamp) < 0) {
                        blockchainHeight = value;
                    } else {
                        updateBlockchainHeight = true;
                    }
                } else {
                    updateBlockchainHeight = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(CryptoAPIsCheckInEthereumTokenTransaction.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            updateBlockchainHeight = true;
        }
        if (updateBlockchainHeight) {
            blockchainHeight = new CryptoAPIsGetLastBlock(blockchain, "mainnet").getResponse();
            if (blockchainHeight == null) {
                Logger.getLogger(CryptoAPIsCheckInEthereumTokenTransaction.class.getName()).log(Level.SEVERE, "BLOCKCHAIN HEIGHT IS NULL");
                return;
            }
            ObjectNode addressesCurrencyTransactionsTypeDataBlockchainHeight = mapper.createObjectNode();
            addressesCurrencyTransactionsTypeDataBlockchainHeight.put("value", blockchainHeight);
            addressesCurrencyTransactionsTypeDataBlockchainHeight.put("timestamp", DateUtil.getCurrentDate());
            ((ObjectNode) addressesCurrencyTransactionsTypeData).set("blockchainHeight", addressesCurrencyTransactionsTypeDataBlockchainHeight);
            FileUtil.editFile(addressesCurrencyTransactionsTypeData, addressesCurrencyTransactionsTypeDataFile);
        }
        File addressesCurrencyTransactionsTypeUserFolder = AddressesFolderLocator.getCurrencyTransactionsTypeUserFolder(currency, "IN", userName);
        File addressesCurrencyTransactionsTypeUserControlFile = new File(addressesCurrencyTransactionsTypeUserFolder, "control.json");
        String currentTimestamp = DateUtil.getCurrentDate();
        if (!addressesCurrencyTransactionsTypeUserControlFile.isFile()) {
            JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.createObjectNode();
            ((ObjectNode) addressesCurrencyTransactionsTypeUserControl).put("lastRequestTimestamp", currentTimestamp);
            FileUtil.createFile(addressesCurrencyTransactionsTypeUserControl, addressesCurrencyTransactionsTypeUserControlFile);
        } else {
            try {
                JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.readTree(addressesCurrencyTransactionsTypeUserControlFile);
                if (addressesCurrencyTransactionsTypeUserControl == null) {
                    addressesCurrencyTransactionsTypeUserControl = mapper.createObjectNode();
                }
                if (addressesCurrencyTransactionsTypeUserControl.has("lastRequestTimestamp") && DateUtil.parseDate(DateUtil.getDateMinutesBefore(currentTimestamp, 20)).before(DateUtil.parseDate(addressesCurrencyTransactionsTypeUserControl.get("lastRequestTimestamp").textValue()))) {
                    super.response = false;
                    return;
                } else {
                    ((ObjectNode) addressesCurrencyTransactionsTypeUserControl).put("lastRequestTimestamp", currentTimestamp);
                    FileUtil.editFile(addressesCurrencyTransactionsTypeUserControl, addressesCurrencyTransactionsTypeUserControlFile);
                }
            } catch (IOException ex) {
                Logger.getLogger(CryptoAPIsCheckInEthereumTokenTransaction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JsonNode transaction = new CryptoAPIsGetEthereumTokenTransaction("IN", "mainnet", transactionId, address, currency, blockchainHeight).getResponse();
        if (transaction.size() == 0) {
            super.response = false;
            return;
        }
        Double amount = transaction.get("amount").doubleValue();
        boolean isConfirmed = transaction.get("isConfirmed").booleanValue();
        String timestamp = transaction.get("timestamp").textValue();
        File addressesCurrencyTransactionTypeUserFile = new File(addressesCurrencyTransactionsTypeUserFolder, transactionId + ".json");
        if (addressesCurrencyTransactionTypeUserFile.isFile()) {
            return;
        }
        try {
            JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.readTree(addressesCurrencyTransactionsTypeUserControlFile);
            if (!addressesCurrencyTransactionsTypeUserControl.has("addresses")) {
                ((ObjectNode) addressesCurrencyTransactionsTypeUserControl).set("addresses", mapper.createObjectNode());
            }
            ((ObjectNode) addressesCurrencyTransactionsTypeUserControl.get("addresses")).put(address, currentTimestamp);
            FileUtil.editFile(addressesCurrencyTransactionsTypeUserControl, addressesCurrencyTransactionsTypeUserControlFile);
        } catch (IOException ex) {
            Logger.getLogger(CryptoAPIsCheckInEthereumTokenTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        File userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userName);
        JsonNode addressesCurrencyTransactionTypeUser = mapper.createObjectNode();
        File addressesCurrencyTransactionTypeUserProcessingFile = new File(addressesCurrencyTransactionsTypeUserFolder, transactionId + "__PROCESSING.json");
        if (addressesCurrencyTransactionTypeUserProcessingFile.isFile()) {
            if (isConfirmed) {
                BaseOperation.changeBalanceOperationStatus(userBalanceFolder, BalanceOperationStatus.OK, transactionId, "transactionHash", null);
                FileUtil.moveFileToFile(addressesCurrencyTransactionTypeUserProcessingFile, addressesCurrencyTransactionTypeUserFile);
                ((ObjectNode) addressesCurrencyTransactionTypeUser).put("isConfirmed", isConfirmed);
                ((ObjectNode) addressesCurrencyTransactionTypeUser).put("timestamp", timestamp);
                FileUtil.editFile(addressesCurrencyTransactionTypeUser, addressesCurrencyTransactionTypeUserFile);
                String notificationMessage = "You have " + String.format("%(,.8f", amount) + " " + currency + " available in your wallet.";
                new NotificationSendMessageByUserName(userName, "Transaction information", notificationMessage).getResponse();
            } else {
                super.response = false;
            }
        } else {
            ((ObjectNode) addressesCurrencyTransactionTypeUser).put("userName", userName);
            ((ObjectNode) addressesCurrencyTransactionTypeUser).put("hash", transactionId);
            ((ObjectNode) addressesCurrencyTransactionTypeUser).put("amount", amount);
            ((ObjectNode) addressesCurrencyTransactionTypeUser).put("isConfirmed", isConfirmed);
            ((ObjectNode) addressesCurrencyTransactionTypeUser).put("timestamp", timestamp);
            BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.OK;
            if (!isConfirmed) {
                balanceOperationStatus = BalanceOperationStatus.PROCESSING;
            }
            ObjectNode additionals = mapper.createObjectNode();
            additionals.put("transactionHash", transactionId);
            BaseOperation.addToBalance(
                    userBalanceFolder,
                    currency,
                    amount,
                    BalanceOperationType.RECEIVE_OUT,
                    balanceOperationStatus,
                    "RECEIVED FROM OUTSIDE WALLET",
                    null,
                    null,
                    false,
                    additionals
            );
            if (walletsTag.equals("mcWallets")) {
//                new MCUserAutomaticChange(userName, amount, "").getResponse();
            }
            if (walletsTag.equals("mcWalletsEthereum")) {
//                new MCUserAutomaticChange(userName, amount, "").getResponse();
            }
            String notificationMessage;
            if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING)) {
                FileUtil.createFile(addressesCurrencyTransactionTypeUser, addressesCurrencyTransactionTypeUserProcessingFile);
                notificationMessage = "You have " + String.format("%(,.8f", amount) + " " + currency + " pending to release in your wallet. You must wait " + confirms + " confirmations from the blockchain network to have your coins. For more information, contact customer service.";
                super.response = false;
            } else {
                FileUtil.createFile(addressesCurrencyTransactionTypeUser, addressesCurrencyTransactionTypeUserFile);
                notificationMessage = "You have " + String.format("%(,.8f", amount) + " " + currency + " available in your wallet.";
            }
            if (notificationMessage != null) {
                new NotificationSendMessageByUserName(userName, "Transaction information", notificationMessage).getResponse();
            }
        }
    }

}
