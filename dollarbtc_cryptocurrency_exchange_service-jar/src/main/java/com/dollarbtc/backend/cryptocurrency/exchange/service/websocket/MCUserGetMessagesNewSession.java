/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message.MCUserGetMessagesNewMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author CarlosDaniel
 */
public class MCUserGetMessagesNewSession {

    private final static Map<Session, ScheduledExecutorService> SESSIONS = new HashMap<>();

    public static void addSession(Session session, JsonNode jsonNode) {
        if (SESSIONS.containsKey(session)) {
            SESSIONS.get(session).shutdown();
        }
        MCUserSessions.getInstance().data.put(jsonNode.get("userName").textValue(), session);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new MCUserGetMessagesNewMessage(jsonNode), 0, 3, TimeUnit.SECONDS);
        SESSIONS.put(session, scheduledExecutorService);
    }

    public static void removeSession(Session session) {
        if (SESSIONS.containsKey(session)) {
            SESSIONS.get(session).shutdown();
        }
        SESSIONS.remove(session);
        Iterator<Map.Entry<String, Session>> iterator = MCUserSessions.getInstance().data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Session> entry = iterator.next();
            if (session.equals(entry.getValue())) {
                iterator.remove();
                break;
            }
        }
        session.close();
    }

}
