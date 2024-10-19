/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyMarketGetTopTraders extends AbstractOperation<ArrayNode> {
    
    public MoneyMarketGetTopTraders() {
        super(ArrayNode.class);
    }
        
    @Override
    protected void execute() {
        try {
            super.response = (ArrayNode) mapper.readTree(MoneyMarketFolderLocator.getTopTradersFile());
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketGetTopTraders.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createArrayNode();
    }
    
}
