/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetChargesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
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
public class OTCGetCharges extends AbstractOperation<JsonNode> {
    
    private final OTCGetChargesRequest otcGetChargesRequest;

    public OTCGetCharges(OTCGetChargesRequest otcGetChargesRequest) {
        super(JsonNode.class);
        this.otcGetChargesRequest = otcGetChargesRequest;
    }
    
    @Override
    protected void execute() {
        if(otcGetChargesRequest == null){
            super.response = method();
        } else {
            super.response = method(otcGetChargesRequest);
        }
    }
        
    private JsonNode method() {
        JsonNode charges = mapper.createObjectNode();
        File otcCurrencyChargesFile = OTCFolderLocator.getCurrencyChargesFile(null, "BTC");
        try {
            ((ObjectNode) charges).set("BTC", mapper.readTree(otcCurrencyChargesFile));
        } catch (IOException ex) {
            Logger.getLogger(OTCGetCharges.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String currency : (List<String>) new OTCGetCurrencies(null).getResponse()) {
            otcCurrencyChargesFile = OTCFolderLocator.getCurrencyChargesFile(null, currency);
            if (!otcCurrencyChargesFile.isFile()) {
                continue;
            }
            try {
                ((ObjectNode) charges).set(currency, mapper.readTree(otcCurrencyChargesFile));
            } catch (IOException ex) {
                Logger.getLogger(OTCGetCharges.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return charges;
    }
    
    private JsonNode method(OTCGetChargesRequest otcGetChargesRequest) {
        return BaseOperation.getChargesNew(otcGetChargesRequest.getCurrency(), otcGetChargesRequest.getAmount(), otcGetChargesRequest.getOperationType(), otcGetChargesRequest.getPaymentType(), null, otcGetChargesRequest.getBalance(), null);
    }
    
}
