/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserPostMessage extends AbstractOperation<String> {

    private final String userName, message, redirectionPath;

    public UserPostMessage(String userName, String message, String redirectionPath) {
        super(String.class);
        this.userName = userName;
        this.message = message;
        this.redirectionPath = redirectionPath;
    }

    @Override
    protected void execute() {
        File userMessagesFolder = UsersFolderLocator.getMessagesFolder(userName);
        if (!userMessagesFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        ObjectNode userMessage = mapper.createObjectNode();
        String id = BaseOperation.getId();
        userMessage.put("id", id);
        userMessage.put("timestamp", DateUtil.getCurrentDate());
        userMessage.put("message", message);
        userMessage.put("readed", false);
        userMessage.put("redirectionPath", redirectionPath);
        FileUtil.createFile(userMessage, new File(userMessagesFolder, id + ".json"));
        super.response = "OK";
    }

}
