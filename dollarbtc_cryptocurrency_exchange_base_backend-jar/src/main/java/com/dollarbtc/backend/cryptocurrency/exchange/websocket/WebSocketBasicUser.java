/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public abstract class WebSocketBasicUser extends WebSocketClient {

    protected final String exchangeId, symbolReal, symbolExchange;
    protected String clientOrderId;
    protected final int id;

    public WebSocketBasicUser(String uri, String exchangeId, String loginAccount, String symbol, String clientOrderId, int id) {
        super(uri);
        this.exchangeId = exchangeId;
        this.symbolReal = symbol;
        this.symbolExchange = ExchangeUtil.getSymbol("HitBTC", symbol);
        this.clientOrderId = clientOrderId;
        this.id = id;
        login(loginAccount);
    }

    protected abstract void prepareRequest();

    public void start() {
        System.out.println("start date: " + DateUtil.getCurrentDate());
    }

    private void login(String loginAccount) {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("method", "login");
        JsonNode jsonNodeParams = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNodeParams).put("algo", "BASIC");
        String[] userCredential = getUserCredentialJsonFile(loginAccount);
        if (userCredential == null) {
            return;
        }
        ((ObjectNode) jsonNodeParams).put("pKey", userCredential[0]);
        ((ObjectNode) jsonNodeParams).put("sKey", userCredential[1]);
        ((ObjectNode) jsonNode).set("params", jsonNodeParams);
//        System.out.println("jsonNode: " + jsonNode);
        clientEndPoint.sendMessage(jsonNode.toString());
    }

    public static String[] getUserCredentialJsonFile(String loginAccount) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File loginAccountsFile = new File(OPERATOR_PATH, "loginAccounts.json");
            ArrayNode loginA = (ArrayNode) mapper.readTree(loginAccountsFile);
            Iterator<JsonNode> loginAs = loginA.elements();
            while (loginAs.hasNext()) {
                JsonNode login = loginAs.next();                
                if(!login.get("name").textValue().equals(loginAccount)){
                    continue;
                }
                if(!login.get("active").booleanValue()){
                    continue;
                }
                String loginAuthParamPKey = login.get("authParams").get("pKey").textValue();
                String loginAuthParamSKey = login.get("authParams").get("sKey").textValue();
                return new String[]{loginAuthParamPKey, loginAuthParamSKey};
            }
        } catch (IOException ex) {
            Logger.getLogger(WebSocketBasicUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
