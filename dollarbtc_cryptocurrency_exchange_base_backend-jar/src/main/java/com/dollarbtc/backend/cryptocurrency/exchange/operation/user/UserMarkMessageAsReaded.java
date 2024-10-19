/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

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
public class UserMarkMessageAsReaded extends AbstractOperation<String> {

    private final String userName, id;

    public UserMarkMessageAsReaded(String userName, String id) {
        super(String.class);
        this.userName = userName;
        this.id = id;
    }

    @Override
    protected void execute() {
        File userMessageFile = new File(UsersFolderLocator.getMessagesFolder(userName), id + ".json");
        if (!userMessageFile.isFile()) {
            userMessageFile = new File(UsersFolderLocator.getMessagesOldFolder(userName), id + ".json");
            if (!userMessageFile.isFile()) {
                super.response = "MESSAGE DOES NOT EXIST";
                return;
            }
        }
        try {
            JsonNode userMessage = mapper.readTree(userMessageFile);
            ((ObjectNode) userMessage).put("readed", true);
            FileUtil.editFile(userMessage, userMessageFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserMarkMessageAsReaded.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
