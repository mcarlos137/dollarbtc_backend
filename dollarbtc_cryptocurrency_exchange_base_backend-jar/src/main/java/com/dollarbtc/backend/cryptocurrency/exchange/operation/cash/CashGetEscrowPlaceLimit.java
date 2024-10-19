/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashGetEscrowPlaceLimit extends AbstractOperation<Double> {
    
    private final String placeId, currency;

    public CashGetEscrowPlaceLimit(String placeId, String currency) {
        super(Double.class);
        this.placeId = placeId;
        this.currency = currency;
    }
        
    @Override
    public void execute() {
        Double escrowRetailLimit = 0.0;
        try {
            File cashPlaceEscrowLimitsFile = CashFolderLocator.getPlacesEscrowLimitsFile();
            JsonNode cashPlaceEscrowLimits = mapper.readTree(cashPlaceEscrowLimitsFile);
            if (cashPlaceEscrowLimits.has(currency)) {
                escrowRetailLimit = cashPlaceEscrowLimits.get(currency).doubleValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(CashGetEscrowPlaceLimit.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            File cashPlaceConfigFile = new File(CashFolderLocator.getPlaceFolder(placeId), "config.json");
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            JsonNode escrowLimits = cashPlaceConfig.get("escrowLimits");
            if (escrowLimits.has(currency)) {
                super.response = escrowLimits.get(currency).doubleValue();
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(CashGetEscrowPlaceLimit.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = escrowRetailLimit;
    }
    
}
