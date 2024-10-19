/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddInfoRequest;
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
public class UserAddInfo extends AbstractOperation<String> {

    private final UserAddInfoRequest userAddInfoRequest;

    public UserAddInfo(UserAddInfoRequest userAddInfoRequest) {
        super(String.class);
        this.userAddInfoRequest = userAddInfoRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userAddInfoRequest.getUserName());
        if (userAddInfoRequest.getType() != null) {
            userConfigFile = UsersFolderLocator.getConfigFile(userAddInfoRequest.getUserName(), userAddInfoRequest.getType());
        }
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has(userAddInfoRequest.getFieldName())) {
                super.response = "FIELD NAME ALREADY EXIST";
                return;
            }
            if (userAddInfoRequest.getFieldValue() != null) {
                if (userAddInfoRequest.getFieldValue() instanceof String) {
                    ((ObjectNode) userConfig).put(userAddInfoRequest.getFieldName(), (String) userAddInfoRequest.getFieldValue());
                } else if (userAddInfoRequest.getFieldValue() instanceof Boolean) {
                    ((ObjectNode) userConfig).put(userAddInfoRequest.getFieldName(), (Boolean) userAddInfoRequest.getFieldValue());
                } else if (userAddInfoRequest.getFieldValue() instanceof Number) {
                    ((ObjectNode) userConfig).put(userAddInfoRequest.getFieldName(), (Double) userAddInfoRequest.getFieldValue());
                } 
            }
            if(userAddInfoRequest.getFieldValueArray() != null){
                ((ObjectNode) userConfig).putArray(userAddInfoRequest.getFieldName()).addAll(userAddInfoRequest.getFieldValueArray());
            }
            FileUtil.editFile(userConfig, userConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(UserAddInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        File userIndexesFile = UsersIndexesFolderLocator.getFile(userAddInfoRequest.getUserName());
        JsonNode userIndex = mapper.createObjectNode();
        if (userIndexesFile.isFile()) {
            try {
                userIndex = mapper.readTree(userIndexesFile);
            } catch (IOException ex) {
                Logger.getLogger(UserAddInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ((ObjectNode) userIndex).put("update", true);
        FileUtil.editFile(userIndex, userIndexesFile);
        super.response = "OK";
    }

}
