/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetClientsBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class OTCAdminGetClientsBalanceSession {

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
                    Map<String, String> params = new HashMap<>();
                    JsonNode jsonNode = sessions.get(session);
                    JsonNode clientsBalance = new OTCAdminGetClientsBalance().getResponse();
                    boolean send = false;
                    if (!jsonNode.has("lastClientsBalance") || !clientsBalance.equals(jsonNode.get("lastClientsBalance"))) {
                        send = true;
                        ((ObjectNode) sessions.get(session)).put("lastClientsBalance", clientsBalance);
                    }
                    if (send) {
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(new OTCAdminGetClientsBalance().getResponse(), "clientsBalance", "params", params).toString());
                    } else {
                        session.getRemote().sendPing(null);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetClientsBalanceSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
