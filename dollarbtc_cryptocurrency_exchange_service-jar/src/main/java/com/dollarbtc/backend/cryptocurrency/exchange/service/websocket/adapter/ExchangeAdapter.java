/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeGetCurrenciesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeGetCurrencySession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeGetSymbolSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeGetSymbolsSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeGetTradesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeSubscribeCandlesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeSubscribeOrderbookSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeSubscribeTickerSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.ExchangeSubscribeTradesSession;
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
public class ExchangeAdapter extends WebSocketAdapter {
    
    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        ExchangeSubscribeCandlesSession.removeSession(super.getSession());
        ExchangeSubscribeOrderbookSession.removeSession(super.getSession());
        ExchangeSubscribeTickerSession.removeSession(super.getSession());
        ExchangeSubscribeTradesSession.removeSession(super.getSession());
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
                case "getCurrency":
                    if (!params.has("currency")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeGetCurrencySession.addSession(super.getSession(), params);
                    break;
                case "getCurrencies":
                    ExchangeGetCurrenciesSession.addSession(super.getSession(), params);
                    break;
                case "getSymbol":
                    if (!params.has("symbol")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeGetSymbolSession.addSession(super.getSession(), params);
                    break;
                case "getSymbols":
                    ExchangeGetSymbolsSession.addSession(super.getSession(), params);
                    break;
                case "subscribeTicker":
                    if (!params.has("symbol")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeSubscribeTickerSession.addSession(super.getSession(), params);
                    break;
                case "subscribeOrderbook":
                    if (!params.has("symbol")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeSubscribeOrderbookSession.addSession(super.getSession(), params);
                    break;
                case "subscribeTrades":
                    if (!params.has("symbol")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeSubscribeTradesSession.addSession(super.getSession(), params);
                    break;
                case "getTrades":
                    if (!params.has("symbol") || !params.has("limit") || !params.has("sort") || !params.has("by")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeGetTradesSession.addSession(super.getSession(), params);
                    break;
                case "subscribeCandles":
                    if (!params.has("symbol") || !params.has("period")) {
                        websocketErrorMessage = "params incorrect";
                    }
                    ExchangeSubscribeCandlesSession.addSession(super.getSession(), params);
                    break;
                default:
                    websocketErrorMessage = "params incorrect";
                    break;
            }
            if (websocketErrorMessage != null) {
                onWebSocketClose(100, websocketErrorMessage);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExchangeAdapter.class.getName()).log(Level.SEVERE, null, ex);
            onWebSocketClose(100, "can not parse message to json");
        }
    }

}
