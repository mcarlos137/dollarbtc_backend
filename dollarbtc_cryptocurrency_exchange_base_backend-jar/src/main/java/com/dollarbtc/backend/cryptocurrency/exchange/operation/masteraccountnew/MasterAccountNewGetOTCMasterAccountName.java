/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class MasterAccountNewGetOTCMasterAccountName extends AbstractOperation<JsonNode> {

    private final String operatorName, currency;

    public MasterAccountNewGetOTCMasterAccountName(String operatorName, String currency) {
        super(JsonNode.class);
        this.operatorName = operatorName;
        this.currency = currency;
    }

    @Override
    public void execute() {
        File masterAccountFile = MasterAccountFolderLocator.getConfigFile(operatorName);
        if (masterAccountFile.isFile()) {
            try {
                Iterator<JsonNode> masterAccountIterator = mapper.readTree(masterAccountFile).iterator();
                while (masterAccountIterator.hasNext()) {
                    JsonNode masterAccountIt = masterAccountIterator.next();
                    Iterator<JsonNode> masterAccountItCurrenciesIterator = masterAccountIt.get("currencies").iterator();
                    while (masterAccountItCurrenciesIterator.hasNext()) {
                        JsonNode masterAccountItCurrenciesIt = masterAccountItCurrenciesIterator.next();
                        if (masterAccountItCurrenciesIt.textValue().equals(currency)) {
                            super.response = masterAccountIt;
                            return;
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MasterAccountNewGetOTCMasterAccountName.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JsonNode result = mapper.createObjectNode();
        ((ObjectNode) result).put("name", "");
        super.response = result;
    }

}
