/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class OTCAdminGetCurrencies extends AbstractOperation<Object> {
    
    private final String userName;

    public OTCAdminGetCurrencies(String userName) {
        super(Object.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        if(userName == null){
            super.response = this.method();
        } else {
            super.response = this.method(userName);
        }
    }
    
    private ArrayNode method(String userName) {
        ArrayNode currencies = mapper.createArrayNode();
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        userCurrencies.stream().map((userCurrency) -> OTCFolderLocator.getCurrencyFile(null, userCurrency)).forEach((otcCurrencyConfigFile) -> {
            try {
                JsonNode currency = mapper.readTree(otcCurrencyConfigFile);
                currencies.add(currency);
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetCurrencies.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return currencies;
    }

    private Set<String> method() {
        Set<String> currencies = new HashSet<>();
        File masterAccountFile = MasterAccountFolderLocator.getConfigFile(null);
        try {
            ArrayNode masterAccount = (ArrayNode) mapper.readTree(masterAccountFile);
            Iterator<JsonNode> masterAccountIterator = masterAccount.iterator();
            while (masterAccountIterator.hasNext()) {
                JsonNode masterAccountIt = masterAccountIterator.next();
                ArrayNode masterAccountItCurrencies = (ArrayNode) masterAccountIt.get("currencies");
                Iterator<JsonNode> masterAccountItCurrenciesIterator = masterAccountItCurrencies.iterator();
                while (masterAccountItCurrenciesIterator.hasNext()) {
                    JsonNode masterAccountItCurrenciesIt = masterAccountItCurrenciesIterator.next();
                    currencies.add(masterAccountItCurrenciesIt.textValue());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetCurrencies.class.getName()).log(Level.SEVERE, null, ex);
        }
        return currencies;
    }
    
}
