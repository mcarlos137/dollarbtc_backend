/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetReferralCodes extends AbstractOperation<JsonNode> {

    public MCUserGetReferralCodes() {
        super(JsonNode.class);
    }    
    
    @Override
    public void execute(){
        try {
            super.response = mapper.readTree(BaseFilesLocator.getReferralCodesFile());
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCUserGetReferralCodes.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }
    
}
