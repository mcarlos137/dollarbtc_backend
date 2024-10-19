/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersCurrenciesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetCurrencies extends AbstractOperation<Set> {

    private final String userName;

    public UserGetCurrencies(String userName) {
        super(Set.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        Set<String> currencies = new HashSet<>();
        try {
            File usersCurrenciesFile = UsersCurrenciesFolderLocator.getFile(userName);
            if (!usersCurrenciesFile.isFile()) {
                super.response = currencies;
                return;
            }
            ArrayNode usersCurrencies = (ArrayNode) mapper.readTree(usersCurrenciesFile);
            Iterator<JsonNode> usersCurrenciesIterator = usersCurrencies.iterator();
            while (usersCurrenciesIterator.hasNext()) {
                currencies.add(usersCurrenciesIterator.next().textValue());
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGetCurrencies.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (currencies.isEmpty()) {
            for (File otcCurrencyFolder : OTCFolderLocator.getFolder(null).listFiles()) {
                if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                    continue;
                }
                currencies.add(otcCurrencyFolder.getName());
            }
        }
        super.response = currencies;
    }

}
