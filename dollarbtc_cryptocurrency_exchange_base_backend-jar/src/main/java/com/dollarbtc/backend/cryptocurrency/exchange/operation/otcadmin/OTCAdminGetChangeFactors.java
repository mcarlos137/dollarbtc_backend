/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminGetChangeFactors extends AbstractOperation<JsonNode> {

    public OTCAdminGetChangeFactors() {
        super(JsonNode.class);
    }

    @Override
    protected void execute() {
        try {
            super.response = mapper.readTree(OTCFolderLocator.getChangeFactorsFile("MAIN"));
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetChangeFactors.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
