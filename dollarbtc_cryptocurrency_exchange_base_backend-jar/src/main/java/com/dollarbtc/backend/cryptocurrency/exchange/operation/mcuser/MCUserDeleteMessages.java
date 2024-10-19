/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserDeleteMessagesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserDeleteMessages extends AbstractOperation<String> {

    private final MCUserDeleteMessagesRequest mcUserDeleteMessagesRequest;

    public MCUserDeleteMessages(MCUserDeleteMessagesRequest mcUserDeleteMessagesRequest) {
        super(String.class);
        this.mcUserDeleteMessagesRequest = mcUserDeleteMessagesRequest;
    }

    @Override
    protected void execute() {
        File userMCChatRoomMessagesFolder = UsersFolderLocator.getMCMessagesFolder(mcUserDeleteMessagesRequest.getChatRoom(), mcUserDeleteMessagesRequest.getUserName());
        for(File userMCMessageFile : userMCChatRoomMessagesFolder.listFiles()){
            if(!userMCMessageFile.isFile()){
                continue;
            }
            String id = userMCMessageFile.getName().replace(".json", "");
            try {
                JsonNode mcUserMessage = mapper.readTree(userMCMessageFile);
                ((ObjectNode) mcUserMessage).put("deleted", true);
                FileUtil.editFile(mcUserMessage, userMCMessageFile);
            } catch (IOException ex) {
                Logger.getLogger(MCUserDeleteMessages.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
            File userMCCUserNameMessagesFolder = UsersFolderLocator.getMCMessagesFolder(mcUserDeleteMessagesRequest.getUserName(), mcUserDeleteMessagesRequest.getChatRoom());
            File userMCCUserNameMessagesOldFolder = UsersFolderLocator.getMCMessagesOldFolder(mcUserDeleteMessagesRequest.getUserName(), mcUserDeleteMessagesRequest.getChatRoom());
            File userMCMessageIdFile = new File(userMCCUserNameMessagesOldFolder, id + ".json");
            try {
                JsonNode userMCMessageId = mapper.readTree(userMCMessageIdFile);
                ((ObjectNode) userMCMessageId).put("deleted", true);
                FileUtil.editFile(userMCMessageId, userMCMessageIdFile);
                FileUtil.moveFileToFolder(userMCMessageIdFile, userMCCUserNameMessagesFolder);
            } catch (IOException ex) {
                Logger.getLogger(MCUserDeleteMessages.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
        }
        super.response = "OK";
    }

}
