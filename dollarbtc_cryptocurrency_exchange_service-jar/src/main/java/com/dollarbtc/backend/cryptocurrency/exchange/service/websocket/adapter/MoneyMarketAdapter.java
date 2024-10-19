/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MoneyMarketGetCandlesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MoneyMarketGetOrderBookSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MoneyMarketGetPricesSession;
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
public class MoneyMarketAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        MoneyMarketGetOrderBookSession.removeSession(super.getSession());
        MoneyMarketGetPricesSession.removeSession(super.getSession());
        MoneyMarketGetCandlesSession.removeSession(super.getSession());
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
            if (!params.has("pair") || params.get("pair") instanceof NullNode || params.get("pair").textValue().equals("")) {
                websocketErrorMessage = "params incorrect";
            } else {
                switch (method) {
                    case "getOrderBook":
                        if (!params.has("type") || params.get("type") instanceof NullNode || params.get("type").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        MoneyMarketGetOrderBookSession.addSession(super.getSession(), params);
                        break;
                    case "getPrices":
                        MoneyMarketGetPricesSession.addSession(super.getSession(), params);
                        break;
                    case "getCandles":
                        if (!params.has("period") || params.get("period") instanceof NullNode || params.get("period").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        MoneyMarketGetCandlesSession.addSession(super.getSession(), params);
                        break;
                    default:
                        websocketErrorMessage = "params incorrect";
                        break;
                }
            }
            if (websocketErrorMessage != null) {
                onWebSocketClose(100, websocketErrorMessage);
            }
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketAdapter.class.getName()).log(Level.SEVERE, null, ex);
            onWebSocketClose(100, "can not parse message to json");
        }
    }

}
