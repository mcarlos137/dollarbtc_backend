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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class UserGetNewAddress extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String wallet;

    public UserGetNewAddress(String wallet) {
        super(JsonNode.class);
        this.wallet = wallet;
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
        String url = "https://api.blockcypher.com/v1/btc/main/wallets/" + wallet + "/addresses/generate" + "?token=b59c3adf5b414a0ab13242db49fa1b53";
        return super.postJsonNode(new ObjectMapper().createObjectNode(), url, RequestRestType.ASYNC, null, null, 0);
    }

}
