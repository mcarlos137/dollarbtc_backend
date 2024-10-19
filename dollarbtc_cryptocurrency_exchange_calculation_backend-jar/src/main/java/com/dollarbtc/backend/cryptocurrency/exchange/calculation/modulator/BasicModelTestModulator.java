/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 *
 * @author CarlosDaniel
 */
public abstract class BasicModelTestModulator extends BasicModulator {

    protected String modelName;
    protected static Map<String, ExchangeAccount> exchangeAccountsByExchangeIdModelNameSymbol = new HashMap<>();

    public BasicModelTestModulator(String threadName) {
        super(threadName);
    }

    protected abstract void runModelTestModulator(
            String exchangeId,
            String symbol,
            int[] periodsDurationInSeconds,
            List<List<Object>> algorithmParams,
            double minimalAssetOrderAmount,
            String finalTimestamp,
            long testPastTimeInHours,
            double lastTradePriceSpread,
            int scanTimeInSeconds
    );

    @Override
    protected void runModulator() {
        File usersFolder = new File(OPERATOR_PATH, "Users");
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            File modelFolder = new File(userFolder, "Models");
            File modelTestsFolder = new File(modelFolder, "Test");
            if (!modelTestsFolder.isDirectory() || modelTestsFolder.listFiles().length == 0) {
                continue;
            }
            File oldModelTestFolder = modelTestsFolder.listFiles()[0];
            modelName = oldModelTestFolder.getName();
            FileUtil.moveFolderToFolder(oldModelTestFolder, modelFolder);
            File modelFile = new File(new File(modelFolder, modelName), "config.json");
            String finalTimestamp = DateUtil.getDate(modelName.split("____")[3]);
            long testPastTimeInHours = Long.parseLong(modelName.split("____")[4]);
            double lastTradePriceSpread = Double.parseDouble(modelName.split("____")[5]);
            int scanTimeInSeconds = Integer.parseInt(modelName.split("____")[6]);
            for (Object[] object : getModelJson(modelFile)) {
                runModelTestModulator(
                        (String) object[0],
                        (String) object[1],
                        (int[]) object[2],
                        (List<List<Object>>) object[3],
                        (double) object[4],
                        finalTimestamp,
                        testPastTimeInHours,
                        lastTradePriceSpread,
                        scanTimeInSeconds
                );
            }
            JsonNode model = null;
            try {
                model = new ObjectMapper().readTree(modelFile);
            } catch (IOException ex) {
                Logger.getLogger(BasicModelTestModulator.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(model == null){
                continue;
            }
            ((ObjectNode) model).put("testStatus", "FINISHED");
            FileUtil.editFile(model, modelFile);
            break;
        }
    }

    private List<Object[]> getModelJson(File modelFile) {
        List<Object[]> objects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode model = null;
        try {
            model = mapper.readTree(modelFile);
        } catch (IOException ex) {
            Logger.getLogger(BasicModelTestModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model == null) {
            return objects;
        }
        String modelN = model.get("name").textValue();
        if (!modelN.equals(modelName)) {
            return objects;
        }
        boolean modelActive = model.get("active").booleanValue();
        if (!modelActive) {
            return objects;
        }
        Iterator<JsonNode> modelSymbolRules = model.get("symbolRules").elements();
        while (modelSymbolRules.hasNext()) {
            JsonNode modelSymbolRule = modelSymbolRules.next();
            String modelSymbolRuleExchangeId = modelSymbolRule.get("exchangeId").textValue();
            String modelSymbolRuleSymbol = modelSymbolRule.get("symbol").textValue();
            boolean modelSymbolRuleActive = modelSymbolRule.get("active").booleanValue();
            if (!modelSymbolRuleActive) {
                continue;
            }
            String[] modelSymbolRulePeriodsDurationInSeconds = modelSymbolRule.get("periodsDurationInSeconds").textValue().split("__");
            double modelSymbolRuleMinimalAssetOrderAmount = modelSymbolRule.get("minimalAssetOrderAmount").doubleValue();
            Iterator<JsonNode> modelSymbolRuleAlgorithmRules = modelSymbolRule.get("algorithmRules").elements();
            List<List<Object>> algorithmParams = new ArrayList<>();
            while (modelSymbolRuleAlgorithmRules.hasNext()) {
                JsonNode modelSymbolRuleAlgorithmRule = modelSymbolRuleAlgorithmRules.next();
                List<Object> algorithmParam = new ArrayList<>();
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("name").textValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("tradingParams").textValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("inParams").textValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("outParams").textValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("useEarningCondition").booleanValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("earningConditionMinTransactionFactor").doubleValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("useNotEnoughBalanceCondition").booleanValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("notEnoughBalanceConditionInARowLimitToOrderQuantity").intValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("useOrderBlockingCondition").booleanValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("orderBlockingConditionMaxLossMaxQuantity").intValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("orderBlockingConditionMaxQuantity").intValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("orderBlockingConditionAction").textValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("useInPriceBandCondition").booleanValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("inPriceBandConditionUpPercent").doubleValue());
                algorithmParam.add(modelSymbolRuleAlgorithmRule.get("inPriceBandConditionDownPercent").doubleValue());
                algorithmParams.add(algorithmParam);
            }
            Object[] object = new Object[5];
            object[0] = modelSymbolRuleExchangeId;
            object[1] = modelSymbolRuleSymbol;
            object[2] = Arrays.asList(modelSymbolRulePeriodsDurationInSeconds).stream().mapToInt(Integer::parseInt).toArray();
            object[3] = algorithmParams;
            object[4] = modelSymbolRuleMinimalAssetOrderAmount;
            objects.add(object);
        }
        ((ObjectNode) model).put("testStatus", "STARTED");
        FileUtil.editFile(model, modelFile);
        return objects;
    }

}
