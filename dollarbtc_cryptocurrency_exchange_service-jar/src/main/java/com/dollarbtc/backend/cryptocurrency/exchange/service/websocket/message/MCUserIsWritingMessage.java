/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message;

import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author carlosmolina
 */
public class MCUserIsWritingMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;

    public MCUserIsWritingMessage(Session session, JsonNode jsonNode) {
        this.session = session;
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        String userName = jsonNode.get("userName").textValue();
        String side = jsonNode.get("side").textValue();
        Map<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("side", side);
        String[] chatRooms = jsonNode.get("chatRooms").textValue().split("____");
        try {
            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(isWriting(userName, chatRooms, side), session.getRemoteAddress().toString(), "params", params).toString());
        } catch (IOException ex) {
            Logger.getLogger(MCUserIsWritingMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JsonNode isWriting(String userName, String[] chatRooms, String side) {
        String timestamp = DateUtil.getCurrentDate();
        if (side.equals("set")) {
            File userMCMessagesLastWritingFile = UsersFolderLocator.getMCMessagesLastWritingFile(userName);
            JsonNode userMCMessagesLastWriting = new ObjectMapper().createObjectNode();
            for (String chatRoom : chatRooms) {
                ((ObjectNode) userMCMessagesLastWriting).put(chatRoom, timestamp);
            }
            ((ObjectNode) userMCMessagesLastWriting).put("currentTimestamp", timestamp);
            FileUtil.editFile(userMCMessagesLastWriting, userMCMessagesLastWritingFile);
            return userMCMessagesLastWriting;
        }
        if (side.equals("get")) {
            JsonNode userMCMessagesLastWriting = new ObjectMapper().createObjectNode();
            for (String chatRoom : chatRooms) {
                File userMCMessagesLastWritingFile = UsersFolderLocator.getMCMessagesLastWritingFile(chatRoom);
                if (!userMCMessagesLastWritingFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userMCMessagesLastW = new ObjectMapper().readTree(userMCMessagesLastWritingFile);
                    if (!userMCMessagesLastW.has(userName)) {
                        ((ObjectNode) userMCMessagesLastWriting).put(chatRoom, timestamp);
                    } else {
                        ((ObjectNode) userMCMessagesLastWriting).put(chatRoom, userMCMessagesLastW.get(userName).textValue());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MCUserIsWritingMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ((ObjectNode) userMCMessagesLastWriting).put("currentTimestamp", timestamp);
            return userMCMessagesLastWriting;
        }
        return new ObjectMapper().createObjectNode();
    }

}
