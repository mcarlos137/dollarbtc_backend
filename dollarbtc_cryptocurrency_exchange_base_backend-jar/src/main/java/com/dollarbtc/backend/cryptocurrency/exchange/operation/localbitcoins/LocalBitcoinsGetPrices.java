/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins;

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
public class LocalBitcoinsGetPrices extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String currency, operation;

    public LocalBitcoinsGetPrices(String currency, String operation) {
        super(JsonNode.class);
        this.currency = currency;
        this.operation = operation;
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
        String endpoint;
        if (currency.toLowerCase().equals("usd") || currency.toLowerCase().equals("ves")) {
            if (currency.toLowerCase().equals("ves")) {
                endpoint = "/" + operation + "-bitcoins-online/" + "ved" + "/transfers-with-specific-bank/.json";
            } else {
                endpoint = "/" + operation + "-bitcoins-online/" + currency.toLowerCase() + "/transfers-with-specific-bank/.json";
            }
        } else {
            endpoint = "/" + operation + "-bitcoins-online/" + currency.toLowerCase() + "/.json";
        }
        return super.getJsonNode("https://localbitcoins.com", endpoint, "", RequestRestType.SYNC, null, null, 30);
    }

}
