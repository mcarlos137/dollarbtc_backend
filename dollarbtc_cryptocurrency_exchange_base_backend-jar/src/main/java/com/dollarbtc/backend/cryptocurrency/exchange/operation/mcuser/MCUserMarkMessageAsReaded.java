/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserMarkMessageAsReadedRequest;
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
public class MCUserMarkMessageAsReaded extends AbstractOperation<String> {

    private final MCUserMarkMessageAsReadedRequest mcUserMarkMessageAsReadedRequest;

    public MCUserMarkMessageAsReaded(MCUserMarkMessageAsReadedRequest mcUserMarkMessageAsReadedRequest) {
        super(String.class);
        this.mcUserMarkMessageAsReadedRequest = mcUserMarkMessageAsReadedRequest;
    }

    @Override
    protected void execute() {
        File mcUserMessageFile = new File(UsersFolderLocator.getMCMessagesOldFolder(mcUserMarkMessageAsReadedRequest.getUserName(), mcUserMarkMessageAsReadedRequest.getChatRoom()), mcUserMarkMessageAsReadedRequest.getId() + ".json");
        if (!mcUserMessageFile.isFile()) {
            super.response = "MESSAGE DOES NOT EXIST";
            return;
        }
        try {
            JsonNode mcUserMessage = mapper.readTree(mcUserMessageFile);
            if (mcUserMessage.has("readed") && !mcUserMessage.get("readed").booleanValue()) {
                ((ObjectNode) mcUserMessage).put("readed", true);
                String fileName = mcUserMarkMessageAsReadedRequest.getId() + ".json";
                if (mcUserMessage.has("id")) {
                    fileName = mcUserMessage.get("id").textValue() + ".json";
                }
                String senderUserName = mcUserMessage.get("senderUserName").textValue();
                String receiverUserName = mcUserMessage.get("receiverUserName").textValue();
                FileUtil.createFile(mcUserMessage, new File(UsersFolderLocator.getMCMessagesFolder(senderUserName, receiverUserName), fileName));
                FileUtil.createFile(mcUserMessage, new File(UsersFolderLocator.getMCMessagesFolder(receiverUserName, senderUserName), fileName));
                super.response = "OK";
                return;
            }
            super.response = "MESSAGE ALREADY READED";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCUserMarkMessageAsReaded.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
