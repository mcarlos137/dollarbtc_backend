/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class UserCreateNewWallet extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String userName;

    public UserCreateNewWallet(String userName) {
        super(JsonNode.class);
        this.userName = userName;
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
        String url = "https://api.blockcypher.com/v1/btc/main/wallets" + "?token=b59c3adf5b414a0ab13242db49fa1b53";
        ObjectNode request = new ObjectMapper().createObjectNode();
        request.put("name", userName);
        return super.postJsonNode(request, url, RequestRestType.ASYNC, null, null, 0);
    }

}