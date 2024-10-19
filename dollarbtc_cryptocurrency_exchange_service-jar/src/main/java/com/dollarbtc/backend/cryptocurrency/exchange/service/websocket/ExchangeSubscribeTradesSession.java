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
public class ExchangeSubscribeTradesSession {

    public final static Map<Session, JsonNode> SESSIONS = new HashMap<>();
    private final static Map<Session, Long> SESSIONS_LAST_ID = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void addSession(Session session, JsonNode jsonNode){
        SESSIONS.put(session, jsonNode);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS, SESSIONS_LAST_ID), 0, 1, TimeUnit.SECONDS);
    }
    
    public static void removeSession(Session session){
        SESSIONS.remove(session);
    }
    
    public static class Message implements Runnable {

        private static Map<Session, JsonNode> sessions;
        private static Map<Session, Long> sessionsLastId;

        public Message(Map<Session, JsonNode> sessions, Map<Session, Long> sessionsLastId) {
            Message.sessions = sessions;
            Message.sessionsLastId = sessionsLastId;
        }

        @Override
        public void run() {
            try {
                for (Session session : sessions.keySet()) {
                    String symbol = sessions.get(session).get("symbol").textValue();
                    Map<String, String> otherParams = new HashMap<>();
                    otherParams.put("symbol", symbol);
                    if(!sessionsLastId.containsKey(session)){
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(ExchangeOperation.getOldTrades(symbol), "snapshotTrades", "params", otherParams).toString());
                    }
                    Object[] tradesObject = ExchangeOperation.subscribeTrades(symbol, sessionsLastId.get(session));
                    if (tradesObject == null) {
                        continue;
                    } 
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData((JsonNode) tradesObject[1], "updateTrades", "params", otherParams).toString());
                    sessionsLastId.put(session, (Long) tradesObject[0]);
                }
            } catch (IOException ex) {
                Logger.getLogger(ExchangeSubscribeOrderbookSession.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }

}
