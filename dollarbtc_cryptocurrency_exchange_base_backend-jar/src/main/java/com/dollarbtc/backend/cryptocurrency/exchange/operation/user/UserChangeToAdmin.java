/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserOperationAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
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
public class UserChangeToAdmin extends AbstractOperation<String> {

    private final String userName;

    public UserChangeToAdmin(String userName) {
        super(String.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            ((ObjectNode) userConfig).put("type", UserType.ADMIN.name());
            ((ObjectNode) userConfig).put("environment", UserEnvironment.NONE.name());
            ((ObjectNode) userConfig).put("operationAccount", UserOperationAccount.MULTIPLE.name());
            FileUtil.editFile(userConfig, userConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserChangeToAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
