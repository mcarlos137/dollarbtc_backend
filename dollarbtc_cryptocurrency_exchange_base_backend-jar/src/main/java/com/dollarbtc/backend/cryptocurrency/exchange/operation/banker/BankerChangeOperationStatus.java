/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeStatusesIndexInCentralFolder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCRemoveOperationInOfferFolder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerifications;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AdminFolderLocator;
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
public class BankerChangeOperationStatus extends AbstractOperation<String> {

    private final BankerChangeOperationStatusRequest bankerChangeOperationStatusRequest;

    public BankerChangeOperationStatus(BankerChangeOperationStatusRequest bankerChangeOperationStatusRequest) {
        super(String.class);
        this.bankerChangeOperationStatusRequest = bankerChangeOperationStatusRequest;
    }

    @Override
    protected void execute() {
        if (bankerChangeOperationStatusRequest.getOtcOperationType().equals(OTCOperationType.MC_BUY_BALANCE)) {
            super.response = buyBalance();
        } else if (bankerChangeOperationStatusRequest.getOtcOperationType().equals(OTCOperationType.SEND_TO_PAYMENT)) {
            super.response = sendToPayment();
        }
        super.response = "FAIL";
    }

    private String buyBalance() {
        try {
            File otcOperationIdFile = new File(OTCFolderLocator.getOperationIdFolder(null, bankerChangeOperationStatusRequest.getId()), "operation.json");
            JsonNode otcOperationId = mapper.readTree(otcOperationIdFile);
            String userName = otcOperationId.get("userName").textValue();
            OTCOperationType otcOperationType = OTCOperationType.valueOf(otcOperationId.get("otcOperationType").textValue());
            if (!bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)
                    && !bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CLAIM)) {
                Map<UserVerificationType, UserVerificationStatus> userVerifications = (Map<UserVerificationType, UserVerificationStatus>) new UserGetVerifications(userName, null).getResponse();
                for (UserVerificationType userVerificationType : userVerifications.keySet()) {
                    if (!userVerifications.get(userVerificationType).equals(UserVerificationStatus.OK)) {
                        return "USER VERIFICATION TYPE " + userVerificationType.name() + " IS " + userVerifications.get(userVerificationType).name();
                    }
                }
                if (!userVerifications.containsKey(UserVerificationType.C)) {
                    return "USER VERIFICATION TYPE " + UserVerificationType.C + " DOES NOT EXIST";
                }
            }
            String currency = otcOperationId.get("currency").textValue();
            double amount = otcOperationId.get("amount").doubleValue();
            OTCOperationStatus otcOperationStatusBase = OTCOperationStatus.valueOf(otcOperationId.get("otcOperationStatus").textValue());
            OTCOperationStatus otcOperationStatusTarget = bankerChangeOperationStatusRequest.getOtcOperationStatus();
            JsonNode charges = otcOperationId.get("charges");
            if (bankerChangeOperationStatusRequest.isUserChange()) {
                boolean success = false;
                if (otcOperationStatusBase.equals(OTCOperationStatus.WAITING_FOR_PAYMENT)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.CANCELED)) {
                    success = true;
                }
                if (otcOperationStatusBase.equals(OTCOperationStatus.WAITING_FOR_PAYMENT)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.PAY_VERIFICATION)) {
                    success = true;
                }
                if ((otcOperationStatusBase.equals(OTCOperationStatus.CANCELED) || otcOperationStatusBase.equals(OTCOperationStatus.SUCCESS)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.CLAIM))) {
                    success = true;
                }
                if (!success) {
                    return "STATUS " + otcOperationStatusBase.name() + " CAN NOT BE CHANGED TO " + otcOperationStatusTarget.name() + " BY USER";
                }
            }
            String paymentId = null;
            if (otcOperationId.get("dollarBTCPayment").has("id")) {
                paymentId = otcOperationId.get("dollarBTCPayment").get("id").textValue();
            }
            if (otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS) && (paymentId == null || paymentId.equals(""))) {
                return "BUY MUST HAVE OPERATION PAYMENT ID TO FINISH STATUS";
            }
            ((ObjectNode) otcOperationId).put("otcOperationStatus", otcOperationStatusTarget.name());
            File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(otcOperationId.get("userName").textValue(), otcOperationId.get("currency").textValue(), otcOperationId.get("otcOperationType").textValue());
            File userOTCCurrencyOperationTypeIdFile = new File(userOTCCurrencyOperationTypeFolder, bankerChangeOperationStatusRequest.getId() + ".json");
            JsonNode userOTCCurrencyOperationTypeId = mapper.readTree(userOTCCurrencyOperationTypeIdFile);
            ((ObjectNode) userOTCCurrencyOperationTypeId).put("otcOperationStatus", otcOperationStatusTarget.name());
            switch (bankerChangeOperationStatusRequest.getOtcOperationStatus()) {
                case SUCCESS:
                    File otcCurrencyPaymentBalanceFolder = OTCFolderLocator.getCurrencyPaymentBalanceFolder(null, currency, paymentId);
                    BaseOperation.addToBalance(
                            otcCurrencyPaymentBalanceFolder,
                            currency,
                            amount,
                            BalanceOperationType.MC_BUY_BALANCE,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + bankerChangeOperationStatusRequest.getId(),
                            null,
                            null,
                            false,
                            null
                    );
                    BaseOperation.addToBalance(
                            MoneyclickFolderLocator.getBalanceFolder(OPERATOR_NAME),
                            currency,
                            amount,
                            BalanceOperationType.MC_BUY_BALANCE,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + bankerChangeOperationStatusRequest.getId(),
                            null,
                            charges,
                            false,
                            null
                    );
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.OK, bankerChangeOperationStatusRequest.getId(), "operationId", null);
                    break;
                case CANCELED:
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.FAIL, bankerChangeOperationStatusRequest.getId(), "operationId", bankerChangeOperationStatusRequest.getCanceledReason());
                    new OTCRemoveOperationInOfferFolder(currency, bankerChangeOperationStatusRequest.getId(), null).getResponse();
            }
            FileUtil.editFile(otcOperationId, otcOperationIdFile);
            FileUtil.editFile(userOTCCurrencyOperationTypeId, userOTCCurrencyOperationTypeIdFile);
            new OTCChangeStatusesIndexInCentralFolder(userName, bankerChangeOperationStatusRequest.getId(), otcOperationStatusBase, otcOperationStatusTarget).getResponse();
            String message = "Operation id " + bankerChangeOperationStatusRequest.getId() + " change status to " + bankerChangeOperationStatusRequest.getOtcOperationStatus();
            new UserPostMessage(userName, message, otcOperationType.name().toLowerCase() + "?id=" + bankerChangeOperationStatusRequest.getId()).getResponse();
            String timestamp = DateUtil.getCurrentDate();
            String fileName = DateUtil.getFileDate(timestamp) + ".json";
            ObjectNode messageObject = mapper.createObjectNode();
            messageObject.put("id", bankerChangeOperationStatusRequest.getId());
            messageObject.put("timestamp", timestamp);
            messageObject.put("userName", userName);
            messageObject.put("message", message);
            FileUtil.createFile(messageObject, new File(AdminFolderLocator.getOperationMessagesFolder(), fileName));
            ObjectNode changeStatusObject = mapper.createObjectNode();
            changeStatusObject.put("id", bankerChangeOperationStatusRequest.getId());
            changeStatusObject.put("timestamp", timestamp);
            changeStatusObject.put("otcOperationStatus", bankerChangeOperationStatusRequest.getOtcOperationStatus().name());
            FileUtil.createFile(changeStatusObject, new File(FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsChangeStatusesUserNameFolder(null, userName)), fileName));
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(BankerChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }

    private String sendToPayment() {
        try {
            File otcOperationIdFile = new File(OTCFolderLocator.getOperationIdFolder(null, bankerChangeOperationStatusRequest.getId()), "operation.json");
            JsonNode otcOperationId = mapper.readTree(otcOperationIdFile);
            String userName = otcOperationId.get("userName").textValue();
            OTCOperationType otcOperationType = OTCOperationType.valueOf(otcOperationId.get("otcOperationType").textValue());
            if (!bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)
                    && !bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CLAIM)) {
                Map<UserVerificationType, UserVerificationStatus> userVerifications = (Map<UserVerificationType, UserVerificationStatus>) new UserGetVerifications(userName, null).getResponse();
                for (UserVerificationType userVerificationType : userVerifications.keySet()) {
                    if (!userVerifications.get(userVerificationType).equals(UserVerificationStatus.OK)) {
                        return "USER VERIFICATION TYPE " + userVerificationType.name() + " IS " + userVerifications.get(userVerificationType).name();
                    }
                }
                if (!userVerifications.containsKey(UserVerificationType.E) && !userVerifications.containsKey(UserVerificationType.C)) {
                    return "USER VERIFICATION TYPE " + UserVerificationType.E + " OR " + UserVerificationType.C + " DO NOT EXIST";
                }
            }
            String currency = otcOperationId.get("currency").textValue();
            File masterAccountBalanceFolder;
            if (otcOperationId.has("otcMasterAccountName")) {
                masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(otcOperationId.get("otcMasterAccountName").textValue());
            } else {
                masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(new MasterAccountNewGetOTCMasterAccountName(null, currency).getResponse().get("name").textValue());
            }
            double amount = otcOperationId.get("amount").doubleValue();
            OTCOperationStatus otcOperationStatusBase = OTCOperationStatus.valueOf(otcOperationId.get("otcOperationStatus").textValue());
            OTCOperationStatus otcOperationStatusTarget = bankerChangeOperationStatusRequest.getOtcOperationStatus();
            JsonNode charges = otcOperationId.get("charges");
            if (bankerChangeOperationStatusRequest.isUserChange()) {
                boolean success = false;
                if ((otcOperationStatusBase.equals(OTCOperationStatus.CANCELED) || otcOperationStatusBase.equals(OTCOperationStatus.SUCCESS)
                        && otcOperationStatusTarget.equals(OTCOperationStatus.CLAIM))) {
                    success = true;
                }
                if (!success) {
                    return "STATUS " + otcOperationStatusBase.name() + " CAN NOT BE CHANGED TO " + otcOperationStatusTarget.name() + " BY USER";
                }
            }
            String paymentId = bankerChangeOperationStatusRequest.getPaymentId();
            if (otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS) && (paymentId == null || paymentId.equals(""))) {
                return "SEND MUST HAVE REQUEST PAYMENT ID TO FINISH STATUS";
            }
            ((ObjectNode) otcOperationId).put("otcOperationStatus", otcOperationStatusTarget.name());
            File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(otcOperationId.get("userName").textValue(), otcOperationId.get("currency").textValue(), otcOperationId.get("otcOperationType").textValue());
            File userOTCCurrencyOperationTypeIdFile = new File(userOTCCurrencyOperationTypeFolder, bankerChangeOperationStatusRequest.getId() + ".json");
            JsonNode userOTCCurrencyOperationTypeId = mapper.readTree(userOTCCurrencyOperationTypeIdFile);
            ((ObjectNode) userOTCCurrencyOperationTypeId).put("otcOperationStatus", otcOperationStatusTarget.name());
            String operatorName = null;
            Iterator<JsonNode> operatorNamesIterator = BaseOperation.getOperators().iterator();
            switch (bankerChangeOperationStatusRequest.getOtcOperationStatus()) {
                case SUCCESS:
                    while (operatorNamesIterator.hasNext()) {
                        String operatorNamesIt = operatorNamesIterator.next().textValue();
                        if (OTCFolderLocator.getCurrencyPaymentFile(operatorNamesIt, currency, paymentId).isFile()) {
                            operatorName = operatorNamesIt;
                            break;
                        }
                    }
                    if (operatorName == null) {
                        return "THERE IS NO DOLLARBTC PAYMENT TO THIS OPERATION";
                    }
                    if (!otcOperationId.has("dollarBTCPayment")) {
                        JsonNode dollarBTCPayment = mapper.readTree(OTCFolderLocator.getCurrencyPaymentFile(operatorName, currency, paymentId));
                        ((ObjectNode) otcOperationId).set("dollarBTCPayment", dollarBTCPayment);
                    }
                    File otcCurrencyPaymentBalanceFolder = OTCFolderLocator.getCurrencyPaymentBalanceFolder(operatorName, currency, paymentId);
                    String substractResult = BaseOperation.substractToBalance(
                            otcCurrencyPaymentBalanceFolder,
                            currency,
                            amount,
                            BalanceOperationType.SEND_TO_PAYMENT,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + bankerChangeOperationStatusRequest.getId(),
                            null,
                            false,
                            null,
                            false,
                            null
                    );
                    if (!substractResult.equals("OK")) {
                        return substractResult;
                    }
                    BaseOperation.substractToBalance(
                            MoneyclickFolderLocator.getBalanceFolder(OPERATOR_NAME),
                            currency,
                            amount,
                            BalanceOperationType.SEND_TO_PAYMENT,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + bankerChangeOperationStatusRequest.getId(),
                            null,
                            true,
                            charges,
                            false,
                            null
                    );
                    File operationChargesFolder = new File(new File(ExchangeUtil.OPERATOR_PATH, "Charges"), "OPERATION__" + bankerChangeOperationStatusRequest.getId());
                    File paymentChargesFolder = new File(new File(ExchangeUtil.OPERATOR_PATH, "Charges"), "PAYMENT__" + paymentId);
                    BaseOperation.moveCharges(operationChargesFolder, paymentChargesFolder);
                    break;
                case CANCELED:
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getBalanceFolder(userName), BalanceOperationStatus.FAIL, bankerChangeOperationStatusRequest.getId(), "additionalInfo", bankerChangeOperationStatusRequest.getCanceledReason());
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.FAIL, bankerChangeOperationStatusRequest.getId(), "operationId", bankerChangeOperationStatusRequest.getCanceledReason());
                    BaseOperation.changeBalanceOperationStatus(masterAccountBalanceFolder, BalanceOperationStatus.FAIL, bankerChangeOperationStatusRequest.getId(), "additionalInfo", bankerChangeOperationStatusRequest.getCanceledReason());
                    new OTCRemoveOperationInOfferFolder(currency, bankerChangeOperationStatusRequest.getId(), null).getResponse();
            }
            FileUtil.editFile(otcOperationId, otcOperationIdFile);
            FileUtil.editFile(userOTCCurrencyOperationTypeId, userOTCCurrencyOperationTypeIdFile);
            new OTCChangeStatusesIndexInCentralFolder(userName, bankerChangeOperationStatusRequest.getId(), otcOperationStatusBase, otcOperationStatusTarget).getResponse();
            String message = "Operation id " + bankerChangeOperationStatusRequest.getId() + " change status to " + bankerChangeOperationStatusRequest.getOtcOperationStatus();
            new UserPostMessage(userName, message, otcOperationType.name().toLowerCase() + "?id=" + bankerChangeOperationStatusRequest.getId()).getResponse();
            String timestamp = DateUtil.getCurrentDate();
            String fileName = DateUtil.getFileDate(timestamp) + ".json";
            ObjectNode messageObject = mapper.createObjectNode();
            messageObject.put("id", bankerChangeOperationStatusRequest.getId());
            messageObject.put("timestamp", timestamp);
            messageObject.put("userName", userName);
            messageObject.put("message", message);
            FileUtil.createFile(messageObject, new File(AdminFolderLocator.getOperationMessagesFolder(), fileName));
            ObjectNode changeStatusObject = mapper.createObjectNode();
            changeStatusObject.put("id", bankerChangeOperationStatusRequest.getId());
            changeStatusObject.put("timestamp", timestamp);
            changeStatusObject.put("otcOperationStatus", bankerChangeOperationStatusRequest.getOtcOperationStatus().name());
            FileUtil.createFile(changeStatusObject, new File(FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsChangeStatusesUserNameFolder(operatorName, userName)), fileName));
            if (otcOperationType.equals(OTCOperationType.SEND_TO_PAYMENT)) {
                String smsMessage = null;
                if (bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)) {
                    smsMessage = "MC Send to Payment Operation of " + String.format ("%.2f", amount) + " " + currency + " was canceled because a problem with your bank account. Please check you bank account info and try again.";
                } else if (bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.SUCCESS)) {
                    smsMessage = "MC Send to Payment Operation of " + String.format ("%.2f", amount) + " " + currency + " succeed. Some banks takes until 48 hours to show new transactions. For any cuestions contact us at customer service section.";
                }
                try {
                    JsonNode userConfig = mapper.readTree(UsersFolderLocator.getConfigFile(userName));
                    if (userConfig.has("phone") && smsMessage != null) {
                        new SMSSender().publish(smsMessage, new String[]{userConfig.get("phone").textValue()});
                    }
                } catch (IOException ex) {
                    Logger.getLogger(OTCChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(OTCChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }

}
