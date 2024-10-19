/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public abstract class WebSocketClient {

    protected WebsocketClientEndpoint clientEndPoint = null;
    protected String response;

    public WebSocketClient(String uri) {
        openWebsocket(uri);
        addListener();
    }

    public void openWebsocket(String uri) {
        try {
            clientEndPoint = new WebsocketClientEndpoint(new URI(uri));
        } catch (URISyntaxException ex) {
            Logger.getLogger(WebSocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void addListener();

}
