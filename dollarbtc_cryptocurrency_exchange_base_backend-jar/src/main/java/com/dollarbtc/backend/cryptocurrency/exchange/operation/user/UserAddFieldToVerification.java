/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddFieldToVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserAddFieldToVerification extends AbstractOperation<String> {

    private final UserAddFieldToVerificationRequest userAddFieldToVerificationRequest;

    public UserAddFieldToVerification(UserAddFieldToVerificationRequest userAddFieldToVerificationRequest) {
        super(String.class);
        this.userAddFieldToVerificationRequest = userAddFieldToVerificationRequest;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userAddFieldToVerificationRequest.getUserName());
        if (!userFile.isFile()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            if (!user.has("verification")) {
                super.response = "VERIFICATION DOES NOT EXIST";
                return;
            }
            JsonNode userVerification = user.get("verification");
            if (!userVerification.has(userAddFieldToVerificationRequest.getUserVerificationType().name())) {
                super.response = "VERIFICATION DOES NOT EXIST";
                return;
            }
            JsonNode userVerificationType = userVerification
                    .get(userAddFieldToVerificationRequest.getUserVerificationType().name());
            for (String fieldName : userAddFieldToVerificationRequest.getFieldNames()) {
                if (!user.has(fieldName)) {
                    super.response = "USER DOES NOT HAVE FIELDNAME " + fieldName;
                    return;
                }
                ((ArrayNode) userVerificationType.get("fieldNames")).add(fieldName);
            }
            FileUtil.editFile(user, userFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserAddFieldToVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
