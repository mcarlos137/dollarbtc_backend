/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserDeleteMessageRequest;
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
public class MCUserDeleteMessage extends AbstractOperation<String> {

    private final MCUserDeleteMessageRequest mcUserDeleteMessageRequest;

    public MCUserDeleteMessage(MCUserDeleteMessageRequest mcUserDeleteMessageRequest) {
        super(String.class);
        this.mcUserDeleteMessageRequest = mcUserDeleteMessageRequest;
    }

    @Override
    protected void execute() {
        //CHATROOM
        File userMCMessagesFolder = UsersFolderLocator.getMCMessagesFolder(mcUserDeleteMessageRequest.getChatRoom(), mcUserDeleteMessageRequest.getUserName());
        File userMCMessagesOldFolder = UsersFolderLocator.getMCMessagesOldFolder(mcUserDeleteMessageRequest.getChatRoom(), mcUserDeleteMessageRequest.getUserName());
        String response = delete(userMCMessagesFolder, userMCMessagesOldFolder);
        if(response.equals("FAIL")){
            super.response = response;
            return;
        }
        userMCMessagesFolder = UsersFolderLocator.getMCMessagesFolder(mcUserDeleteMessageRequest.getUserName(), mcUserDeleteMessageRequest.getChatRoom());
        userMCMessagesOldFolder = UsersFolderLocator.getMCMessagesOldFolder(mcUserDeleteMessageRequest.getUserName(), mcUserDeleteMessageRequest.getChatRoom());        
        super.response = delete(userMCMessagesFolder, userMCMessagesOldFolder);
    }
    
    private String delete (File userMCMessagesFolder, File userMCMessagesOldFolder){
        File userMCMessageIdFile = new File(userMCMessagesFolder, mcUserDeleteMessageRequest.getId() + ".json");
        if (userMCMessageIdFile.isFile()) {
            try {
                JsonNode mcUserMessage = mapper.readTree(userMCMessageIdFile);
                ((ObjectNode) mcUserMessage).put("deleted", true);
                FileUtil.editFile(mcUserMessage, userMCMessageIdFile);
            } catch (IOException ex) {
                Logger.getLogger(MCUserDeleteMessage.class.getName()).log(Level.SEVERE, null, ex);
                return "FAIL";
            }
        } else {
            userMCMessageIdFile = new File(userMCMessagesOldFolder, mcUserDeleteMessageRequest.getId() + ".json");
            if (userMCMessageIdFile.isFile()) {
                try {
                    JsonNode mcUserMessage = mapper.readTree(userMCMessageIdFile);
                    ((ObjectNode) mcUserMessage).put("deleted", true);
                    FileUtil.editFile(mcUserMessage, userMCMessageIdFile);
                    FileUtil.moveFileToFolder(userMCMessageIdFile, userMCMessagesFolder);
                } catch (IOException ex) {
                    Logger.getLogger(MCUserDeleteMessage.class.getName()).log(Level.SEVERE, null, ex);
                    return "FAIL";
                }
            }
        }
        return "OK";
    } 

}
