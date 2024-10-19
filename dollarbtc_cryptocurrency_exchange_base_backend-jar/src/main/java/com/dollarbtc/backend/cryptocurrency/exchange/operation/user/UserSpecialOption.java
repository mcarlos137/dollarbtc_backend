/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserSpecialOptionRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
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
public class UserSpecialOption extends AbstractOperation<String> {

    private final UserSpecialOptionRequest userSpecialOptionRequest;

    public UserSpecialOption(UserSpecialOptionRequest userSpecialOptionRequest) {
        super(String.class);
        this.userSpecialOptionRequest = userSpecialOptionRequest;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userSpecialOptionRequest.getUserName());
        if (!userFile.exists()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            switch (userSpecialOptionRequest.getOption()) {
                case "REQUEST_DEBIT_CARDS":
                    ((ObjectNode) user).put("enableRequestDebitCards", userSpecialOptionRequest.getEnable());
                    FileUtil.editFile(user, userFile);
                    super.response = "OK";
                    return;
                case "ONE_DEPOSIT_VERIFICATION":
                    ((ObjectNode) user).put("enableOneDepositVerification", userSpecialOptionRequest.getEnable());
                    FileUtil.editFile(user, userFile);
                    super.response = "OK";
                    return;
                case "ACTIVATE_GIFT_CARDS":
                    ((ObjectNode) user).put("enableActivateGiftCards", userSpecialOptionRequest.getEnable());
                    FileUtil.editFile(user, userFile);
                    super.response = "OK";
                    return;
                default:
                    super.response = "INVALID SPECIAL OPERATION";
                    return;
            }
        } catch (IOException ex) {
            Logger.getLogger(UserSpecialOption.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
