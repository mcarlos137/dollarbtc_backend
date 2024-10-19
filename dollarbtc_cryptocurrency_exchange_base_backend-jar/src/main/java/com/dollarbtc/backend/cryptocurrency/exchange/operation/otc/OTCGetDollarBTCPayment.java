/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetDollarBTCPayment extends AbstractOperation<JsonNode> {

    private final String currency, id;

    public OTCGetDollarBTCPayment(String currency, String id) {
        super(JsonNode.class);
        this.currency = currency;
        this.id = id;
    }

    @Override
    public void execute() {
        File otcCurrencyPaymentFile = OTCFolderLocator.getCurrencyPaymentFile(null, currency, id);
        if (!otcCurrencyPaymentFile.isFile()) {
            super.response = mapper.createObjectNode();
            return;
        }
        try {
            super.response = mapper.readTree(otcCurrencyPaymentFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCGetDollarBTCPayment.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
