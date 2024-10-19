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
public class SendToPaymentCreateOperationNew extends AbstractOperation<String> {

    private final MCUserSendToPaymentRequest mcUserSendToPaymentRequest;

    public SendToPaymentCreateOperationNew(MCUserSendToPaymentRequest mcUserSendToPaymentRequest) {
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
        PaymentType paymentType = null;
        String operatorName = null;
        if (mcUserSendToPaymentRequest.getPaymentType() == null) {
            Map<PaymentType, String> payments = new HashMap<>();
            Iterator<JsonNode> dollarBTCPaymentsIterator = new OTCGetDollarBTCPayments(mcUserSendToPaymentRequest.getCurrency()).getResponse().iterator();
            while (dollarBTCPaymentsIterator.hasNext()) {
                JsonNode dollarBTCPaymentsIt = dollarBTCPaymentsIterator.next();
                String id = dollarBTCPaymentsIt.get("id").textValue();
                if (!dollarBTCPaymentsIt.has("bank")) {
                    continue;
                }
                String bank = dollarBTCPaymentsIt.get("bank").textValue();
                boolean active = dollarBTCPaymentsIt.get("active").booleanValue();
                boolean acceptOut = dollarBTCPaymentsIt.get("acceptOut").booleanValue();
                if (!dollarBTCPaymentsIt.has("sendToPayments")) {
                    continue;
                }
                ArrayNode sendToPayments = (ArrayNode) dollarBTCPaymentsIt.get("sendToPayments");
                if (!active || !acceptOut) {
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
                Iterator<JsonNode> paymentBalanceIterator = BaseOperation.getBalance(OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, mcUserSendToPaymentRequest.getCurrency(), id)).iterator();
                boolean paymentHasEnoughBalance = false;
                while (paymentBalanceIterator.hasNext()) {
                    JsonNode paymentBalanceIt = paymentBalanceIterator.next();
                    if (mcUserSendToPaymentRequest.getCurrency().equals(paymentBalanceIt.get("currency").textValue())) {
                        if (mcUserSendToPaymentRequest.getAmount() <= paymentBalanceIt.get("amount").doubleValue()) {
                            paymentHasEnoughBalance = true;
                        }
                    }
                }
                if (!paymentHasEnoughBalance) {
                    continue;
                }
                boolean sameBank = false;
                if (bank.equals(mcUserSendToPaymentRequest.getPayment().get("bank").textValue())) {
                    sameBank = true;
                }
                Iterator<JsonNode> sendToPaymentsIterator = sendToPayments.iterator();
                while (sendToPaymentsIterator.hasNext()) {
                    JsonNode sendToPaymentsIt = sendToPaymentsIterator.next();
                    PaymentType paymentTypee = PaymentType.valueOf(sendToPaymentsIt.get("type").textValue());
                    Double minPerOperationAmount = sendToPaymentsIt.get("minPerOperationAmount").doubleValue();
                    Double maxPerOperationAmount = sendToPaymentsIt.get("maxPerOperationAmount").doubleValue();
                    if (sameBank) {
                        if (paymentTypee.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK) || paymentTypee.equals(PaymentType.ACH) || paymentTypee.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                            payments.put(paymentTypee, id + "__" + minPerOperationAmount + "__" + maxPerOperationAmount);
                        }
                    } else {
                        if (!paymentTypee.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                            payments.put(paymentTypee, id + "__" + minPerOperationAmount + "__" + maxPerOperationAmount);
                        }
                    }
                }
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                paymentType = PaymentType.GENERIC_THIRD_ACCOUNT;
                payments.put(PaymentType.GENERIC_THIRD_ACCOUNT, payments.get(PaymentType.TRANSFER_WITH_SPECIFIC_BANK));
                if ((mcUserSendToPaymentRequest.getPayment().has("verified")
                        && mcUserSendToPaymentRequest.getPayment().get("verified").booleanValue())
                        || (mcUserSendToPaymentRequest.getPayment().has("mcVerified")
                        && mcUserSendToPaymentRequest.getPayment().get("mcVerified").booleanValue())) {
                    paymentType = PaymentType.TRANSFER_WITH_SPECIFIC_BANK;
                }
                if(mcUserSendToPaymentRequest.getPayment().has("own") && mcUserSendToPaymentRequest.getPayment().get("own").booleanValue()){
                    paymentType = PaymentType.TRANSFER_WITH_SPECIFIC_BANK;
                }
            }
            if (paymentType == null && payments.containsKey(PaymentType.ACH)) {
                paymentType = PaymentType.ACH_THIRD_ACCOUNT;
                if ((mcUserSendToPaymentRequest.getPayment().has("verified")
                        && mcUserSendToPaymentRequest.getPayment().get("verified").booleanValue())
                        || (mcUserSendToPaymentRequest.getPayment().has("mcVerified")
                        && mcUserSendToPaymentRequest.getPayment().get("mcVerified").booleanValue())) {
                    paymentType = PaymentType.ACH;
                }
                if(mcUserSendToPaymentRequest.getPayment().has("own") && mcUserSendToPaymentRequest.getPayment().get("own").booleanValue()){
                    paymentType = PaymentType.ACH;
                }
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_NATIONAL_BANK)) {
                paymentType = PaymentType.GENERIC_THIRD_ACCOUNT;
                payments.put(PaymentType.GENERIC_THIRD_ACCOUNT, payments.get(PaymentType.TRANSFER_NATIONAL_BANK));
                if ((mcUserSendToPaymentRequest.getPayment().has("verified")
                        && mcUserSendToPaymentRequest.getPayment().get("verified").booleanValue())
                        || (mcUserSendToPaymentRequest.getPayment().has("mcVerified")
                        && mcUserSendToPaymentRequest.getPayment().get("mcVerified").booleanValue())) {
                    paymentType = PaymentType.TRANSFER_NATIONAL_BANK;
                }
                if(mcUserSendToPaymentRequest.getPayment().has("own") && mcUserSendToPaymentRequest.getPayment().get("own").booleanValue()){
                    paymentType = PaymentType.TRANSFER_NATIONAL_BANK;
                }
            }
            if (paymentType == null && payments.containsKey(PaymentType.CASH_DEPOSIT)) {
                paymentType = PaymentType.CASH_DEPOSIT;
            }
            if (paymentType == null && payments.containsKey(PaymentType.CASHIER_CHECK_DEPOSIT)) {
                paymentType = PaymentType.CASHIER_CHECK_DEPOSIT;
            }
            if (paymentType == null && payments.containsKey(PaymentType.MONEY_ORDER)) {
                paymentType = PaymentType.MONEY_ORDER;
            }
            if (paymentType == null && payments.containsKey(PaymentType.PERSONAL_CHECK_DEPOSIT)) {
                paymentType = PaymentType.PERSONAL_CHECK_DEPOSIT;
            }
            if (paymentType == null && payments.containsKey(PaymentType.WIRE_TRANSFER)) {
                paymentType = PaymentType.GENERIC_THIRD_ACCOUNT;
                payments.put(PaymentType.GENERIC_THIRD_ACCOUNT, payments.get(PaymentType.WIRE_TRANSFER));
                if ((mcUserSendToPaymentRequest.getPayment().has("verified")
                        && mcUserSendToPaymentRequest.getPayment().get("verified").booleanValue())
                        || (mcUserSendToPaymentRequest.getPayment().has("mcVerified")
                        && mcUserSendToPaymentRequest.getPayment().get("mcVerified").booleanValue())) {
                    paymentType = PaymentType.WIRE_TRANSFER;
                }
                if(mcUserSendToPaymentRequest.getPayment().has("own") && mcUserSendToPaymentRequest.getPayment().get("own").booleanValue()){
                    paymentType = PaymentType.WIRE_TRANSFER;
                }
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_INTERNATIONAL_BANK)) {
                paymentType = PaymentType.TRANSFER_INTERNATIONAL_BANK;
            }
            if (paymentType == null && payments.containsKey(PaymentType.PAYPAL)) {
                paymentType = PaymentType.PAYPAL;
            }
            if (paymentType == null) {
                super.response = "THERE IS NO DOLLARBTC SEND TO PAYMENT TO THIS CURRENCY";
                return;
            }
            Logger.getLogger(SendToPaymentCreateOperationNew.class.getName()).log(Level.INFO, paymentType.name());
            PaymentType paymentTypee = paymentType;
            if (paymentTypee.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                paymentTypee = PaymentType.ACH;
            }
            Double minPerOperationAmount = Double.parseDouble(payments.get(paymentTypee).split("__")[1]);
            Double maxPerOperationAmount = Double.parseDouble(payments.get(paymentTypee).split("__")[2]);
            if (mcUserSendToPaymentRequest.getAmount() < minPerOperationAmount || mcUserSendToPaymentRequest.getAmount() > maxPerOperationAmount) {
                if (paymentType.equals(PaymentType.ACH)) {
                    super.response = paymentType.name() + "__" + PaymentType.ACH_EXPRESS.name() + "____" + minPerOperationAmount + "__" + maxPerOperationAmount;
                    return;
                }
                if (paymentType.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                    super.response = paymentType.name() + "__" + PaymentType.ACH_THIRD_ACCOUNT_EXPRESS.name() + "____" + minPerOperationAmount + "__" + maxPerOperationAmount;
                    return;
                }
                super.response = paymentType.name() + "____" + minPerOperationAmount + "__" + maxPerOperationAmount;
                return;
            }
            if (mcUserSendToPaymentRequest.isMultiOperator()) {
                if (paymentType.equals(PaymentType.ACH)) {
                    super.response = paymentType.name() + "__" + PaymentType.ACH_EXPRESS.name() + "____" + operatorName;
                    return;
                }
                if (paymentType.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                    super.response = paymentType.name() + "__" + PaymentType.ACH_THIRD_ACCOUNT_EXPRESS.name() + "____" + operatorName;
                    return;
                }
                super.response = paymentType.name() + "____" + operatorName;
                return;
            }
            if (paymentType.equals(PaymentType.ACH)) {
                super.response = paymentType.name() + "__" + PaymentType.ACH_EXPRESS.name();
                return;
            }
            if (paymentType.equals(PaymentType.ACH_THIRD_ACCOUNT)) {
                super.response = paymentType.name() + "__" + PaymentType.ACH_THIRD_ACCOUNT_EXPRESS.name();
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
                BaseOperation.getChargesNew(mcUserSendToPaymentRequest.getCurrency(), mcUserSendToPaymentRequest.getAmount(), BalanceOperationType.SEND_TO_PAYMENT, mcUserSendToPaymentRequest.getPaymentType(), "OPERATOR__OPERATION__" + id, null, null),
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
        if (clientPayment.has("own") && clientPayment.get("own").booleanValue()) {
            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_FOR_PAYMENT.name());
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
