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
public class InitiateTransferOperation extends AbstractDwollaRestClient<JsonNode> {

    private static final String ENDPOINT = "/transfers";
    private final JsonNode requestBody = new ObjectMapper().createObjectNode();

    public InitiateTransferOperation(String source, String destination, String amountValue, String amountCurrency, String idempotencyKey) throws IOException {
        super(JsonNode.class, true);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode _links = mapper.createObjectNode();
        JsonNode _linksSource = mapper.createObjectNode();
        ((ObjectNode) _linksSource).put("href", url + "/funding-sources/" + source);
        ((ObjectNode) _links).set("source", _linksSource);
        JsonNode _linksDestination = mapper.createObjectNode();
        ((ObjectNode) _linksDestination).put("href", url + "/funding-sources/" + destination);
        ((ObjectNode) _links).set("destination", _linksDestination);
        JsonNode amount = mapper.createObjectNode();
        ((ObjectNode) amount).put("currency", amountCurrency);
        ((ObjectNode) amount).put("value", amountValue);
        ((ObjectNode) requestBody).set("_links", _links);
        ((ObjectNode) requestBody).set("amount", amount);
        super.headers.add("Idempotency-Key", idempotencyKey);
    }

    public JsonNode getResponse() {
        return super.postJsonNode(requestBody, url + ENDPOINT, RequestRestType.SYNC, SecurityType.BEARER, new String[]{accessToken}, 0);
    }

}
