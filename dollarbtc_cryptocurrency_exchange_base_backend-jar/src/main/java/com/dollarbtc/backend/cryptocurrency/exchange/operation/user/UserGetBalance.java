/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author carlosmolina
 */
public class UserGetBalance extends AbstractOperation<JsonNode> {
    
    private final String userName;

    public UserGetBalance(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }
    
    @Override
    protected void execute() {
        new UserAddWallet(userName, false, false).getResponse();
        super.response = LocalData.getUserBalance(userName, false, true, false, null, false);
    }
    
}
