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
public class CreateCustomerOperation extends AbstractDwollaRestClient<JsonNode> {

    private static final String ENDPOINT = "/customers";
    private final JsonNode requestBody = new ObjectMapper().createObjectNode();

    public CreateCustomerOperation(String firstName, String lastName, String email, String type, String businessName, String ipAddress) throws IOException {
        super(JsonNode.class, true);
        if (type == null) {
            type = "receive-only";
        }
        ((ObjectNode) requestBody).put("firstName", firstName);
        ((ObjectNode) requestBody).put("lastName", lastName);
        ((ObjectNode) requestBody).put("email", email);
        ((ObjectNode) requestBody).put("type", type);
        
        if(businessName != null){
            ((ObjectNode) requestBody).put("businessName", businessName);
        }
        if(ipAddress != null){
            ((ObjectNode) requestBody).put("ipAddress", ipAddress);
        }
    }

    public JsonNode getResponse() {
        return super.postJsonNode(requestBody, url + ENDPOINT, RequestRestType.SYNC, SecurityType.BEARER, new String[]{accessToken}, 0);
    }

}
