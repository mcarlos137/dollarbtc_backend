/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCNewGetFinancialTypes extends AbstractOperation<Object> {
    
    private final String currency;

    public OTCNewGetFinancialTypes(String currency) {
        super(Object.class);
        this.currency = currency;
    }
    
    @Override
    protected void execute() {
        File otcCurrencyConfigFile = new File(new File(OTCFolderLocator.getFolder(null), currency), "configNew.json");
        if (otcCurrencyConfigFile.isFile()) {
            try {
                super.response = (ArrayNode) mapper.readTree(otcCurrencyConfigFile).get("financialTypes");
                return;
            } catch (IOException ex) {
                Logger.getLogger(OTCNewGetFinancialTypes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = mapper.createArrayNode();        
    }
            
}
