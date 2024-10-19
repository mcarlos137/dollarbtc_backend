/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerRemoveOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCCreateOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCRemoveOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerRemoveOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OperationMessageSide;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOperatorNameRandom;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
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
public class OTCCreateOperation extends AbstractOperation<String> {

    private final OTCCreateOperationRequest otcCreateOperationRequest;

    public OTCCreateOperation(OTCCreateOperationRequest otcCreateOperationRequest) {
        super(String.class);
        this.otcCreateOperationRequest = otcCreateOperationRequest;
    }

    @Override
    protected void execute() {
        File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(otcCreateOperationRequest.getUserName(), otcCreateOperationRequest.getCurrency(), otcCreateOperationRequest.getOtcOperationType().name());
        if (!userOTCCurrencyOperationTypeFolder.isDirectory()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        JsonNode operation = otcCreateOperationRequest.toJsonNode();
        boolean allowNoDollarBTCPayment = false;
        if (otcCreateOperationRequest.getDollarBTCPayment() == null) {
            if (otcCreateOperationRequest.getOtcOperationType().equals(OTCOperationType.SELL)) {
                allowNoDollarBTCPayment = true;
            } else {
                super.response = "OPERATION DOES NOT HAVE DOLLARBTC PAYMENT";
                return;
            }
        }
        String operatorName = null;
        if (!allowNoDollarBTCPayment) {
            String dollarBTCPaymentId = operation.get("dollarBTCPayment").get("id").textValue();
            Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
            while (operatorsIterator.hasNext()) {
                JsonNode operatorsIt = operatorsIterator.next();
                operatorName = operatorsIt.textValue();
                if (OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, otcCreateOperationRequest.getCurrency(), dollarBTCPaymentId).isDirectory()) {
                    break;
                } else {
                    operatorName = null;
                }
            }
            if (operatorName == null) {
                super.response = "OPERATION HAS INVALID PARAMS";
                return;
            }
        }
        if (otcCreateOperationRequest.getOtcOperationType().equals(OTCOperationType.BUY)
                && operation.get("dollarBTCPayment").has("joinMyPayments")
                && operation.get("dollarBTCPayment").get("joinMyPayments").booleanValue()) {
            if (!operation.has("clientPayment") || operation.get("clientPayment") == null) {
                super.response = "OPERATION DOES NOT HAVE CLIENT PAYMENT";
                return;
            } else {
                if (!operation.get("clientPayment").has("verified") || !operation.get("clientPayment").get("verified").booleanValue()) {
                    super.response = "OPERATION DOES NOT HAVE CLIENT PAYMENT VERIFIED";
                    return;
                }
            }
        }
        OfferType offerType;
        switch (otcCreateOperationRequest.getOtcOperationType()) {
            case BUY:
                offerType = OfferType.ASK;
                break;
            case SELL:
                offerType = OfferType.BID;
                break;
            default:
                super.response = otcCreateOperationRequest.getOtcOperationType().name() + "IS NOT ALLOWED";
                return;
        }
        String inLimits = BaseOperation.inLimits(otcCreateOperationRequest.getUserName(), otcCreateOperationRequest.getCurrency(), otcCreateOperationRequest.getAmount(), BalanceOperationType.valueOf(otcCreateOperationRequest.getOtcOperationType().name()));
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String paymentId = null;
        PaymentType paymentType = null;
        String offerTimestamp = null;
        Double offerTotalAmount = null;
        boolean brokerOperation = false;
        if (otcCreateOperationRequest.getBrokerUserName() != null && !otcCreateOperationRequest.getBrokerUserName().equals("")) {
            brokerOperation = true;
        }
        if (!allowNoDollarBTCPayment) {
            paymentId = otcCreateOperationRequest.getDollarBTCPayment().get("id").textValue();
            paymentType = PaymentType.valueOf(otcCreateOperationRequest.getDollarBTCPayment().get("type").textValue());
            JsonNode offers;
            String offerId;
            JsonNode offer = null;
            if (!brokerOperation) {
                offers = new OTCGetOffers(otcCreateOperationRequest.getCurrency(), paymentId, offerType, paymentType, false).getResponse();
                offerId = offerType.name() + "__" + paymentId + "__" + paymentType.name();
                if (offers.has(otcCreateOperationRequest.getCurrency()) && offers.get(otcCreateOperationRequest.getCurrency()).has(offerId)) {
                    offer = offers.get(otcCreateOperationRequest.getCurrency()).get(offerId);
                }
            } else {
                offers = new BrokerGetOffers(otcCreateOperationRequest.getBrokerUserName(), otcCreateOperationRequest.getCurrency(), paymentId, offerType, paymentType, false).getResponse();
                offerId = otcCreateOperationRequest.getCurrency() + "__" + offerType.name() + "__" + paymentId + "__" + paymentType.name();
                if (offers.has(offerId)) {
                    offer = offers.get(offerId);
                }
            }
            if (offer == null) {
                super.response = "THERE IS NO MATCH OFFER";
                return;
            }
            offerTimestamp = offer.get("timestamp").textValue();
            Double offerPrice = offer.get("price").doubleValue();
            Double offerMinPerOperationAmount = offer.get("minPerOperationAmount").doubleValue();
            Double offerMaxPerOperationAmount = offer.get("maxPerOperationAmount").doubleValue();
            offerTotalAmount = offer.get("totalAmount").doubleValue();
            Double offerAccumulatedAmount = offer.get("accumulatedAmount").doubleValue();
            Double diffPercent = 2.0;
            if (otcCreateOperationRequest.getPrice() > (100 + diffPercent) * offerPrice / 100 || otcCreateOperationRequest.getPrice() < (100 - diffPercent) * offerPrice / 100) {
                super.response = "PRICE CHANGE. NEW PRICE: " + offerPrice;
                return;
            }
            double offerTotalAmountLeft = offerTotalAmount - offerAccumulatedAmount;
            if (offerTotalAmountLeft <= 0) {
                super.response = "OFFER WITH NO ENOUGH AMOUNT";
                return;
            } else {
                if (offerTotalAmountLeft < offerMinPerOperationAmount) {
                    offerMinPerOperationAmount = offerTotalAmountLeft;
                }
                if (offerTotalAmountLeft < offerMaxPerOperationAmount) {
                    offerMaxPerOperationAmount = offerTotalAmountLeft;
                }
            }
            if (otcCreateOperationRequest.getAmount() < offerMinPerOperationAmount || otcCreateOperationRequest.getAmount() > offerMaxPerOperationAmount) {
                super.response = "AMOUNT IS NOT BETWEEN MIN AND MAX " + offerMinPerOperationAmount + " - " + offerMaxPerOperationAmount;
                return;
            }
        }
        String id = BaseOperation.getId();
        String timestamp = DateUtil.getCurrentDate();
        ((ObjectNode) operation).put("id", id);
        ((ObjectNode) operation).put("timestamp", timestamp);
        ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_FOR_PAYMENT.name());
//        boolean waitingToStart = false;
//        if (otcCreateOperationRequest.getOtcOperationType().equals(OTCOperationType.SELL)
//                && otcCreateOperationRequest.getClientPayment().has("emailReceiver")
//                && !otcCreateOperationRequest.getClientPayment().get("emailReceiver").textValue().equals("")) {
//            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.WAITING_TO_START_OPERATION.name());
//            waitingToStart = true;
//        }
        if (otcCreateOperationRequest.getOtcOperationType().equals(OTCOperationType.BUY)
                && paymentType != null
                && paymentType.equals(PaymentType.CREDIT_CARD)) {
            ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.PAY_VERIFICATION.name());
        }
        if (operatorName == null) {
            operatorName = new MasterAccountNewGetOperatorNameRandom(otcCreateOperationRequest.getCurrency()).getResponse();
        }
        String otcMasterAccountName = new MasterAccountNewGetOTCMasterAccountName(operatorName, otcCreateOperationRequest.getCurrency()).getResponse().get("name").textValue();
        File masterAccountBalanceFolder;
        boolean allowNegativeBalance = false;
        if (!brokerOperation) {
            masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(otcMasterAccountName);
            ((ObjectNode) operation).put("otcMasterAccountName", otcMasterAccountName);
        } else {
            masterAccountBalanceFolder = UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getBrokerUserName());
            ((ObjectNode) operation).put("brokerUserName", otcCreateOperationRequest.getBrokerUserName());
            Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "operation: {0}", operation);
            allowNegativeBalance = true;
        }
        double btcAmount = otcCreateOperationRequest.getAmount() / otcCreateOperationRequest.getPrice();
        JsonNode charges = BaseOperation.getChargesNew(otcCreateOperationRequest.getCurrency(), otcCreateOperationRequest.getAmount(), BalanceOperationType.valueOf(otcCreateOperationRequest.getOtcOperationType().name()), null, "OPERATOR__MASTER_ACCOUNT__" + masterAccountBalanceFolder.getName(), null, null);
        if (paymentType != null && otcCreateOperationRequest.getOtcOperationType().equals(OTCOperationType.BUY)) {
            switch (paymentType) {
                case CREDIT_CARD:
                    charges = BaseOperation.getChargesNew(otcCreateOperationRequest.getCurrency(), otcCreateOperationRequest.getAmount(), BalanceOperationType.valueOf(otcCreateOperationRequest.getOtcOperationType().name()), paymentType, "OPERATOR__MASTER_ACCOUNT__" + masterAccountBalanceFolder.getName(), null, null);
                    break;
            }
        }
        ((ObjectNode) operation).set("charges", charges);
        switch (otcCreateOperationRequest.getOtcOperationType()) {
            case BUY:
                if (!BaseOperation.substractToBalance(
                        masterAccountBalanceFolder,
                        "BTC", btcAmount,
                        BalanceOperationType.TRANSFER_TO_CLIENTS,
                        BalanceOperationStatus.OK,
                        "OTC operation id " + id,
                        null,
                        allowNegativeBalance,
                        null,
                        false,
                        null
                ).equals("OK")) {
                    super.response = "MASTER ACCOUNT HAS NOT ENOUGH BALANCE";
                    return;
                }
                if (operation.get("clientPayment").has("automatic")) {
                    String automaticPaymentBalanceOperation = BaseOperation.automaticPaymentBalanceOperation(operation.get("clientPayment"), operation.get("dollarBTCPayment"), otcCreateOperationRequest.getAmount(), otcCreateOperationRequest.getOtcOperationType());
                    if (!automaticPaymentBalanceOperation.equals("OK")) {
                        BaseOperation.changeBalanceOperationStatus(masterAccountBalanceFolder, BalanceOperationStatus.FAIL, id, "additionalInfo", "THERE IS A PROBLEM WITH YOUR ACCOUNT");
                        super.response = "THERE IS A PROBLEM WITH YOUR ACCOUNT";
                        return;
                    }
                    ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.SUCCESS.name());
                    FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getUserName()),
                            "BTC",
                            btcAmount,
                            BalanceOperationType.BUY,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + id,
                            otcCreateOperationRequest.getPrice(),
                            charges,
                            false,
                            null
                    );
                    BaseOperation.addToBalance(
                            OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, otcCreateOperationRequest.getCurrency(), paymentId),
                            otcCreateOperationRequest.getCurrency(),
                            otcCreateOperationRequest.getAmount(),
                            BalanceOperationType.BUY,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + id,
                            null,
                            null,
                            false,
                            null
                    );
                    if (brokerOperation) {
                        BaseOperation.addToBalance(
                                BrokersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getBrokerUserName()),
                                otcCreateOperationRequest.getCurrency(),
                                otcCreateOperationRequest.getAmount(),
                                BalanceOperationType.BUY,
                                BalanceOperationStatus.OK,
                                "OTC operation id " + id,
                                null,
                                null,
                                false,
                                null
                        );
                    }
                    break;
                }
                FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
                BaseOperation.addToBalance(
                        UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getUserName()),
                        "BTC",
                        btcAmount,
                        BalanceOperationType.BUY,
                        BalanceOperationStatus.PROCESSING,
                        "OTC operation id " + id,
                        otcCreateOperationRequest.getPrice(),
                        charges,
                        false,
                        null
                );
                break;
            case SELL:
                if (!BaseOperation.substractToBalance(
                        UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getUserName()),
                        "BTC",
                        btcAmount,
                        BalanceOperationType.SELL,
                        BalanceOperationStatus.OK, "OTC operation id " + id,
                        otcCreateOperationRequest.getPrice(),
                        false,
                        charges,
                        false,
                        null
                ).equals("OK")) {
                    super.response = "USER HAS NOT ENOUGH BALANCE";
                    return;
                }
                if (!allowNoDollarBTCPayment) {
                    if (!BaseOperation.substractToBalance(
                            OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, otcCreateOperationRequest.getCurrency(), paymentId),
                            otcCreateOperationRequest.getCurrency(),
                            otcCreateOperationRequest.getAmount(),
                            BalanceOperationType.SELL,
                            BalanceOperationStatus.PROCESSING,
                            "OTC operation id " + id,
                            null,
                            false,
                            null,
                            false,
                            null
                    ).equals("OK")) {
                        BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getUserName()), BalanceOperationStatus.FAIL, id, "additionalInfo", "THIS PAYMENT IS NOT AVAILABLE AT THIS MOMENT");
                        super.response = "THIS PAYMENT IS NOT AVAILABLE AT THIS MOMENT";
                        return;
                    }
                }
                Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "otcCreateOperationRequest.getBrokerUserName(): {0}", otcCreateOperationRequest.getBrokerUserName());
                Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "brokerOperation: {0}", brokerOperation);
                if (brokerOperation) {
                    Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "1");
                    if (!BaseOperation.substractToBalance(
                            BrokersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getBrokerUserName()),
                            otcCreateOperationRequest.getCurrency(),
                            otcCreateOperationRequest.getAmount(),
                            BalanceOperationType.SELL,
                            BalanceOperationStatus.PROCESSING,
                            "OTC operation id " + id,
                            null,
                            false,
                            null,
                            false,
                            null
                    ).equals("OK")) {
                        Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "2");
                        BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getUserName()), BalanceOperationStatus.FAIL, id, "additionalInfo", "BROKER HAS NOT ENOUGH BALANCE");
                        if (!allowNoDollarBTCPayment) {
                            BaseOperation.changeBalanceOperationStatus(OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, otcCreateOperationRequest.getCurrency(), paymentId), BalanceOperationStatus.FAIL, id, "additionalInfo", "BROKER HAS NOT ENOUGH BALANCE");
                        }
                        Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "3");
                        super.response = "BROKER HAS NOT ENOUGH BALANCE";
                        return;
                    }
                    Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "4");
                }
                Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.INFO, "5");
                if (operation.get("clientPayment").has("automatic")) {
                    String automaticPaymentBalanceOperation = BaseOperation.automaticPaymentBalanceOperation(operation.get("dollarBTCPayment"), operation.get("clientPayment"), otcCreateOperationRequest.getAmount(), otcCreateOperationRequest.getOtcOperationType());
                    if (!automaticPaymentBalanceOperation.equals("OK")) {
                        BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getBalanceFolder(otcCreateOperationRequest.getUserName()), BalanceOperationStatus.FAIL, id, "additionalInfo", "THERE IS A PROBLEM WITH YOUR ACCOUNT");
                        BaseOperation.changeBalanceOperationStatus(OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, otcCreateOperationRequest.getCurrency(), paymentId), BalanceOperationStatus.FAIL, id, "additionalInfo", "THERE IS A PROBLEM WITH YOUR ACCOUNT");
                        super.response = "THERE IS A PROBLEM WITH YOUR ACCOUNT";
                        return;
                    }
                    BaseOperation.changeBalanceOperationStatus(OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, otcCreateOperationRequest.getCurrency(), paymentId), BalanceOperationStatus.OK, id, "additionalInfo", null);
                    ((ObjectNode) operation).put("otcOperationStatus", OTCOperationStatus.SUCCESS.name());
                }
                FileUtil.createFile(operation, new File(userOTCCurrencyOperationTypeFolder, id + ".json"));
                BaseOperation.addToBalance(
                        masterAccountBalanceFolder,
                        "BTC",
                        btcAmount,
                        BalanceOperationType.TRANSFER_TO_MASTER,
                        BalanceOperationStatus.OK,
                        "OTC operation id " + id,
                        null,
                        null,
                        false,
                        null
                );
                break;
            default:
                break;
        }
        BaseOperation.createOperationInCentralFolder(operation, operatorName);
        BaseOperation.createIndexesInCentralFolder(operation, operatorName);
        if (offerTimestamp != null && offerTotalAmount != null && paymentId != null && paymentType != null) {
            createOperationInOfferFolder(operatorName, otcCreateOperationRequest.getCurrency(), offerType, offerTimestamp, offerTotalAmount, paymentId, paymentType, operation, otcCreateOperationRequest.getBrokerUserName());
        }
        String message = "Operation id " + id + " was created";
        new UserPostMessage(otcCreateOperationRequest.getUserName(), message, null).getResponse();
        if (otcCreateOperationRequest.getMessage() != null && !otcCreateOperationRequest.getMessage().equals("")) {
            BaseOperation.postOperationMessage(id, otcCreateOperationRequest.getUserName(), otcCreateOperationRequest.getMessage(), OperationMessageSide.USER, null);
        }
//        if (waitingToStart) {
//            try {
//                String subject = "DollarBTC " + otcCreateOperationRequest.getUserName() + " has sent you money.";
//                message = "DollarBTC " + otcCreateOperationRequest.getUserName() + " has sent you money. To receive it, click this link " + getWaitingToStartLink(operation);
//                Set<String> recipients = new HashSet<>();
//                recipients.add(otcCreateOperationRequest.getClientPayment().get("emailReceiver").textValue());
//                new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("admin@dollarbtc.com__DOLLARBTC", subject, message, recipients, null);
//            } catch (MailException ex) {
//                Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        super.response = id;
    }

    private static void createOperationInOfferFolder(String operatorName, String currency, OfferType offerType, String offerTimestamp, double offerTotalAmount, String paymentId, PaymentType paymentType, JsonNode operation, String brokerUserName) {
        offerTimestamp = DateUtil.getFileDate(offerTimestamp);
        File offerIdFolder;
        if (brokerUserName == null || brokerUserName.equals("")) {
            offerIdFolder = OTCFolderLocator.getCurrencyOffersTypeOperationsOfferIdFolder(operatorName, currency, offerType, paymentId, paymentType, offerTimestamp);
        } else {
            offerIdFolder = BrokersFolderLocator.getOfferOperationsOfferIdFolder(brokerUserName, currency, offerType, paymentId, paymentType, offerTimestamp);
        }
        FileUtil.createFile(operation, new File(offerIdFolder, operation.get("id").textValue() + ".json"));
        ObjectMapper mapper = new ObjectMapper();
        offerIdFolder = OTCFolderLocator.getCurrencyOffersTypeOperationsOfferIdFolder(operatorName, currency, offerType, paymentId, paymentType, offerTimestamp);
        Double offerCurrentAmount = 0.0;
        for (File otcCurrencyOffersTypeOperationsOfferIdFile : offerIdFolder.listFiles()) {
            if (!otcCurrencyOffersTypeOperationsOfferIdFile.isFile()) {
                continue;
            }
            try {
                JsonNode otcCurrencyOffersTypeOperationsOfferId = mapper.readTree(otcCurrencyOffersTypeOperationsOfferIdFile);
                offerCurrentAmount = offerCurrentAmount + otcCurrencyOffersTypeOperationsOfferId.get("amount").doubleValue();
            } catch (IOException ex) {
                Logger.getLogger(OTCCreateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (offerCurrentAmount >= offerTotalAmount) {
            if (brokerUserName == null || brokerUserName.equals("")) {
                new OTCRemoveOffer(new OTCRemoveOfferRequest(currency, paymentId, offerType, paymentType)).getResponse();
            } else {
                new BrokerRemoveOffer(new BrokerRemoveOfferRequest(brokerUserName, currency, paymentId, offerType, paymentType)).getResponse();
            }
        }
    }

    private static String getWaitingToStartLink(JsonNode operation) {
        return "";
    }

}
