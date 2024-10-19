/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.trongrid;

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
public class TronGridGenerateAddress extends AbstractRestClient<JsonNode> {

    private static Client client;

    public TronGridGenerateAddress() {
        super(JsonNode.class);
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
        headers.add("TRON_PRO_API_KEY", "391f11d2-b32f-4d2b-9eab-83fbd6fb11bc");
        String url = "https://api.shasta.trongrid.io/wallet/generateaddress";
        return super.getJsonNode(url, "", "", RequestRestType.SYNC, null, null, 30);
    }

}
