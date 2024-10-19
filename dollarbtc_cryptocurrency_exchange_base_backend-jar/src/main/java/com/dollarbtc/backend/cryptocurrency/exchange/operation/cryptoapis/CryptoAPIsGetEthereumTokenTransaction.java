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
public class CryptoAPIsGetEthereumTokenTransaction extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String type, network, id, address, currency;
    private final Integer blockchainHeight;

    public CryptoAPIsGetEthereumTokenTransaction(String type, String network, String id, String address, String currency, Integer blockchainHeight) {
        super(JsonNode.class);
        this.type = type;
        this.network = network;
        this.id = id;
        this.address = address;
        this.currency = currency;
        this.blockchainHeight = blockchainHeight;
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
        String params = "/" + id + "/tokens-transfers";
        String url = "https://rest.cryptoapis.io/v2/blockchain-data";
        JsonNode transactionResponse = super.getJsonNode(url, "/" + "ethereum" + "/" + network + "/transactions", params, RequestRestType.SYNC, null, null, 30);
        if (!transactionResponse.has("data") || !transactionResponse.get("data").has("items")) {
            return response;
        }
        Integer confirmations = null;
        String transactionHash = null;
        String tokenCurrency = null;
        Double amount = null;
        String timestamp = null;
        String trxType = null;
        Iterator<JsonNode> transactionResponseIterator = transactionResponse.get("data").get("items").iterator();
        while (transactionResponseIterator.hasNext()) {
            JsonNode transactionResponseIt = transactionResponseIterator.next();
//                String contractAddress = transactionResponseIt.get("contractAddress").textValue();
            String recipientAddress = transactionResponseIt.get("recipientAddress").textValue();
            String senderAddress = transactionResponseIt.get("senderAddress").textValue();
//                String tokenName = transactionResponseIt.get("tokenName").textValue();
//                String tokenType = transactionResponseIt.get("tokenType").textValue();                
            if (recipientAddress.equals(address)) {
                trxType = "IN";
            } else if (senderAddress.equals(address)) {
                trxType = "OUT";
            }
            tokenCurrency = transactionResponseIt.get("tokenSymbol").textValue();
            if (!tokenCurrency.equals(currency)) {
                tokenCurrency = null;
                continue;
            }
            Integer minedInBlockHeight = transactionResponseIt.get("minedInBlockHeight").intValue();
            confirmations = blockchainHeight - minedInBlockHeight;
            transactionHash = transactionResponseIt.get("transactionHash").textValue();
            amount = Double.parseDouble(transactionResponseIt.get("tokensAmount").textValue());
            timestamp = DateUtil.getDate(transactionResponseIt.get("transactionTimestamp").longValue() * 1000);
        }
        if (trxType == null) {
            return response;
        }
        if (!trxType.equals(type)) {
            return response;
        }
        if (amount == null || confirmations == null || timestamp == null || tokenCurrency == null) {
            return response;
        }
        boolean isConfirmed = false;
        if (confirmations >= 20) {
            isConfirmed = true;
        }
        ((ObjectNode) response).put("transactionId", transactionHash);
        ((ObjectNode) response).put("address", address);
        ((ObjectNode) response).put("currency", currency);
        ((ObjectNode) response).put("amount", amount);
        ((ObjectNode) response).put("isConfirmed", isConfirmed);
        ((ObjectNode) response).put("timestamp", timestamp);
        return response;
    }

}
