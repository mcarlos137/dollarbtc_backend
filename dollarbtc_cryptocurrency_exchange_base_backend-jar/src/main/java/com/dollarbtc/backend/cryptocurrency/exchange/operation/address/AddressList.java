/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.address;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
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
public class AddressList extends AbstractOperation<ArrayNode> {
    
    private final String currency;

    public AddressList(String currency) {
        super(ArrayNode.class);
        this.currency = currency;
    }
        
    @Override
    protected void execute() {
        File addressesCurrencyFolder = AddressesFolderLocator.getCurrencyFolder(currency);
        ArrayNode result = mapper.createArrayNode();
        for (File addressesCurrencyAddressFolder : addressesCurrencyFolder.listFiles()) {
            if (!addressesCurrencyAddressFolder.isDirectory() || addressesCurrencyAddressFolder.getName().equals("Operations") || addressesCurrencyAddressFolder.getName().equals("Transactions")) {
                continue;
            }
            try {
                JsonNode addressesCurrencyAddress = mapper.readTree(new File(addressesCurrencyAddressFolder, "config.json"));
                if (addressesCurrencyAddress.has("privateKey") && !addressesCurrencyAddress.get("privateKey").textValue().equals("")
                        || addressesCurrencyAddress.has("private") && !addressesCurrencyAddress.get("private").textValue().equals("")) {
                    ((ObjectNode) addressesCurrencyAddress).put("hasPrivateKey", true);
                    ((ObjectNode) addressesCurrencyAddress).remove("privateKey");
                    ((ObjectNode) addressesCurrencyAddress).remove("private");
                } else {
                    ((ObjectNode) addressesCurrencyAddress).put("hasPrivateKey", false);
                }
                result.add(addressesCurrencyAddress);
            } catch (IOException ex) {
                Logger.getLogger(AddressList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = result;
    }
    
}
