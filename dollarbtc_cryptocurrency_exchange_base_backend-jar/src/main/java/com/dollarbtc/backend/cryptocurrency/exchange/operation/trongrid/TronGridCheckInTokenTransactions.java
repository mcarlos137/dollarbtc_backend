/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.trongrid;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class TronGridCheckInTokenTransactions extends AbstractOperation<Void> {

    private final String userName, currency;

    public TronGridCheckInTokenTransactions(String userName, String currency) {
        super(Void.class);
        this.userName = userName;
        this.currency = currency;
    }

    @Override
    protected void execute() {
        Logger.getLogger(TronGridCheckInTokenTransactions.class.getName()).log(Level.INFO, userName);
        String walletsTag = "mcWalletsTron";
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        JsonNode userConfig = null;
        try {
            userConfig = mapper.readTree(userConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(TronGridCheckInTokenTransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userConfig == null) {
            return;
        }
        Map<String, Integer> walletStatusTagRequestTime = new HashMap<>();
        walletStatusTagRequestTime.put("current", 10);
        walletStatusTagRequestTime.put("old", 60);
        File addressesCurrencyTransactionsTypeUserFolder = AddressesFolderLocator.getCurrencyTransactionsTypeUserFolder(currency, "IN", userName);
        File addressesCurrencyTransactionsTypeUserControlFile = new File(addressesCurrencyTransactionsTypeUserFolder, "control.json");
        String currentTimestamp = DateUtil.getCurrentDate();
        for (String walletStatusTag : walletStatusTagRequestTime.keySet()) {
            if (!addressesCurrencyTransactionsTypeUserControlFile.isFile()) {
                JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.createObjectNode();
                ((ObjectNode) addressesCurrencyTransactionsTypeUserControl).put("lastRequestTimestampTron", currentTimestamp);
                FileUtil.createFile(addressesCurrencyTransactionsTypeUserControl, addressesCurrencyTransactionsTypeUserControlFile);
            } else {
                try {
                    JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.readTree(addressesCurrencyTransactionsTypeUserControlFile);
                    if (addressesCurrencyTransactionsTypeUserControl == null) {
                        addressesCurrencyTransactionsTypeUserControl = mapper.createObjectNode();
                    }
                    if (addressesCurrencyTransactionsTypeUserControl.has("lastRequestTimestampTron") && DateUtil.parseDate(DateUtil.getDateMinutesBefore(currentTimestamp, walletStatusTagRequestTime.get(walletStatusTag))).before(DateUtil.parseDate(addressesCurrencyTransactionsTypeUserControl.get("lastRequestTimestampTron").textValue()))) {
                        return;
                    } else {
                        ((ObjectNode) addressesCurrencyTransactionsTypeUserControl).put("lastRequestTimestampTron", currentTimestamp);
                        FileUtil.editFile(addressesCurrencyTransactionsTypeUserControl, addressesCurrencyTransactionsTypeUserControlFile);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TronGridCheckInTokenTransactions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!userConfig.has(walletsTag)) {
                continue;
            }
            if (!userConfig.get(walletsTag).has(walletStatusTag)) {
                continue;
            }
            Set<String> addresses = new HashSet<>();
            Iterator<JsonNode> usersWalletsStatusTagIterator = userConfig.get(walletsTag).get(walletStatusTag).iterator();
            while (usersWalletsStatusTagIterator.hasNext()) {
                JsonNode usersWalletsStatusTagIt = usersWalletsStatusTagIterator.next();
                addresses.add(usersWalletsStatusTagIt.get("address").textValue());
            }
            JsonNode transactions = new TronGridGetTokenTransactions(addresses, "IN", currency).getResponse();
            Iterator<String> transactionsIterator = transactions.fieldNames();
            while (transactionsIterator.hasNext()) {
                String transactionsIt = transactionsIterator.next();
                String[] transactionsItParams = transactions.get(transactionsIt).textValue().split("____");
                Double amount = Double.parseDouble(transactionsItParams[0]);
                Boolean success = Boolean.parseBoolean(transactionsItParams[1]);
                String confirmedTimestamp = transactionsItParams[2];
                String address = transactionsItParams[3];
                File addressesCurrencyTransactionTypeUserFile = new File(addressesCurrencyTransactionsTypeUserFolder, transactionsIt + ".json");
                if (addressesCurrencyTransactionTypeUserFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.readTree(addressesCurrencyTransactionsTypeUserControlFile);
                    if (!addressesCurrencyTransactionsTypeUserControl.has("addresses")) {
                        ((ObjectNode) addressesCurrencyTransactionsTypeUserControl).set("addresses", mapper.createObjectNode());
                    }
                    ((ObjectNode) addressesCurrencyTransactionsTypeUserControl.get("addresses")).put(address, currentTimestamp);
                    FileUtil.editFile(addressesCurrencyTransactionsTypeUserControl, addressesCurrencyTransactionsTypeUserControlFile);
                } catch (IOException ex) {
                    Logger.getLogger(TronGridCheckInTokenTransactions.class.getName()).log(Level.SEVERE, null, ex);
                }
                File userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userName);
                JsonNode addressesCurrencyTransactionTypeUser = mapper.createObjectNode();
                File addressesCurrencyTransactionTypeUserProcessingFile = new File(addressesCurrencyTransactionsTypeUserFolder, transactionsIt + "__PROCESSING.json");
                if (addressesCurrencyTransactionTypeUserProcessingFile.isFile()) {
                    if (success) {
                        BaseOperation.changeBalanceOperationStatus(userBalanceFolder, BalanceOperationStatus.OK, transactionsIt, "transactionHash", null);
                        FileUtil.moveFileToFile(addressesCurrencyTransactionTypeUserProcessingFile, addressesCurrencyTransactionTypeUserFile);
                        ((ObjectNode) addressesCurrencyTransactionTypeUser).put("success", success);
                        ((ObjectNode) addressesCurrencyTransactionTypeUser).put("confirmedTimestamp", confirmedTimestamp);
                        FileUtil.editFile(addressesCurrencyTransactionTypeUser, addressesCurrencyTransactionTypeUserFile);
                        String notificationMessage = "You have " + String.format("%(,.2f", amount) + " " + currency + " available in your wallet.";
                        new NotificationSendMessageByUserName(userName, "Transaction information", notificationMessage).getResponse();
                    }
                } else {
                    ((ObjectNode) addressesCurrencyTransactionTypeUser).put("userName", userName);
                    ((ObjectNode) addressesCurrencyTransactionTypeUser).put("hash", transactionsIt);
                    ((ObjectNode) addressesCurrencyTransactionTypeUser).put("amount", amount);
                    ((ObjectNode) addressesCurrencyTransactionTypeUser).put("success", success);
                    ((ObjectNode) addressesCurrencyTransactionTypeUser).put("confirmedTimestamp", confirmedTimestamp);
                    BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.OK;
                    if (!success) {
                        balanceOperationStatus = BalanceOperationStatus.PROCESSING;
                    }
                    ObjectNode additionals = mapper.createObjectNode();
                    additionals.put("transactionHash", transactionsIt);
                    BaseOperation.addToBalance(
                            userBalanceFolder,
                            currency,
                            Double.parseDouble(transactionsItParams[0]),
                            BalanceOperationType.RECEIVE_OUT,
                            balanceOperationStatus,
                            "RECEIVED FROM OUTSIDE WALLET",
                            null,
                            null,
                            false,
                            additionals
                    );
                    if (walletsTag.equals("mcWalletsEthereum")) {
//                            new MCUserAutomaticChange(userName, amount, "").getResponse();
                    }
                    String notificationMessage;
                    if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING)) {
                        FileUtil.createFile(addressesCurrencyTransactionTypeUser, addressesCurrencyTransactionTypeUserProcessingFile);
                        notificationMessage = "You have " + String.format("%(,.2f", amount) + " " + currency + " pending to release in your wallet. You must wait 3 confirmations from the blockchain network to have your coins. For more information, contact customer service.";
                    } else {
                        FileUtil.createFile(addressesCurrencyTransactionTypeUser, addressesCurrencyTransactionTypeUserFile);
                        notificationMessage = "You have " + String.format("%(,.2f", amount) + " " + currency + " available in your wallet.";
                    }
                    if (notificationMessage != null) {
                        new NotificationSendMessageByUserName(userName, "Transaction information", notificationMessage).getResponse();
                    }
                }
            }
        }
    }

}
