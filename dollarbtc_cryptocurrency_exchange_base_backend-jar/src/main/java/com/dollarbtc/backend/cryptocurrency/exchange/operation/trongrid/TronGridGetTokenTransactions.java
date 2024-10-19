/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.trongrid;

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
public class TronGridGetTokenTransactions extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final Set<String> addresses;
    private final String type, currency;

    public TronGridGetTokenTransactions(Set<String> addresses, String type, String currency) {
        super(JsonNode.class);
        this.addresses = addresses;
        this.type = type;
        this.currency = currency;
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
        headers.add("TRON_PRO_API_KEY", "391f11d2-b32f-4d2b-9eab-83fbd6fb11bc");
        for (String address : addresses) {
            JsonNode addressResponse = super.getJsonNode("https://api.trongrid.io/v1/accounts/" + address, "/transactions/trc20", "", RequestRestType.SYNC, null, null, 30);
            if (addressResponse == null || !addressResponse.has("data")) {
                return mapper.createObjectNode();
            }
            Boolean success = addressResponse.get("success").booleanValue();
            Iterator<JsonNode> addressTxsResponseIterator = addressResponse.get("data").iterator();
            while (addressTxsResponseIterator.hasNext()) {
                JsonNode addressTxsResponseIt = addressTxsResponseIterator.next();
                String trxType = null;
                String timestamp = DateUtil.getDate(addressTxsResponseIt.get("block_timestamp").longValue());
                String recipientAddress = addressTxsResponseIt.get("to").textValue();
                String senderAddress = addressTxsResponseIt.get("from").textValue();
                String tokenName = addressTxsResponseIt.get("token_info").get("name").textValue();
                String tokenSymbol = addressTxsResponseIt.get("token_info").get("symbol").textValue();
                Integer tokenDecimals = addressTxsResponseIt.get("token_info").get("decimals").intValue();
                String tokenType = "TRC20";
                Double tokensAmount = Double.parseDouble(addressTxsResponseIt.get("value").textValue()) / Math.pow(10, tokenDecimals);
                String transactionHash = addressTxsResponseIt.get("transaction_id").textValue();
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
                ((ObjectNode) response).put(transactionHash, tokensAmount + "____" + success + "____" + timestamp + "____" + address);
            }
        }
        return response;
    }

}
