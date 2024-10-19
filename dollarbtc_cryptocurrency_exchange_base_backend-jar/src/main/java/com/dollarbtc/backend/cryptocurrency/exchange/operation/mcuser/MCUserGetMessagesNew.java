/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetMessagesNew extends AbstractOperation<JsonNode> {

    private final String userName, chatRoom;
    private final boolean old;

    public MCUserGetMessagesNew(String userName, String chatRoom, boolean old) {
        super(JsonNode.class);
        this.userName = userName;
        this.chatRoom = chatRoom;
        this.old = old;
    }

    @Override
    protected void execute() {
        JsonNode mcUserMessages = mapper.createObjectNode();
        List<File> mcUserMessagesFolders = new ArrayList<>();
        if (chatRoom != null) {
            mcUserMessagesFolders.add(UsersFolderLocator.getMCMessagesFolder(userName, chatRoom));
        } else {
            for (File mcUserMessagesFolder : UsersFolderLocator.getMCMessagesFolder(userName).listFiles()) {
                if (!mcUserMessagesFolder.isDirectory()) {
                    continue;
                }
                mcUserMessagesFolders.add(mcUserMessagesFolder);
            }
        }
        for (File mcUserMessagesFolder : mcUserMessagesFolders) {
            String chatRoomm = mcUserMessagesFolder.getName();
            if (old) {
                mcUserMessagesFolder = FileUtil.createFolderIfNoExist(new File(mcUserMessagesFolder, "Old"));
            }
            Map<String, File> orderedFiles = new TreeMap<>();
            for (File mcUserMessageFile : mcUserMessagesFolder.listFiles()) {
                if (!mcUserMessageFile.isFile()) {
                    continue;
                }
                ((ObjectNode) mcUserMessages).put("newMessage", true);
                if(mcUserMessageFile.getName().contains(".swp")){
                    continue;
                }
                orderedFiles.put(mcUserMessageFile.getName().replace(".json", ""), mcUserMessageFile);
            }
            ArrayNode mcUserMessagesArray = mapper.createArrayNode();
            for (String key : orderedFiles.keySet()) {
                try {
                    JsonNode mcUserMessage = mapper.readTree(orderedFiles.get(key));
                    mcUserMessagesArray.add(mcUserMessage);
//                    if (mcUserMessage.has("offer")) {
//                        continue;
//                    }
                    if (!old) {
                        String senderUserName = mcUserMessage.get("senderUserName").textValue();
                        if (!userName.equals(senderUserName)) {
                            ((ObjectNode) mcUserMessage).put("delivered", true);
                            FileUtil.editFile(mcUserMessage, orderedFiles.get(key));
                            FileUtil.createFile(mcUserMessage, new File(UsersFolderLocator.getMCMessagesFolder(senderUserName, userName), mcUserMessage.get("time").longValue() + ".json"));
                        }
                        FileUtil.moveFileToFolder(orderedFiles.get(key), FileUtil.createFolderIfNoExist(new File(mcUserMessagesFolder, "Old")));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MCUserGetMessagesNew.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(mcUserMessagesArray.size() > 0 && !chatRoomm.equals(userName)){
                ((ObjectNode) mcUserMessages).putArray(chatRoomm).addAll(mcUserMessagesArray);
            }
        }
        ObjectNode pendingToDeliver = mapper.createObjectNode();
        pendingToDeliver.put("notificationSended", false);
        pendingToDeliver.put("timestamp", DateUtil.getCurrentDate());
        FileUtil.editFile(pendingToDeliver, UsersFolderLocator.getMCMessagesPendingToDeliverFile(userName));
        super.response = mcUserMessages;
    }

}
