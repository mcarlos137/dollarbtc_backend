/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserGetMessagesNewSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserGetMessagesSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserHandleAnswerSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserHandleOfferSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserIsConnectedSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserIsWritingSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserSessions;
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
public class MCUserAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        MCUserGetMessagesSession.removeSession(super.getSession());
        MCUserGetMessagesNewSession.removeSession(super.getSession());
        MCUserIsConnectedSession.removeSession(super.getSession());
        MCUserIsWritingSession.removeSession(super.getSession());
        MCUserHandleOfferSession.removeSession(super.getSession());
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
            if (!params.has("userName") || params.get("userName") instanceof NullNode || params.get("userName").textValue().equals("")) {
                websocketErrorMessage = "params incorrect";
            } else {
                switch (method) {
                    case "getMessages":
                        MCUserGetMessagesSession.addSession(super.getSession(), params);
                        break;
                    case "getMessagesNew":
                        MCUserGetMessagesNewSession.addSession(super.getSession(), params);
                        break;
                    case "isConnected":
                        if (!params.has("side") || params.get("side") instanceof NullNode || params.get("side").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        MCUserIsConnectedSession.addSession(super.getSession(), params);
                        break;
                    case "isWriting":
                        if (!params.has("side") || params.get("side") instanceof NullNode || params.get("side").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (!params.has("chatRooms") || params.get("chatRooms") instanceof NullNode || params.get("chatRooms").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        MCUserIsWritingSession.addSession(super.getSession(), params);
                        break;
                    case "handleOffer":
                        if (!params.has("target") || params.get("target") instanceof NullNode || params.get("target").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (!params.has("caller") || params.get("caller") instanceof NullNode || params.get("caller").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (!params.has("sdp") || params.get("sdp") instanceof NullNode || params.get("sdp").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        MCUserHandleOfferSession.addSession(super.getSession(), params);
                        if (MCUserSessions.getInstance().data.containsKey(params.get("target").textValue())) {
                            MCUserSessions.getInstance().data.get(params.get("target").textValue()).getRemote().sendString(ServiceUtil.createWSResponseWithData(params, method, null, null).toString());
                        } else {
                            new NotificationSendMessageByUserName(params.get("target").textValue(), "Chat", "Someone is trying to call you").getResponse();
                        }
                        break;
                    case "handleAnswer":
                        if (!params.has("target") || params.get("target") instanceof NullNode || params.get("target").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (!params.has("caller") || params.get("caller") instanceof NullNode || params.get("caller").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (!params.has("sdp") || params.get("sdp") instanceof NullNode || params.get("sdp").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        MCUserHandleAnswerSession.addSession(super.getSession(), params);
                        if (MCUserSessions.getInstance().data.containsKey(params.get("target").textValue())) {
                            MCUserSessions.getInstance().data.get(params.get("target").textValue()).getRemote().sendString(ServiceUtil.createWSResponseWithData(params, method, null, null).toString());
                        } else {
                            //createDeliveredThread(userName, chatRoom, time);
                        }
                        break;
                    case "handleIceCandidate":
                        if (!params.has("target") || params.get("target") instanceof NullNode || params.get("target").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (!params.has("candidate") || params.get("candidate") instanceof NullNode || params.get("candidate").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                        }
                        if (MCUserSessions.getInstance().data.containsKey(params.get("target").textValue())) {
                            MCUserSessions.getInstance().data.get(params.get("target").textValue()).getRemote().sendString(ServiceUtil.createWSResponseWithData(params.get("candidate"), method, null, null).toString());
                        } else {
                            //createDeliveredThread(userName, chatRoom, time);
                        }
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
            Logger.getLogger(MCUserAdapter.class.getName()).log(Level.SEVERE, null, ex);
            onWebSocketClose(100, "can not parse message to json");
        }
    }

}
