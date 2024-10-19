/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetAdminOperationMessages;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author CarlosDaniel
 */
public class OTCGetAdminOperationMessagesSession {

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
                List<Session> keys = new ArrayList<>(sessions.keySet());
                Session session = keys.get(new Random().nextInt(keys.size()));
                JsonNode jsonNode = sessions.get(session);
                Integer maxQuantity = jsonNode.get("maxQuantity").intValue();
                Map<String, String> params = new HashMap<>();
                params.put("maxQuantity", Integer.toString(maxQuantity));
                ArrayNode adminOperationMessages = new OTCGetAdminOperationMessages(maxQuantity).getResponse();
                if(adminOperationMessages.size() > 0){
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(adminOperationMessages, "currentAdminOperationMessages", "params", params).toString());
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetAdminOperationMessagesSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
