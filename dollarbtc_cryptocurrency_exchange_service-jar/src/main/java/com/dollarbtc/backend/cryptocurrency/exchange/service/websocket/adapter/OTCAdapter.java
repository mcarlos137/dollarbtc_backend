/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OTCGetAdminOperationMessagesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OTCGetOperationChangeStatusesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OTCGetOperationMessagesSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

/**
 *
 * @author CarlosDaniel
 */
public class OTCAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        OTCGetOperationMessagesSession.removeSession(super.getSession());
        OTCGetAdminOperationMessagesSession.removeSession(super.getSession());
        OTCGetOperationChangeStatusesSession.removeSession(super.getSession());
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("message: " + message);
        ObjectMapper mapper = new ObjectMapper();
        String websocketErrorMessage = null;
        try {
            JsonNode jsonNode = mapper.readTree(message);
            String method = jsonNode.get("method").textValue();
            JsonNode params = jsonNode.get("params");
            if (!params.has("websocketKey") || params.get("websocketKey") instanceof NullNode) {
                websocketErrorMessage = "params websocketKey is not present";
            } else {
                switch (method) {
                    case "getOperationMessages":
                        if (!params.has("id") || params.get("id") instanceof NullNode || params.get("id").textValue().equals("")
                                || !params.has("side") || params.get("side") instanceof NullNode || params.get("side").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        OTCGetOperationMessagesSession.addSession(super.getSession(), params);
                        break;
                    case "getAdminOperationMessages":
                        if (!params.has("maxQuantity") || params.get("maxQuantity") instanceof NullNode || params.get("maxQuantity").intValue() <= 0) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        OTCGetAdminOperationMessagesSession.addSession(super.getSession(), params);
                        break;
                    case "getOperationChangeStatuses":
                        if (!params.has("userName") || params.get("userName") instanceof NullNode || params.get("userName").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        OTCGetOperationChangeStatusesSession.addSession(super.getSession(), params);
                        break;
                    default:
                        websocketErrorMessage = "method does not exist";
                        break;
                }
            }
            if (websocketErrorMessage != null) {
                onWebSocketClose(100, websocketErrorMessage);
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCAdapter.class.getName()).log(Level.SEVERE, null, ex);
            onWebSocketClose(100, "can not parse message to json");
        }
    }

}
