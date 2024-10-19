/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOperationsOverall extends AbstractOperation<JsonNode> {

    public OTCGetOperationsOverall() {
        super(JsonNode.class);
    }
        
    @Override
    public void execute() {
        String[] timePeriods = new String[]{"1H"};
        JsonNode operationsOverall = mapper.createObjectNode();
        for (String timePeriod : timePeriods) {
            switch (timePeriod) {
                case "1H":
                    break;
                case "4H":
                    break;
                case "12H":
                    break;
                case "1D":
                    break;
                case "2D":
                    break;
                case "1W":
                    break;
                case "2W":
                    break;
                case "1M":
                    break;
            }
            new OTCGetOperations(new OTCGetOperationsRequest()).getResponse();
        }
        super.response = operationsOverall;
    }

}
