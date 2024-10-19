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
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class ChatMarkAdminMessagesAsReaded extends AbstractOperation<String> {

    private final String userName, subject, language;

    public ChatMarkAdminMessagesAsReaded(String userName, String subject, String language) {
        super(String.class);
        this.userName = userName;
        this.subject = subject;
        this.language = language;
    }

    @Override
    protected void execute() {
        File chatsAdminSpecificFolder = new File(ChatsFolderLocator.getAdminFolder(), subject + "__" + language + "__" + userName);
        if (!chatsAdminSpecificFolder.isDirectory()) {
            super.response = "THERE IS NO MESSAGES";
            return;
        }
        JsonNode control = mapper.createObjectNode();
        ((ObjectNode) control).put("notReadedMessages", false);
        File chatsAdminSpecificControlFile = new File(chatsAdminSpecificFolder, "control.json");
        if (!chatsAdminSpecificControlFile.isFile()) {
            FileUtil.createFile(control, chatsAdminSpecificControlFile);
        } else {
            FileUtil.editFile(control, chatsAdminSpecificControlFile);
        }
        super.response = "OK";
    }

}
