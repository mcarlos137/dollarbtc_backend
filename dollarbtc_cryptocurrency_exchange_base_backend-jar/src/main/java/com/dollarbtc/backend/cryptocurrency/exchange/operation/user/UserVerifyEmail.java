/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
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
public class UserVerifyEmail extends AbstractOperation<String> {

    private final String userName, id;

    public UserVerifyEmail(String userName, String id) {
        super(String.class);
        this.userName = userName;
        this.id = id;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.isFile()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            if (!user.has("email")) {
                super.response = "USER DOES NOT HAVE FIELDNAME " + "email";
                return;
            }
            if (!user.has("verification")) {
                super.response = "USER DOES NOT HAVE ANY VERIFICATION";
                return;
            }
            JsonNode userVerification = user.get("verification");
            if (!userVerification.has("A")) {
                super.response = "USER DOES NOT HAVE EMAIL VERIFICATION";
                return;
            }
            if(id.equals(userVerification.get("A").get("verificationOperationId").textValue())){
                ((ObjectNode) userVerification.get("A")).put("userVerificationStatus", UserVerificationStatus.OK.name());
                FileUtil.editFile(user, userFile);
                super.response = "OK";
                return;
            } else {
                super.response = "ID DOES NOT MATCH WITH USER VERIFICATION";
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(UserVerifyEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
