/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOperation extends AbstractOperation<JsonNode> {

    private final String id;

    public OTCGetOperation(String id) {
        super(JsonNode.class);
        this.id = id;
    }

    @Override
    public void execute() {
        File otcOperationIdFolder = OTCFolderLocator.getOperationIdFolder(null, id);
        try {
            File otcOperationIdFile = new File(otcOperationIdFolder, "operation.json");
            JsonNode otcOperationId = mapper.readTree(otcOperationIdFile);
            BaseOperation.putBuyOperationMinutesLeft(otcOperationId);
            super.response = otcOperationId;
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCGetOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
