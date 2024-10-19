/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.dwolla;

import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 *
 * @author CarlosDaniel
 */
public class GetCustomersOperation extends AbstractDwollaRestClient<JsonNode> {

    private static final String ENDPOINT = "/customers";

    public GetCustomersOperation() throws IOException {
        super(JsonNode.class, true);
    }

    public JsonNode getResponse() {
        return super.getJsonNode(url, ENDPOINT, "", RequestRestType.SYNC, SecurityType.BEARER, new String[]{accessToken}, 0);
    }

}
