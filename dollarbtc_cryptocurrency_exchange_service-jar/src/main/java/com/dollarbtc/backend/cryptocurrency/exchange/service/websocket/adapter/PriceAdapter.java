/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.PriceGetChatP2PSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.PriceGetFastChangeSession;
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
public class PriceAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        PriceGetFastChangeSession.removeSession(super.getSession());
        PriceGetChatP2PSession.removeSession(super.getSession());
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
            switch (method) {
                case "getFastChange":
                    if (!params.has("currency") || params.get("currency") instanceof NullNode || params.get("currency").textValue().equals("")) {
                        websocketErrorMessage = "params incorrect";
                        break;
                    }
                    PriceGetFastChangeSession.addSession(super.getSession(), params);
                    break;
                case "getChatP2P":
                    PriceGetChatP2PSession.addSession(super.getSession(), params);
                    break;
                default:
                    websocketErrorMessage = "method does not exist";
                    break;
            }
            if (websocketErrorMessage != null) {
                onWebSocketClose(100, websocketErrorMessage);
            }
        } catch (IOException ex) {
            Logger.getLogger(PriceAdapter.class.getName()).log(Level.SEVERE, null, ex);
            onWebSocketClose(100, "can not parse message to json");
        }
    }

}
