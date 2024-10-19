/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFlagsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserListByFlagColor extends AbstractOperation<JsonNode> {
    
    private final String flagColor;

    public UserListByFlagColor(String flagColor) {
        super(JsonNode.class);
        this.flagColor = flagColor;
    }

    @Override
    protected void execute() {
        try {
            super.response = mapper.readTree(UsersFlagsFolderLocator.getColorFile(flagColor));
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserListByFlagColor.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
