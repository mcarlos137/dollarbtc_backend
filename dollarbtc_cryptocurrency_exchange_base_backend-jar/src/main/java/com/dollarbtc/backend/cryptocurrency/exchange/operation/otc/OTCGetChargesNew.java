/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetChargesNewRequest;
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
public class OTCGetChargesNew extends AbstractOperation<JsonNode> {
    
    private final OTCGetChargesNewRequest otcGetChargesNewRequest;

    public OTCGetChargesNew(OTCGetChargesNewRequest otcGetChargesNewRequest) {
        super(JsonNode.class);
        this.otcGetChargesNewRequest = otcGetChargesNewRequest;
    }
    
    @Override
    protected void execute() {
        if(otcGetChargesNewRequest == null){
            super.response = method();
        } else {
            super.response = method(otcGetChargesNewRequest);
        }
    }
        
    private JsonNode method() {
        JsonNode chargesNew = mapper.createObjectNode();
        File otcCurrencyChargesNewFile = OTCFolderLocator.getCurrencyChargesNewFile(null, "BTC");
        try {
            ((ObjectNode) chargesNew).set("BTC", mapper.readTree(otcCurrencyChargesNewFile));
        } catch (IOException ex) {
            Logger.getLogger(OTCGetChargesNew.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String currency : (List<String>) new OTCGetCurrencies(null).getResponse()) {
            otcCurrencyChargesNewFile = OTCFolderLocator.getCurrencyChargesNewFile(null, currency);
            if (!otcCurrencyChargesNewFile.isFile()) {
                continue;
            }
            try {
                ((ObjectNode) chargesNew).set(currency, mapper.readTree(otcCurrencyChargesNewFile));
            } catch (IOException ex) {
                Logger.getLogger(OTCGetChargesNew.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return chargesNew;
    }
    
    private JsonNode method(OTCGetChargesNewRequest otcGetChargesNewRequest) {
        return BaseOperation.getChargesNew(otcGetChargesNewRequest.getCurrency(), otcGetChargesNewRequest.getAmount(), otcGetChargesNewRequest.getOperationType(), otcGetChargesNewRequest.getPaymentType(), null, otcGetChargesNewRequest.getBalance(), otcGetChargesNewRequest.getTargetCurrency());
    }
    
}
