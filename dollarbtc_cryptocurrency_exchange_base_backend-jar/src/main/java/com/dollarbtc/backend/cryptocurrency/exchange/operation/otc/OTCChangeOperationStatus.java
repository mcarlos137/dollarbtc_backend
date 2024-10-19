/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerifications;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AdminFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCChangeOperationStatus extends AbstractOperation<String> {

    private final OTCChangeOperationStatusRequest otcChangeOperationStatusRequest;

    public OTCChangeOperationStatus(OTCChangeOperationStatusRequest otcChangeOperationStatusRequest) {
        super(String.class);
        this.otcChangeOperationStatusRequest = otcChangeOperationStatusRequest;
    }

    @Override
    public void execute() {
        try {
            File otcOperationIdFile = new File(OTCFolderLocator.getOperationIdFolder(null, otcChangeOperationStatusRequest.getId()), "operation.json");
            JsonNode otcOperationId = mapper.readTree(otcOperationIdFile);
            String userName = otcOperationId.get("userName").textValue();
            OTCOperationType otcOperationType = OTCOperationType.valueOf(otcOperationId.get("otcOperationType").textValue());
            if (otcOperationType.equals(OTCOperationType.MC_BUY_BALANCE)) {
                super.response = "OPERATION NOT ALLOWED";
                return;
            }
            if (otcOperationType.equals(OTCOperationType.BUY) || otcOperationType.equals(OTCOperationType.SEND_TO_PAYMENT)) {
                if (!otcChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)
                        && !otcChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CLAIM)) {
                    Map<UserVerificationType, UserVerificationStatus> userVerifications = (Map<UserVerificationType, UserVerificationStatus>) new UserGetVerifications(userName, null).getResponse();
                    for (UserVerificationType userVerificationType : userVerifications.keySet()) {
                        if (!userVerifications.get(userVerificationType).equals(UserVerificationStatus.OK)) {
                            super.response = "USER VERIFICATION TYPE " + userVerificationType.name() + " IS " + userVerifications.get(userVerificationType).name();
                            return;
                        }
                    }
                    switch (otcOperationType) {
                        case BUY:
                            if (!userVerifications.containsKey(UserVerificationType.C)) {
                                super.response = "USER VERIFICATION TYPE " + UserVerificationType.C + " DOES NOT EXIST";
                                return;
                            }
                            break;
                        case SEND_TO_PAYMENT:
                            if (!userVerifications.containsKey(UserVerificationType.E) && !userVerifications.containsKey(UserVerificationType.C)) {
                                super.response = "USER VERIFICATION TYPE " + UserVerificationType.E + " OR " + UserVerificationType.C + " DO NOT EXIST";
                                return;
                            }
                            break;
                    }
                }
            }
            String currency = otcOperationId.get("currency").textValue();
            File masterAccountBalanceFolder;
            String brokerUserName = null;
            if (otcOperationId.has("otcMasterAccountName")) {
                masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(otcOperationId.get("otcMasterAccountName").textValue());
            } else if (otcOperationId.has("brokerUserName")) {
                masterAccountBalanceFolder = UsersFolderLocator.getBalanceFolder(otcOperationId.get("brokerUserName").textValue());
                brokerUserName = otcOperationId.get("brokerUserName").textValue();
            } else {
                masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(new MasterAccountNewGetOTCMasterAccountName(null, currency).getResponse().get("name").textValue());
            }
            double amount = otcOperationId.get("amount").doubleValue();
            OTCOperationStatus otcOperationStatusBase = OTCOperationStatus.valueOf(otcOperationId.get("otcOperationStatus").textValue());
            OTCOperationStatus otcOperationStatusTarget = otcChangeOperationStatusRequest.getOtcOperationStatus();
            JsonNode charges = otcOperationId.get("charges");
            if (otcChangeOperationStatusRequest.isUserChange()) {
                boolean success = false;
                if (otcOperationStatusBase.equals(OTCOperationStatus.WAITING_FOR_PAYMENT)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.CANCELED)
                        && otcOperationType.equals(OTCOperationType.BUY)) {
                    success = true;
                }
                if (otcOperationStatusBase.equals(OTCOperationStatus.WAITING_FOR_PAYMENT)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.PAY_VERIFICATION)
                        && otcOperationType.equals(OTCOperationType.BUY)) {
                    success = true;
                }
                if (otcOperationStatusBase.equals(OTCOperationStatus.WAITING_TO_START_OPERATION)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.CANCELED)
                        && otcOperationType.equals(OTCOperationType.SELL)) {
                    success = true;
                }
                if ((otcOperationStatusBase.equals(OTCOperationStatus.CANCELED) || otcOperationStatusBase.equals(OTCOperationStatus.SUCCESS)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.CLAIM))) {
                    success = true;
                }
                if (!success) {
                    super.response = "STATUS " + otcOperationStatusBase.name() + " CAN NOT BE CHANGED TO " + otcOperationStatusTarget.name() + " BY USER";
                    return;
                }
            } else {
                //excluded from operations
                /*boolean success = true;
                if (otcOperationStatusBase.equals(OTCOperationStatus.CANCELED)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS)) {
                    success = false;
                }
                if (!success) {
                    super.response = "STATUS " + otcOperationStatusBase.name() + " CAN NOT BE CHANGED TO " + otcOperationStatusTarget.name() + " BY USER";
                    return;
                }*/
            }
            String paymentId = null;
            boolean dollarBTCSellPayment = false;
            switch (otcOperationType) {
                case BUY:
                    if (otcOperationId.get("dollarBTCPayment").has("id")) {
                        paymentId = otcOperationId.get("dollarBTCPayment").get("id").textValue();
                    }
                    if (otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS) && (paymentId == null || paymentId.equals(""))) {
                        super.response = "BUY MUST HAVE OPERATION PAYMENT ID TO FINISH STATUS";
                        return;
                    }
                    break;
                case SELL:
                    if (otcOperationId.has("dollarBTCPayment") && otcOperationId.get("dollarBTCPayment").has("id")) {
                        paymentId = otcOperationId.get("dollarBTCPayment").get("id").textValue();
                        dollarBTCSellPayment = true;
                    } else {
                        paymentId = otcChangeOperationStatusRequest.getPaymentId();
                    }
                    if (otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS) && (paymentId == null || paymentId.equals(""))) {
                        super.response = "SELL MUST HAVE REQUEST PAYMENT ID TO FINISH STATUS";
                        return;
                    }
                    break;
                case SEND_TO_PAYMENT:
                    if (otcOperationId.has("dollarBTCPayment") && otcOperationId.get("dollarBTCPayment").has("id")) {
                        paymentId = otcOperationId.get("dollarBTCPayment").get("id").textValue();
                        dollarBTCSellPayment = true;
                    } else {
                        paymentId = otcChangeOperationStatusRequest.getPaymentId();
                    }
                    if (otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS) && (paymentId == null || paymentId.equals(""))) {
                        super.response = "SEND MUST HAVE REQUEST PAYMENT ID TO FINISH STATUS";
                        return;
                    }
                    break;
            }
            ((ObjectNode) otcOperationId).put("otcOperationStatus", otcOperationStatusTarget.name());
            File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(otcOperationId.get("userName").textValue(), otcOperationId.get("currency").textValue(), otcOperationId.get("otcOperationType").textValue());
            File userOTCCurrencyOperationTypeIdFile = new File(userOTCCurrencyOperationTypeFolder, otcChangeOperationStatusRequest.getId() + ".json");
            JsonNode userOTCCurrencyOperationTypeId = mapper.readTree(userOTCCurrencyOperationTypeIdFile);
            ((ObjectNode) userOTCCurrencyOperationTypeId).put("otcOperationStatus", otcOperationStatusTarget.name());
            String operatorName = null;
            Iterator<JsonNode> operatorNamesIterator = BaseOperation.getOperators().iterator();
            switch (otcChangeOperationStatusRequest.getOtcOperationStatus()) {
                case SUCCESS:
                    while (operatorNamesIterator.hasNext()) {
                        String operatorNamesIt = operatorNamesIterator.next().textValue();
                        if (OTCFolderLocator.getCurrencyPaymentFile(operatorNamesIt, currency, paymentId).isFile()) {
                            operatorName = operatorNamesIt;
                            break;
                        }
                    }
                    if (operatorName == null) {
                        super.response = "THERE IS NO DOLLARBTC PAYMENT TO THIS OPERATION";
                        return;
                    }
                    if (!otcOperationId.has("dollarBTCPayment")) {
                        JsonNode dollarBTCPayment = mapper.readTree(OTCFolderLocator.getCurrencyPaymentFile(operatorName, currency, paymentId));
                        ((ObjectNode) otcOperationId).set("dollarBTCPayment", dollarBTCPayment);
                    }
                    File otcCurrencyPaymentBalanceFolder = OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, currency, paymentId);
                    switch (otcOperationType) {
                        case SELL:
                            if (dollarBTCSellPayment) {
                                BaseOperation.changeBalanceOperationStatus(otcCurrencyPaymentBalanceFolder, BalanceOperationStatus.OK, otcChangeOperationStatusRequest.getId(), "additionalInfo", null);
                                if (brokerUserName != null) {
                                    BaseOperation.changeBalanceOperationStatus(BrokersFolderLocator.getBalanceFolder(brokerUserName), BalanceOperationStatus.OK, otcChangeOperationStatusRequest.getId(), "additionalInfo", null);
                                }
                            } else {
                                String substractResult = BaseOperation.substractToBalance(
                                        otcCurrencyPaymentBalanceFolder,
                                        currency,
                                        amount,
                                        BalanceOperationType.SELL,
                                        BalanceOperationStatus.OK,
                                        "OTC operation id " + otcChangeOperationStatusRequest.getId(),
                                        null,
                                        false,
                                        null,
                                        false,
                                        null
                                );
                                if (!substractResult.equals("OK")) {
                                    super.response = substractResult;
                                    return;
                                }
                                if (brokerUserName != null) {
                                    substractResult = BaseOperation.substractToBalance(
                                            BrokersFolderLocator.getBalanceFolder(brokerUserName),
                                            currency,
                                            amount,
                                            BalanceOperationType.SELL,
                                            BalanceOperationStatus.OK,
                                            "OTC operation id " + otcChangeOperationStatusRequest.getId(),
                                            null,
                                            false,
                                            null,
                                            false,
                                            null
                                    );
                                    if (!substractResult.equals("OK")) {
                                        BaseOperation.changeBalanceOperationStatus(otcCurrencyPaymentBalanceFolder, BalanceOperationStatus.FAIL, otcChangeOperationStatusRequest.getId(), "additionalInfo", null);
                                        super.response = substractResult;
                                        return;
                                    }
                                }
                            }
                            BaseOperation.changeBalanceOperationStatus(masterAccountBalanceFolder, BalanceOperationStatus.OK, otcChangeOperationStatusRequest.getId(), "additionalInfo", null);
//                            if (otcOperationId.get("clientPayment").has("emailReceiver")) {
//                                sendSellSuccessEmail(userName, currency, amount, otcOperationId.get("clientPayment").get("emailReceiver").textValue(), otcOperationId.get("clientPayment").get("accountNumber").textValue(), otcOperationId.get("clientPayment").get("bank").textValue());
//                            }
                            double price = otcOperationId.get("price").doubleValue();
                            new OTCChangeOperationPrice(currency, amount, "SELL", price).getResponse();
                            break;
                        case BUY:
                            BaseOperation.addToBalance(
                                    otcCurrencyPaymentBalanceFolder,
                                    currency,
                                    amount,
                                    BalanceOperationType.BUY,
                                    BalanceOperationStatus.OK,
                                    "OTC operation id " + otcChangeOperationStatusRequest.getId(),
                                    null,
                                    null,
                                    false,
                                    null
                            );
                            if (brokerUserName != null) {
                                BaseOperation.addToBalance(
                                        BrokersFolderLocator.getBalanceFolder(brokerUserName),
                                        currency,
                                        amount,
                                        BalanceOperationType.BUY,
                                        BalanceOperationStatus.OK,
                                        "OTC operation id " + otcChangeOperationStatusRequest.getId(),
                                        null,
                                        null,
                                        false,
                                        null
                                );
                            }
                            BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getBalanceFolder(userName), BalanceOperationStatus.OK, otcChangeOperationStatusRequest.getId(), "additionalInfo", null);
                            BaseOperation.changeBalanceOperationStatus(masterAccountBalanceFolder, BalanceOperationStatus.OK, otcChangeOperationStatusRequest.getId(), "additionalInfo", null);
                            price = otcOperationId.get("price").doubleValue();
                            new OTCChangeOperationPrice(currency, amount, "BUY", price).getResponse();
                            break;
                        case SEND_TO_PAYMENT:
                            //if (BaseOperation.containsBalanceOperationStatus(otcCurrencyPaymentBalanceFolder, otcChangeOperationStatusRequest.getId(), "operationId")) {
                            //    super.response = "OK";
                            //    return;
                            //}
                            ObjectNode additionals = mapper.createObjectNode();
                            additionals.put("operationId", otcChangeOperationStatusRequest.getId());
                            String substractResult = BaseOperation.substractToBalance(
                                    otcCurrencyPaymentBalanceFolder,
                                    currency,
                                    amount,
                                    BalanceOperationType.SEND_TO_PAYMENT,
                                    BalanceOperationStatus.OK,
                                    "OTC operation id " + otcChangeOperationStatusRequest.getId(),
                                    null,
                                    false,
                                    null,
                                    false,
                                    additionals
                            );
                            if (!substractResult.equals("OK")) {
                                super.response = substractResult;
                                return;
                            }
                            BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.OK, otcChangeOperationStatusRequest.getId(), "operationId", null);
                            BaseOperation.substractToBalance(
                                    MoneyclickFolderLocator.getBalanceFolder(OPERATOR_NAME),
                                    currency,
                                    amount,
                                    BalanceOperationType.SEND_TO_PAYMENT,
                                    BalanceOperationStatus.OK,
                                    "OTC operation id " + otcChangeOperationStatusRequest.getId(),
                                    null,
                                    true,
                                    charges,
                                    false,
                                    null
                            );
                            File operationChargesFolder = new File(new File(ExchangeUtil.OPERATOR_PATH, "Charges"), "OPERATION__" + otcChangeOperationStatusRequest.getId());
                            if (operationChargesFolder.isDirectory()) {
                                File paymentChargesFolder = new File(new File(ExchangeUtil.OPERATOR_PATH, "Charges"), "PAYMENT__" + paymentId);
                                BaseOperation.moveCharges(operationChargesFolder, paymentChargesFolder);
                            }
                            addToAccumulatedAmount(otcOperationId, currency, amount);
                            break;
                    }
                    break;
                case CANCELED:
                    if (dollarBTCSellPayment) {
                        while (operatorNamesIterator.hasNext()) {
                            String operatorNamesIt = operatorNamesIterator.next().textValue();
                            if (OTCFolderLocator.getCurrencyPaymentFile(operatorNamesIt, currency, paymentId).isFile()) {
                                operatorName = operatorNamesIt;
                                break;
                            }
                        }
                        BaseOperation.changeBalanceOperationStatus(OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, currency, paymentId), BalanceOperationStatus.FAIL, otcChangeOperationStatusRequest.getId(), "additionalInfo", otcChangeOperationStatusRequest.getCanceledReason());
                    }
                    if (brokerUserName != null) {
                        BaseOperation.changeBalanceOperationStatus(BrokersFolderLocator.getBalanceFolder(brokerUserName), BalanceOperationStatus.FAIL, otcChangeOperationStatusRequest.getId(), "additionalInfo", otcChangeOperationStatusRequest.getCanceledReason());
                    }
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getBalanceFolder(userName), BalanceOperationStatus.FAIL, otcChangeOperationStatusRequest.getId(), "additionalInfo", otcChangeOperationStatusRequest.getCanceledReason());
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.FAIL, otcChangeOperationStatusRequest.getId(), "operationId", otcChangeOperationStatusRequest.getCanceledReason());
                    BaseOperation.changeBalanceOperationStatus(masterAccountBalanceFolder, BalanceOperationStatus.FAIL, otcChangeOperationStatusRequest.getId(), "additionalInfo", otcChangeOperationStatusRequest.getCanceledReason());
                    new OTCRemoveOperationInOfferFolder(currency, otcChangeOperationStatusRequest.getId(), brokerUserName).getResponse();
            }
            FileUtil.editFile(otcOperationId, otcOperationIdFile);
            FileUtil.editFile(userOTCCurrencyOperationTypeId, userOTCCurrencyOperationTypeIdFile);
            new OTCChangeStatusesIndexInCentralFolder(userName, otcChangeOperationStatusRequest.getId(), otcOperationStatusBase, otcOperationStatusTarget).getResponse();
            String message = "Operation id " + otcChangeOperationStatusRequest.getId() + " change status to " + otcChangeOperationStatusRequest.getOtcOperationStatus();
            new UserPostMessage(userName, message, otcOperationType.name().toLowerCase() + "?id=" + otcChangeOperationStatusRequest.getId()).getResponse();
            String timestamp = DateUtil.getCurrentDate();
            String fileName = DateUtil.getFileDate(timestamp) + ".json";
            ObjectNode messageObject = mapper.createObjectNode();
            messageObject.put("id", otcChangeOperationStatusRequest.getId());
            messageObject.put("timestamp", timestamp);
            messageObject.put("userName", userName);
            messageObject.put("message", message);
            FileUtil.createFile(messageObject, new File(AdminFolderLocator.getOperationMessagesFolder(), fileName));
            ObjectNode changeStatusObject = mapper.createObjectNode();
            changeStatusObject.put("id", otcChangeOperationStatusRequest.getId());
            changeStatusObject.put("timestamp", timestamp);
            changeStatusObject.put("otcOperationStatus", otcChangeOperationStatusRequest.getOtcOperationStatus().name());
            FileUtil.createFile(changeStatusObject, new File(FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsChangeStatusesUserNameFolder(operatorName, userName)), fileName));
            if (otcOperationType.equals(OTCOperationType.SEND_TO_PAYMENT)) {
                String notificationMessage = null;
                if (otcChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)) {
                    notificationMessage = "Transfer Operation of " + String.format("%(,.2f", amount) + " " + currency + " was canceled because a problem with your bank account. Please check you bank account info and try again.";
                } else if (otcChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.SUCCESS)) {
                    notificationMessage = "Transfer Operation of " + String.format("%(,.2f", amount) + " " + currency + " succeed. Some banks takes until 48 hours to show new transactions. For any questions contact us at customer service section.";
                }
                if (notificationMessage != null) {
                    new NotificationSendMessageByUserName(userName, "Transaction information", notificationMessage).getResponse();
                }
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void addToAccumulatedAmount(JsonNode operation, String currency, Double amount) {
        if (!operation.has("dollarBTCPayment")) {
            return;
        }
        String dollarBTCPaymentId = operation.get("dollarBTCPayment").get("id").textValue();
        String dollarBTCPaymentBank = operation.get("dollarBTCPayment").get("bank").textValue();
        if (!operation.has("clientPayment") || !operation.get("clientPayment").has("bank")) {
            return;
        }
        String clientPaymentBank = operation.get("clientPayment").get("bank").textValue();
        boolean sameBank = true;
        if (!dollarBTCPaymentBank.equals(clientPaymentBank)) {
            sameBank = false;
        }
        File otcCurrencyPaymentFile = OTCFolderLocator.getCurrencyPaymentFile(null, currency, dollarBTCPaymentId);
        try {
            JsonNode otcCurrencyPayment = mapper.readTree(otcCurrencyPaymentFile);
            if (!otcCurrencyPayment.has("buyBalance")) {
                return;
            }
//            Iterator<JsonNode> otcCurrencyPaymentSendToPaymentsIterator = otcCurrencyPayment.get("sendToPayments").iterator();
//            while (otcCurrencyPaymentSendToPaymentsIterator.hasNext()) {
//                JsonNode otcCurrencyPaymentSendToPaymentsIt = otcCurrencyPaymentSendToPaymentsIterator.next();
//                PaymentType otcCurrencyPaymentBuyBalanceItPaymentType = PaymentType.valueOf(otcCurrencyPaymentSendToPaymentsIt.get("type").textValue());
//                if (otcCurrencyPaymentBuyBalanceItPaymentType.equals(PaymentType.TRANSFER_WITH_SPECIFIC_BANK) && sameBank) {
//                    if (!otcCurrencyPaymentSendToPaymentsIt.has("accumulatedAmount")) {
//                        ((ObjectNode) otcCurrencyPaymentSendToPaymentsIt).put("accumulatedAmount", 0.0);
//                    }
//                    ((ObjectNode) otcCurrencyPaymentSendToPaymentsIt).put("accumulatedAmount", otcCurrencyPaymentSendToPaymentsIt.get("accumulatedAmount").doubleValue() + amount);
//                    if (otcCurrencyPaymentSendToPaymentsIt.has("totalAmount") && otcCurrencyPaymentSendToPaymentsIt.get("accumulatedAmount").doubleValue() >= otcCurrencyPaymentSendToPaymentsIt.get("totalAmount").doubleValue()) {
//                        otcCurrencyPaymentSendToPaymentsIterator.remove();
//                    }
//                    FileUtil.editFile(otcCurrencyPayment, otcCurrencyPaymentFile);
//                    break;
//                }
//                if (otcCurrencyPaymentBuyBalanceItPaymentType.equals(PaymentType.TRANSFER_NATIONAL_BANK) && !sameBank) {
//                    if (!otcCurrencyPaymentSendToPaymentsIt.has("accumulatedAmount")) {
//                        ((ObjectNode) otcCurrencyPaymentSendToPaymentsIt).put("accumulatedAmount", 0.0);
//                    }
//                    ((ObjectNode) otcCurrencyPaymentSendToPaymentsIt).put("accumulatedAmount", otcCurrencyPaymentSendToPaymentsIt.get("accumulatedAmount").doubleValue() + amount);
//                    if (otcCurrencyPaymentSendToPaymentsIt.has("totalAmount") && otcCurrencyPaymentSendToPaymentsIt.get("accumulatedAmount").doubleValue() >= otcCurrencyPaymentSendToPaymentsIt.get("totalAmount").doubleValue()) {
//                        otcCurrencyPaymentSendToPaymentsIterator.remove();
//                    }
//                    FileUtil.editFile(otcCurrencyPayment, otcCurrencyPaymentFile);
//                    break;
//                }
//            }
        } catch (IOException ex) {
            Logger.getLogger(OTCChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private static void sendSellSuccessEmail(String userName, String currency, Double amount, String emailReceiver, String accountNumber, String bank) {
//        String subject = userName + " te envío de dinero";
//        String message = userName + " te envió " + amount + " " + currency + " a tu cuenta bancaria " + accountNumber + " " + bank;
//        Set<String> recipients = new HashSet<>();
//        recipients.add(emailReceiver);
//        try {
//            new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("dollarbtcofficial@gmail.com__DOLLARBTC", subject, message, recipients, null);
//        } catch (MailException ex) {
//            Logger.getLogger(OTCChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
