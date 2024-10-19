/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.payment;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga.ConsultaProductoOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentBank;
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
public class PaymentGetBalance extends AbstractOperation<JsonNode> {

    private final String userName, currency, id;
    private final PaymentBank paymentBank;

    public PaymentGetBalance(String userName, String currency, String id, PaymentBank paymentBank) {
        super(JsonNode.class);
        this.userName = userName;
        this.currency = currency;
        this.id = id;
        this.paymentBank = paymentBank;
    }

    @Override
    protected void execute() {
        JsonNode balance = mapper.createObjectNode();
        File userOTCCurrencyPaymentsFile = UsersFolderLocator.getOTCCurrencyPaymentsFile(userName, currency);
        try {
            JsonNode payment = null;
            ArrayNode userOTCCurrencyPayments = (ArrayNode) mapper.readTree(userOTCCurrencyPaymentsFile);
            Iterator<JsonNode> userOTCCurrencyPaymentsIterator = userOTCCurrencyPayments.iterator();
            while (userOTCCurrencyPaymentsIterator.hasNext()) {
                JsonNode userOTCCurrencyPaymentsIt = userOTCCurrencyPaymentsIterator.next();
                if (!userOTCCurrencyPaymentsIt.get("id").textValue().equals(id)) {
                    continue;
                }
                payment = userOTCCurrencyPaymentsIt;
            }
            if (payment == null) {
                super.response = balance;
                return;
            }

            getBalanceApiRequest(balance, payment.get("accountNumber").textValue(), paymentBank);
            ((ObjectNode) balance).put("paymentId", id);
            ((ObjectNode) balance).put("currency", currency);
            super.response = balance;
            return;
        } catch (IOException ex) {
            Logger.getLogger(PaymentGetBalance.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = balance;
    }
    
    private static void getBalanceApiRequest(JsonNode balance, String accountNumber, PaymentBank paymentBank) {
        switch (paymentBank) {
            case BANCAMIGA:
                ConsultaProductoOperation consultaProductoOperation = new ConsultaProductoOperation(accountNumber);
                Iterator<JsonNode> consultaProductoDataIterator = consultaProductoOperation.getResponse().get("data").iterator();
                while (consultaProductoDataIterator.hasNext()) {
                    JsonNode consultaProductoDataIt = consultaProductoDataIterator.next();
                    ((ObjectNode) balance).put("status", consultaProductoDataIt.get("STATUS").textValue());
                    ((ObjectNode) balance).put("amount", consultaProductoDataIt.get("SALDO1").textValue());
                }
                break;
        }
    }

}
