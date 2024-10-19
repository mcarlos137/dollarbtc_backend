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
public class MCUserIsConnectedMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;

    public MCUserIsConnectedMessage(Session session, JsonNode jsonNode) {
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
        try {
            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(isConnected(userName, side), "isConnected", "params", params).toString());
        } catch (IOException ex) {
            Logger.getLogger(MCUserIsConnectedMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JsonNode isConnected(String userName, String side) {
        File userMCMessagesLastConnectionFile = UsersFolderLocator.getMCMessagesLastConnectionFile(userName);
        String timestamp = DateUtil.getCurrentDate();
        if (side.equals("set")) {
            JsonNode userMCMessagesLastConnection = new ObjectMapper().createObjectNode();
            ((ObjectNode) userMCMessagesLastConnection).put("lastTimestamp", timestamp);
            ((ObjectNode) userMCMessagesLastConnection).put("currentTimestamp", timestamp);
            FileUtil.editFile(userMCMessagesLastConnection, userMCMessagesLastConnectionFile);
            return userMCMessagesLastConnection;
        }
        if (side.equals("get")) {
            if (!userMCMessagesLastConnectionFile.isFile()) {
                return new ObjectMapper().createObjectNode();
            }
            try {
                JsonNode userMCMessagesLastConnection = new ObjectMapper().readTree(userMCMessagesLastConnectionFile);
                ((ObjectNode) userMCMessagesLastConnection).put("currentTimestamp", timestamp);
                return userMCMessagesLastConnection;
            } catch (IOException ex) {
                Logger.getLogger(MCUserIsConnectedMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new ObjectMapper().createObjectNode();
    }

}
