/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author CarlosDaniel
 */
public class GetDashboardOperation extends AbstractRestClient<JsonNode> {

    private static Client client;
    private static final String ENDPOINT = "/api/dashboard/";
    private final String key, secret;

    public GetDashboardOperation(String key, String secret) {
        super(JsonNode.class);
        this.key = key;
        this.secret = secret;
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
        return super.getJsonNode(BasicLocalBitcoinsOperation.URL, ENDPOINT, "", RequestRestType.SYNC, SecurityType.HMAC, new String[]{key, secret, ENDPOINT}, 30);
    }

}
