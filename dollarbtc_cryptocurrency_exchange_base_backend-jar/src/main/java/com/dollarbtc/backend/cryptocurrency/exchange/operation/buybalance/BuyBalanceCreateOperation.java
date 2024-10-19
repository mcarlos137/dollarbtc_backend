/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.buybalance;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.buyBalance.BuyBalanceCreateOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OperationMessageSide;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class BuyBalanceCreateOperation extends AbstractOperation<String> {

    private final BuyBalanceCreateOperationRequest buyBalanceCreateOperationRequest;

    public BuyBalanceCreateOperation(BuyBalanceCreateOperationRequest buyBalanceCreateOperationRequest) {
        super(String.class);
        this.buyBalanceCreateOperationRequest = buyBalanceCreateOperationRequest;
    }

    @Override
    protected void execute() {
        File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(buyBalanceCreateOperationRequest.getUserName(), buyBalanceCreateOperationRequest.getCurrency(), OTCOperationType.MC_BUY_BALANCE.name());
        if (!userOTCCurrencyOperationTypeFolder.isDirectory()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        JsonNode operation = buyBalanceCreateOperationRequest.toJsonNode();
        if (buyBalanceCreateOperationRequest.getDollarBTCPayment() == null) {
            Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "OPERATION DOES NOT HAVE DOLLARBTC PAYMENT");
            super.response = "OPERATION DOES NOT HAVE DOLLARBTC PAYMENT";
            return;
        }
        if (operation.get("dollarBTCPayment").has("joinMyPayments")
                && operation.get("dollarBTCPayment").get("joinMyPayments").booleanValue()) {
            if (!operation.has("clientPayment") || operation.get("clientPayment") == null) {
                Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "OPERATION DOES NOT HAVE CLIENT PAYMENT");
                super.response = "OPERATION DOES NOT HAVE CLIENT PAYMENT";
                return;
            } else {
                if (!operation.get("clientPayment").has("verified") || !operation.get("clientPayment").get("verified").booleanValue()) {
                    Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "OPERATION DOES NOT HAVE CLIENT PAYMENT VERIFIED");
                    super.response = "OPERATION DOES NOT HAVE CLIENT PAYMENT VERIFIED";
                    return;
                }
            }
        }
        String paymentId = buyBalanceCreateOperationRequest.getDollarBTCPayment().get("id").textValue();
        String operatorName = null;
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            operatorName = operatorsIt.textValue();
            if (OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, buyBalanceCreateOperationRequest.getCurrency(), paymentId).isDirectory()) {
                break;
            } else {
                operatorName = null;
            }
        }
        if (operatorName == null) {
            Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "OPERATION HAS INVALID PARAMS");
            super.response = "OPERATION HAS INVALID PARAMS";
            return;
        }
        String inLimits = BaseOperation.inLimits(buyBalanceCreateOperationRequest.getUserName(), buyBalanceCreateOperationRequest.getCurrency(), buyBalanceCreateOperationRequest.getAmount(), BalanceOperationType.MC_BUY_BALANCE);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        PaymentType paymentType = null;
        String id = BaseOperation.getId();
        String timestamp = DateUtil.getCurrentDate();
        ((ObjectNode) operation).put("id", id);
        ((ObjectNode) operation).put("timestamp", timestamp);
        ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_FOR_PAYMENT.name());
        if (paymentType != null && paymentType.equals(PaymentType.CREDIT_CARD)) {
            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.PAY_VERIFICATION.name());
        }
        if(buyBalanceCreateOperationRequest.getDollarBTCPayment().has("type")){
            paymentType = PaymentType.valueOf(buyBalanceCreateOperationRequest.getDollarBTCPayment().get("type").textValue());
        }
        Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "&&&&&&&&&&&&&&0 " + buyBalanceCreateOperationRequest.getDollarBTCPayment());
        Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "&&&&&&&&&&&&&&1 " + paymentType);
        JsonNode charges = BaseOperation.getChargesNew(buyBalanceCreateOperationRequest.getCurrency(), buyBalanceCreateOperationRequest.getAmount(), BalanceOperationType.MC_BUY_BALANCE, paymentType, "OPERATOR__PAYMENT__" + paymentId, null, null);
//        if (paymentType != null) {
//            switch (paymentType) {
//                case CREDIT_CARD:
//                    charges = BaseOperation.getChargesNew(buyBalanceCreateOperationRequest.getCurrency(), buyBalanceCreateOperationRequest.getAmount(), BalanceOperationType.MC_BUY_BALANCE, paymentType, "OPERATOR__PAYMENT__" + paymentId, null, null);
//                    break;
//            }
//        }
        Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "&&&&&&&&&&&&&&2 " + charges);
        ((ObjectNode) operation).set("charges", charges);
        ObjectNode additionals = new ObjectMapper().createObjectNode();
        additionals.put("operationId", id);
        if (operation.has("clientPayment") && operation.get("clientPayment").has("automatic")) {
            String automaticPaymentBalanceOperation = BaseOperation.automaticPaymentBalanceOperation(operation.get("clientPayment"), operation.get("dollarBTCPayment"), buyBalanceCreateOperationRequest.getAmount(), OTCOperationType.MC_BUY_BALANCE);
            if (!automaticPaymentBalanceOperation.equals("OK")) {
                Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.INFO, "THERE IS A PROBLEM WITH YOUR ACCOUNT");
                super.response = "THERE IS A PROBLEM WITH YOUR ACCOUNT";
                return;
            }
            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.SUCCESS.name());
            FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
            BaseOperation.addToBalance(
                    UsersFolderLocator.getMCBalanceFolder(buyBalanceCreateOperationRequest.getUserName()),
                    buyBalanceCreateOperationRequest.getCurrency(),
                    buyBalanceCreateOperationRequest.getAmount(),
                    BalanceOperationType.MC_BUY_BALANCE,
                    BalanceOperationStatus.OK,
                    buyBalanceCreateOperationRequest.getDescription(),
                    null,
                    charges,
                    false,
                    additionals
            );
            BaseOperation.addToBalance(
                    OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, buyBalanceCreateOperationRequest.getCurrency(), paymentId),
                    buyBalanceCreateOperationRequest.getCurrency(),
                    buyBalanceCreateOperationRequest.getAmount(),
                    BalanceOperationType.MC_BUY_BALANCE,
                    BalanceOperationStatus.OK,
                    "OTC operation id " + id,
                    null,
                    null,
                    false,
                    additionals
            );
            BaseOperation.addToBalance(
                    MoneyclickFolderLocator.getBalanceFolder(OPERATOR_NAME),
                    buyBalanceCreateOperationRequest.getCurrency(),
                    buyBalanceCreateOperationRequest.getAmount(),
                    BalanceOperationType.MC_BUY_BALANCE,
                    BalanceOperationStatus.OK,
                    "OTC operation id " + id,
                    null,
                    charges,
                    false,
                    additionals
            );
        } else {
            FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
            BaseOperation.addToBalance(
                    UsersFolderLocator.getMCBalanceFolder(buyBalanceCreateOperationRequest.getUserName()),
                    buyBalanceCreateOperationRequest.getCurrency(),
                    buyBalanceCreateOperationRequest.getAmount(),
                    BalanceOperationType.MC_BUY_BALANCE,
                    BalanceOperationStatus.PROCESSING,
                    buyBalanceCreateOperationRequest.getDescription(),
                    null,
                    charges,
                    false,
                    additionals
            );
        }
        BaseOperation.createOperationInCentralFolder(operation, operatorName);
        BaseOperation.createIndexesInCentralFolder(operation, operatorName);
        String message = "Operation id " + id + " was created";
        new UserPostMessage(buyBalanceCreateOperationRequest.getUserName(), message, null).getResponse();
        if (buyBalanceCreateOperationRequest.getMessage() != null && !buyBalanceCreateOperationRequest.getMessage().equals("")) {
            BaseOperation.postOperationMessage(id, buyBalanceCreateOperationRequest.getUserName(), buyBalanceCreateOperationRequest.getMessage(), OperationMessageSide.USER, null);
        }
        addToAccumulatedAmount(buyBalanceCreateOperationRequest.getDollarBTCPayment(), buyBalanceCreateOperationRequest.getCurrency(), buyBalanceCreateOperationRequest.getAmount());
        super.response = id;
    }

    private void addToAccumulatedAmount(JsonNode dollarBTCPayment, String currency, Double amount) {
        if (!dollarBTCPayment.has("id")) {
            return;
        }
        String paymentId = dollarBTCPayment.get("id").textValue();
        if (!dollarBTCPayment.has("type")) {
            return;
        }
        PaymentType dollarBTCPaymentType = PaymentType.valueOf(dollarBTCPayment.get("type").textValue());
        File otcCurrencyPaymentFile = OTCFolderLocator.getCurrencyPaymentFile(null, currency, paymentId);
        try {
            JsonNode otcCurrencyPayment = mapper.readTree(otcCurrencyPaymentFile);
            if (!otcCurrencyPayment.has("buyBalance")) {
                return;
            }
            Iterator<JsonNode> otcCurrencyPaymentBuyBalanceIterator = otcCurrencyPayment.get("buyBalance").iterator();
            while (otcCurrencyPaymentBuyBalanceIterator.hasNext()) {
                JsonNode otcCurrencyPaymentBuyBalanceIt = otcCurrencyPaymentBuyBalanceIterator.next();
                PaymentType otcCurrencyPaymentBuyBalanceItPaymentType = PaymentType.valueOf(otcCurrencyPaymentBuyBalanceIt.get("type").textValue());
                if (otcCurrencyPaymentBuyBalanceItPaymentType.equals(dollarBTCPaymentType)) {
                    if (!otcCurrencyPaymentBuyBalanceIt.has("accumulatedAmountBuy")) {
                        ((ObjectNode) otcCurrencyPaymentBuyBalanceIt).put("accumulatedAmountBuy", 0.0);
                    }
                    ((ObjectNode) otcCurrencyPaymentBuyBalanceIt).put("accumulatedAmountBuy", otcCurrencyPaymentBuyBalanceIt.get("accumulatedAmountBuy").doubleValue() + amount);
                    if (otcCurrencyPaymentBuyBalanceIt.has("totalAmountBuy") && otcCurrencyPaymentBuyBalanceIt.get("accumulatedAmountBuy").doubleValue() >= otcCurrencyPaymentBuyBalanceIt.get("totalAmountBuy").doubleValue()) {
                        otcCurrencyPaymentBuyBalanceIterator.remove();
                    }
                    FileUtil.editFile(otcCurrencyPayment, otcCurrencyPaymentFile);
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
