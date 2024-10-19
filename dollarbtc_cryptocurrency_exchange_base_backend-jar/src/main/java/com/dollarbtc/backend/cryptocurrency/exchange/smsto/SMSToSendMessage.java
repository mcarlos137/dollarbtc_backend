/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.smsto;

import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class SMSToSendMessage extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String phone, message;

    public SMSToSendMessage(String phone, String message) {
        super(JsonNode.class);
        this.phone = phone;
        this.message = message;
    }

    @Override
    public Client getClient() {
        if (client == null) {
            client = ClientBuilder.newClient();
        }
        return client;
    }
    
    @Override
    public String getMediaType() {
        return MediaType.APPLICATION_JSON;
    }

    public JsonNode getResponse() {
        String url = "https://api.sms.to/sms/send";
        headers.add("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2F1dGg6ODA4MC9hcGkvdjEvdXNlcnMvYXBpL2tleS9nZW5lcmF0ZSIsImlhdCI6MTY1NTQwMzIwNCwibmJmIjoxNjU1NDAzMjA0LCJqdGkiOiJkdm8weHRnRmc2RWZ3cFBRIiwic3ViIjozODE4NjIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.yWW0wXy4CCrDhHt0cRdbMb9ShIZptW3TMXYk-BKGD74");
        ObjectNode request = new ObjectMapper().createObjectNode();
        request.put("to", phone);
        request.put("message", message);
        return super.postJsonNode(request, url, RequestRestType.ASYNC, null, null, 0);
    }

}
