/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserRemoveVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserRemoveVerification extends AbstractOperation<String> {

    private final UserRemoveVerificationRequest userRemoveVerificationRequest;

    public UserRemoveVerification(UserRemoveVerificationRequest userRemoveVerificationRequest) {
        super(String.class);
        this.userRemoveVerificationRequest = userRemoveVerificationRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userRemoveVerificationRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        if (userRemoveVerificationRequest.getUserVerificationType().equals(UserVerificationType.A)) {
            super.response = "VERIFICATION A CAN NOT BE REMOVED";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has("verification")) {
                ((ObjectNode) userConfig.get("verification")).remove(userRemoveVerificationRequest.getUserVerificationType().name());
                if (userRemoveVerificationRequest.getRemoveFieldNames() != null) {
                    for (String removeFieldName : userRemoveVerificationRequest.getRemoveFieldNames()) {
                        ((ObjectNode) userConfig).remove(removeFieldName);
                    }
                }
                FileUtil.editFile(userConfig, userConfigFile);
                super.response = "OK";
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(UserRemoveVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
