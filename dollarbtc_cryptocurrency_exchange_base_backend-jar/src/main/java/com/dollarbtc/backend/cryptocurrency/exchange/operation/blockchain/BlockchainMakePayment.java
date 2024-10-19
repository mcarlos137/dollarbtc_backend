/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.blockchain;

import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class BlockchainMakePayment extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final static String PASSWORD = "";
    private final static String API_CODE = "";
    private final String guid, to;
    private final Long amount, fee;

    public BlockchainMakePayment(String guid, String to, Long amount, Long fee) {
        super(JsonNode.class);
        this.guid = guid;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
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
        String endpoint = "/merchant/"
                + guid
                + "/payment?"
                + "password=" + PASSWORD
                + "&api_code=" + API_CODE
                + "&to=" + to
                + "&amount=" + amount
                + "&fee=" + fee;
        return super.getJsonNode("http://172.20.169.102:3000", endpoint, "", RequestRestType.SYNC, null, null, 30);
    }

}
