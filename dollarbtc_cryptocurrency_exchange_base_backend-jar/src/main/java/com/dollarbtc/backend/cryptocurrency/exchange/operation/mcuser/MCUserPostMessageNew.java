/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserOperationAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MCUserPostMessageNew extends AbstractOperation<String> {

    private final String userName, chatRoom, message, replyId, publicKey, operationId, attachmentFileName;
    private final Long time;

    public MCUserPostMessageNew(String userName, String chatRoom, String message, String replyId, String attachmentFileName, String publicKey, Long time, String operationId) {
        super(String.class);
        this.userName = userName;
        this.chatRoom = chatRoom;
        this.message = message;
        this.replyId = replyId;
        this.attachmentFileName = attachmentFileName;
        this.publicKey = publicKey;
        this.time = time;
        this.operationId = operationId;
    }

    @Override
    protected void execute() {
        File[] mcUserMessagesFolders = new File[2];
        if(operationId == null){
            mcUserMessagesFolders[0] = UsersFolderLocator.getMCMessagesFolder(userName, chatRoom);
        } else {
            mcUserMessagesFolders[0] = UsersFolderLocator.getMCMessagesFolder(userName, chatRoom + "__" + operationId);
        }
        if (!UsersFolderLocator.getConfigFile(chatRoom).isFile()) {
            createFromMCPostMessage(chatRoom);
            new SMSSender().publish("User " + userName + " is trying to contact you. Download MC from Google Play at https://play.google.com/store/apps/details?id=com.dollarbtc.moneyclick&hl=es or from Apple Store at https://apps.apple.com/us/app/moneyclick/id1501864260", new String[]{chatRoom});
        }
        if(operationId == null){
            mcUserMessagesFolders[1] = UsersFolderLocator.getMCMessagesFolder(chatRoom, userName);
        } else {
            mcUserMessagesFolders[1] = UsersFolderLocator.getMCMessagesFolder(chatRoom, userName + "__" + operationId);
        }
        createMessage(mcUserMessagesFolders[0]);
        createMessage(mcUserMessagesFolders[1]);
        super.response = "OK";
    }
    
    private void createMessage(File mcUserMessagesFolder){
        ObjectNode mcUserMessage = mapper.createObjectNode();
        String fileName = time + ".json";
        mcUserMessage.put("time", time);
        mcUserMessage.put("senderUserName", userName);
        mcUserMessage.put("receiverUserName", chatRoom);
        mcUserMessage.put("message", message);
        mcUserMessage.put("replyId", replyId);
        mcUserMessage.put("attachmentFileName", attachmentFileName);
        mcUserMessage.put("publicKey", publicKey);
        mcUserMessage.put("delivered", false);
        mcUserMessage.put("readed", false);
        if(operationId != null){
            mcUserMessage.put("operationId", operationId);
        }
        FileUtil.createFile(mcUserMessage, mcUserMessagesFolder, fileName);
    }

    private static void createFromMCPostMessage(String userName) {
        File userFolder = FileUtil.createFolderIfNoExist(UsersFolderLocator.getFolder(userName));
        FileUtil.createFolderIfNoExist(userFolder, "Balance");
        FileUtil.createFolderIfNoExist(userFolder, "MCBalance");
        FileUtil.createFolderIfNoExist(userFolder, "Models");
        File userFile = new File(userFolder, "config.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode user = mapper.createObjectNode();
        ((ObjectNode) user).put("name", userName);
        ((ObjectNode) user).put("active", false);
        ((ObjectNode) user).put("createdFromMCSend", true);
        ((ObjectNode) user).put("type", UserType.NORMAL.name());
        ((ObjectNode) user).put("environment", UserEnvironment.NONE.name());
        ((ObjectNode) user).put("operationAccount", UserOperationAccount.SELF.name());
        FileUtil.createFile(user, userFile);
    }

}
