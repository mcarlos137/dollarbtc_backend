/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.ExchangeOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author CarlosDaniel
 */
public class ExchangeGetTradesSession {

    private final static Map<Session, JsonNode> SESSIONS = new HashMap<>();

    public static void addSession(Session session, JsonNode jsonNode) {
        SESSIONS.put(session, jsonNode);
        new Message(SESSIONS).run();
    }

    public static void removeSession(Session session, int statusCode, String reason) {
        SESSIONS.remove(session);
        session.close(statusCode, reason);
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
                    String symbol = jsonNode.get("symbol").textValue();
                    Integer limit = jsonNode.get("limit").intValue();
                    String sort = jsonNode.get("sort").textValue();
                    String by = jsonNode.get("by").textValue();
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(ExchangeOperation.getTrades(symbol, limit, sort, by), null, "result", new HashMap<>()).toString());
                    removeSession(session, 100, "normal close");
                }
            } catch (IOException ex) {
                Logger.getLogger(ExchangeGetTradesSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
