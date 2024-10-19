/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserModifyInfoRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersIndexesFolderLocator;
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
public class UserModifyInfo extends AbstractOperation<String> {

    private final UserModifyInfoRequest userModifyInfoRequest;

    public UserModifyInfo(UserModifyInfoRequest userModifyInfoRequest) {
        super(String.class);
        this.userModifyInfoRequest = userModifyInfoRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userModifyInfoRequest.getUserName());
        if (userModifyInfoRequest.getType() != null) {
            userConfigFile = UsersFolderLocator.getConfigFile(userModifyInfoRequest.getUserName(), userModifyInfoRequest.getType());
        }
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (!userConfig.has(userModifyInfoRequest.getFieldName())) {
                super.response = "FIELD NAME DOES NOT ALREADY EXIST";
                return;
            }
            if (userModifyInfoRequest.getFieldValue() != null) {
                if (userModifyInfoRequest.getFieldValue() instanceof String) {
                    ((ObjectNode) userConfig).put(userModifyInfoRequest.getFieldName(), (String) userModifyInfoRequest.getFieldValue());
                } else if (userModifyInfoRequest.getFieldValue() instanceof Boolean) {
                    ((ObjectNode) userConfig).put(userModifyInfoRequest.getFieldName(), (Boolean) userModifyInfoRequest.getFieldValue());
                } else if (userModifyInfoRequest.getFieldValue() instanceof Number) {
                    ((ObjectNode) userConfig).put(userModifyInfoRequest.getFieldName(), (Double) userModifyInfoRequest.getFieldValue());
                } 
            }
            if(userModifyInfoRequest.getFieldValueArray() != null){
                ((ObjectNode) userConfig).putArray(userModifyInfoRequest.getFieldName()).addAll(userModifyInfoRequest.getFieldValueArray());
            }
            FileUtil.editFile(userConfig, userConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(UserModifyInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        File userIndexesFile = UsersIndexesFolderLocator.getFile(userModifyInfoRequest.getUserName());
        JsonNode userIndex = mapper.createObjectNode();
        if (userIndexesFile.isFile()) {
            try {
                userIndex = mapper.readTree(userIndexesFile);
            } catch (IOException ex) {
                Logger.getLogger(UserModifyInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ((ObjectNode) userIndex).put("update", true);
        FileUtil.editFile(userIndex, userIndexesFile);
        super.response = "OK";
    }

}
