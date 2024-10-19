/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersCodesFolderLocator;
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
public class UserProcess extends AbstractOperation<String> {

    private final String userName;
    private final ProcessType processType;
    private final String registrationCode;

    public UserProcess(String userName, ProcessType processType, String registrationCode) {
        super(String.class);
        this.userName = userName;
        this.processType = processType;
        this.registrationCode = registrationCode;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.exists()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        JsonNode user = null;
        try {
            user = mapper.readTree(userFile);
        } catch (IOException ex) {
            Logger.getLogger(UserProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user == null) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        if (UsersFolderLocator.getLockFile(userName).isFile()) {
            super.response = "USER IS LOCKED";
            return;
        }
        if (registrationCode != null && !registrationCode.equals("")) {
            File usersCodesRegistrationUserFile = UsersCodesFolderLocator.getRegistrationUserFile(userName);
            if (!usersCodesRegistrationUserFile.isFile()) {
                super.response = "USER HAS NOT REGISTRATION CODE";
                return;
            }
            try {
                JsonNode usersCodesRegistrationUser = mapper.readTree(usersCodesRegistrationUserFile);
                if (!usersCodesRegistrationUser.get("optCode").textValue().equals(registrationCode)) {
                    super.response = "REGISTRATION CODE IS INVALID FOR USER";
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(UserProcess.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
        }
        try {
            String timestamp = DateUtil.getCurrentDate();
            ObjectNode lock = mapper.createObjectNode();
            lock.put("timestamp", timestamp);
            FileUtil.createFile(lock, UsersFolderLocator.getLockFile(userName));
            switch (processType) {
                case ACTIVATE:
                    ((ObjectNode) user).put("active", true);
                    break;
                case INACTIVATE:
                    ((ObjectNode) user).put("active", false);
                    break;
            }
            FileUtil.editFile(user, userFile);
            super.response = "OK";
        } finally {
            FileUtil.deleteFile(UsersFolderLocator.getLockFile(userName));
        }
    }

    public static enum ProcessType {
        ACTIVATE, INACTIVATE;
    }

}
