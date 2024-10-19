/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailInactivate extends AbstractOperation<String> {

    private final String retailId;

    public MCRetailInactivate(String retailId) {
        super(String.class);
        this.retailId = retailId;
    }

    @Override
    public void execute() {
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(retailId);
        if (!moneyclickRetailConfigFile.isFile()) {
            super.response = "RETAIL DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            ((ObjectNode) moneyclickRetailConfig).put("active", false);
            FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailInactivate.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
