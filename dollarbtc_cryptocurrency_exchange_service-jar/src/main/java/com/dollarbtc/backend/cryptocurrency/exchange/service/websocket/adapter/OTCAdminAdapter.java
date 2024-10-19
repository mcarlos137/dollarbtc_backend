/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.adapter;

import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OTCAdminGetClientsBalanceSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OTCAdminGetOperationBalanceSession;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.OTCAdminGetOperationLiquidityAndVolumeSession;
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
public class OTCAdminAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketConnect(Session session) {
        System.err.println("Open connection");
        super.onWebSocketConnect(session);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.err.println("Close connection " + statusCode + ", " + reason);
        OTCAdminGetOperationBalanceSession.removeSession(super.getSession());
        OTCAdminGetOperationLiquidityAndVolumeSession.removeSession(super.getSession());
        OTCAdminGetClientsBalanceSession.removeSession(super.getSession());
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
                boolean correctParams = false;
                switch (method) {
                    case "getOperationBalance":
                        if (!params.has("currency") || params.get("currency") instanceof NullNode || params.get("currency").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        if (params.has("amountBand") && !(params.get("amountBand") instanceof NullNode) && params.get("amountBand").doubleValue() > 0.0) {
                            correctParams = true;
                        }
                        if (params.has("timeBand") && !(params.get("timeBand") instanceof NullNode) && params.get("timeBand").intValue() > 0
                                && params.has("timeBandBase") && !(params.get("timeBandBase") instanceof NullNode) && !params.get("timeBandBase").textValue().equals("")) {
                            correctParams = true;
                        }
                        if (!correctParams) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        OTCAdminGetOperationBalanceSession.addSession(super.getSession(), params);
                        break;
                    case "getOperationLiquidityAndVolume":
                        if (!params.has("currency") || params.get("currency") instanceof NullNode || params.get("currency").textValue().equals("")
                                || !params.has("period") || params.get("period") instanceof NullNode || params.get("period").textValue().equals("")) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        if (params.has("amountBand") && !(params.get("amountBand") instanceof NullNode) && params.get("amountBand").doubleValue() > 0.0) {
                            correctParams = true;
                        }
                        if (params.has("timeBand") && !(params.get("timeBand") instanceof NullNode) && params.get("timeBand").intValue() > 0
                                && params.has("timeBandBase") && !(params.get("timeBandBase") instanceof NullNode) && !params.get("timeBandBase").textValue().equals("")) {
                            correctParams = true;
                        }
                        if (!correctParams) {
                            websocketErrorMessage = "params incorrect";
                            break;
                        }
                        OTCAdminGetOperationLiquidityAndVolumeSession.addSession(super.getSession(), params);
                        break;
                    case "getClientsBalance":
                        OTCAdminGetClientsBalanceSession.addSession(super.getSession(), params);
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
            Logger.getLogger(OTCAdminAdapter.class.getName()).log(Level.SEVERE, null, ex);
            onWebSocketClose(100, "can not parse message to json");
        }
    }

}
