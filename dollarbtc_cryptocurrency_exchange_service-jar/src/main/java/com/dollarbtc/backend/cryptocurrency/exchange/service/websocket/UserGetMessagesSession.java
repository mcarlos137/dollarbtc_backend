/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetMessages;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author CarlosDaniel
 */
public class UserGetMessagesSession {

    private final static Map<Session, JsonNode> SESSIONS = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void addSession(Session session, JsonNode jsonNode) {
        SESSIONS.put(session, jsonNode);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS), 0, 30, TimeUnit.SECONDS);
    }

    public static void removeSession(Session session) {
        SESSIONS.remove(session);
    }
    
    public static class Message implements Runnable {

        private static Map<Session, JsonNode> sessions;

        public Message(Map<Session, JsonNode> sessions) {
            Message.sessions = sessions;
        }

        @Override
        public void run() {
            try {
                for (Session session : sessions.keySet()) {
                    JsonNode jsonNode = sessions.get(session);
                    String websocketKey = jsonNode.get("websocketKey").textValue();
                    String userName = jsonNode.get("userName").textValue();
                    Map<String, String> params = new HashMap<>();
                    params.put("userName", userName);
                    if(!BaseOperation.websocketKeyAlreadyExist(websocketKey)){
                        ArrayNode messages = new UserGetMessages(userName, true).getResponse();
                        if(messages.size() > 0){
                            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(messages, "oldOperationMessages", "params", params).toString());
                        }
                    }
                    ArrayNode messages = new UserGetMessages(userName, false).getResponse();
                    if(messages.size() > 0){
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(messages, "currentOperationMessages", "params", params).toString());
                    } else {
                        session.getRemote().sendPing(null);
                    }
                    BaseOperation.createWebsocketKeyFile(websocketKey);
                }
            } catch (IOException ex) {
                Logger.getLogger(ExchangeSubscribeTickerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
