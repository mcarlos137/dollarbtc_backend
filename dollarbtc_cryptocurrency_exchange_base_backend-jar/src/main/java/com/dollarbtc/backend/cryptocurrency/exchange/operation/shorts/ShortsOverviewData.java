/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
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
public class ShortsOverviewData extends AbstractOperation<ArrayNode> {

    private final String userName;

    public ShortsOverviewData(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File shortsOverviewFile = ShortsFolderLocator.getOverviewFile();
        if (!shortsOverviewFile.isFile()) {
            super.response = mapper.createArrayNode();
            return;
        }
        try {
            JsonNode shortsOverview = mapper.readTree(shortsOverviewFile);
            super.response = (ArrayNode) shortsOverview.get("data");
            return;
        } catch (IOException ex) {
            Logger.getLogger(ShortsOverviewData.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createArrayNode();
    }

}
