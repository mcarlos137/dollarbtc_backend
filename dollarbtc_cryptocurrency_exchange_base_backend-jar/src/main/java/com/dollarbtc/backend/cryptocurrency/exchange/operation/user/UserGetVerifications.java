/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetClientPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersVerificationFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetVerifications extends AbstractOperation<Object> {

    private final String userName;
    private final UserVerificationStatus userVerificationStatus;

    public UserGetVerifications(String userName, UserVerificationStatus userVerificationStatus) {
        super(Object.class);
        this.userName = userName;
        this.userVerificationStatus = userVerificationStatus;
    }

    @Override
    protected void execute() {
        if (userName != null && userVerificationStatus != null) {
            super.response = method(userName, userVerificationStatus);
        } else if (userName == null && userVerificationStatus != null) {
            super.response = method(userVerificationStatus);
        } else if (userName != null && userVerificationStatus == null) {
            super.response = method(userName);
        }
    }

    private Map<UserVerificationType, UserVerificationStatus> method(String userName) {
        Map<UserVerificationType, UserVerificationStatus> verifications = new HashMap<>();
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.isFile()) {
            return verifications;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            if (!user.has("verification")) {
                return verifications;
            }
            JsonNode userVerification = user.get("verification");
            Iterator<String> userVerificationTypes = userVerification.fieldNames();
            while (userVerificationTypes.hasNext()) {
                String userVerificationType = userVerificationTypes.next();
                verifications.put(UserVerificationType.valueOf(userVerificationType), UserVerificationStatus
                        .valueOf(userVerification.get(userVerificationType).get("userVerificationStatus").textValue()));
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGetVerifications.class.getName()).log(Level.SEVERE, null, ex);
        }
        return verifications;
    }

    private ArrayNode method(UserVerificationStatus userVerificationStatus) {
        ArrayNode verifications = mapper.createArrayNode();
        File usersVerificationStatusFolder = UsersVerificationFolderLocator.getStatusFolder(userVerificationStatus);
        if (!usersVerificationStatusFolder.isDirectory()) {
            return verifications;
        }
        for (File userVerificationStatusFile : usersVerificationStatusFolder.listFiles()) {
            if (!userVerificationStatusFile.isFile()) {
                continue;
            }
            try {
                JsonNode verificationStatus = mapper.readTree(userVerificationStatusFile);
                verifications.add(verificationStatus);
            } catch (IOException ex) {
                Logger.getLogger(UserGetVerifications.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return verifications;
    }

    private ArrayNode method(String userName, UserVerificationStatus userVerificationStatus) {
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        List<UserVerificationType> allowedUserVerificationType = new ArrayList<>();
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has("allowVerificationTypeC") && userConfig.get("allowVerificationTypeC").booleanValue()) {
                allowedUserVerificationType.add(UserVerificationType.C);
            }
            if (userConfig.has("allowVerificationTypeD") && userConfig.get("allowVerificationTypeD").booleanValue()) {
                allowedUserVerificationType.add(UserVerificationType.D);
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGetVerifications.class.getName()).log(Level.SEVERE, null, ex);
        }
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        ArrayNode verifications = mapper.createArrayNode();
        File usersVerificationStatusFolder = UsersVerificationFolderLocator.getStatusFolder(userVerificationStatus);
        if (!usersVerificationStatusFolder.isDirectory()) {
            return verifications;
        }
        for (File userVerificationStatusFile : usersVerificationStatusFolder.listFiles()) {
            if (!userVerificationStatusFile.isFile()) {
                continue;
            }
            try {
                JsonNode verificationStatus = mapper.readTree(userVerificationStatusFile);
                if(!UsersFolderLocator.getFolder(verificationStatus.get("userName").textValue()).isDirectory()){
                    continue;
                }
                UserVerificationType userVerificationType = UserVerificationType.valueOf(verificationStatus.get("userVerificationType").textValue());
                if (!allowedUserVerificationType.contains(userVerificationType)) {
                    continue;
                }
                if (verificationStatus.has("paymentId")) {
                    JsonNode payment = new OTCGetClientPayment(verificationStatus.get("userName").textValue(), verificationStatus.get("paymentId").textValue()).getResponse();
                    if (!payment.has("currency")) {
                        continue;
                    }
                    if (!userCurrencies.contains(payment.get("currency").textValue())) {
                        continue;
                    }
                }
                verifications.add(verificationStatus);
            } catch (IOException ex) {
                Logger.getLogger(UserGetVerifications.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return verifications;
    }

}
