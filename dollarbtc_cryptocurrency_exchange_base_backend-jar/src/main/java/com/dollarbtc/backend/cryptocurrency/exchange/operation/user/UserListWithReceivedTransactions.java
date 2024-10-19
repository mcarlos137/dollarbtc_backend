/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
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
public class UserListWithReceivedTransactions extends AbstractOperation<ArrayNode> {

    private final String currency;
    
    public UserListWithReceivedTransactions(String currency) {
        super(ArrayNode.class);
        this.currency = currency;
    }

    @Override
    protected void execute() {
        ArrayNode usersWithReceivedTransactions = mapper.createArrayNode();
        File addressesCurrencyTransactionsTypeFolder = AddressesFolderLocator.getCurrencyTransactionsTypeFolder(currency, "IN");
        File addressesCurrencyTransactionsTypeDataFile = new File(addressesCurrencyTransactionsTypeFolder, "data.json");
        JsonNode addressesCurrencyTransactionsTypeData = mapper.createObjectNode();
        if (addressesCurrencyTransactionsTypeDataFile.isFile()) {
            try {
                addressesCurrencyTransactionsTypeData = mapper.readTree(addressesCurrencyTransactionsTypeDataFile);
                usersWithReceivedTransactions = (ArrayNode) addressesCurrencyTransactionsTypeData.get("usersWithReceivedTransactions");
            } catch (IOException ex) {
                Logger.getLogger(UserListWithReceivedTransactions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        createUpdateThread(addressesCurrencyTransactionsTypeData, addressesCurrencyTransactionsTypeDataFile, addressesCurrencyTransactionsTypeFolder);
        super.response = usersWithReceivedTransactions;
    }
    
    private void createUpdateThread(JsonNode addressesCurrencyTransactionsTypeData, File addressesCurrencyTransactionsTypeDataFile, File addressesCurrencyTransactionsTypeFolder){
        Thread createUpdateThread = new Thread(() -> {
            ArrayNode updateUsersWithReceivedTransactions = mapper.createArrayNode();
            for (File addressesCurrencyTransactionsTypeUserFolder : addressesCurrencyTransactionsTypeFolder.listFiles()) {
                if (!addressesCurrencyTransactionsTypeUserFolder.isDirectory()) {
                    continue;
                }
                if (addressesCurrencyTransactionsTypeUserFolder.listFiles().length > 1) {
                    updateUsersWithReceivedTransactions.add(addressesCurrencyTransactionsTypeUserFolder.getName());
                }
            }
            ((ObjectNode) addressesCurrencyTransactionsTypeData).putArray("usersWithReceivedTransactions").addAll(updateUsersWithReceivedTransactions);
            FileUtil.editFile(addressesCurrencyTransactionsTypeData, addressesCurrencyTransactionsTypeDataFile);
        });
        createUpdateThread.start();
    }

}
