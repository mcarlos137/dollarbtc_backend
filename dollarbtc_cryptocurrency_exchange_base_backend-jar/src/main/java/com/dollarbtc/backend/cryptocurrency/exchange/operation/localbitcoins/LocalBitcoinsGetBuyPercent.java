/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author carlosmolina
 */
public class LocalBitcoinsGetBuyPercent extends AbstractOperation<Integer> {

    private final String symbol;

    public LocalBitcoinsGetBuyPercent(String symbol) {
        super(Integer.class);
        this.symbol = symbol;
    }

    @Override
    protected void execute() {
        JsonNode ticker = new LocalBitcoinsGetTicker(symbol).getResponse();
        List<String> operations = new ArrayList<>();
        operations.add("bid");
        operations.add("ask");
        List<String> types = new ArrayList<>();
        types.add("average");
        types.add("low");
        types.add("high");
        double factor = 0.4;
        Map<String, Double> operationSum = new HashMap<>();
        for (String operation : operations) {
            double opSum = 0;
            for (String type : types) {
                if ((operation.equals("bid") && type.equals("high")) || (operation.equals("ask") && type.equals("low"))) {
                    factor = 0.2;
                }
                if (ticker.has(operation)
                        && ticker.get(operation).has(type)
                        && ticker.get(operation).get(type).has("6H%")
                        && ticker.get(operation).get(type).has("24H%")
                        && ticker.get(operation).get(type).get("6H%").isDouble() && ticker.get(operation).get(type).get("24H%").isDouble()) {
                    opSum = opSum + factor * (ticker.get(operation).get(type).get("24H%").doubleValue() - ticker.get(operation).get(type).get("6H%").doubleValue());
                } else {
                    super.response = 50;
                    return;
                }
            }
            operationSum.put(operation, opSum);
        }
        Double result = (operationSum.get("ask") / (operationSum.get("bid") + operationSum.get("ask"))) * 100;
        if (result < 0.0) {
            result = 0.0;
        }
        if (result > 100.0) {
            result = 100.0;
        }
        super.response = result.intValue();
    }

}
