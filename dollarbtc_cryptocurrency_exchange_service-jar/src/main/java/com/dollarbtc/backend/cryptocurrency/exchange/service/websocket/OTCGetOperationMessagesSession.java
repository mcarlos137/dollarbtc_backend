/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperationMessages;
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
public class OTCGetOperationMessagesSession {

    private final static Map<Session, JsonNode> SESSIONS = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void addSession(Session session, JsonNode jsonNode) {
        SESSIONS.put(session, jsonNode);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS), 0, 15, TimeUnit.SECONDS);
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
                    String id = jsonNode.get("id").textValue();
                    String side = jsonNode.get("side").textValue();
                    Map<String, String> params = new HashMap<>();
                    params.put("id", id);
                    params.put("side", side);
                    if(!BaseOperation.websocketKeyAlreadyExist(websocketKey)){
                        ArrayNode operationMessages = new OTCGetOperationMessages(id, side, true).getResponse();
                        if(operationMessages.size() > 0){
                            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(operationMessages, "oldOperationMessages", "params", params).toString());
                        } 
                    }
                    ArrayNode operationMessages = new OTCGetOperationMessages(id, side, false).getResponse();
                    if(operationMessages.size() > 0){
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(operationMessages, "currentOperationMessages", "params", params).toString());
                    } 
                    BaseOperation.createWebsocketKeyFile(websocketKey);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetOperationMessagesSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
