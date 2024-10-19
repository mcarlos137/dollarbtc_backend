/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetCurrenciesOrder extends AbstractOperation<ArrayNode> {
    
    private final String userName;

    public OTCGetCurrenciesOrder(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }
        
    @Override
    public void execute() {
        String phone = null;
        try {
            JsonNode user = mapper.readTree(UsersFolderLocator.getConfigFile(userName));
            if (user.has("phone")) {
                phone = user.get("phone").textValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCGetCurrenciesOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayNode currenciesOrder = mapper.createArrayNode();
        if (phone == null) {
            currenciesOrder.add("USD");
            currenciesOrder.add("BTC");
        } else {
            Iterator<JsonNode> currencies = ((ArrayNode) new OTCGetCurrencies(true).getResponse()).iterator();
            while (currencies.hasNext()) {
                JsonNode currency = currencies.next();
                String shortName = currency.get("shortName").textValue();
                boolean active = currency.get("active").booleanValue();
                String countryPhoneCode = null;
                if (currency.has("countryPhoneCode")) {
                    countryPhoneCode = currency.get("countryPhoneCode").textValue();
                }
                if (countryPhoneCode == null) {
                    continue;
                }
                if (!active) {
                    continue;
                }
                if (phone.startsWith(countryPhoneCode)) {
                    currenciesOrder.add(shortName);
                    break;
                }
            }
        }
        Set<String> currenciesReverseOrdered = new TreeSet<>(Collections.reverseOrder());
        if (!currenciesOrder.toString().contains("USD")) {
            currenciesReverseOrdered.add("00010__USD");
        }
        Map<String, Integer> currenciesBalanceWeight = new TreeMap<>();
        File[] userBalanceFolders = new File[]{UsersFolderLocator.getBalanceFolder(userName), UsersFolderLocator.getMCBalanceFolder(userName)};
        for (File userBalanceFolder : userBalanceFolders) {
            if (!userBalanceFolder.isDirectory()) {
                continue;
            }
            for (File userBalanceFile : userBalanceFolder.listFiles()) {
                if(!userBalanceFile.isFile()){
                    continue;
                }
                try {
                    JsonNode userBalance = mapper.readTree(userBalanceFile);
                    String currency = null;
                    if (userBalance.has("addedAmount") && userBalance.get("addedAmount").get("amount").doubleValue() != 0) {
                        currency = userBalance.get("addedAmount").get("currency").textValue();
                    }
                    if (userBalance.has("substractedAmount") && userBalance.get("substractedAmount").get("amount").doubleValue() != 0) {
                        currency = userBalance.get("substractedAmount").get("currency").textValue();
                    }
                    if (currency == null) {
                        continue;
                    }
                    if (!currenciesBalanceWeight.containsKey(currency)) {
                        currenciesBalanceWeight.put(currency, 0);
                    }
                    currenciesBalanceWeight.put(currency, currenciesBalanceWeight.get(currency) + 1);
                } catch (IOException ex) {
                    Logger.getLogger(OTCGetCurrenciesOrder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (String key : currenciesBalanceWeight.keySet()) {
            String value = Integer.toString(currenciesBalanceWeight.get(key));
            while (value.length() < 5) {
                value = "0" + value;
            }
            currenciesReverseOrdered.add(value + "__" + key);
        }
        for (String currency : currenciesReverseOrdered) {
            currency = currency.split("__")[1];
            if (!currenciesOrder.toString().contains(currency)) {
                currenciesOrder.add(currency);
            }
        }
        Iterator<JsonNode> currencies = ((ArrayNode) new OTCGetCurrencies(true).getResponse()).iterator();
        while (currencies.hasNext()) {
            JsonNode currency = currencies.next();
            String shortName = currency.get("shortName").textValue();
            boolean active = currency.get("active").booleanValue();
            if (!active) {
                continue;
            }
            if (currenciesOrder.toString().contains(shortName)) {
                continue;
            }
            currenciesOrder.add(shortName);
        }
        if (!currenciesOrder.toString().contains("BTC")) {
            currenciesOrder.add("BTC");
        }
        JsonNode offers = new OTCGetOffers(null, "MONEYCLICK", null, PaymentType.MAIN, false).getResponse();
        List<String> currencyAllowed = new ArrayList<>();
        currencyAllowed.add("BTC");
        Iterator<String> offersCurrenciesIterator = offers.fieldNames();
        while (offersCurrenciesIterator.hasNext()) {
            String offersCurrenciesIt = offersCurrenciesIterator.next();
            if (offers.get(offersCurrenciesIt).size() == 2) {
                currencyAllowed.add(offersCurrenciesIt);
            }
        }
        Iterator<JsonNode> currenciesOrderIterator = currenciesOrder.iterator();
        while (currenciesOrderIterator.hasNext()) {
            String currenciesOrderIt = currenciesOrderIterator.next().textValue();
            if (!currencyAllowed.contains(currenciesOrderIt)) {
                currenciesOrderIterator.remove();
            }
        }
        super.response = currenciesOrder;
    }
    
}
