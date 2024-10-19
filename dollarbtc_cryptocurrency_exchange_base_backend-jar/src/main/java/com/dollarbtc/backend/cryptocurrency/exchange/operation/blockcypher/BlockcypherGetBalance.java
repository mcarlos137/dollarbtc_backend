/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.blockcypher;

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
public class BlockcypherGetBalance extends AbstractRestClient<Double> {

    private static Client client;
    private final String address;

    public BlockcypherGetBalance(String address) {
        super(Double.class);
        this.address = address;
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

    public Double getResponse() {
        String params = "/" + address + "/balance";
        JsonNode addressResponse = super.getJsonNode("https://api.blockcypher.com/v1/btc/main", "/addrs", params, RequestRestType.SYNC, null, null, 30);
        if (!addressResponse.has("final_balance")) {
            return 0.0;
        }
        return addressResponse.get("final_balance").doubleValue() / 100000000;
    }

}
