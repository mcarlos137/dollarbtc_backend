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
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetMessages extends AbstractOperation<ArrayNode> {

    private final String userName;
    private final boolean old;

    public UserGetMessages(String userName, boolean old) {
        super(ArrayNode.class);
        this.userName = userName;
        this.old = old;
    }

    @Override
    protected void execute() {
        ArrayNode userMessages = mapper.createArrayNode();
        File userMessagesFolder = UsersFolderLocator.getMessagesFolder(userName);
        if (old) {
            userMessagesFolder = UsersFolderLocator.getMessagesOldFolder(userName);
        }
        if (!userMessagesFolder.isDirectory()) {
            super.response = userMessages;
            return;
        }
        Map<String, File> orderedFiles = new TreeMap<>();
        for (File userMessageFile : userMessagesFolder.listFiles()) {
            if (!userMessageFile.isFile()) {
                continue;
            }
            orderedFiles.put(userMessageFile.getName().replace(".json", ""), userMessageFile);
        }
        File userMessagesOldFolder = UsersFolderLocator.getMessagesOldFolder(userName);
        for (String key : orderedFiles.keySet()) {
            try {
                JsonNode userMessage = mapper.readTree(orderedFiles.get(key));
                userMessages.add(userMessage);
                if (!old) {
                    FileUtil.moveFileToFolder(orderedFiles.get(key), userMessagesOldFolder);
                }
            } catch (IOException ex) {
                Logger.getLogger(UserGetMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = userMessages;
    }

}
