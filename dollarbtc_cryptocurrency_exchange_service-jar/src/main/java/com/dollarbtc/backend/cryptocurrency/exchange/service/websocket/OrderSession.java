/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
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
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class OrderSession {

    private final static Map<Session, JsonNode> SESSIONS = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void addSession(Session session, JsonNode jsonNode){
        SESSIONS.put(session, jsonNode);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS), 0, 1, TimeUnit.SECONDS);
    }
    
    public static void removeSession(Session session){
        SESSIONS.remove(session);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS), 0, 1, TimeUnit.SECONDS);
    }
    
    public static class Message implements Runnable {

        private static Map<Session, JsonNode> sessions;

        public Message(Map<Session, JsonNode> sessions) {
            Message.sessions = sessions;
        }

        @Override
        public void run() {
            try {
                for(Session session : sessions.keySet()){
                    JsonNode jsonNode = sessions.get(session);
                    String exchangeId = jsonNode.get("exchangeId").textValue();
                    String symbol = jsonNode.get("symbol").textValue();
                    String model = jsonNode.get("model").textValue();
                    List<Order> lastOrders = LocalData.getLastOrders(exchangeId, symbol, model, null, null, 100, true);
                    for(Order order : lastOrders){
                        session.getRemote().sendString(order.toJsonNode().toString());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OrderSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
