/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAllowAssignedPaymentsOnlyRequest;
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
public class UserAllowAssignedPaymentsOnly extends AbstractOperation<String> {

    private final UserAllowAssignedPaymentsOnlyRequest userAllowAssignedPaymentsOnlyRequest;

    public UserAllowAssignedPaymentsOnly(UserAllowAssignedPaymentsOnlyRequest userAllowAssignedPaymentsOnlyRequest) {
        super(String.class);
        this.userAllowAssignedPaymentsOnlyRequest = userAllowAssignedPaymentsOnlyRequest;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userAllowAssignedPaymentsOnlyRequest.getUserName());
        if (!userFile.exists()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            ((ObjectNode) user).put("allowAssignedPaymentsOnly", userAllowAssignedPaymentsOnlyRequest.getAllow());
            FileUtil.editFile(user, userFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserAllowAssignedPaymentsOnly.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
