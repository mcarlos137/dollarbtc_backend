/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 *
 * @author CarlosDaniel
 */
@ClientEndpoint
public class WebsocketClientEndpoint {

    public Session session = null;
    private MessageHandler messageHandler;
    public static File mainFolder;

    public WebsocketClientEndpoint(URI uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opening websocket");
        this.session = session;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.session = null;
        this.inactivateWebSocket();
        System.exit(0);
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }
    
    public static boolean isActive(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(mapper.readTree(new File(mainFolder, "websocket.json")).get("active").booleanValue()){
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(WebsocketClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static void activateWebSocket() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("active", true);
        FileUtil.editFile(jsonNode, mainFolder, "websocket.json");
    }

    public static void inactivateWebSocket() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("active", false);
        FileUtil.editFile(jsonNode, mainFolder, "websocket.json");
    }

    public static interface MessageHandler {

        public void handleMessage(String message);

    }

}
