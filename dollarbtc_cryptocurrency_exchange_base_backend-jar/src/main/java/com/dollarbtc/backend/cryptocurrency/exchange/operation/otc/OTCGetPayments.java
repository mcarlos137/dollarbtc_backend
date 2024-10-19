/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class OTCGetPayments extends AbstractOperation<Object> {

    private final String userName, currency;

    public OTCGetPayments(String userName, String currency) {
        super(Object.class);
        this.userName = userName;
        this.currency = currency;
    }

    @Override
    public void execute(){
        if(currency == null){
            super.response = method(userName);
        } else {
            super.response = method(userName, currency);
        }
    }
    
    private ArrayNode method(String userName, String currency) {
        if (userName.equals("dollarBTC")) {
            ArrayNode payments = mapper.createArrayNode();
            Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
            while (operatorsIterator.hasNext()) {
                JsonNode operatorsIt = operatorsIterator.next();
                String operatorName = operatorsIt.textValue();
                File paymentsFolder = new File(new File(OTCFolderLocator.getFolder(operatorName), currency), "Payments");

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
                            Logger.getLogger(OTCGetPayments.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            return payments;
        } else {
            File paymentsFile = new File(new File(UsersFolderLocator.getOTCFolder(userName), currency), "payments.json");
            if (paymentsFile.isFile()) {
                try {
                    ArrayNode payments = mapper.createArrayNode();
                    Iterator<JsonNode> paymentsIterator = mapper.readTree(paymentsFile).iterator();
                    while (paymentsIterator.hasNext()) {
                        JsonNode paymentsIt = paymentsIterator.next();
                        if (paymentsIt.has("plaid_accessToken")) {
                            ((ObjectNode) paymentsIt).remove("plaid_accessToken");
                        }
                        if (paymentsIt.has("plaid_itemId")) {
                            ((ObjectNode) paymentsIt).remove("plaid_itemId");
                        }
                        payments.add(paymentsIt);
                    }
                    return payments;
                } catch (IOException ex) {
                    Logger.getLogger(OTCGetPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return mapper.createArrayNode();
    }
    
    private JsonNode method(String userName) {
        JsonNode payments = mapper.createObjectNode();
        File baseFolder;
        if (userName.equals("dollarBTC")) {
            Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
            while (operatorsIterator.hasNext()) {
                JsonNode operatorsIt = operatorsIterator.next();
                String operatorName = operatorsIt.textValue();
                baseFolder = OTCFolderLocator.getFolder(operatorName);
                for (File currencyFolder : baseFolder.listFiles()) {
                    if (!currencyFolder.isDirectory() || currencyFolder.getName().equals("Operations")) {
                        continue;
                    }
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
                            Logger.getLogger(OTCGetPayments.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } else {
            baseFolder = UsersFolderLocator.getOTCFolder(userName);
            if (baseFolder.isDirectory()) {
                for (File currencyFolder : baseFolder.listFiles()) {
                    if (!currencyFolder.isDirectory() || currencyFolder.getName().equals("Operations")) {
                        continue;
                    }
                    File paymentsFile = new File(currencyFolder, "payments.json");
                    if (paymentsFile.isFile()) {
                        ArrayNode paymentss = mapper.createArrayNode();
                        try {
                            Iterator<JsonNode> paymentsIterator = mapper.readTree(paymentsFile).iterator();
                            while (paymentsIterator.hasNext()) {
                                JsonNode paymentsIt = paymentsIterator.next();
                                if (paymentsIt.has("plaid_accessToken")) {
                                    ((ObjectNode) paymentsIt).remove("plaid_accessToken");
                                }
                                if (paymentsIt.has("plaid_itemId")) {
                                    ((ObjectNode) paymentsIt).remove("plaid_itemId");
                                }
                                paymentss.add(paymentsIt);
                            }
                            ((ObjectNode) payments).putArray(currencyFolder.getName()).addAll(paymentss);
                        } catch (IOException ex) {
                            Logger.getLogger(OTCGetPayments.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        return payments;
    }

}
