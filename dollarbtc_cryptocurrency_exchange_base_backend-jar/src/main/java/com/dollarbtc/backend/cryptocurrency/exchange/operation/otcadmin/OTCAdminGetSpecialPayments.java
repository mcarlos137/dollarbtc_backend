/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminGetSpecialPayments extends AbstractOperation<JsonNode> {
    
    private final String userName;

    public OTCAdminGetSpecialPayments(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        JsonNode result = mapper.createObjectNode();
        Iterator<JsonNode> currenciesIterator = ((ArrayNode) new OTCAdminGetCurrencies(userName).getResponse()).iterator();
        while (currenciesIterator.hasNext()) {
            JsonNode currenciesIt = currenciesIterator.next();
            String currency = currenciesIt.get("shortName").textValue();
            File otcCurrencySpecialPaymentsFile = OTCFolderLocator.getCurrencySpecialPaymentsFile(null, currency);
            if (!otcCurrencySpecialPaymentsFile.isFile()) {
                continue;
            }
            try {
                ((ObjectNode) result).put(currency, mapper.readTree(otcCurrencySpecialPaymentsFile));
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetSpecialPayments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = result;
    }
        
}
