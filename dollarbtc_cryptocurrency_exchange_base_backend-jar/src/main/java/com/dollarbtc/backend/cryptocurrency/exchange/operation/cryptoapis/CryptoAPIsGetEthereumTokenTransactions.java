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
public class CryptoAPIsGetEthereumTokenTransactions extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final Set<String> addresses;
    private final String type, network, currency;
    private final Integer blockchainHeight;

    public CryptoAPIsGetEthereumTokenTransactions(Set<String> addresses, String type, String network, String currency, Integer blockchainHeight) {
        super(JsonNode.class);
        this.addresses = addresses;
        this.type = type;
        this.network = network;
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
        for (String address : addresses) {
            String params = "/" + address + "/tokens-transfers?limit=10&offset=0";
            JsonNode addressResponse = super.getJsonNode("https://rest.cryptoapis.io/v2/blockchain-data/ethereum/" + network, "/addresses", params, RequestRestType.SYNC, null, null, 30);
            if (!addressResponse.has("data") || !addressResponse.get("data").has("items")) {
                return mapper.createObjectNode();
            }
            Iterator<JsonNode> addressTxsResponseIterator = addressResponse.get("data").get("items").iterator();
            while (addressTxsResponseIterator.hasNext()) {
                JsonNode addressTxsResponseIt = addressTxsResponseIterator.next();
                String trxType = null;
                String timestamp = DateUtil.getDate(addressTxsResponseIt.get("transactionTimestamp").longValue() * 1000);
//                    String contractAddress = addressTxsResponseIt.get("contractAddress").textValue();
                Integer minedInBlockHeight = addressTxsResponseIt.get("minedInBlockHeight").intValue();
                Integer confirmations = blockchainHeight - minedInBlockHeight;
                String recipientAddress = addressTxsResponseIt.get("recipientAddress").textValue();
                String senderAddress = addressTxsResponseIt.get("senderAddress").textValue();
                String tokenName = addressTxsResponseIt.get("tokenName").textValue();
                String tokenSymbol = addressTxsResponseIt.get("tokenSymbol").textValue();
                String tokenType = addressTxsResponseIt.get("tokenType").textValue();
                Double tokensAmount = Double.parseDouble(addressTxsResponseIt.get("tokensAmount").textValue());
                String transactionHash = addressTxsResponseIt.get("transactionHash").textValue();
                if (!currency.equals(tokenSymbol)) {
                    continue;
                }
                if (address.equals(recipientAddress)) {
                    trxType = "IN";
                } else if (address.equals(senderAddress)) {
                    trxType = "OUT";
                }
                if (trxType == null) {
                    continue;
                }
                if (!type.equals(trxType)) {
                    continue;
                }
                ((ObjectNode) response).put(transactionHash, tokensAmount + "____" + confirmations + "____" + timestamp + "____" + address);
            }
        }
        return response;
    }

}
