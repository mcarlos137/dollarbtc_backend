/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.chat;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ChatsFolderLocator;
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
public class ChatGetMessages extends AbstractOperation<ArrayNode> {

    private final String userName, subject, language, side;
    private final boolean old;

    public ChatGetMessages(String userName, String subject, String language, String side, boolean old) {
        super(ArrayNode.class);
        this.userName = userName;
        this.subject = subject;
        this.language = language;
        this.side = side;
        this.old = old;
    }

    @Override
    protected void execute() {
        ArrayNode messages = mapper.createArrayNode();
        File chatsSpecificFolder = null;
        switch (side) {
            case "User":
                File chatsUserNameFolder = ChatsFolderLocator.getUserNameFolder(userName);
                if (!chatsUserNameFolder.isDirectory()) {
                    break;
                }
                chatsSpecificFolder = new File(chatsUserNameFolder, subject + "__" + language);
                if (!chatsSpecificFolder.isDirectory()) {
                    super.response = messages;
                    return;
                }
                break;
            case "Admin":
                chatsSpecificFolder = new File(ChatsFolderLocator.getAdminFolder(), subject + "__" + language + "__" + userName);
                if (!chatsSpecificFolder.isDirectory()) {
                    super.response = messages;
                    return;
                }
                break;
        }
        if (chatsSpecificFolder == null) {
            super.response = messages;
            return;
        }
        if (old) {
            chatsSpecificFolder = new File(chatsSpecificFolder, "Old");
        }
        if (!chatsSpecificFolder.isDirectory()) {
            super.response = messages;
            return;
        }
        Map<String, File> orderedFiles = new TreeMap<>();
        for (File chatsSpecificFile : chatsSpecificFolder.listFiles()) {
            if (!chatsSpecificFile.isFile() || chatsSpecificFile.getName().equals("control.json")) {
                continue;
            }
            orderedFiles.put(chatsSpecificFile.getName().replace(".json", ""), chatsSpecificFile);
        }
        File chatsSpecificOldFolder = null;
        if (!old) {
            chatsSpecificOldFolder = FileUtil.createFolderIfNoExist(chatsSpecificFolder, "Old");
        }
        for (String key : orderedFiles.keySet()) {
            try {
                JsonNode message = mapper.readTree(orderedFiles.get(key));
                messages.add(message);
                if (!old) {
                    FileUtil.moveFileToFolder(orderedFiles.get(key), chatsSpecificOldFolder);
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatGetMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = messages;
    }

}
