/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.payment;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.payment.PaymentCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga.ConsultaDocumentoOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga.LoginOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga.LogoffOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentBank;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class PaymentCreate extends AbstractOperation<JsonNode> {
    
    private final PaymentCreateRequest paymentCreateRequest;

    public PaymentCreate(PaymentCreateRequest paymentCreateRequest) {
        super(JsonNode.class);
        this.paymentCreateRequest = paymentCreateRequest;
    }

    @Override
    protected void execute() {
        JsonNode payment = createApiRequest(paymentCreateRequest.getBankLogin(), paymentCreateRequest.getBankPassword(), paymentCreateRequest.getCurrency(), paymentCreateRequest.getPaymentBank());
        if (payment == null) {
            super.response = mapper.createObjectNode();
            return;
        }
        ((ObjectNode) payment).put("bank", paymentCreateRequest.getPaymentBank().toString());
        ((ObjectNode) payment).put("verified", true);
        ((ObjectNode) payment).put("automatic", true);
        File userOTCFolder = UsersFolderLocator.getOTCFolder(paymentCreateRequest.getUserName());
        if (!userOTCFolder.isDirectory()) {
            super.response = mapper.createObjectNode();
            return;
        }
        File userOTCCurrencyFolder = FileUtil.createFolderIfNoExist(userOTCFolder, paymentCreateRequest.getCurrency());
        File userOTCCurrencyPaymentsFile = new File(userOTCCurrencyFolder, "payments.json");
        if (!userOTCCurrencyPaymentsFile.isFile()) {
            FileUtil.createFile(mapper.createArrayNode(), userOTCCurrencyPaymentsFile);
        }
        try {
            ArrayNode userOTCCurrencyPayments = (ArrayNode) mapper.readTree(userOTCCurrencyPaymentsFile);
            Iterator<JsonNode> userOTCCurrencyPaymentsIterator = userOTCCurrencyPayments.iterator();
            while (userOTCCurrencyPaymentsIterator.hasNext()) {
                JsonNode userOTCCurrencyPaymentsIt = userOTCCurrencyPaymentsIterator.next();
                if (userOTCCurrencyPaymentsIt.get("accountNumber").textValue().equals(payment.get("accountNumber").textValue())) {
                    super.response = userOTCCurrencyPaymentsIt; 
                    return;
                }
            }
            String id = BaseOperation.getId();
            ((ObjectNode) payment).put("id", id);
            ((ObjectNode) payment).put("type", paymentCreateRequest.getPaymentType().name());
            ((ObjectNode) payment).put("currency", paymentCreateRequest.getCurrency());
            userOTCCurrencyPayments.add(payment);
            FileUtil.editFile(userOTCCurrencyPayments, userOTCCurrencyPaymentsFile);
            super.response =  payment;
            return;
        } catch (IOException ex) {
            Logger.getLogger(PaymentCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }
    
    private JsonNode createApiRequest(String login, String password, String currency, PaymentBank paymentBank) {
        JsonNode payment = null;
        switch (paymentBank) {
            case BANCAMIGA:
                String token = null;
                try {
                    LoginOperation loginOperation = new LoginOperation(login, password);
                    JsonNode loginOperationResponse = loginOperation.getResponse();
                    Logger.getLogger(PaymentCreate.class.getName()).log(Level.INFO, "loginOperationResponse: {0}", loginOperationResponse);
                    if (!loginOperationResponse.has("ci")) {
                        return payment;
                    }
                    String accountHolderId = loginOperationResponse.get("ci").textValue();
                    token = loginOperationResponse.get("token").textValue();
                    ConsultaDocumentoOperation consultaDocumento = new ConsultaDocumentoOperation(accountHolderId);
                    JsonNode consultaDocumentoResponse = consultaDocumento.getResponse();
                    Logger.getLogger(PaymentCreate.class.getName()).log(Level.INFO, "consultaDocumentoResponse: {0}", consultaDocumentoResponse);
                    if (consultaDocumentoResponse.get("productos").get("PROD") instanceof ArrayNode) {
                        Iterator<JsonNode> consultaDocumentoProductosPRODIterator = consultaDocumentoResponse.get("productos").get("PROD").iterator();
                        while (consultaDocumentoProductosPRODIterator.hasNext()) {
                            JsonNode consultaDocumentoProductosPRODIt = consultaDocumentoProductosPRODIterator.next();
                            String accountType = consultaDocumentoProductosPRODIt.get("NOMBRE").textValue();
                            String accountStatus = consultaDocumentoProductosPRODIt.get("STATUS").textValue();
                            if (accountType.contains("USD") && !currency.equals("USD")) {
                                continue;
                            }
                            if (!accountStatus.equals("Activa")) {
                                continue;
                            }
                            String accountHolderName = consultaDocumentoResponse.get("data").get("NOMBRE").textValue();
                            if (consultaDocumentoResponse.get("data").has("APELLIDO")) {
                                accountHolderName = accountHolderName + " " + consultaDocumentoResponse.get("data").get("APELLIDO").textValue();
                            }
                            accountHolderName = accountHolderName.replaceAll(",", "");
                            payment = mapper.createObjectNode();
                            ((ObjectNode) payment).put("accountNumber", consultaDocumentoProductosPRODIt.get("NUMERO").textValue());
                            ((ObjectNode) payment).put("accountHolderName", accountHolderName);
                            ((ObjectNode) payment).put("accountHolderId", accountHolderId);
                            ((ObjectNode) payment).put("accountType", accountType);
                            ((ObjectNode) payment).put("accountStatus", accountStatus);
                            ((ObjectNode) payment).put("accountCurrency", consultaDocumentoResponse.get("data").get("DES_MONEDA").textValue());
                            ((ObjectNode) payment).put("accountBalance", consultaDocumentoProductosPRODIt.get("SALDO1").textValue());
                            break;
                        }
                    } else if (consultaDocumentoResponse.get("productos").get("PROD") instanceof JsonNode) {
                        String accountType = consultaDocumentoResponse.get("productos").get("PROD").get("NOMBRE").textValue();
                        String accountStatus = consultaDocumentoResponse.get("productos").get("PROD").get("STATUS").textValue();
                        if (accountType.contains("USD") && !currency.equals("USD")) {
                            return payment;
                        }
                        if (!accountStatus.equals("Activa")) {
                            return payment;
                        }
                        String accountHolderName = consultaDocumentoResponse.get("data").get("NOMBRE").textValue();
                        if (consultaDocumentoResponse.get("data").has("APELLIDO")) {
                            accountHolderName = accountHolderName + " " + consultaDocumentoResponse.get("data").get("APELLIDO").textValue();
                        }
                        accountHolderName = accountHolderName.replaceAll(",", "");
                        payment = mapper.createObjectNode();
                        ((ObjectNode) payment).put("accountNumber", consultaDocumentoResponse.get("productos").get("PROD").get("NUMERO").textValue());
                        ((ObjectNode) payment).put("accountHolderName", accountHolderName);
                        ((ObjectNode) payment).put("accountHolderId", accountHolderId);
                        ((ObjectNode) payment).put("accountType", accountType);
                        ((ObjectNode) payment).put("accountStatus", accountStatus);
                        ((ObjectNode) payment).put("accountCurrency", consultaDocumentoResponse.get("data").get("DES_MONEDA").textValue());
                        ((ObjectNode) payment).put("accountBalance", consultaDocumentoResponse.get("productos").get("PROD").get("SALDO1").textValue());
                    }
                } finally {
                    if (token != null) {
                        LogoffOperation logoff = new LogoffOperation(login, token);
                        JsonNode logoffResponse = logoff.getResponse();
                    }
                }
                break;
        }
        return payment;
    }
    
}
