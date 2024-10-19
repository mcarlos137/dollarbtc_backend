/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetAllowedAddPayments extends AbstractOperation<JsonNode> {
    
    private final String userName, currency;

    public OTCGetAllowedAddPayments(String userName, String currency) {
        super(JsonNode.class);
        this.userName = userName;
        this.currency = currency;
    }    
    
    @Override
    public void execute() {
        JsonNode allowedAddPayments = mapper.createObjectNode();
        ((ObjectNode) allowedAddPayments).put("own", false);
        ((ObjectNode) allowedAddPayments).put("thirds", false);
        File otcAllowedAddPaymentsFile = OTCFolderLocator.getAllowedAddPaymentsFile(null);
        if (otcAllowedAddPaymentsFile.isFile()) {
            try {
                JsonNode otcAllowedAddPayments = mapper.readTree(otcAllowedAddPaymentsFile);
                if (otcAllowedAddPayments.has(currency)) {
                    ((ObjectNode) allowedAddPayments).setAll((ObjectNode) otcAllowedAddPayments.get(currency));
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetAllowedAddPayments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File userAllowedAddPaymentsFile = UsersFolderLocator.getAllowedAddPaymentsFile(userName);
        if (userAllowedAddPaymentsFile.isFile()) {
            try {
                JsonNode userAllowedAddPayments = mapper.readTree(userAllowedAddPaymentsFile);
                if (userAllowedAddPayments.has(currency)) {
                    ((ObjectNode) allowedAddPayments).setAll((ObjectNode) userAllowedAddPayments.get(currency));
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetAllowedAddPayments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = allowedAddPayments;
    }
    
}
