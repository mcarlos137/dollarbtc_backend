/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class BankerGetDollarBTCPayments extends AbstractOperation<Object> {

    private final String userName, currency;

    public BankerGetDollarBTCPayments(String userName, String currency) {
        super(Object.class);
        this.userName = userName;
        this.currency = currency;
    }

    @Override
    protected void execute() {
        if (userName != null && currency == null) {
            super.response = this.method(userName);
        } else if (userName != null && currency != null) {
            super.response = this.method(userName, currency);
        }
    }

    private JsonNode method(String userName) {
        JsonNode payments = mapper.createObjectNode();
//        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        Set<String> userCurrencies = new HashSet<>();
        userCurrencies.add("VES");
        userCurrencies.add("COP");
        userCurrencies.add("CLP");
//        userCurrencies.add("PEN");
        userCurrencies.add("MXN");
        userCurrencies.add("ARS");
        for (String userCurrency : userCurrencies) {
            File currencyPaymentsFolder = BankersFolderLocator.getPaymentsCurrencyFolder(userName, userCurrency);
            if (!currencyPaymentsFolder.isDirectory()) {
                continue;
            }
            ((ObjectNode) payments).putArray(userCurrency);
            for (File currencyPaymentFolder : currencyPaymentsFolder.listFiles()) {
                if (!currencyPaymentFolder.isDirectory()) {
                    continue;
                }
                try {
                    JsonNode currencyPayment = mapper.readTree(new File(currencyPaymentFolder, "config.json"));
                    Iterator<JsonNode> currencyPaymentIterator = currencyPayment.get("types").iterator();
                    while (currencyPaymentIterator.hasNext()) {
                        JsonNode currencyPaymentIt = currencyPaymentIterator.next();
                        String type = currencyPaymentIt.get("name").textValue();
                        ((ObjectNode) currencyPaymentIt).put("type", type);
                        ((ObjectNode) currencyPaymentIt).remove("name");
                        Iterator<String> currencyPaymentFieldNameIterator = currencyPayment.fieldNames();
                        while (currencyPaymentFieldNameIterator.hasNext()) {
                            String currencyPaymentFieldNameIt = currencyPaymentFieldNameIterator.next();
                            if (currencyPaymentFieldNameIt.equals("types")) {
                                continue;
                            }
                            ((ObjectNode) currencyPaymentIt).set(currencyPaymentFieldNameIt, currencyPayment.get(currencyPaymentFieldNameIt));
                        }
                        ((ArrayNode) payments.get(userCurrency)).add(currencyPaymentIt);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BankerGetDollarBTCPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return payments;
    }

    private ArrayNode method(String userName, String currency) {
//        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
//        if (!userCurrencies.contains(currency)) {
//            mapper.createArrayNode();
//        }
        File paymentsFolder = BankersFolderLocator.getPaymentsCurrencyFolder(userName, currency);;
        ArrayNode payments = mapper.createArrayNode();
        if (paymentsFolder.isDirectory()) {
            for (File paymentFolder : paymentsFolder.listFiles()) {
                try {
                    JsonNode payment = mapper.readTree(new File(paymentFolder, "config.json"));
                    Iterator<JsonNode> currencyPaymentIterator = payment.get("types").iterator();
                    while (currencyPaymentIterator.hasNext()) {
                        JsonNode currencyPaymentIt = currencyPaymentIterator.next();
                        String type = currencyPaymentIt.get("name").textValue();
                        ((ObjectNode) currencyPaymentIt).put("type", type);
                        ((ObjectNode) currencyPaymentIt).remove("name");
                        Iterator<String> currencyPaymentFieldNameIterator = payment.fieldNames();
                        while (currencyPaymentFieldNameIterator.hasNext()) {
                            String currencyPaymentFieldNameIt = currencyPaymentFieldNameIterator.next();
                            if (currencyPaymentFieldNameIt.equals("types")) {
                                continue;
                            }
                            ((ObjectNode) currencyPaymentIt).set(currencyPaymentFieldNameIt, payment.get(currencyPaymentFieldNameIt));
                        }
                        payments.add(currencyPaymentIt);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BankerGetDollarBTCPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return payments;
    }

}
