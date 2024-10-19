/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OrderSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

/**
 *
 * @author CarlosDaniel
 */
public class OrderAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        OrderSession.removeSession(super.getSession());
        super.onWebSocketClose(statusCode, reason);

    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("message: " + message);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode exchangeIdSymbolModel = null;
        try {
            exchangeIdSymbolModel = mapper.readTree(message);
        } catch (IOException ex) {
            Logger.getLogger(OrderAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (exchangeIdSymbolModel == null
                || !exchangeIdSymbolModel.has("exchangeId")
                || !exchangeIdSymbolModel.has("symbol")
                || !exchangeIdSymbolModel.has("model")) {
            onWebSocketClose(100, "can not parse message to json");
        }
        OrderSession.addSession(super.getSession(), exchangeIdSymbolModel);

    }

}
