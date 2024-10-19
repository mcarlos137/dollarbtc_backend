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
public class CryptoAPIsUnsubscribeAddress extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String id, blockchain, network;

    public CryptoAPIsUnsubscribeAddress(String id, String blockchain, String network) {
        super(JsonNode.class);
        this.id = id;
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

    public JsonNode getResponse() {
        headers.add("X-API-Key", "294289f7439502ecab5a58453b323b09b2094bb2");
        String url = "https://rest.cryptoapis.io/v2/blockchain-events/" + blockchain + "/" + network + "/subscriptions/" + id;
        return super.deleteJsonNode(url, RequestRestType.SYNC, null, null, 30);
    }

}
