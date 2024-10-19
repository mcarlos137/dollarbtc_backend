/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsOverviewId extends AbstractOperation<String> {
    
    private final String userName;

    public ShortsOverviewId(String userName) {
        super(String.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File shortsOverviewFile = ShortsFolderLocator.getOverviewFile();
        if (!shortsOverviewFile.isFile()) {
            super.response = "";
            return;
        }
        try {
            JsonNode shortsOverview = mapper.readTree(shortsOverviewFile);
            super.response = shortsOverview.get("id").textValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(ShortsOverviewId.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "";
    }

}
