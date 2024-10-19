/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author carlosmolina
 */
public class OTCGetThirdPartySendData extends AbstractOperation<JsonNode> {
    
    private final String id;

    public OTCGetThirdPartySendData(String id) {
        super(JsonNode.class);
        this.id = id;
    }

    @Override
    protected void execute() {
        super.response = mapper.createObjectNode();
    }    
    
}
