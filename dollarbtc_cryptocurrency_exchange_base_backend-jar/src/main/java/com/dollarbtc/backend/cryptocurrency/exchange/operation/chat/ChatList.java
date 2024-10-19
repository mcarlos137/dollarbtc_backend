/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.chat;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.chat.ChatListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ChatsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ChatList extends AbstractOperation<ArrayNode> {

    private final ChatListRequest chatListRequest;

    public ChatList(ChatListRequest chatListRequest) {
        super(ArrayNode.class);
        this.chatListRequest = chatListRequest;
    }

    @Override
    protected void execute() {
        ArrayNode list = mapper.createArrayNode();
        File chatsAdminFolder = ChatsFolderLocator.getAdminFolder();
        for (File chatsAdminSpecificFolder : chatsAdminFolder.listFiles()) {
            if (!chatsAdminSpecificFolder.isDirectory()) {
                continue;
            }
            if (chatsAdminSpecificFolder.getName().split("__").length != 3) {
                continue;
            }
            if (chatListRequest.getUserName() != null && !chatListRequest.getUserName().equals("") && !chatsAdminSpecificFolder.getName().split("__")[2].equals(chatListRequest.getUserName())) {
                continue;
            }
            if (chatListRequest.getSubject() != null && !chatListRequest.getSubject().equals("") && !chatsAdminSpecificFolder.getName().split("__")[0].equals(chatListRequest.getSubject())) {
                continue;
            }
            if (chatListRequest.getLanguage() != null && !chatListRequest.getLanguage().equals("") && !chatsAdminSpecificFolder.getName().split("__")[1].equals(chatListRequest.getLanguage())) {
                continue;
            }
            ObjectNode chatsAdminSpecific = mapper.createObjectNode();
            chatsAdminSpecific.put("userName", chatsAdminSpecificFolder.getName().split("__")[2]);
            chatsAdminSpecific.put("subject", chatsAdminSpecificFolder.getName().split("__")[0]);
            chatsAdminSpecific.put("language", chatsAdminSpecificFolder.getName().split("__")[1]);
            File chatsAdminSpecificControlFile = new File(chatsAdminSpecificFolder, "control.json");
            if (!chatsAdminSpecificControlFile.isFile()) {
                chatsAdminSpecific.put("notReadedMessages", true);
            } else {
                try {
                    JsonNode chatsAdminSpecificControl = mapper.readTree(chatsAdminSpecificControlFile);
                    if (chatsAdminSpecificControl.has("notReadedMessages")) {
                        chatsAdminSpecific.put("notReadedMessages", chatsAdminSpecificControl.get("notReadedMessages").booleanValue());
                    } else {
                        chatsAdminSpecific.put("notReadedMessages", true);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ChatList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            list.add(chatsAdminSpecific);
        }
        super.response = list;
    }

}
