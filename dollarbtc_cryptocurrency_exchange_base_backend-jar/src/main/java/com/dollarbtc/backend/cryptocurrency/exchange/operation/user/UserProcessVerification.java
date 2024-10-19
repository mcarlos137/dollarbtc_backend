/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserProcessVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCVerifyClientPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersVerificationFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class UserProcessVerification extends AbstractOperation<String> {

    private final UserProcessVerificationRequest userProcessVerificationRequest;

    public UserProcessVerification(UserProcessVerificationRequest userProcessVerificationRequest) {
        super(String.class);
        this.userProcessVerificationRequest = userProcessVerificationRequest;
    }

    @Override
    protected void execute() {
        File usersVerificationStatusFolder = UsersVerificationFolderLocator.getStatusFolder(userProcessVerificationRequest.getLastUserVerificationStatus());
        if (!usersVerificationStatusFolder.isDirectory()) {
            super.response = "VERIFICATION STATUS DOES NOT EXIST";
            return;
        }
        for (File userVerificationStatusFile : usersVerificationStatusFolder.listFiles()) {
            if (!userVerificationStatusFile.isFile()) {
                continue;
            }
            String fileName = DateUtil.getFileDate(userProcessVerificationRequest.getTimestamp()) + ".json";
            if (userVerificationStatusFile.getName().equals(fileName)) {
                try {
                    JsonNode userVerificationStatus = mapper.readTree(userVerificationStatusFile);
                    if (userProcessVerificationRequest.getUserName()
                            .equals(userVerificationStatus.get("userName").textValue())
                            && userProcessVerificationRequest.getUserVerificationType().equals(UserVerificationType
                                    .valueOf(userVerificationStatus.get("userVerificationType").textValue()))) {
                        UserVerificationStatus userVerificationS;
                        if (userProcessVerificationRequest.isSuccess()) {
                            if (userProcessVerificationRequest.getUserVerificationType()
                                    .equals(UserVerificationType.D)) {
                                File userConfigFile = UsersFolderLocator.getConfigFile(userProcessVerificationRequest.getUserName());
                                JsonNode userConfig = mapper.readTree(userConfigFile);
                                String userName = userConfig.get("name").textValue();
                                String paymentId = null;
                                Iterator<JsonNode> fielNamesIterator = userConfig.get("verification")
                                        .get(userProcessVerificationRequest.getUserVerificationType().name())
                                        .get("fieldNames").elements();
                                while (fielNamesIterator.hasNext()) {
                                    String fielName = fielNamesIterator.next().textValue();
                                    if (fielName.contains("paymentId")) {
                                        paymentId = fielName.split("__")[1];
                                        break;
                                    }
                                }
                                if (paymentId == null) {
                                    super.response = "VERIFICATION WITH NO PAYMENT";
                                    return;
                                }
                                String verifyClientPaymentResponse = new OTCVerifyClientPayment(userProcessVerificationRequest.getUserName(), paymentId).getResponse();
                                if (!verifyClientPaymentResponse.equals("OK")) {
                                    super.response = verifyClientPaymentResponse;
                                    return;
                                }
                            }
                            userVerificationS = UserVerificationStatus.OK;
                        } else {
                            userVerificationS = UserVerificationStatus.FAIL;
                        }
                        FileUtil.moveFileToFolder(userVerificationStatusFile,
                                UsersVerificationFolderLocator.getStatusFolder(userVerificationS));
                        File userFile = UsersFolderLocator.getConfigFile(userProcessVerificationRequest.getUserName());
                        JsonNode user = mapper.readTree(userFile);
                        ((ObjectNode) user.get("verification")
                                .get(userProcessVerificationRequest.getUserVerificationType().name()))
                                .put("userVerificationStatus", userVerificationS.name());
                        FileUtil.editFile(user, userFile);
                        String notificationMessage = null;
                        if (userProcessVerificationRequest.getUserVerificationType().equals(UserVerificationType.C)) {
                            if (userVerificationS.equals(UserVerificationStatus.OK)) {
                                notificationMessage = "Your user verification has been accepted. Go to deposits and complete your operation.";
                            } else if (userVerificationS.equals(UserVerificationStatus.FAIL)) {
                                notificationMessage = "Your user verification has failed because some of your documents are missing or do not comply with the standard. Please contact customer support for more details.";
                            }
                        }
                        if (userProcessVerificationRequest.getUserVerificationType().equals(UserVerificationType.D)) {
                            if (userVerificationS.equals(UserVerificationStatus.OK)) {
                                notificationMessage = "Your bank account verification has been accepted. Go to deposits and complete your operation.";
                            } else if (userVerificationS.equals(UserVerificationStatus.FAIL)) {
                                notificationMessage = "Your bank account verification has failed. Please contact customer support for more details.";
                            }
                        }
                        if (notificationMessage != null) {
                            new NotificationSendMessageByUserName(userProcessVerificationRequest.getUserName(), "Verification information", notificationMessage).getResponse();
                        }
                        super.response = "OK";
                        return;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UserProcessVerification.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.response = "FAIL";
    }

}
