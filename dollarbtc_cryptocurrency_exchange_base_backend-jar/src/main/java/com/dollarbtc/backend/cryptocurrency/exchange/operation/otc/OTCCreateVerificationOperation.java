/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author carlosmolina
 */
public class OTCCreateVerificationOperation extends AbstractOperation<String> {
    
    private final String userName;

    public OTCCreateVerificationOperation(String userName) {
        super(String.class);
        this.userName = userName;
    }    
    
    @Override
    public void execute() {
        String id = BaseOperation.getId();
        String timestamp = DateUtil.getCurrentDate();
        JsonNode operation = new ObjectMapper().createObjectNode();
        ((ObjectNode) operation).put("id", id);
        ((ObjectNode) operation).put("timestamp", timestamp);
        ((ObjectNode) operation).put("userName", userName);
        BaseOperation.createOperationInCentralFolder(operation, null);
        BaseOperation.createIndexesInCentralFolder(operation, null);
        super.response = id;
    }
    
}
