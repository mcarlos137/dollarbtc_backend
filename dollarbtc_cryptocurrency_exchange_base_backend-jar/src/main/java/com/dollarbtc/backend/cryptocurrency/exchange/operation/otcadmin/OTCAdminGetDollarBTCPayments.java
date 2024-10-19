/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminGetDollarBTCPayments extends AbstractOperation<Object> {

    private final String userName, currency;

    public OTCAdminGetDollarBTCPayments(String userName, String currency) {
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
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        for (String userCurrency : userCurrencies) {
            File currencyFolder = OTCFolderLocator.getCurrencyFolder(null, userCurrency);
            File currencyPaymentsFolder = new File(currencyFolder, "Payments");
            if (!currencyPaymentsFolder.isDirectory()) {
                continue;
            }
            ((ObjectNode) payments).putArray(currencyFolder.getName());
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
                        ((ArrayNode) payments.get(currencyFolder.getName())).add(currencyPaymentIt);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(OTCAdminGetDollarBTCPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return payments;
    }

    private ArrayNode method(String userName, String currency) {
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        if (!userCurrencies.contains(currency)) {
            mapper.createArrayNode();
        }
        File paymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, currency);
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
                    Logger.getLogger(OTCAdminGetDollarBTCPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return payments;
    }

}
