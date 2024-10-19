/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

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
public class MCRetailNewGetEscrowRetailLimit extends AbstractOperation<Double> {
    
    private final String retailId, currency;

    public MCRetailNewGetEscrowRetailLimit(String retailId, String currency) {
        super(Double.class);
        this.retailId = retailId;
        this.currency = currency;
    }
        
    @Override
    public void execute() {
        Double escrowRetailLimit = 0.0;
        try {
            File moneyclickRetailEscrowLimitsFile = MoneyclickFolderLocator.getRetailEscrowLimitsFile();
            JsonNode moneyclickRetailEscrowLimits = mapper.readTree(moneyclickRetailEscrowLimitsFile);
            if (moneyclickRetailEscrowLimits.has(currency)) {
                escrowRetailLimit = moneyclickRetailEscrowLimits.get(currency).doubleValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewGetEscrowRetailLimit.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            File moneyclickRetailConfigFile = new File(MoneyclickFolderLocator.getRetailFolder(retailId), "config.json");
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            JsonNode escrowLimits = moneyclickRetailConfig.get("escrowLimits");
            if (escrowLimits.has(currency)) {
                super.response = escrowLimits.get(currency).doubleValue();
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewGetEscrowRetailLimit.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = escrowRetailLimit;
    }
    
}
