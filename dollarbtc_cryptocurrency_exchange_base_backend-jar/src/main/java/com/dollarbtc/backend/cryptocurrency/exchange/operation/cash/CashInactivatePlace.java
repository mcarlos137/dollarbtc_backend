/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashInactivatePlace extends AbstractOperation<String> {

    private final String placeId;

    public CashInactivatePlace(String placeId) {
        super(String.class);
        this.placeId = placeId;
    }

    @Override
    public void execute() {
        File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(placeId);
        if (!cashPlaceConfigFile.isFile()) {
            super.response = "CASH PLACE DOES NOT EXIST";
            return;
        }
        try {
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            ((ObjectNode) cashPlaceConfig).put("active", false);
            FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(CashInactivatePlace.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
