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
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class CryptoAPIsGetCoinTransansactions extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final Set<String> addresses;
    private final String type, blockchain, network;
    private final Integer blockchainHeight;

    public CryptoAPIsGetCoinTransansactions(Set<String> addresses, String type, String blockchain, String network, Integer blockchainHeight) {
        super(JsonNode.class);
        this.addresses = addresses;
        this.type = type;
        this.blockchain = blockchain;
        this.network = network;
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
        for (String address : addresses) {
            int limit = 10;
            int offset = 0;
            String params = "/" + address + "/transactions?limit=" + limit + "&offset=" + offset;
            JsonNode addressResponse = super.getJsonNode("https://rest.cryptoapis.io/v2/blockchain-data/" + blockchain + "/" + network, "/addresses", params, RequestRestType.SYNC, null, null, 30);
            if (!addressResponse.has("data") || !addressResponse.get("data").has("items")) {
                continue;
            }
            int i = 0;
            Iterator<JsonNode> addressTxsResponseIterator = addressResponse.get("data").get("items").iterator();
            while (addressTxsResponseIterator.hasNext()) {
                JsonNode addressTxsResponseIt = addressTxsResponseIterator.next();
                i++;
                if (i > 5) {
                    break;
                }
                Double amount = null;
                String trxType = null;
                String timestamp = DateUtil.getDate(addressTxsResponseIt.get("timestamp").longValue() * 1000);
                String transactionId = addressTxsResponseIt.get("transactionId").textValue();
                Integer minedInBlockHeight = addressTxsResponseIt.get("minedInBlockHeight").intValue();
                Integer confirmations = blockchainHeight - minedInBlockHeight;
                Iterator<JsonNode> addressTxsResponseItRecipientsIterator = addressTxsResponseIt.get("recipients").iterator();
                while (addressTxsResponseItRecipientsIterator.hasNext()) {
                    JsonNode addressTxsResponseItRecipientsIt = addressTxsResponseItRecipientsIterator.next();
                    String addr = addressTxsResponseItRecipientsIt.get("address").textValue();
                    if (!addr.equals(address)) {
                        continue;
                    }
                    amount = Double.parseDouble(addressTxsResponseItRecipientsIt.get("amount").textValue());
                    trxType = "IN";
                }
                Iterator<JsonNode> addressTxsResponseItSendersIterator = addressTxsResponseIt.get("senders").iterator();
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
                    continue;
                }
                if (!trxType.equals(type)) {
                    continue;
                }
                if (amount == null) {
                    continue;
                }
                ((ObjectNode) response).put(transactionId, amount + "____" + confirmations + "____" + timestamp + "____" + address);
            }
        }
        return response;
    }

}
