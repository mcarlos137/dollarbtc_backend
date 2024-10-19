/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCGetDollarBTCPaymentBalances extends AbstractOperation<ArrayNode> {
    
    private final String currency, initTimestamp, finalTimestamp;

    public OTCGetDollarBTCPaymentBalances(String currency, String initTimestamp, String finalTimestamp) {
        super(ArrayNode.class);
        this.currency = currency;
        this.initTimestamp = initTimestamp;
        this.finalTimestamp = finalTimestamp;
    }

    @Override
    protected void execute() {
        if(initTimestamp == null && finalTimestamp == null){
            super.response = method(currency);
        } else if(initTimestamp != null && finalTimestamp != null){
            super.response = method(currency, initTimestamp, finalTimestamp);
        }
    }
        
    private ArrayNode method(String currency) {
        ArrayNode dollarBTCPaymentsBalance = mapper.createArrayNode();
        File otcCurrencyPaymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, currency);
        if (!otcCurrencyPaymentsFolder.isDirectory()) {
            return dollarBTCPaymentsBalance;
        }
        for (File otcCurrencyPaymentFolder : otcCurrencyPaymentsFolder.listFiles()) {
            if (!otcCurrencyPaymentFolder.isDirectory()) {
                continue;
            }
            JsonNode dollarBTCPaymentBalance = mapper.createObjectNode();
            ((ObjectNode) dollarBTCPaymentBalance).put("id", otcCurrencyPaymentFolder.getName());
            ((ObjectNode) dollarBTCPaymentBalance).set("balance", BaseOperation.getBalance(new File(otcCurrencyPaymentFolder, "Balance")));
            dollarBTCPaymentsBalance.add(dollarBTCPaymentBalance);
        }
        return dollarBTCPaymentsBalance;
    }

    private ArrayNode method(String currency, String initTimestamp, String finalTimestamp) {
        ArrayNode dollarBTCPaymentsBalance = mapper.createArrayNode();
        File otcCurrencyPaymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, currency);
        if (!otcCurrencyPaymentsFolder.isDirectory()) {
            return dollarBTCPaymentsBalance;
        }
        for (File otcCurrencyPaymentFolder : otcCurrencyPaymentsFolder.listFiles()) {
            if (!otcCurrencyPaymentFolder.isDirectory()) {
                continue;
            }
            JsonNode dollarBTCPaymentBalance = mapper.createObjectNode();
            ((ObjectNode) dollarBTCPaymentBalance).put("id", otcCurrencyPaymentFolder.getName());
            ((ObjectNode) dollarBTCPaymentBalance).set("balance", BaseOperation.getBalance(new File(otcCurrencyPaymentFolder, "Balance"), initTimestamp, finalTimestamp));
            dollarBTCPaymentsBalance.add(dollarBTCPaymentBalance);
        }
        return dollarBTCPaymentsBalance;
    }

}
