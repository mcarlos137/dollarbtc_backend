/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.forex;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 *
 * @author carlosmolina
 */
public class ForexGetHistoricalRates extends AbstractOperation<ArrayNode> {
    
    private final String symbol;

    public ForexGetHistoricalRates(String symbol) {
        super(ArrayNode.class);
        this.symbol = symbol;
    }

    @Override
    protected void execute() {
        super.response = mapper.createArrayNode();
    }
    
}
