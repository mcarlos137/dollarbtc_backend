/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.websocket.api.Session;
import java.util.Map;
import java.util.Iterator;

/**
 *
 * @author CarlosDaniel
 */
public class MCUserHandleIceCandidateSession {

    public static void addSession(Session session, JsonNode jsonNode) {
        MCUserSessions.getInstance().data.put(jsonNode.get("caller").textValue(), session);
    }

    public static void removeSession(Session session) {
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
