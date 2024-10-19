/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.SecundaryOperation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.StreamSupport;
import java.util.Arrays;

/**
 *
 * @author CarlosDaniel
 */
public abstract class BasicModelModulator extends BasicModulator {

    private static final Map<String, Date> lastTradeDateByExchangeIdSymbolModelName = new HashMap<>();
    protected static Map<String, ExchangeAccount> exchangeAccountsByExchangeIdModelNameSymbol = new HashMap<>();

    public BasicModelModulator(String threadName) {
        super(threadName);
    }

    protected abstract void runModelModulator(
            String exchangeId,
            String symbol,
            String modelName,
            int[] periodsDurationInSeconds,
            List<List<Object>> algorithmParams,
            double minimalAssetOrderAmount,
            String initialTimestamp, 
            boolean blockMarketStatus
    );

    @Override
    protected void runModulator() {
        File activeModelsThreadFolder = new File(new File(OPERATOR_PATH, "ActiveModels"), threadName);
        if (!activeModelsThreadFolder.isDirectory()) {
            return;
        }
        List<String> modelNames = new ArrayList<>();
        for (File activeModelsThreadFile : activeModelsThreadFolder.listFiles()) {
            if (!activeModelsThreadFile.isFile()) {
                continue;
            }
            String[] modelNameArray = activeModelsThreadFile.getName().split("____");
            String modelName;
            if(modelNameArray.length == 3){
                modelName = modelNameArray[1] + "____" + modelNameArray[2].replace(".json", "");
            } else {
                modelName = modelNameArray[1].replace(".json", "");
            }
            modelNames.add(modelName);
            System.out.println(modelName);
        }
        System.out.println("--------------- STARTING THREAD ---------------");
        for (String modelName : modelNames) {
            File modelFile = new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), modelName.split("__")[0]), "Models"), modelName), "config.json");
            if (!modelFile.isFile()) {
                System.out.println("modelFile does not exist: " + modelFile.getAbsolutePath());
                continue;
            }
            getModelJson(modelFile).stream().forEach((object) -> {
                runModelModulator(
                        (String) object[0],
                        (String) object[1],
                        modelName,
                        (int[]) object[2],
                        (List<List<Object>>) object[3],
                        (double) object[4],
                        (String) object[5],
                        (boolean) object[6]
                );
            });
        }
        System.out.println("--------------- FINISHING THREAD ---------------");
    }

    private List<Object[]> getModelJson(File modelFile) {
        List<Object[]> objects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode model = null;
        try {
            model = mapper.readTree(modelFile);
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model == null) {
            return objects;
        }
        String modelName = model.get("name").textValue();
        boolean modelActive = model.get("active").booleanValue();
        File userFile = new File(new File(new File(OPERATOR_PATH, "Users"), modelName.split("__")[0]), "config.json");
        JsonNode user = null;
        try {
            user = mapper.readTree(userFile);
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user == null) {
            return objects;
        }
        boolean userActive = user.get("active").booleanValue();
        String userType = null;
        if (user.has("type")) {
            userType = user.get("type").textValue();
        }
        String initialTimestamp;
        if (model.has("lastActivationInactivationTimestamp")) {
            initialTimestamp = model.get("lastActivationInactivationTimestamp").textValue();
        } else {
            initialTimestamp = model.get("copyTimestamp").textValue();
        }
        Iterator<JsonNode> modelSymbolRules = model.get("symbolRules").elements();
        while (modelSymbolRules.hasNext()) {
            JsonNode modelSymbolRule = modelSymbolRules.next();
            String modelSymbolRuleExchangeId = modelSymbolRule.get("exchangeId").textValue();
            String modelSymbolRuleSymbol = modelSymbolRule.get("symbol").textValue();
            if (!existNewTrade(modelSymbolRuleExchangeId, modelSymbolRuleSymbol, modelName)) {
                continue;
            }
            boolean blockMarketStatus = getBlockMarketStatus(modelSymbolRuleExchangeId, modelSymbolRuleSymbol);
            if (exchangeAccountsByExchangeIdModelNameSymbol.containsKey(modelSymbolRuleExchangeId + "__" + modelSymbolRuleSymbol + "__" + modelName)
                    && exchangeAccountsByExchangeIdModelNameSymbol.get(modelSymbolRuleExchangeId + "__" + modelSymbolRuleSymbol + "__" + modelName) == null
                    && blockMarketStatus) {
                changeToProtectionCurrency(modelName);
                continue;
            }
            if (exchangeAccountsByExchangeIdModelNameSymbol.containsKey(modelSymbolRuleExchangeId + "__" + modelSymbolRuleSymbol + "__" + modelName)
                    && exchangeAccountsByExchangeIdModelNameSymbol.get(modelSymbolRuleExchangeId + "__" + modelSymbolRuleSymbol + "__" + modelName) != null
                    && exchangeAccountsByExchangeIdModelNameSymbol.get(modelSymbolRuleExchangeId + "__" + modelSymbolRuleSymbol + "__" + modelName).getStartedTimestamp() == null
                    && blockMarketStatus) {
                changeToProtectionCurrency(modelName);
                continue;
            }
            boolean modelSymbolRuleActive = modelSymbolRule.get("active").booleanValue();
            boolean doNotEnter = false;
            if (modelActive) {
                boolean symbolMarketStatus = getActiveMarketStatus(modelSymbolRuleExchangeId, modelSymbolRuleSymbol);
                if (!symbolMarketStatus && !modelSymbolRuleActive) {
                    doNotEnter = true;
                } else if (!symbolMarketStatus && modelSymbolRuleActive) {
                    ((ObjectNode) modelSymbolRule).put("active", false);
                } else if (symbolMarketStatus && !modelSymbolRuleActive) {
                    ((ObjectNode) modelSymbolRule).put("active", true);
                }
            } else {
                if (!modelSymbolRuleActive) {
                    doNotEnter = true;
                } else {
                    ((ObjectNode) modelSymbolRule).put("active", false);
                }
            }
            if (doNotEnter) {
                changeToProtectionCurrency(modelName);
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
            Object[] object = new Object[7];
            object[0] = modelSymbolRuleExchangeId;
            object[1] = modelSymbolRuleSymbol;
            object[2] = Arrays.asList(modelSymbolRulePeriodsDurationInSeconds).stream().mapToInt(Integer::parseInt).toArray();
            object[3] = algorithmParams;
            object[4] = modelSymbolRuleMinimalAssetOrderAmount;
            object[5] = initialTimestamp;
            object[6] = blockMarketStatus;
            objects.add(object);
        }
        FileUtil.editFile(model, modelFile);
        return objects;
    }

    public static boolean existNewTrade(String exchangeId, String symbol, String modelName) {
        Date currentLastTradeDate = getLastTradeDate(exchangeId, symbol);
        if (currentLastTradeDate == null) {
            return false;
        }
        if (!lastTradeDateByExchangeIdSymbolModelName.containsKey(exchangeId + "__" + symbol + "__" + modelName) || !lastTradeDateByExchangeIdSymbolModelName.get(exchangeId + "__" + symbol + "__" + modelName).equals(currentLastTradeDate)) {
            lastTradeDateByExchangeIdSymbolModelName.put(exchangeId + "__" + symbol + "__" + modelName, currentLastTradeDate);
            return true;
        }
        return false;
    }

    private static Date getLastTradeDate(String exchangeId, String symbol) {
        File operationFolder = ExchangesFolderLocator.getExchangeSymbolTradesFolder(exchangeId, symbol);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(operationFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(o -> (!o.getFileName().toFile().getName().contains("websocket")))
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id1.compareTo(id2);
                    })
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                JsonNode tradeData = mapper.readTree(it.toFile());
                if (tradeData == null) {
                    continue;
                }
                if (tradeData.get("timestamp") == null) {
                    continue;
                }
                return DateUtil.parseDate(tradeData.get("timestamp").textValue());
            }
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static boolean getBlockMarketStatus(String exchangeId, String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        File marketManualRulesFile = new File(new File(OPERATOR_PATH, "MarketModulator"), "manualRules.json");
        try {
            JsonNode marketManualRules = mapper.readTree(marketManualRulesFile);
            boolean marketManualRulesBlockMarketsAll = marketManualRules.get("blockMarketsAll").booleanValue();
            if (marketManualRulesBlockMarketsAll) {
                System.out.println("blockMarketsAll " + exchangeId + " " + symbol);
                return true;
            }
            String marketManualRulesBlockMarketsBase = marketManualRules.get("blockMarketsBase").textValue();
            if (marketManualRulesBlockMarketsBase != null && !marketManualRulesBlockMarketsBase.equals("")) {
                for (String value : marketManualRulesBlockMarketsBase.split("__")) {
                    if (value.equals(AccountBase.getSymbolBase(symbol))) {
                        System.out.println("blockMarketsBase " + exchangeId + " " + symbol);
                        return true;
                    }
                }
            }
            String marketManualRulesBlockMarketsSpecific = marketManualRules.get("blockMarketsSpecific").textValue();
            if (marketManualRulesBlockMarketsSpecific != null && !marketManualRulesBlockMarketsSpecific.equals("")) {
                for (String value : marketManualRulesBlockMarketsSpecific.split("__")) {
                    if (value.equals(symbol)) {
                        System.out.println("blockMarketsSpecific " + exchangeId + " " + symbol);
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static boolean getActiveMarketStatus(String exchangeId, String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        File marketManualRulesFile = new File(new File(OPERATOR_PATH, "MarketModulator"), "manualRules.json");
        try {
            JsonNode marketManualRules = mapper.readTree(marketManualRulesFile);
            boolean marketManualRulesShutdownMarketsAll = marketManualRules.get("shutdownMarketsAll").booleanValue();
            if (marketManualRulesShutdownMarketsAll) {
                System.out.println("shutdownMarketsAll " + exchangeId + " " + symbol);
                return false;
            }
            String marketManualRulesShutdownMarketsBase = marketManualRules.get("shutdownMarketsBase").textValue();
            if (marketManualRulesShutdownMarketsBase != null && !marketManualRulesShutdownMarketsBase.equals("")) {
                for (String value : marketManualRulesShutdownMarketsBase.split("__")) {
                    if (value.equals(AccountBase.getSymbolBase(symbol))) {
                        System.out.println("shutdownMarketsBase " + exchangeId + " " + symbol);
                        return false;
                    }
                }
            }
            String marketManualRulesShutdownMarketsSpecific = marketManualRules.get("shutdownMarketsSpecific").textValue();
            if (marketManualRulesShutdownMarketsSpecific != null && !marketManualRulesShutdownMarketsSpecific.equals("")) {
                for (String value : marketManualRulesShutdownMarketsSpecific.split("__")) {
                    if (value.equals(symbol)) {
                        System.out.println("shutdownMarketsSpecific " + exchangeId + " " + symbol);
                        return false;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        File marketActiveSymbolsFile = new File(new File(OPERATOR_PATH, "MarketModulator"), "activeSymbols.json");
        if (!marketActiveSymbolsFile.isFile()) {
            return false;
        }
        try {
            JsonNode marketActiveSymbols = mapper.readTree(marketActiveSymbolsFile);
            Iterator<JsonNode> marketActiveSymbolsIterator = marketActiveSymbols.get("activeSymbols").elements();
            while (marketActiveSymbolsIterator.hasNext()) {
                JsonNode marketActiveSymbolsIt = marketActiveSymbolsIterator.next();
                String marketActiveSymbolsItExchangeId = marketActiveSymbolsIt.get("exchangeId").textValue();
                String marketActiveSymbolsItSymbol = marketActiveSymbolsIt.get("symbol").textValue();
                if (marketActiveSymbolsItExchangeId.equals(exchangeId) && marketActiveSymbolsItSymbol.equals(symbol)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    protected static int getPeriodsIntValue(String periods, String periodName, int position) {
        String[] ps = periods.split("____");
        for (String p : ps) {
            if (p.contains(periodName)) {
                return Integer.parseInt(p.split("__")[position]);
            }
        }
        return 0;
    }

    protected static double getPeriodsDoubleValue(String periods, String periodName, int position) {
        String[] ps = periods.split("____");
        for (String p : ps) {
            if (p.contains(periodName)) {
                return Double.parseDouble(p.split("__")[position]);
            }
        }
        return 0;
    }

    protected static void changeToProtectionCurrency(String modelName) {
        ObjectMapper mapper = new ObjectMapper();
        File marketManualRulesFile = new File(new File(OPERATOR_PATH, "MarketModulator"), "manualRules.json");
        JsonNode marketManualRules = null;
        try {
            marketManualRules = mapper.readTree(marketManualRulesFile);
        } catch (IOException ex) {
            Logger.getLogger(ExchangeAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
        String marketManualRulesProtectionCurrency = marketManualRules.get("protectionCurrency").textValue();
        if (marketManualRulesProtectionCurrency != null
                && !marketManualRulesProtectionCurrency.equals("")) {
            JsonNode modelBalanceAvailableAmounts = LocalData.getModelBalance(modelName, true, true, false).get("availableAmounts");
            Iterator<JsonNode> modelBalanceAvailableAmountsIterator = modelBalanceAvailableAmounts.elements();
            while (modelBalanceAvailableAmountsIterator.hasNext()) {
                JsonNode modelBalanceAvailableAmountsIt = modelBalanceAvailableAmountsIterator.next();
                String modelBalanceAvailableAmountsItCurrency = modelBalanceAvailableAmountsIt.get("currency").textValue();
                double modelBalanceAvailableAmountsItAmount = modelBalanceAvailableAmountsIt.get("amount").doubleValue();
                if (!modelBalanceAvailableAmountsItCurrency.equals(marketManualRulesProtectionCurrency) && modelBalanceAvailableAmountsItAmount > 0.02) {
                    SecundaryOperation.currencyChangeModel(modelName, modelBalanceAvailableAmountsItCurrency, marketManualRulesProtectionCurrency, BigDecimal.valueOf(modelBalanceAvailableAmountsItAmount));
                }
            }
        }
    }

//    private static void addNewParamsToModelConfig(JsonNode userModelConfig, File userModelConfigFile){
//        if(!userModelConfig.has("holdingPeriodInDays")){
//            ((ObjectNode) userModelConfig).put("holdingPeriodInDays", 90);
//        }
//        if(!userModelConfig.has("cutOffPeriodInDays")){
//            ((ObjectNode) userModelConfig).put("cutOffPeriodInDays", 30);
//        }
//        FileUtil.editFile(userModelConfig, userModelConfigFile);
//    }
}
