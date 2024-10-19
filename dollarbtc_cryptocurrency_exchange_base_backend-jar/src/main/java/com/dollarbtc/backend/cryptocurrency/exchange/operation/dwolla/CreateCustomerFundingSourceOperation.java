/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.dwolla;

import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

/**
 *
 * @author CarlosDaniel
 */
public class CreateCustomerFundingSourceOperation extends AbstractDwollaRestClient<JsonNode> {

    private static final String ENDPOINT = "/customers";
    private static final String ENDPOINT_SUFFIX = "/funding-sources";
    private final String id;
    private final JsonNode requestBody = new ObjectMapper().createObjectNode();

    public CreateCustomerFundingSourceOperation(String id, String plaidAccessToken, String name) throws IOException {
        super(JsonNode.class, true);
        this.id = id;
        ((ObjectNode) requestBody).put("plaidToken", plaidAccessToken);
        ((ObjectNode) requestBody).put("name", name);
    }
    
    public CreateCustomerFundingSourceOperation(String id, String routingNumber, String accountNumber, String bankAccountType, String name) throws IOException {
        super(JsonNode.class, true);
        this.id = id;
        ((ObjectNode) requestBody).put("routingNumber", routingNumber);
        ((ObjectNode) requestBody).put("accountNumber", accountNumber);
        ((ObjectNode) requestBody).put("bankAccountType", bankAccountType);
        ((ObjectNode) requestBody).put("name", name);
    }

    public JsonNode getResponse() {
        return super.postJsonNode(requestBody, url + ENDPOINT + "/" + id + ENDPOINT_SUFFIX, RequestRestType.SYNC, SecurityType.BEARER, new String[]{accessToken}, 0);
    }

}
