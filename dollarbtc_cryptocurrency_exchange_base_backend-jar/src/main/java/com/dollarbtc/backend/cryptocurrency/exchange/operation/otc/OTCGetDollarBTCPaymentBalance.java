/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetDollarBTCPaymentBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class OTCGetDollarBTCPaymentBalance extends AbstractOperation<JsonNode> {

    private final OTCGetDollarBTCPaymentBalanceRequest otcGetDollarBTCPaymentBalanceRequest;

    public OTCGetDollarBTCPaymentBalance(OTCGetDollarBTCPaymentBalanceRequest otcGetDollarBTCPaymentBalanceRequest) {
        super(JsonNode.class);
        this.otcGetDollarBTCPaymentBalanceRequest = otcGetDollarBTCPaymentBalanceRequest;
    }

    @Override
    public void execute() {
        JsonNode dollarBTCPaymentBalance = mapper.createObjectNode();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            File otcCurrencyPaymentsFolder = new File(new File(OTCFolderLocator.getFolder(operator), otcGetDollarBTCPaymentBalanceRequest.getCurrency()), "Payments");
            Set<String> paymentsIds = new HashSet<>();
            for (String paymentId : otcGetDollarBTCPaymentBalanceRequest.getPaymentIds()) {
                if (paymentsIds.contains(paymentId)) {
                    continue;
                }
                paymentsIds.add(paymentId);
                File otcCurrencyPaymentFolder = new File(otcCurrencyPaymentsFolder, paymentId);
                if (!otcCurrencyPaymentFolder.isDirectory()) {
                    continue;
                }
                if(otcGetDollarBTCPaymentBalanceRequest.getInitTimestamp() != null || otcGetDollarBTCPaymentBalanceRequest.getFinalTimestamp() != null){
                    ((ObjectNode) dollarBTCPaymentBalance).set(paymentId, BaseOperation.getBalance(new File(otcCurrencyPaymentFolder, "Balance"), otcGetDollarBTCPaymentBalanceRequest.getInitTimestamp(), otcGetDollarBTCPaymentBalanceRequest.getFinalTimestamp()));
                } else {
                    ((ObjectNode) dollarBTCPaymentBalance).set(paymentId, BaseOperation.getBalance(new File(otcCurrencyPaymentFolder, "Balance")));
                }
            }
        }
        super.response = dollarBTCPaymentBalance;
    }

}
