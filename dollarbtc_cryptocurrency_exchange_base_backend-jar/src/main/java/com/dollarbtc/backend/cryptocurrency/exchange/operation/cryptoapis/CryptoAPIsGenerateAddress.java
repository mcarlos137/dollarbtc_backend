/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis;

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
public class CryptoAPIsGenerateAddress extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String blockchain, network;

    public CryptoAPIsGenerateAddress(String blockchain, String network) {
        super(JsonNode.class);
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode requestJsonNode = mapper.createObjectNode();
        ((ObjectNode) requestJsonNode).put("context", "");
        JsonNode requestJsonNodeData = mapper.createObjectNode();
        JsonNode requestJsonNodeItem = mapper.createObjectNode();
        ((ObjectNode) requestJsonNodeData).set("item", requestJsonNodeItem);
        ((ObjectNode) requestJsonNode).set("data", requestJsonNodeData);
        String url = "https://rest.cryptoapis.io/v2/blockchain-tools/" + blockchain + "/" + network + "/addresses/generate";
        JsonNode response = super.postJsonNode(requestJsonNode, url, RequestRestType.SYNC, null, null, 30);
        return response;
    }

}
