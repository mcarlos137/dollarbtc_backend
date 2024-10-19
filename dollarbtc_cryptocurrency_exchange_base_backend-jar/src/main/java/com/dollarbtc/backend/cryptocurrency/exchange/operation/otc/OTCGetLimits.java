/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetLimits extends AbstractOperation<JsonNode> {

    public OTCGetLimits() {
        super(JsonNode.class);
    }
        
    @Override
    public void execute() {
        JsonNode limits = mapper.createObjectNode();
        File otcCurrencyLimitsFile = OTCFolderLocator.getCurrencyLimitsFile(null, "BTC");
        try {
            ((ObjectNode) limits).set("BTC", mapper.readTree(otcCurrencyLimitsFile));
        } catch (IOException ex) {
            Logger.getLogger(OTCGetLimits.class.getName()).log(Level.SEVERE, null, ex);
        }
        ((List<String>) new OTCGetCurrencies(null).getResponse()).forEach((currency) -> {
            try {
                File otcCurrencyLimitsFilee = OTCFolderLocator.getCurrencyLimitsFile(null, currency);
                if (otcCurrencyLimitsFilee.isFile()) {
                    ((ObjectNode) limits).set(currency, mapper.readTree(otcCurrencyLimitsFilee));
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetLimits.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        super.response = limits;
    }
    
}
