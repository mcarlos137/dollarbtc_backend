/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BankerGetCurrencies extends AbstractOperation<Object> {

    private final String userName;

    public BankerGetCurrencies(String userName) {
        super(Object.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        ArrayNode currencies = mapper.createArrayNode();
//        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        Set<String> userCurrencies = new HashSet<>();
        userCurrencies.add("VES");
        userCurrencies.add("COP");
        userCurrencies.add("CLP");
//        userCurrencies.add("PEN");
        userCurrencies.add("MXN");
        userCurrencies.add("ARS");
        userCurrencies.stream().map((userCurrency) -> OTCFolderLocator.getCurrencyFile(null, userCurrency)).forEach((otcCurrencyConfigFile) -> {
            try {
                JsonNode currency = mapper.readTree(otcCurrencyConfigFile);
                currencies.add(currency);
            } catch (IOException ex) {
                Logger.getLogger(BankerGetCurrencies.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        super.response = currencies;
    }

}
