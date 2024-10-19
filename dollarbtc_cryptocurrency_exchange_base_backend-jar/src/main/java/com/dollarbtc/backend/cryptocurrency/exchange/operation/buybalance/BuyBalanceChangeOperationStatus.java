/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.buybalance;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.buyBalance.BuyBalanceChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeStatusesIndexInCentralFolder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCRemoveOperationInOfferFolder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerifications;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AdminFolderLocator;
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
public class BuyBalanceChangeOperationStatus extends AbstractOperation<String> {

    private final BuyBalanceChangeOperationStatusRequest buyBalanceChangeOperationStatusRequest;

    public BuyBalanceChangeOperationStatus(BuyBalanceChangeOperationStatusRequest buyBalanceChangeOperationStatusRequest) {
        super(String.class);
        this.buyBalanceChangeOperationStatusRequest = buyBalanceChangeOperationStatusRequest;
    }

    @Override
    protected void execute() {
        try {
            File otcOperationIdFile = new File(OTCFolderLocator.getOperationIdFolder(null, buyBalanceChangeOperationStatusRequest.getId()), "operation.json");
            JsonNode otcOperationId = mapper.readTree(otcOperationIdFile);
            String userName = otcOperationId.get("userName").textValue();
            OTCOperationType otcOperationType = OTCOperationType.valueOf(otcOperationId.get("otcOperationType").textValue());
            if (!buyBalanceChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)
                    && !buyBalanceChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CLAIM)) {
                Map<UserVerificationType, UserVerificationStatus> userVerifications = (Map<UserVerificationType, UserVerificationStatus>) new UserGetVerifications(userName, null).getResponse();
                for (UserVerificationType userVerificationType : userVerifications.keySet()) {
                    if (!userVerifications.get(userVerificationType).equals(UserVerificationStatus.OK)) {
                        super.response = "USER VERIFICATION TYPE " + userVerificationType.name() + " IS " + userVerifications.get(userVerificationType).name();
                        return;
                    }
                }
                if (!(userVerifications.containsKey(UserVerificationType.C) || userVerifications.containsKey(UserVerificationType.F))) {
                    super.response = "USER VERIFICATION TYPE " + UserVerificationType.C + " DOES NOT EXIST";
                    return;
                }
            }
            String currency = otcOperationId.get("currency").textValue();
            double amount = otcOperationId.get("amount").doubleValue();
            OTCOperationStatus otcOperationStatusBase = OTCOperationStatus.valueOf(otcOperationId.get("otcOperationStatus").textValue());
            OTCOperationStatus otcOperationStatusTarget = buyBalanceChangeOperationStatusRequest.getOtcOperationStatus();
            JsonNode charges = otcOperationId.get("charges");
            if (buyBalanceChangeOperationStatusRequest.isUserChange()) {
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
                    super.response = "STATUS " + otcOperationStatusBase.name() + " CAN NOT BE CHANGED TO " + otcOperationStatusTarget.name() + " BY USER";
                    return;
                }
            }
            String paymentId = null;
            if (otcOperationId.get("dollarBTCPayment").has("id")) {
                paymentId = otcOperationId.get("dollarBTCPayment").get("id").textValue();
            }
            if (otcOperationStatusTarget.equals(OTCOperationStatus.SUCCESS) && (paymentId == null || paymentId.equals(""))) {
                super.response = "BUY MUST HAVE OPERATION PAYMENT ID TO FINISH STATUS";
                return;
            }
            ((ObjectNode) otcOperationId).put("otcOperationStatus", otcOperationStatusTarget.name());
            File userOTCCurrencyOperationTypeFolder = UsersFolderLocator.getOTCCurrencyOperationTypeFolder(otcOperationId.get("userName").textValue(), otcOperationId.get("currency").textValue(), otcOperationId.get("otcOperationType").textValue());
            File userOTCCurrencyOperationTypeIdFile = new File(userOTCCurrencyOperationTypeFolder, buyBalanceChangeOperationStatusRequest.getId() + ".json");
            JsonNode userOTCCurrencyOperationTypeId = mapper.readTree(userOTCCurrencyOperationTypeIdFile);
            ((ObjectNode) userOTCCurrencyOperationTypeId).put("otcOperationStatus", otcOperationStatusTarget.name());
            switch (buyBalanceChangeOperationStatusRequest.getOtcOperationStatus()) {
                case SUCCESS:
                    File otcCurrencyPaymentBalanceFolder = OTCFolderLocator.getCurrencyPaymentBalanceFolder(null, currency, paymentId);
                    BaseOperation.addToBalance(
                            otcCurrencyPaymentBalanceFolder,
                            currency,
                            amount,
                            BalanceOperationType.MC_BUY_BALANCE,
                            BalanceOperationStatus.OK,
                            "OTC operation id " + buyBalanceChangeOperationStatusRequest.getId(),
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
                            "OTC operation id " + buyBalanceChangeOperationStatusRequest.getId(),
                            null,
                            charges,
                            false,
                            null
                    );
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.OK, buyBalanceChangeOperationStatusRequest.getId(), "operationId", null);
                    break;
                case CANCELED:
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.FAIL, buyBalanceChangeOperationStatusRequest.getId(), "operationId", buyBalanceChangeOperationStatusRequest.getCanceledReason());
                    new OTCRemoveOperationInOfferFolder(currency, buyBalanceChangeOperationStatusRequest.getId(), null).getResponse();
                    substractToAccumulatedAmount(otcOperationId.get("dollarBTCPayment"), currency, amount);
            }
            FileUtil.editFile(otcOperationId, otcOperationIdFile);
            FileUtil.editFile(userOTCCurrencyOperationTypeId, userOTCCurrencyOperationTypeIdFile);
            new OTCChangeStatusesIndexInCentralFolder(userName, buyBalanceChangeOperationStatusRequest.getId(), otcOperationStatusBase, otcOperationStatusTarget).getResponse();
            String message = "Operation id " + buyBalanceChangeOperationStatusRequest.getId() + " change status to " + buyBalanceChangeOperationStatusRequest.getOtcOperationStatus();
            new UserPostMessage(userName, message, otcOperationType.name().toLowerCase() + "?id=" + buyBalanceChangeOperationStatusRequest.getId()).getResponse();
            String timestamp = DateUtil.getCurrentDate();
            String fileName = DateUtil.getFileDate(timestamp) + ".json";
            ObjectNode messageObject = mapper.createObjectNode();
            messageObject.put("id", buyBalanceChangeOperationStatusRequest.getId());
            messageObject.put("timestamp", timestamp);
            messageObject.put("userName", userName);
            messageObject.put("message", message);
            FileUtil.createFile(messageObject, new File(AdminFolderLocator.getOperationMessagesFolder(), fileName));
            ObjectNode changeStatusObject = mapper.createObjectNode();
            changeStatusObject.put("id", buyBalanceChangeOperationStatusRequest.getId());
            changeStatusObject.put("timestamp", timestamp);
            changeStatusObject.put("otcOperationStatus", buyBalanceChangeOperationStatusRequest.getOtcOperationStatus().name());
            FileUtil.createFile(changeStatusObject, new File(FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsChangeStatusesUserNameFolder(null, userName)), fileName));
            String notificationMessage = null;
            if (buyBalanceChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)) {
                notificationMessage = "Deposit Operation of " + String.format("%(,.2f", amount) + " " + currency + " was canceled because a problem with submited information. Contact our operators for more details.";
            } else if (buyBalanceChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.SUCCESS)) {
                notificationMessage = "Deposit Operation of " + String.format("%(,.2f", amount) + " " + currency + " succeed. See your balance to confirm.";
            }
            if (notificationMessage != null) {
                new NotificationSendMessageByUserName(userName, "Transaction information", notificationMessage).getResponse();
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(BuyBalanceChangeOperationStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void substractToAccumulatedAmount(JsonNode dollarBTCPayment, String currency, Double amount) {
        if(!dollarBTCPayment.has("id")){
            return;
        }
        String paymentId = dollarBTCPayment.get("id").textValue();
        if(!dollarBTCPayment.has("type")){
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
                    ((ObjectNode) otcCurrencyPaymentBuyBalanceIt).put("accumulatedAmountBuy", otcCurrencyPaymentBuyBalanceIt.get("accumulatedAmountBuy").doubleValue() - amount);
                    FileUtil.editFile(otcCurrencyPayment, otcCurrencyPaymentFile);
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BuyBalanceCreateOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
