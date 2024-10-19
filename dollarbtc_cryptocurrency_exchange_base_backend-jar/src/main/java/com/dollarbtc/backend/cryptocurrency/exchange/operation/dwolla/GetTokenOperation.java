/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.dwolla;

import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author CarlosDaniel
 */
public class GetTokenOperation extends AbstractDwollaRestClient<JsonNode> {

    private static final String ENDPOINT = "/token";
    private final MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

    public GetTokenOperation() throws IOException {
        super(JsonNode.class, false);
        super.headers.add("content-type", "application/x-www-form-urlencoded");
        this.formData.add("grant_type", "client_credentials");
    }
    
    public JsonNode getResponse() {
        return super.postJsonNode(this.formData, url + ENDPOINT, RequestRestType.ASYNC, SecurityType.BASIC, new String[]{clientKey, clientSecret}, 30);
    }

}
