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
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserPostMessage extends AbstractOperation<String> {

    private final String userName, chatRoom, id, message, replyId, publicKey;
    private final boolean attachment;

    public MCUserPostMessage(String userName, String chatRoom, String id, String message, String replyId, boolean attachment, String publicKey) {
        super(String.class);
        this.userName = userName;
        this.chatRoom = chatRoom;
        this.id = id;
        this.message = message;
        this.replyId = replyId;
        this.attachment = attachment;
        this.publicKey = publicKey;
    }

    @Override
    protected void execute() {
        File[] mcUserMessagesFolders = new File[2];
        mcUserMessagesFolders[0] = UsersFolderLocator.getMCMessagesFolder(userName, chatRoom);
        if (!UsersFolderLocator.getConfigFile(chatRoom).isFile()) {
            createFromMCPostMessage(chatRoom);
            new SMSSender().publish("User " + userName + " is trying to contact you. Download MC from Google Play at https://play.google.com/store/apps/details?id=com.dollarbtc.moneyclick&hl=es or from Apple Store at https://apps.apple.com/us/app/moneyclick/id1501864260", new String[]{chatRoom});
        }
        mcUserMessagesFolders[1] = UsersFolderLocator.getMCMessagesFolder(chatRoom, userName);
        ObjectNode mcUserMessage = mapper.createObjectNode();
        String fileName = id + ".json";
        mcUserMessage.put("id", id);
        mcUserMessage.put("senderUserName", userName);
        mcUserMessage.put("receiverUserName", chatRoom);
        mcUserMessage.put("chatRoom", chatRoom);
        mcUserMessage.put("timestamp", DateUtil.getDate(id));
        mcUserMessage.put("message", message);
        mcUserMessage.put("replyId", replyId);
        mcUserMessage.put("attachment", attachment);
        mcUserMessage.put("publicKey", publicKey);
        mcUserMessage.put("delivered", false);
        mcUserMessage.put("readed", false);
        FileUtil.createFile(mcUserMessage, mcUserMessagesFolders[0], fileName);
        mcUserMessage.put("chatRoom", userName);
        FileUtil.createFile(mcUserMessage, mcUserMessagesFolders[1], fileName);
        createDeliveredThread(userName, chatRoom, id);
        super.response = "OK";
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

    private void createDeliveredThread(String userName, String chatRoom, String id) {
        Thread createDeliveredThread = new Thread(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MCUserPostMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
            File usersMCMessagesPendingToDeliverFile = UsersFolderLocator.getMCMessagesPendingToDeliverFile(chatRoom);
            if (new File(UsersFolderLocator.getMCMessagesFolder(chatRoom, userName), id + ".json").isFile()) {
                if (!usersMCMessagesPendingToDeliverFile.isFile()) {
                    ObjectNode pendingToDeliver = mapper.createObjectNode();
                    pendingToDeliver.put("notificationSended", true);
                    pendingToDeliver.put("timestamp", DateUtil.getCurrentDate());
                    FileUtil.editFile(pendingToDeliver, usersMCMessagesPendingToDeliverFile);
                    //SEND NOTIFICATION
                    new NotificationSendMessageByUserName(chatRoom, "Chat P2P", "You have new messages").getResponse();
                } else {
                    try {
                        JsonNode pendingToDeliver = mapper.readTree(usersMCMessagesPendingToDeliverFile);
                        String currentTimestamp = DateUtil.getCurrentDate();
                        if (pendingToDeliver.has("notificationSended") && !pendingToDeliver.get("notificationSended").booleanValue()
                                || pendingToDeliver.has("timestamp") && DateUtil.getDateMinutesBefore(currentTimestamp, 10).compareTo(pendingToDeliver.get("timestamp").textValue()) > 0) {
                            ((ObjectNode) pendingToDeliver).put("notificationSended", true);
                            ((ObjectNode) pendingToDeliver).put("timestamp", currentTimestamp);
                            FileUtil.editFile(pendingToDeliver, usersMCMessagesPendingToDeliverFile);
                            //SEND NOTIFICATION
                            new NotificationSendMessageByUserName(chatRoom, "Chat P2P", "You have new messages").getResponse();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MCUserPostMessage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        createDeliveredThread.start();
    }

}
