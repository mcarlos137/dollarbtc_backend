/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAutomaticChangeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
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
public class UserAutomaticChange extends AbstractOperation<String> {

    private final UserAutomaticChangeRequest userAutomaticChangeRequest;

    public UserAutomaticChange(UserAutomaticChangeRequest userAutomaticChangeRequest) {
        super(String.class);
        this.userAutomaticChangeRequest = userAutomaticChangeRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userAutomaticChangeRequest.getUserName());
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            JsonNode automaticChange = mapper.createObjectNode();
            ((ObjectNode) automaticChange).put("timestamp", DateUtil.getCurrentDate());
            ((ObjectNode) userConfig).put("automaticChange", userAutomaticChangeRequest.toJsonNode(automaticChange));
            FileUtil.editFile(userConfig, userConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserAutomaticChange.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
