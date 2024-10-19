/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.chat;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.chat.ChatPostMessageRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ChatsFolderLocator;
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
public class ChatPostMessage extends AbstractOperation<String> {

    private final ChatPostMessageRequest chatPostMessageRequest;

    public ChatPostMessage(ChatPostMessageRequest chatPostMessageRequest) {
        super(String.class);
        this.chatPostMessageRequest = chatPostMessageRequest;
    }

    @Override
    protected void execute() {
        File chatsFolder = ChatsFolderLocator.getFolder();
        File chatsAdminFolder = ChatsFolderLocator.getAdminFolder();
        File chatsUserNameFolder = new File(chatsFolder, chatPostMessageRequest.getUserName());
        String timestamp = DateUtil.getCurrentDate();
        long id = DateUtil.parseDate(timestamp).getTime();
        if (!chatsUserNameFolder.isDirectory()) {
            chatsUserNameFolder = FileUtil.createFolderIfNoExist(chatsUserNameFolder);
        }
        JsonNode info = null;
        File infoFile = new File(chatsUserNameFolder, "info.json");
        if (!infoFile.isFile()) {
            info = mapper.createObjectNode();
            ((ObjectNode) info).put("initialTimestamp", timestamp);
            FileUtil.createFile(info, infoFile);
        } else {
            try {
                info = mapper.readTree(infoFile);
            } catch (IOException ex) {
                Logger.getLogger(ChatPostMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (info != null && chatPostMessageRequest.getName() != null && !chatPostMessageRequest.getName().equals("")) {
            ((ObjectNode) info).put("name", chatPostMessageRequest.getName());
            FileUtil.editFile(info, infoFile);
        }
        File chatsUserNameSubjectLanguageFolder = FileUtil.createFolderIfNoExist(chatsUserNameFolder, chatPostMessageRequest.getSubject() + "__" + chatPostMessageRequest.getLanguage());
        JsonNode message = mapper.createObjectNode();
        ((ObjectNode) message).put("id", id);
        ((ObjectNode) message).put("timestamp", timestamp);
        message = chatPostMessageRequest.toJsonNode(message);
        FileUtil.createFile(message, new File(chatsUserNameSubjectLanguageFolder, id + ".json"));
        FileUtil.createFile(message, new File(FileUtil.createFolderIfNoExist(chatsAdminFolder, chatPostMessageRequest.getSubject() + "__" + chatPostMessageRequest.getLanguage() + "__" + chatPostMessageRequest.getUserName()), id + ".json"));
        File controlFile = new File(FileUtil.createFolderIfNoExist(chatsAdminFolder, chatPostMessageRequest.getSubject() + "__" + chatPostMessageRequest.getLanguage() + "__" + chatPostMessageRequest.getUserName()), "control.json");
        JsonNode control = mapper.createObjectNode();
        ((ObjectNode) control).put("notReadedMessages", true);
        if (controlFile.isFile()) {
            FileUtil.editFile(control, controlFile);
        } else {
            FileUtil.createFile(control, controlFile);
        }
        super.response = "OK";
    }

}
