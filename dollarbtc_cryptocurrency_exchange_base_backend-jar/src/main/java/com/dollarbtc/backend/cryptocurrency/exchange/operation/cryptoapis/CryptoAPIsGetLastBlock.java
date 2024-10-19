/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis;

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
public class CryptoAPIsGetLastBlock extends AbstractRestClient<Integer> {

    private static Client client;
    private final String blockchain, network;

    public CryptoAPIsGetLastBlock(String blockchain, String network) {
        super(Integer.class);
        this.blockchain = blockchain;
        this.network = network;
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

    public Integer getResponse() {
        headers.add("X-API-Key", "294289f7439502ecab5a58453b323b09b2094bb2");
        JsonNode addressResponse = super.getJsonNode("https://rest.cryptoapis.io/v2/blockchain-data/" + blockchain + "/" + network, "/blocks/last", "", RequestRestType.SYNC, null, null, 30);
        if (!addressResponse.has("data") || !addressResponse.get("data").has("item")) {
            return null;
        }
        return addressResponse.get("data").get("item").get("height").intValue();
    }

}
