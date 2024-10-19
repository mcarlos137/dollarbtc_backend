/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author CarlosDaniel
 */
public abstract class BasicMasterAccountModulator extends BasicModulator {

    public BasicMasterAccountModulator(String threadName) {
        super(threadName);
    }

    protected abstract void runMasterAccountModulator(
            String baseAccount,
            Map<String, Double> targetAccountOrClientModelNamePercent,
            BalanceOperationType balanceOperationType,
            Integer executionPeriodInHours
    );

    @Override
    protected void runModulator() {
        File masterAccountConfigFile = new File(new File(OPERATOR_PATH, "MasterAccount"), "automaticRules.json");
        if (!masterAccountConfigFile.isFile()) {
            return;
        }
        System.out.println("--------------- STARTING THREAD ---------------");
        getMasterAccountConfigJson(masterAccountConfigFile).stream().forEach((object) -> {
            runMasterAccountModulator(
                    (String) object[0],
                    (Map<String, Double>) object[1],
                    (BalanceOperationType) object[2],
                    (Integer) object[3]
            );
        });
        System.out.println("--------------- FINISHING THREAD ---------------");
    }

    private static List<Object[]> getMasterAccountConfigJson(File masterAccountConfigFile) {
        List<Object[]> objects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode masterAccountConfig = (ArrayNode) mapper.readTree(masterAccountConfigFile);
            Iterator<JsonNode> masterAccountConfigIterator = masterAccountConfig.elements();
            while (masterAccountConfigIterator.hasNext()) {
                JsonNode masterAccountConfigIt = masterAccountConfigIterator.next();
                if (!masterAccountConfigIt.get("active").booleanValue()) {
                    continue;
                }
                String masterAccountConfigItBaseAccount = masterAccountConfigIt.get("baseAccount").textValue();
                BalanceOperationType masterAccountConfigItBalanceOperationType = BalanceOperationType.valueOf(masterAccountConfigIt.get("balanceOperationType").textValue());
                Integer masterAccountConfigItExecutionPeriodInHours = masterAccountConfigIt.get("executionPeriodInHours").intValue();
                Map<String, Double> targetAccountOrClientModelNamePercent = new HashMap<>();
                Iterator<JsonNode> masterAccountConfigItTargetAccountsOrClientModelNameAndPercentsIterator = masterAccountConfigIt.get("targetAccountsOrClientModelNameAndPercents").elements();
                while (masterAccountConfigItTargetAccountsOrClientModelNameAndPercentsIterator.hasNext()) {
                    JsonNode masterAccountConfigItTargetAccountsOrClientModelNameAndPercentsIt = masterAccountConfigItTargetAccountsOrClientModelNameAndPercentsIterator.next();
                    targetAccountOrClientModelNamePercent.put(masterAccountConfigItTargetAccountsOrClientModelNameAndPercentsIt.get("name").textValue(), masterAccountConfigItTargetAccountsOrClientModelNameAndPercentsIt.get("percent").doubleValue());
                }
                Object[] object = new Object[4];
                object[0] = masterAccountConfigItBaseAccount;
                object[1] = targetAccountOrClientModelNamePercent;
                object[2] = masterAccountConfigItBalanceOperationType;
                object[3] = masterAccountConfigItExecutionPeriodInHours;
                objects.add(object);
            }
        } catch (IOException ex) {
            Logger.getLogger(BasicMasterAccountModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return objects;
    }

}
