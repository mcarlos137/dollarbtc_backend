/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.review;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ReviewsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ReviewGet extends AbstractOperation<JsonNode> {

    private final String operationId;

    public ReviewGet(String operationId) {
        super(JsonNode.class);
        this.operationId = operationId;
    }

    @Override
    protected void execute() {
        File reviewFile = new File(new File(ReviewsFolderLocator.getFolder(), "OperationIds"), operationId + ".json");
        if (!reviewFile.isFile()) {
            super.response = mapper.createObjectNode();
            return;
        }
        try {
            super.response = mapper.readTree(reviewFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(ReviewGet.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
