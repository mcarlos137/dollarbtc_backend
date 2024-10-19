/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetMessagesNew;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserSessions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetMessagesNewMessage implements Runnable {

    private final JsonNode jsonNode;

    public MCUserGetMessagesNewMessage(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        try {
            String userName = jsonNode.get("userName").textValue();
            String chatRoom = null;
            if (jsonNode.has("chatRoom")) {
                chatRoom = jsonNode.get("chatRoom").textValue();
            }
            Map<String, String> params = new HashMap<>();
            params.put("userName", userName);
            if (chatRoom != null) {
                params.put("chatRoom", chatRoom);
            }
            JsonNode messages = new MCUserGetMessagesNew(userName, chatRoom, false).getResponse();
            if (messages.has("newMessage") && messages.get("newMessage").booleanValue()) {
                ((ObjectNode) messages).remove("newMessage");
                MCUserSessions.getInstance().data.get(userName).getRemote().sendString(ServiceUtil.createWSResponseWithData(messages, "currentMessages", "params", params).toString());
            } else {
                MCUserSessions.getInstance().data.get(userName).getRemote().sendPing(null);
            }
        } catch (IOException ex) {
            Logger.getLogger(MCUserGetMessagesNewMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
