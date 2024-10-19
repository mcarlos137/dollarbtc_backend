/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserStartVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCCreateVerificationOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersVerificationFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserStartVerification extends AbstractOperation<String> {

    private final UserStartVerificationRequest userStartVerificationRequest;

    public UserStartVerification(UserStartVerificationRequest userStartVerificationRequest) {
        super(String.class);
        this.userStartVerificationRequest = userStartVerificationRequest;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userStartVerificationRequest.getUserName());
        if (!userFile.isFile()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            if (!user.has("verification")) {
                ((ObjectNode) user).set("verification", mapper.createObjectNode());
            }
            JsonNode userVerification = user.get("verification");
            if (!userVerification.has(userStartVerificationRequest.getUserVerificationType().name())) {
                ((ObjectNode) userVerification).set(userStartVerificationRequest.getUserVerificationType().name(),
                        mapper.createObjectNode());
            }
            if (userStartVerificationRequest.getUserVerificationType().equals(UserVerificationType.D)
                    && userVerification.has(userStartVerificationRequest.getUserVerificationType().name())
                    && userVerification.get(userStartVerificationRequest.getUserVerificationType().name())
                            .has("userVerificationStatus")
                    && UserVerificationStatus
                            .valueOf(userVerification.get(userStartVerificationRequest.getUserVerificationType().name())
                                    .get("userVerificationStatus").textValue())
                            .equals(UserVerificationStatus.OK)) {
                ((ObjectNode) userVerification).set(userStartVerificationRequest.getUserVerificationType().name(),
                        mapper.createObjectNode());
            }
            JsonNode userVerificationType = userVerification
                    .get(userStartVerificationRequest.getUserVerificationType().name());
            if (!userVerificationType.has("userVerificationStatus")) {
                switch (userStartVerificationRequest.getUserVerificationType()) {
                    case A:   
                    case B:
                    case E:
                    case F:
                        ((ObjectNode) userVerificationType).put("userVerificationStatus", UserVerificationStatus.OK.name());
                        break;
                    case C:
                    case D:
                        ((ObjectNode) userVerificationType).put("userVerificationStatus",
                                UserVerificationStatus.PROCESSING.name());
                        ((ObjectNode) userVerificationType).put("verificationOperationId",
                                new OTCCreateVerificationOperation(userStartVerificationRequest.getUserName()).getResponse());
                        break;
                }
                String paymentId = null;
                if (userStartVerificationRequest.getFieldNames() != null) {
                    ((ObjectNode) userVerificationType).putArray("fieldNames");
                    for (String fieldName : userStartVerificationRequest.getFieldNames()) {
                        if (fieldName.contains("paymentId")) {
                            paymentId = fieldName.split("__")[1];
                        }
                        if (!user.has(fieldName)) {
                            super.response = "USER DOES NOT HAVE FIELDNAME " + fieldName;
                            return;
                        }
                        ((ArrayNode) userVerificationType.get("fieldNames")).add(fieldName);
                    }
                }
//                if(paymentId != null){
//                    JsonNode clientPayment = OTCOperation.getClientPayment(userStartVerificationRequest.getUserName(), paymentId);
//                    if(clientPayment.has("type") && PaymentType.valueOf(clientPayment.get("type").textValue()).equals(PaymentType.CREDIT_CARD)){
//                        return "OK";
//                    }
//                }
                String timestamp = DateUtil.getCurrentDate();
                ((ObjectNode) userVerificationType).put("timestamp", timestamp);
                ((ObjectNode) userVerificationType).put("info", userStartVerificationRequest.getInfo());
                FileUtil.editFile(user, userFile);
                ((ObjectNode) userVerificationType).remove("userVerificationStatus");
                ((ObjectNode) userVerificationType).remove("fieldNames");
                ((ObjectNode) userVerificationType).put("userName", userStartVerificationRequest.getUserName());
                ((ObjectNode) userVerificationType).put("userVerificationType",
                        userStartVerificationRequest.getUserVerificationType().name());
                switch (userStartVerificationRequest.getUserVerificationType()) {
                    case C:
                    case D:
                        if (paymentId != null) {
                            ((ObjectNode) userVerificationType).put("paymentId", paymentId);
                        }
                        for (String fieldName : userStartVerificationRequest.getFieldNames()) {
                            ((ObjectNode) userVerificationType).set(fieldName, user.get(fieldName));
                        }
                        FileUtil.createFile(userVerificationType,
                                new File(UsersVerificationFolderLocator.getStatusFolder(UserVerificationStatus.PROCESSING),
                                        DateUtil.getFileDate(timestamp) + ".json"));
                        break;
                    case F:
                        FileUtil.createFile(userVerificationType,
                                new File(UsersVerificationFolderLocator.getStatusFolder(UserVerificationStatus.ONE_DEPOSIT),
                                        DateUtil.getFileDate(timestamp) + ".json"));
                        break;
                    default:
                        break;
                }
                super.response = "OK";
                return;
            } else {
                UserVerificationStatus userVerificationStatus = UserVerificationStatus
                        .valueOf(userVerificationType.get("userVerificationStatus").textValue());
                switch (userVerificationStatus) {
                    case PROCESSING:
                        super.response = "VERIFICATION ALREADY STARTED";
                        return;
                    case OK:
                        super.response = "VERIFICATION OK";
                        return;
                    case FAIL:
                        super.response = "VERIFICATION FAIL";
                        return;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserStartVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
