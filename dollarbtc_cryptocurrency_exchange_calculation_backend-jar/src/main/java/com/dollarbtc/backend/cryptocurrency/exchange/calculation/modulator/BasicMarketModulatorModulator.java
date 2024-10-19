/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author CarlosDaniel
 */
public abstract class BasicMarketModulatorModulator extends BasicModulator {
    
    public BasicMarketModulatorModulator(String threadName) {
        super(threadName);
    }

    protected abstract void runMarketModulatorModulator(
            String exchangeId,
            String symbol,
            String period,
            List<List<Object>> algorithmParams
    );


    @Override
    protected void runModulator() {
        File marketModulatorAutomaticRulesFile = new File(new File(OPERATOR_PATH, "MarketModulator"), "automaticRules.json");
        if(!marketModulatorAutomaticRulesFile.isFile()){
            return;
        }
        System.out.println("--------------- STARTING THREAD ---------------");
        getMarketModulatorRulesJson(marketModulatorAutomaticRulesFile).stream().forEach((object) -> {
                runMarketModulatorModulator(
                        (String) object[0],
                        (String) object[1],
                        (String) object[2],
                        (List<List<Object>>) object[3]
                );
            });
        System.out.println("--------------- FINISHING THREAD ---------------");
    }

    private List<Object[]> getMarketModulatorRulesJson(File marketModulatorRulesFile) {
        List<Object[]> objects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode marketModulatorRules = null;
        try {
            marketModulatorRules = mapper.readTree(marketModulatorRulesFile);
        } catch (IOException ex) {
            Logger.getLogger(BasicMarketModulatorModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (marketModulatorRules == null) {
            return objects;
        }
        Iterator<JsonNode> marketModulatorRulesMarketModulatorRules = marketModulatorRules.get("automaticRules").elements();
        while (marketModulatorRulesMarketModulatorRules.hasNext()) {
            JsonNode marketModulatorRulesMarketModulatorRule = marketModulatorRulesMarketModulatorRules.next();
            String marketModulatorRulesMarketModulatorRuleExchangeId = marketModulatorRulesMarketModulatorRule.get("exchangeId").textValue();
            String marketModulatorRulesMarketModulatorRuleSymbol = marketModulatorRulesMarketModulatorRule.get("symbol").textValue();
            boolean marketModulatorRulesMarketModulatorRuleActive = marketModulatorRulesMarketModulatorRule.get("active").booleanValue();
            if(!marketModulatorRulesMarketModulatorRuleActive){
                continue;
            }
            String marketModulatorRulesMarketModulatorRulesPeriod = marketModulatorRulesMarketModulatorRule.get("period").textValue();
            List<List<Object>> algorithmParams = new ArrayList<>();
            Iterator<JsonNode> marketModulatorRulesMarketModulatorRulesAlgorithmRules = marketModulatorRulesMarketModulatorRule.get("algorithmRules").elements();
            while (marketModulatorRulesMarketModulatorRulesAlgorithmRules.hasNext()) {
                JsonNode marketModulatorRulesMarketModulatorRulesAlgorithmRule = marketModulatorRulesMarketModulatorRulesAlgorithmRules.next();
                List<Object> algorithmParam = new ArrayList<>();
                algorithmParam.add(marketModulatorRulesMarketModulatorRulesAlgorithmRule.get("name").textValue());
                algorithmParam.add(marketModulatorRulesMarketModulatorRulesAlgorithmRule.get("inactivateParams").textValue());
                algorithmParams.add(algorithmParam);
            }
            Object[] object = new Object[4];
            object[0] = marketModulatorRulesMarketModulatorRuleExchangeId;
            object[1] = marketModulatorRulesMarketModulatorRuleSymbol;
            object[2] = marketModulatorRulesMarketModulatorRulesPeriod;
            object[3] = algorithmParams;
            objects.add(object);
        }
        return objects;
    }
    
}
