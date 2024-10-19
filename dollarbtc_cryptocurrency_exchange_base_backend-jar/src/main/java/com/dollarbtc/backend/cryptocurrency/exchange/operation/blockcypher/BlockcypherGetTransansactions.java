/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.blockcypher;

import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class BlockcypherGetTransansactions extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final Set<String> addresses;
    private final String type;

    public BlockcypherGetTransansactions(Set<String> addresses, String type) {
        super(JsonNode.class);
        this.addresses = addresses;
        this.type = type;
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
        Map<String, Double> responseValues = new HashMap<>();
        Map<String, String[]> responseConfirmations = new HashMap<>();
        for (String address : addresses) {
            String params = "/" + address + "?token=b59c3adf5b414a0ab13242db49fa1b53";
            JsonNode addressResponse = super.getJsonNode("https://api.blockcypher.com/v1/btc/main", "/addrs", params, RequestRestType.SYNC, null, null, 30);
            if (!addressResponse.has("txrefs")) {
                return mapper.createObjectNode();
            }
            Iterator<JsonNode> addressTxsResponseIterator = addressResponse.get("txrefs").iterator();
            while (addressTxsResponseIterator.hasNext()) {
                JsonNode addressTxsResponseIt = addressTxsResponseIterator.next();
                String tx_hash = addressTxsResponseIt.get("tx_hash").textValue();
                Double value = addressTxsResponseIt.get("value").doubleValue();
                Integer confirmations = addressTxsResponseIt.get("confirmations").intValue();
                String confirmedTimestamp = addressTxsResponseIt.get("confirmed").textValue();
                int tx_input_n = addressTxsResponseIt.get("tx_input_n").intValue();
                int tx_output_n = addressTxsResponseIt.get("tx_output_n").intValue();
                if (!responseValues.containsKey(tx_hash)) {
                    responseValues.put(tx_hash, 0.0);
                }
                if (tx_input_n == -1) {
                    responseValues.put(tx_hash, responseValues.get(tx_hash) + value);
                } else if (tx_output_n == -1) {
                    responseValues.put(tx_hash, responseValues.get(tx_hash) - value);
                }
                if (!responseConfirmations.containsKey(tx_hash)) {
                    responseConfirmations.put(tx_hash, new String[]{confirmations.toString(), confirmedTimestamp, address});
                }
            }
            if (addressResponse.has("unconfirmed_txrefs")) {
                Iterator<JsonNode> addressUnconfirmedTxsResponseIterator = addressResponse.get("unconfirmed_txrefs").iterator();
                while (addressUnconfirmedTxsResponseIterator.hasNext()) {
                    JsonNode addressUnconfirmedTxsResponseIt = addressUnconfirmedTxsResponseIterator.next();
                    String tx_hash = addressUnconfirmedTxsResponseIt.get("tx_hash").textValue();
                    Double value = addressUnconfirmedTxsResponseIt.get("value").doubleValue();
                    Integer confirmations = addressUnconfirmedTxsResponseIt.get("confirmations").intValue();
                    String confirmedTimestamp = "NOT_YET";
                    int tx_input_n = addressUnconfirmedTxsResponseIt.get("tx_input_n").intValue();
                    int tx_output_n = addressUnconfirmedTxsResponseIt.get("tx_output_n").intValue();
                    if (!responseValues.containsKey(tx_hash)) {
                        responseValues.put(tx_hash, 0.0);
                    }
                    if (tx_input_n == -1) {
                        responseValues.put(tx_hash, responseValues.get(tx_hash) + value);
                    } else if (tx_output_n == -1) {
                        responseValues.put(tx_hash, responseValues.get(tx_hash) - value);
                    }
                    if (!responseConfirmations.containsKey(tx_hash)) {
                        responseConfirmations.put(tx_hash, new String[]{confirmations.toString(), confirmedTimestamp, address});
                    }
                }
            }
        }
        for (String tx_hash : responseValues.keySet()) {
            if (responseValues.get(tx_hash) < 0 && type.equals("IN")) {
                continue;
            }
            if (responseValues.get(tx_hash) > 0 && type.equals("OUT")) {
                continue;
            }
            ((ObjectNode) response).put(tx_hash, responseValues.get(tx_hash) / 100000000 + "____" + responseConfirmations.get(tx_hash)[0] + "____" + responseConfirmations.get(tx_hash)[1] + "____" + responseConfirmations.get(tx_hash)[2]);
        }
        return response;
    }

}
