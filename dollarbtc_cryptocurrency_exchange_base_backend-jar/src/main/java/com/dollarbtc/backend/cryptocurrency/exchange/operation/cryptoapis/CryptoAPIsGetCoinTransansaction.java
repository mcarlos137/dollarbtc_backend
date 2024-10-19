/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis;

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
public class CryptoAPIsGetCoinTransansaction extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String type, blockchain, network, id, address;

    public CryptoAPIsGetCoinTransansaction(String type, String blockchain, String network, String id, String address) {
        super(JsonNode.class);
        this.type = type;
        this.blockchain = blockchain;
        this.network = network;
        this.id = id;
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

    public JsonNode getResponse() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.createObjectNode();
        headers.add("X-API-Key", "294289f7439502ecab5a58453b323b09b2094bb2");
        String params = "/" + id;
        String url = "https://rest.cryptoapis.io/v2/blockchain-data";
        JsonNode transactionResponse = super.getJsonNode(url, "/" + blockchain + "/" + network + "/transactions", params, RequestRestType.SYNC, null, null, 30);
        if (!transactionResponse.has("data") || !transactionResponse.get("data").has("item")) {
            return response;
        }
        Double amount = null;
        String trxType = null;
        String timestamp = DateUtil.getDate(transactionResponse.get("data").get("item").get("timestamp").longValue() * 1000);
        String transactionId = transactionResponse.get("data").get("item").get("transactionId").textValue();
        boolean isConfirmed = transactionResponse.get("data").get("item").get("isConfirmed").booleanValue();
        Iterator<JsonNode> transactionResponseRecipientsIterator = transactionResponse.get("data").get("item").get("recipients").iterator();
        while (transactionResponseRecipientsIterator.hasNext()) {
            JsonNode transactionResponseRecipientsIt = transactionResponseRecipientsIterator.next();
            String addr = transactionResponseRecipientsIt.get("address").textValue();
            if (!addr.equals(address)) {
                continue;
            }
            amount = Double.parseDouble(transactionResponseRecipientsIt.get("amount").textValue());
            trxType = "IN";
        }
        Iterator<JsonNode> addressTxsResponseItSendersIterator = transactionResponse.get("data").get("item").get("senders").iterator();
        while (addressTxsResponseItSendersIterator.hasNext()) {
            JsonNode addressTxsResponseItSendersIt = addressTxsResponseItSendersIterator.next();
            String addr = addressTxsResponseItSendersIt.get("address").textValue();
            if (!addr.equals(address)) {
                continue;
            }
            amount = Double.parseDouble(addressTxsResponseItSendersIt.get("amount").textValue());
            trxType = "OUT";
        }
        if (trxType == null) {
            return response;
        }
        if (!trxType.equals(type)) {
            return response;
        }
        if (amount == null) {
            return response;
        }
        ((ObjectNode) response).put("transactionId", transactionId);
        ((ObjectNode) response).put("address", address);
        ((ObjectNode) response).put("amount", amount);
        ((ObjectNode) response).put("isConfirmed", isConfirmed);
        ((ObjectNode) response).put("timestamp", timestamp);
        return response;
    }

}
