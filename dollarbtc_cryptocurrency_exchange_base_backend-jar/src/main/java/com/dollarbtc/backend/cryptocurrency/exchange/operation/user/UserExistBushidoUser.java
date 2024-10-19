/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

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
public class UserExistBushidoUser extends AbstractRestClient<Boolean> {

    private static Client client;
    private final String userName;

    public UserExistBushidoUser(String userName) {
        super(Boolean.class);
        this.userName = userName;
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

    public Boolean getResponse() {
        JsonNode response = super.getJsonNode("https://service8080.dollarbtc.com", "/bushido-wallet-service-1.0.3/api/v2/user/find/" + userName, "", RequestRestType.SYNC, null, null, 0);
        return !(response.has("errors") && response.get("errors").size() > 0);
    }

}
