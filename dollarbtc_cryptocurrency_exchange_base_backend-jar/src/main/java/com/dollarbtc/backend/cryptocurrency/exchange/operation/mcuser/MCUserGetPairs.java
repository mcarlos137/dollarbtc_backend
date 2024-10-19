/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetPairs extends AbstractOperation<JsonNode> {

    public MCUserGetPairs() {
        super(JsonNode.class);
    }

    @Override
    protected void execute() {
        File moneyclickPairsFile = MoneyclickFolderLocator.getPairsFile();
        try {
            super.response = mapper.readTree(moneyclickPairsFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCUserGetPairs.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createArrayNode();
    }

}
