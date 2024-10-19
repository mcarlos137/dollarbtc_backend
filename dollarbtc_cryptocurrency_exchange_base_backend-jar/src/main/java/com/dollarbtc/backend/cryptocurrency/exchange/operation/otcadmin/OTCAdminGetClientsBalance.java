/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminGetClientsBalance extends AbstractOperation<JsonNode> {

    public OTCAdminGetClientsBalance() {
        super(JsonNode.class);
    }

    @Override
    protected void execute() {
        File clientsBalanceFile = BaseFilesLocator.getClientsBalanceFile();
        try {
            super.response = mapper.readTree(clientsBalanceFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetClientsBalance.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
