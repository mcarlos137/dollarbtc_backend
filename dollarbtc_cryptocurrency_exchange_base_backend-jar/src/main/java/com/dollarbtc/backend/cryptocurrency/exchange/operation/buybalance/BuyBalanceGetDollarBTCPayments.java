/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.buybalance;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BuyBalanceGetDollarBTCPayments extends AbstractOperation<ArrayNode> {
    
    private final String userName, currency;

    public BuyBalanceGetDollarBTCPayments(String userName, String currency) {
        super(ArrayNode.class);
        this.userName = userName;
        this.currency = currency;
    }
    
    @Override
    protected void execute() {
        ArrayNode dollarBTCPayments = mapper.createArrayNode();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            File paymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(operator, currency);
            if (!paymentsFolder.isDirectory()) {
                continue;
            }
            for (File paymentFolder : paymentsFolder.listFiles()) {
                File paymentFile = new File(paymentFolder, "config.json");
                try {
                    JsonNode payment = mapper.readTree(paymentFile);
                    if (!payment.has("buyBalance")) {
                        continue;
                    }
                    if (!payment.get("active").booleanValue() || !payment.get("acceptIn").booleanValue()) {
                        continue;
                    }
                    dollarBTCPayments.add(payment);
                } catch (IOException ex) {
                    Logger.getLogger(BuyBalanceGetDollarBTCPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.response = dollarBTCPayments;
    }
    
}
