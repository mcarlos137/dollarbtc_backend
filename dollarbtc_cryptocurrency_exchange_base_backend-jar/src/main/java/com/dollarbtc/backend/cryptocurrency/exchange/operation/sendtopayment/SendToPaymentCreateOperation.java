/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.sendtopayment;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSendToPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SendToPaymentCreateOperation extends AbstractOperation<String> {

    private final MCUserSendToPaymentRequest mcUserSendToPaymentRequest;

    public SendToPaymentCreateOperation(MCUserSendToPaymentRequest mcUserSendToPaymentRequest) {
        super(String.class);
        this.mcUserSendToPaymentRequest = mcUserSendToPaymentRequest;
    }

    @Override
    protected void execute() {
        String inLimits = BaseOperation.inLimits(mcUserSendToPaymentRequest.getUserName(), mcUserSendToPaymentRequest.getCurrency(), mcUserSendToPaymentRequest.getAmount(), BalanceOperationType.SEND_TO_PAYMENT);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String operatorName = null;
        if (mcUserSendToPaymentRequest.getPaymentType() == null) {
            switch (mcUserSendToPaymentRequest.getPayment().get("bank").textValue()) {
                case "PAYPAL":
                    super.response = PaymentType.PAYPAL.name();
                    return;
            }
            Map<PaymentType, String> payments = new HashMap<>();
            Iterator<JsonNode> dollarBTCPaymentsIterator = new OTCGetDollarBTCPayments(mcUserSendToPaymentRequest.getCurrency()).getResponse().iterator();
            while (dollarBTCPaymentsIterator.hasNext()) {
                JsonNode dollarBTCPaymentsIt = dollarBTCPaymentsIterator.next();
                String id = dollarBTCPaymentsIt.get("id").textValue();
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "id: {0}", id);
                if (!dollarBTCPaymentsIt.has("bank")) {
                    continue;
                }
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "{0}", "99");
                String bank = dollarBTCPaymentsIt.get("bank").textValue();
                boolean active = dollarBTCPaymentsIt.get("active").booleanValue();
                boolean acceptOut = dollarBTCPaymentsIt.get("acceptOut").booleanValue();
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "bank: {0}", bank);
                if (!dollarBTCPaymentsIt.has("sendToPayments")) {
                    Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "NO SEND TO PAYMENTS: {0}", id);
                    continue;
                }
                ArrayNode sendToPayments = (ArrayNode) dollarBTCPaymentsIt.get("sendToPayments");
                if (!active || !acceptOut) {
                    Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "NO ACTIVE: {0} {1} {2}", new Object[]{id, sendToPayments, bank});
                    continue;
                }
                Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
                while (operatorsIterator.hasNext()) {
                    JsonNode operatorsIt = operatorsIterator.next();
                    operatorName = operatorsIt.textValue();
                    if (OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, mcUserSendToPaymentRequest.getCurrency(), id).isDirectory()) {
                        break;
                    } else {
                        operatorName = null;
                    }
                }
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "operatorName {0}", operatorName);
                Iterator<JsonNode> paymentBalanceIterator = BaseOperation.getBalance(OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, mcUserSendToPaymentRequest.getCurrency(), id)).iterator();
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "paymentBalanceIterator {0}", paymentBalanceIterator);
                boolean paymentHasEnoughBalance = false;
                while (paymentBalanceIterator.hasNext()) {
                    JsonNode paymentBalanceIt = paymentBalanceIterator.next();
                    if (mcUserSendToPaymentRequest.getCurrency().equals(paymentBalanceIt.get("currency").textValue())) {
                        if (mcUserSendToPaymentRequest.getAmount() <= paymentBalanceIt.get("amount").doubleValue()) {
                            Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "BALANCE: {0} {1} {2} {3}", new Object[]{id, sendToPayments, bank, paymentBalanceIt.get("amount").doubleValue()});
                            paymentHasEnoughBalance = true;
                        }
                    }
                }
                if (!paymentHasEnoughBalance) {
                    Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "NO BALANCE {0} {1} {2}", new Object[]{id, sendToPayments, bank});
                    continue;
                }
                boolean sameBank = false;
                if (bank.equals(mcUserSendToPaymentRequest.getPayment().get("bank").textValue())) {
                    Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "SAME BANK");
                    sameBank = true;
                }
                Iterator<JsonNode> sendToPaymentsIterator = sendToPayments.iterator();
                while (sendToPaymentsIterator.hasNext()) {
                    JsonNode sendToPaymentsIt = sendToPaymentsIterator.next();
                    PaymentType paymentType = PaymentType.valueOf(sendToPaymentsIt.get("type").textValue());
                    Double minPerOperationAmount = sendToPaymentsIt.get("minPerOperationAmount").doubleValue();
                    Double maxPerOperationAmount = sendToPaymentsIt.get("maxPerOperationAmount").doubleValue();
                    if (sameBank) {
                        if (paymentType.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK) || paymentType.equals(PaymentType.ACH) || paymentType.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                            Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "0");
                            payments.put(paymentType, id + "__" + minPerOperationAmount + "__" + maxPerOperationAmount);
                        }
                    } else {
                        if (!paymentType.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                            Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "1");
                            payments.put(paymentType, id + "__" + minPerOperationAmount + "__" + maxPerOperationAmount);
                        }
                    }
                }
            }
            PaymentType paymentType = null;
            Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "00");
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "01");
                paymentType = PaymentType.TRANSFER_WITH_SPECIFIC_BANK;
            }
            if (paymentType == null && payments.containsKey(PaymentType.ACH)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "02.1");
                paymentType = PaymentType.ACH_THIRD_ACCOUNT;
                if (mcUserSendToPaymentRequest.getPayment().has("verified")
                        || mcUserSendToPaymentRequest.getPayment().has("mcVerified")) {
                    Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "02.2");
                    paymentType = PaymentType.ACH;
                }
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_NATIONAL_BANK)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "03");
                paymentType = PaymentType.TRANSFER_NATIONAL_BANK;
            }
            if (paymentType == null && payments.containsKey(PaymentType.CASH_DEPOSIT)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "04");
                paymentType = PaymentType.CASH_DEPOSIT;
            }
            if (paymentType == null && payments.containsKey(PaymentType.CASHIER_CHECK_DEPOSIT)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "05");
                paymentType = PaymentType.CASHIER_CHECK_DEPOSIT;
            }
            if (paymentType == null && payments.containsKey(PaymentType.MONEY_ORDER)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "06");
                paymentType = PaymentType.MONEY_ORDER;
            }
            if (paymentType == null && payments.containsKey(PaymentType.PERSONAL_CHECK_DEPOSIT)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "07");
                paymentType = PaymentType.PERSONAL_CHECK_DEPOSIT;
            }
            if (paymentType == null && payments.containsKey(PaymentType.WIRE_TRANSFER)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "08");
                paymentType = PaymentType.WIRE_TRANSFER;
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_INTERNATIONAL_BANK)) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "09");
                paymentType = PaymentType.TRANSFER_INTERNATIONAL_BANK;
            }
            if (paymentType == null) {
                Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, "THERE IS NO DOLLARBTC SEND TO PAYMENT TO THIS CURRENCY");
                super.response = "THERE IS NO DOLLARBTC SEND TO PAYMENT TO THIS CURRENCY";
                return;
            }
            Logger.getLogger(SendToPaymentCreateOperation.class.getName()).log(Level.INFO, paymentType.name());
            PaymentType paymentTypee = paymentType;
            if (paymentTypee.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                paymentTypee = PaymentType.ACH;
            }
            Double minPerOperationAmount = Double.parseDouble(payments.get(paymentTypee).split("__")[1]);
            Double maxPerOperationAmount = Double.parseDouble(payments.get(paymentTypee).split("__")[2]);
            if (mcUserSendToPaymentRequest.getAmount() < minPerOperationAmount || mcUserSendToPaymentRequest.getAmount() > maxPerOperationAmount) {
                super.response = "AMOUNT MUST BE BETWEEN " + minPerOperationAmount + " AND " + maxPerOperationAmount + " " + mcUserSendToPaymentRequest.getCurrency();
                return;
            }
            if (mcUserSendToPaymentRequest.isMultiOperator()) {
                super.response = paymentType.name() + "____" + operatorName;
                return;
            }
            super.response = paymentType.name();
            return;
        }
        String id = BaseOperation.getId();
        super.response = createOperation(
                id,
                mcUserSendToPaymentRequest.getUserName(),
                mcUserSendToPaymentRequest.getCurrency(),
                mcUserSendToPaymentRequest.getAmount(),
                mcUserSendToPaymentRequest.getDescription(),
                mcUserSendToPaymentRequest.getPayment(),
                BaseOperation.getChargesNew(mcUserSendToPaymentRequest.getCurrency(), mcUserSendToPaymentRequest.getAmount(), BalanceOperationType.SEND_TO_PAYMENT, null, "OPERATOR__OPERATION__" + id, null, null),
                mcUserSendToPaymentRequest.getOperatorName()
        );
    }

    private static String createOperation(String id, String userName, String currency, Double amount, String description, JsonNode clientPayment, JsonNode charges, String operatorName) {
        ObjectNode additionals = new ObjectMapper().createObjectNode();
        additionals.put("operationId", id);
        additionals.set("clientPayment", clientPayment);
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(userName),
                currency,
                amount,
                BalanceOperationType.SEND_TO_PAYMENT,
                BalanceOperationStatus.PROCESSING,
                "SEND TO PAYMENT DESCRIPTION " + description,
                null,
                false,
                charges,
                false,
                additionals
        );
        if (!substractToBalance.equals("OK")) {
            return substractToBalance;
        }
        JsonNode operation = new ObjectMapper().createObjectNode();
        String timestamp = DateUtil.getCurrentDate();
        ((ObjectNode) operation).put("id", id);
        ((ObjectNode) operation).put("userName", userName);
        ((ObjectNode) operation).put("currency", currency);
        ((ObjectNode) operation).put("amount", amount);
        ((ObjectNode) operation).put("timestamp", timestamp);
        ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_FOR_PAYMENT.name());
        if (!clientPayment.has("mcVerified") || !clientPayment.get("mcVerified").booleanValue()) {
            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_TO_START_OPERATION.name());
        }
        ((ObjectNode) operation).put("otcOperationType", OTCOperationType.SEND_TO_PAYMENT.name());
        ((ObjectNode) operation).set("clientPayment", clientPayment);
        ((ObjectNode) operation).set("charges", charges);
        File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(userName, currency, OTCOperationType.SEND_TO_PAYMENT.name());
        FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
        BaseOperation.createOperationInCentralFolder(operation, operatorName);
        BaseOperation.createIndexesInCentralFolder(operation, operatorName);
        String message = "Operation id " + id + " was created";
        new UserPostMessage(userName, message, null).getResponse();
        return id;
    }

}
