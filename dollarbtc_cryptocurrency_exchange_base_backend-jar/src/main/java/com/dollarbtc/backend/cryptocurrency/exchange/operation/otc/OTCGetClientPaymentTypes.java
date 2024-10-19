/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetClientPaymentTypes extends AbstractOperation<Object> {
    
    private final String currency;

    public OTCGetClientPaymentTypes(String currency) {
        super(Object.class);
        this.currency = currency;
    }
    
    @Override
    protected void execute() {
        if(currency == null){
            super.response = method();
        } else {
            super.response = method(currency);
        }
    }
        
    private ArrayNode method(String currency) {
        File otcCurrencyConfigFile = new File(new File(OTCFolderLocator.getFolder(null), currency), "config.json");
        if (otcCurrencyConfigFile.isFile()) {
            try {
                return (ArrayNode) mapper.readTree(otcCurrencyConfigFile).get("clientPaymentTypes");
            } catch (IOException ex) {
                Logger.getLogger(OTCGetClientPaymentTypes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mapper.createArrayNode();
    }
    
    private JsonNode method() {
        JsonNode paymentTypes = mapper.createObjectNode();
        for (File otcCurrencyFolder : OTCFolderLocator.getFolder(null).listFiles()) {
            if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                continue;
            }
            File otcCurrencyConfigFile = new File(otcCurrencyFolder, "config.json");
            if (otcCurrencyConfigFile.isFile()) {
                try {
                    ((ObjectNode) paymentTypes).putArray(otcCurrencyFolder.getName()).addAll((ArrayNode) mapper.readTree(otcCurrencyConfigFile).get("clientPaymentTypes"));
                } catch (IOException ex) {
                    Logger.getLogger(OTCGetClientPaymentTypes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return paymentTypes;
    }
    
}
