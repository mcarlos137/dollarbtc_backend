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
import java.util.HashMap;
import org.eclipse.jetty.websocket.api.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
/**
 *
 * @author CarlosDaniel
 */
public class ExchangeGetCurrenciesSession {

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
                    session.getRemote().sendString(ServiceUtil.createWSResponse(ExchangeOperation.getCurrencies(), null, "result").toString());
                    removeSession(session, 100, "normal close");
                }
            } catch (IOException ex) {
                Logger.getLogger(ExchangeGetCurrenciesSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
