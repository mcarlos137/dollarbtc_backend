/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailGetBTCEscrowRetailLimit extends AbstractOperation<Double> {

    private final String retailId;

    public MCRetailGetBTCEscrowRetailLimit(String retailId) {
        super(Double.class);
        this.retailId = retailId;
    }

    @Override
    public void execute() {
        Double btcEscrowRetailLimit = 0.5;
        File moneyclickRetailConfigFile = new File(MoneyclickFolderLocator.getRetailFolder(retailId), "config.json");
        try {
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            super.response = moneyclickRetailConfig.get("btcEscrowLimit").doubleValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailGetBTCEscrowRetailLimit.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = btcEscrowRetailLimit;
    }

}
