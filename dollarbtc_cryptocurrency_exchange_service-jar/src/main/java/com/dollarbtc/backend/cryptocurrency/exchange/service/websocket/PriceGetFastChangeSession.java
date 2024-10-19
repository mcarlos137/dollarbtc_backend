/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message.PriceGetFastChangeMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author CarlosDaniel
 */
public class PriceGetFastChangeSession {

    private final static Map<Session, ScheduledExecutorService> SESSIONS = new HashMap<>();

    public static void addSession(Session session, JsonNode jsonNode) {
        if (SESSIONS.containsKey(session)) {
            SESSIONS.get(session).shutdown();
        } 
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new PriceGetFastChangeMessage(session, jsonNode), 0, 7, TimeUnit.SECONDS);
        SESSIONS.put(session, scheduledExecutorService);
    }
    
    public static void removeSession(Session session) {
        if (SESSIONS.containsKey(session)) {
            SESSIONS.get(session).shutdown();
        }
        SESSIONS.remove(session);
        session.close();
    }

}
