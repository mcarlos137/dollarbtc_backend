/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserListAddressesWithReceivedTransactions extends AbstractOperation<ArrayNode> {

    private final String currency;
    
    public UserListAddressesWithReceivedTransactions(String currency) {
        super(ArrayNode.class);
        this.currency = currency;
    }

    @Override
    protected void execute() {
        ArrayNode usersWithReceivedTransactions = mapper.createArrayNode();
        File addressesCurrencyTransactionsTypeFolder = AddressesFolderLocator.getCurrencyTransactionsTypeFolder(currency, "IN");
        Iterator<JsonNode> listIterator = new UserListWithReceivedTransactions(currency).getResponse().iterator();
        while (listIterator.hasNext()) {
            JsonNode listIt = listIterator.next();
            String userName = listIt.textValue();
            File addressesCurrencyTransactionsTypeUserFolder = new File(addressesCurrencyTransactionsTypeFolder, userName);
            if (!addressesCurrencyTransactionsTypeUserFolder.isDirectory()) {
                continue;
            }
            JsonNode usersWithReceivedTransaction = mapper.createObjectNode();
            ((ObjectNode) usersWithReceivedTransaction).put("userName", addressesCurrencyTransactionsTypeUserFolder.getName());
            File addressesCurrencyTransactionsTypeUserControlFile = new File(addressesCurrencyTransactionsTypeUserFolder, "control.json");
            try {
                JsonNode addressesCurrencyTransactionsTypeUserControl = mapper.readTree(addressesCurrencyTransactionsTypeUserControlFile);
                if (addressesCurrencyTransactionsTypeUserControl.has("addresses")) {
                    ObjectNode addresses = mapper.createObjectNode();
                    Iterator<String> addressesCurrencyTransactionsTypeUserControlFieldNamesIterator = addressesCurrencyTransactionsTypeUserControl.get("addresses").fieldNames();
                    while (addressesCurrencyTransactionsTypeUserControlFieldNamesIterator.hasNext()) {
                        String addressesCurrencyTransactionsTypeUserControlFieldNamesIt = addressesCurrencyTransactionsTypeUserControlFieldNamesIterator.next();
//                            Double balance = new BlockcypherGetBalance(addressesCurrencyTransactionsTypeUserControlFieldNamesIt).getResponse();
//                            addresses.put(addressesCurrencyTransactionsTypeUserControlFieldNamesIt, String.format("%.8f", balance));
                        addresses.put(addressesCurrencyTransactionsTypeUserControlFieldNamesIt, addressesCurrencyTransactionsTypeUserControl.get("addresses").get(addressesCurrencyTransactionsTypeUserControlFieldNamesIt).textValue());
                    }
                    ((ObjectNode) usersWithReceivedTransaction).set("addresses", addresses);
                }
            } catch (IOException ex) {
                Logger.getLogger(UserListAddressesWithReceivedTransactions.class.getName()).log(Level.SEVERE, null, ex);
            }
            usersWithReceivedTransactions.add(usersWithReceivedTransaction);
        }
        super.response = usersWithReceivedTransactions;
    }

}
