/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerSendToPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.substractToBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class BrokerSendToPayment extends AbstractOperation<String> {

    private final BrokerSendToPaymentRequest brokerSendToPaymentRequest;

    public BrokerSendToPayment(BrokerSendToPaymentRequest brokerSendToPaymentRequest) {
        super(String.class);
        this.brokerSendToPaymentRequest = brokerSendToPaymentRequest;
    }

    @Override
    protected void execute() {
        String inLimits = BaseOperation.inLimits(brokerSendToPaymentRequest.getUserName(), brokerSendToPaymentRequest.getCurrency(), brokerSendToPaymentRequest.getAmount(), BalanceOperationType.BROKER_SEND_TO_PAYMENT);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        JsonNode userBalanceAvailableAmounts = new UserGetBalance(brokerSendToPaymentRequest.getUserName()).getResponse().get("availableAmounts");
        Iterator<JsonNode> userBalanceAvailableAmountsIterator = userBalanceAvailableAmounts.iterator();
        while (userBalanceAvailableAmountsIterator.hasNext()) {
            JsonNode userBalanceAvailableAmountsIt = userBalanceAvailableAmountsIterator.next();
            if (userBalanceAvailableAmountsIt.get("currency").textValue().equals("BTC")) {
                if (userBalanceAvailableAmountsIt.get("amount").doubleValue() < 0.0) {
                    super.response = "USER HAS BTC NEGATIVE BALANCE. BTC BALANCE HAS TO BE EQUAL OR GREATER THAN ZERO";
                    return;
                }
                break;
            }
        }
        if (brokerSendToPaymentRequest.getPaymentType() == null) {
            Map<PaymentType, String> payments = new HashMap<>();
            Iterator<JsonNode> dollarBTCPaymentsIterator = new OTCGetDollarBTCPayments(brokerSendToPaymentRequest.getCurrency()).getResponse().iterator();
            while (dollarBTCPaymentsIterator.hasNext()) {
                JsonNode dollarBTCPaymentsIt = dollarBTCPaymentsIterator.next();
                String id = dollarBTCPaymentsIt.get("id").textValue();
                String bank = dollarBTCPaymentsIt.get("bank").textValue();
                boolean active = dollarBTCPaymentsIt.get("active").booleanValue();
                boolean acceptOut = dollarBTCPaymentsIt.get("acceptOut").booleanValue();
                if (!dollarBTCPaymentsIt.has("sendToPayments")) {
                    Logger.getLogger(BrokerSendToPayment.class.getName()).log(Level.INFO, "NO SEND TO PAYMENTS: {0}", id);
                    continue;
                }
                ArrayNode sendToPayments = (ArrayNode) dollarBTCPaymentsIt.get("sendToPayments");
                if (!active || !acceptOut) {
                    Logger.getLogger(BrokerSendToPayment.class.getName()).log(Level.INFO, "NO ACTIVE: {0} {1} {2}", new Object[]{id, sendToPayments, bank});
                    continue;
                }
                Iterator<JsonNode> paymentBalanceIterator = BaseOperation.getBalance(OTCFolderLocator.getCurrencyPaymentBalanceFolder(null, brokerSendToPaymentRequest.getCurrency(), id)).iterator();
                boolean paymentHasEnoughBalance = false;
                while (paymentBalanceIterator.hasNext()) {
                    JsonNode paymentBalanceIt = paymentBalanceIterator.next();
                    if (brokerSendToPaymentRequest.getCurrency().equals(paymentBalanceIt.get("currency").textValue())) {
                        if (brokerSendToPaymentRequest.getAmount() <= paymentBalanceIt.get("amount").doubleValue()) {
                            Logger.getLogger(BrokerSendToPayment.class.getName()).log(Level.INFO, "BALANCE: {0} {1} {2} {3}", new Object[]{id, sendToPayments, bank, paymentBalanceIt.get("amount").doubleValue()});
                            paymentHasEnoughBalance = true;
                        }
                    }
                }
                if (!paymentHasEnoughBalance) {
                    Logger.getLogger(BrokerSendToPayment.class.getName()).log(Level.INFO, "NO BALANCE {0} {1} {2}", new Object[]{id, sendToPayments, bank});
                    continue;
                }
                boolean sameBank = false;
                if (bank.equals(brokerSendToPaymentRequest.getPayment().get("bank").textValue())) {
                    sameBank = true;
                }
                Iterator<JsonNode> sendToPaymentsIterator = sendToPayments.iterator();
                while (sendToPaymentsIterator.hasNext()) {
                    JsonNode sendToPaymentsIt = sendToPaymentsIterator.next();
                    PaymentType paymentType = PaymentType.valueOf(sendToPaymentsIt.get("type").textValue());
                    Double minPerOperationAmount = sendToPaymentsIt.get("minPerOperationAmount").doubleValue();
                    Double maxPerOperationAmount = sendToPaymentsIt.get("maxPerOperationAmount").doubleValue();
                    if (sameBank) {
                        if (paymentType.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                            payments.put(paymentType, id + "__" + minPerOperationAmount + "__" + maxPerOperationAmount);
                        }
                    } else {
                        if (!paymentType.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                            payments.put(paymentType, id + "__" + minPerOperationAmount + "__" + maxPerOperationAmount);
                        }
                    }
                }
            }
            PaymentType paymentType = null;
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_WITH_SPECIFIC_BANK)) {
                paymentType = PaymentType.TRANSFER_WITH_SPECIFIC_BANK;
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_NATIONAL_BANK)) {
                paymentType = PaymentType.TRANSFER_NATIONAL_BANK;
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
                paymentType = PaymentType.WIRE_TRANSFER;
            }
            if (paymentType == null && payments.containsKey(PaymentType.TRANSFER_INTERNATIONAL_BANK)) {
                paymentType = PaymentType.TRANSFER_INTERNATIONAL_BANK;
            }
            if (paymentType == null) {
                Logger.getLogger(BrokerSendToPayment.class.getName()).log(Level.INFO, "THERE IS NO DOLLARBTC SEND TO PAYMENT TO THIS CURRENCY");
                super.response = "THERE IS NO DOLLARBTC SEND TO PAYMENT TO THIS CURRENCY";
                return;
            }
            Logger.getLogger(BrokerSendToPayment.class.getName()).log(Level.INFO, paymentType.name());
            Double minPerOperationAmount = Double.parseDouble(payments.get(paymentType).split("__")[1]);
            Double maxPerOperationAmount = Double.parseDouble(payments.get(paymentType).split("__")[2]);
            if (brokerSendToPaymentRequest.getAmount() < minPerOperationAmount || brokerSendToPaymentRequest.getAmount() > maxPerOperationAmount) {
                super.response = "AMOUNT MUST BE BETWEEN " + minPerOperationAmount + " AND " + maxPerOperationAmount + " " + brokerSendToPaymentRequest.getCurrency();
                return;
            }
            super.response = paymentType.name();
            return;
        }
        JsonNode charges = BaseOperation.getChargesNew(brokerSendToPaymentRequest.getCurrency(), brokerSendToPaymentRequest.getAmount(), BalanceOperationType.BROKER_SEND_TO_PAYMENT, null, "OPERATOR__PAYMENT__" + brokerSendToPaymentRequest.getPayment().get("id").textValue(), null, null);
        Double amount = brokerSendToPaymentRequest.getAmount();
        Iterator<JsonNode> chargesIterator = charges.iterator();
        while (chargesIterator.hasNext()) {
            JsonNode chargesIt = chargesIterator.next();
            if (brokerSendToPaymentRequest.getCurrency().equals(chargesIt.get("currency").textValue())) {
                amount = amount - chargesIt.get("amount").doubleValue();
            }
        }
        String substractToBalance = substractToBalance(
                BrokersFolderLocator.getBalanceFolder(brokerSendToPaymentRequest.getUserName()),
                brokerSendToPaymentRequest.getCurrency(),
                amount,
                BalanceOperationType.BROKER_SEND_TO_PAYMENT,
                BalanceOperationStatus.OK,
                "SEND TO PAYMENT DESCRIPTION " + brokerSendToPaymentRequest.getDescription(),
                null,
                false,
                charges,
                false,
                null
        );
        if (!substractToBalance.equals("OK")) {
            super.response = substractToBalance;
            return;
        }
        JsonNode operation = mapper.createObjectNode();
        String id = BaseOperation.getId();
        String timestamp = DateUtil.getCurrentDate();
        ((ObjectNode) operation).put("id", id);
        ((ObjectNode) operation).put("userName", brokerSendToPaymentRequest.getUserName());
        ((ObjectNode) operation).put("currency", brokerSendToPaymentRequest.getCurrency());
        ((ObjectNode) operation).put("amount", amount);
        ((ObjectNode) operation).put("timestamp", timestamp);
        ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_FOR_PAYMENT.name());
        if (brokerSendToPaymentRequest.getPayment().has("emailReceiver")) {
            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_TO_START_OPERATION.name());
        }
        ((ObjectNode) operation).put("otcOperationType", OTCOperationType.SEND_TO_PAYMENT.name());
        ((ObjectNode) operation).set("clientPayment", brokerSendToPaymentRequest.getPayment());
        ((ObjectNode) operation).set("charges", charges);
        File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(brokerSendToPaymentRequest.getUserName(), brokerSendToPaymentRequest.getCurrency(), OTCOperationType.SEND_TO_PAYMENT.name());
        FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
        BaseOperation.createOperationInCentralFolder(operation, null);
        BaseOperation.createIndexesInCentralFolder(operation, null);
        String message = "Operation id " + id + " was created";
        new UserPostMessage(brokerSendToPaymentRequest.getUserName(), message, null).getResponse();
        super.response = id;
    }

}
