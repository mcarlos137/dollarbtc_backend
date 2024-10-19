/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelCopyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelGetDataResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelTestRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetNames;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserHasEnoughBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ActiveModelsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ModelsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class ModelOperation {

    public static String create(ModelCreateRequest modelCreateRequest) {
        if (!modelCreateRequest.getConfig().getName().contains("__")) {
            File modelFolder = ModelsFolderLocator.getFolder(modelCreateRequest.getConfig().getName());
            if (modelFolder.isDirectory()) {
                return "MODEL ALREADY EXIST";
            }
            modelFolder = FileUtil.createFolderIfNoExist(modelFolder);
            File modelFile = new File(modelFolder, "config.json");
            JsonNode model = modelCreateRequest.getConfig().toJsonNode();
            FileUtil.createFile(model, modelFile);
            writeComment(modelFolder, "created model " + modelCreateRequest.getConfig().getName());
        } else {
            return "MODEL WITH BAD FORMAT";
        }
        return "OK";
    }

    public static String copy(ModelCopyRequest modelCopyRequest) {
        File userModelsFolder = UsersFolderLocator.getModelsFolder(modelCopyRequest.getUserName());
        if (!userModelsFolder.isDirectory()) {
            return "USER DOES NOT EXIST";
        }
        String suffix = "";
        File userModelFolder = new File(userModelsFolder, modelCopyRequest.getUserName() + "__" + modelCopyRequest.getModelName());
        if (userModelFolder.isDirectory()) {
            int i = 1;
            while (true) {
                userModelFolder = new File(userModelsFolder, modelCopyRequest.getUserName() + "__" + modelCopyRequest.getModelName() + "____" + i);
                if (!userModelFolder.isDirectory()) {
                    suffix = "____" + i;
                    break;
                }
                i++;
            }
        }
        File modelFile = ModelsFolderLocator.getFile(modelCopyRequest.getModelName());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode model = null;
        try {
            model = mapper.readTree(modelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String currency : modelCopyRequest.getAmounts().keySet()) {
            String hasEnoughBalance = new UserHasEnoughBalance(modelCopyRequest.getUserName(), currency, modelCopyRequest.getAmounts().get(currency), modelCopyRequest.isIsMoneyClick()).getResponse();
            if (!hasEnoughBalance.equals("OK")) {
                return hasEnoughBalance;
            }
        }
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(modelCopyRequest.getUserName());
        if(modelCopyRequest.isIsMoneyClick()){
            userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(modelCopyRequest.getUserName());
        }
        for (String currency : modelCopyRequest.getAmounts().keySet()) {
            BaseOperation.substractToBalance(
                    userBalanceFolder,
                    currency,
                    modelCopyRequest.getAmounts().get(currency),
                    BalanceOperationType.MODEL_ACTIVATION,
                    BalanceOperationStatus.OK,
                    "USER MODEL NAME " + modelCopyRequest.getUserName() + "__" + modelCopyRequest.getModelName(),
                    null,
                    false,
                    null,
                    false,
                    null
            );
        }
        String name = modelCopyRequest.getUserName() + "__" + modelCopyRequest.getModelName() + suffix;
        ((ObjectNode) model).put("name", name);
        ((ObjectNode) model).put("isMoneyClick", modelCopyRequest.isIsMoneyClick());
        ((ObjectNode) model).set("initialAmounts", mapper.valueToTree(modelCopyRequest.getAmounts()));
        ((ObjectNode) model).put("copyTimestamp", DateUtil.getCurrentDate());
        userModelFolder = FileUtil.createFolderIfNoExist(userModelsFolder, name);
        FileUtil.createFile(model, new File(userModelFolder, "config.json"));
        File userModelBalanceFile = new File(userModelFolder, "balance.json");
        JsonNode userModelBalance = mapper.createObjectNode();
        ArrayNode availableAmountsArrayNode = mapper.createArrayNode();
        modelCopyRequest.getAmounts().keySet().stream().map((currency) -> {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("amount", modelCopyRequest.getAmounts().get(currency));
            objectNode.put("currency", currency);
            return objectNode;
        }).forEach((objectNode) -> {
            availableAmountsArrayNode.add(objectNode);
        });
        ((ObjectNode) userModelBalance).putArray("availableAmounts").addAll(availableAmountsArrayNode);
        ArrayNode reservedAmountsArrayNode = mapper.createArrayNode();
        ((ObjectNode) userModelBalance).putArray("reservedAmounts").addAll(reservedAmountsArrayNode);
        FileUtil.editFile(userModelBalance, userModelBalanceFile);
        writeComment(userModelFolder, "copied model " + modelCopyRequest.getModelName() + " to user " + modelCopyRequest.getUserName());
        firstTimeActivationModelProcess(name, model.get("initialAmounts"));
        return "OK";
    }

    public static String test(ModelTestRequest modelTestRequest,
            String currentTimestamp,
            int testPastTimeInHours,
            double lastTradePriceSpread,
            int scanTimeInSeconds) {
        String name = modelTestRequest.getConfig().toJsonNode().get("name").textValue() + "____test____" + DateUtil.getFileDate(null) + "____" + DateUtil.getFileDate(currentTimestamp) + "____" + testPastTimeInHours + "____" + lastTradePriceSpread + "____" + scanTimeInSeconds;
        modelTestRequest.getConfig().setName(name);
        File userModelsFolder = UsersFolderLocator.getModelsFolder(modelTestRequest.getConfig().getName());
        if (!userModelsFolder.exists()) {
            return "USER DOES NOT EXIST";
        }
        File userModelsTestFolder = FileUtil.createFolderIfNoExist(new File(userModelsFolder, "Test"));
        File userModelTestFolder = FileUtil.createFolderIfNoExist(userModelsTestFolder, name);
        FileUtil.editFile(modelTestRequest.getConfig().toJsonNode(), new File(userModelTestFolder, "config.json"));
        File modelBalanceFile = new File(userModelTestFolder, "balance.json");
        File baseTestAmountsFile = BaseFilesLocator.getBaseTestAmountsFile();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode baseTestAmounts = null;
        try {
            baseTestAmounts = mapper.readTree(baseTestAmountsFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtil.editFile(baseTestAmounts, modelBalanceFile);
        return name;
    }

    public static JsonNode getConfig(String modelName) {
        ObjectMapper mapper = new ObjectMapper();
        File modelFile;
        if (modelName.contains("__")) {
            File userModelsFolder = UsersFolderLocator.getModelsFolder(modelName);
            if (!userModelsFolder.isDirectory()) {
                return mapper.createObjectNode();
            }
            modelFile = new File(new File(userModelsFolder, modelName), "config.json");
            if (!modelFile.isFile()) {
                return mapper.createObjectNode();
            }
        } else {
            modelFile = ModelsFolderLocator.getFile(modelName);
        }
        if (!modelFile.isFile()) {
            return mapper.createObjectNode();
        }
        JsonNode model = null;
        try {
            model = mapper.readTree(modelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;
    }

    public static JsonNode list(String userName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode models = mapper.createObjectNode();
        ArrayNode modelsArrayNode = mapper.createArrayNode();
        File userModelsFolder = UsersFolderLocator.getModelsFolder(userName);
        if (!userModelsFolder.exists()) {
            return models;
        }
        for (File userModelFolder : userModelsFolder.listFiles()) {
            File userModelFile = new File(userModelFolder, "config.json");
            if (!userModelFile.isFile()) {
                continue;
            }
            JsonNode userModel = null;
            try {
                userModel = new ObjectMapper().readTree(userModelFile);
            } catch (IOException ex) {
                Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (userModel == null) {
                continue;
            }
            JsonNode listModel = mapper.createObjectNode();
            ((ObjectNode) listModel).put("name", userModel.get("name").textValue());
            String initialTimestamp;
            if (!userModel.get("active").booleanValue() && userModel.has("copyTimestamp")) {
                initialTimestamp = userModel.get("copyTimestamp").textValue();
            } else if (userModel.get("active").booleanValue() && userModel.has("lastActivationInactivationTimestamp")) {
                initialTimestamp = userModel.get("lastActivationInactivationTimestamp").textValue();
            } else if (userModel.get("active").booleanValue() && !userModel.has("lastActivationInactivationTimestamp") && userModel.has("copyTimestamp")) {
                initialTimestamp = userModel.get("copyTimestamp").textValue();
            } else {
                initialTimestamp = "";
            }
            ((ObjectNode) listModel).put("active", userModel.get("active").booleanValue());
            if (userModel.has("spanishName")) {
                ((ObjectNode) listModel).put("spanishName", userModel.get("spanishName").textValue());
            }
            if (userModel.has("englishName")) {
                ((ObjectNode) listModel).put("englishName", userModel.get("englishName").textValue());
            }
            if (userModel.has("type")) {
                ((ObjectNode) listModel).put("type", userModel.get("type").textValue());
            }
            if (userModel.has("description")) {
                ((ObjectNode) listModel).put("description", userModel.get("description").textValue());
            }
            ((ObjectNode) listModel).put("initialTimestamp", initialTimestamp);
            int holdingPeriodInDays = userModel.get("holdingPeriodInDays").intValue();
            if (initialTimestamp.equals("")) {
                ((ObjectNode) listModel).put("finalTimestamp", "");
            } else {
                ((ObjectNode) listModel).put("finalTimestamp", DateUtil.getDateDaysAfter(initialTimestamp, holdingPeriodInDays));
            }
            Map<String, Double> initialAmountsMap = new HashMap<>();
            Iterator<String> jsonNodeIterator = userModel.get("initialAmounts").fieldNames();
            ArrayNode initialAmounts = mapper.createArrayNode();
            while (jsonNodeIterator.hasNext()) {
                String jsonNodeIt = jsonNodeIterator.next();
                JsonNode initialAmount = mapper.createObjectNode();
                ((ObjectNode) initialAmount).put("currency", jsonNodeIt);
                ((ObjectNode) initialAmount).put("amount", userModel.get("initialAmounts").get(jsonNodeIt).doubleValue());
                initialAmounts.add(initialAmount);
                if (!initialAmountsMap.containsKey(jsonNodeIt)) {
                    initialAmountsMap.put(jsonNodeIt, 0.0);
                }
                initialAmountsMap.put(jsonNodeIt, initialAmountsMap.get(jsonNodeIt) + userModel.get("initialAmounts").get(jsonNodeIt).doubleValue());
            }
            ((ObjectNode) listModel).putArray("initialAmounts").addAll(initialAmounts);
            if (userModel.has("createdByUser")) {
                ((ObjectNode) listModel).put("createdByUser", userModel.get("createdByUser").booleanValue());
            }
            File userModelAdditionalMovementsFile = new File(userModelFolder, "additionalMovements.json");
            if (userModelAdditionalMovementsFile.isFile()) {
                try {
                    JsonNode userModelAdditionalMovements = new ObjectMapper().readTree(userModelAdditionalMovementsFile);
                    ((ObjectNode) listModel).set("additionalMovements", userModelAdditionalMovements);
                } catch (IOException ex) {
                    Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            File userModelBalanceFile = new File(userModelFolder, "balance.json");
            if (userModelBalanceFile.isFile()) {
                try {
                    JsonNode userModelBalance = new ObjectMapper().readTree(userModelBalanceFile);
                    ((ObjectNode) listModel).set("balance", userModelBalance);
                } catch (IOException ex) {
                    Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Map<String, Double> additionalMovementsMap = new HashMap<>();
            if (listModel.get("balance").has("timestamp")) {
                Map<String, Double> currentBalanceMap = new HashMap<>();
                Iterator<JsonNode> balanceAvailableAmountsIterator = listModel.get("balance").get("availableAmounts").iterator();
                while (balanceAvailableAmountsIterator.hasNext()) {
                    JsonNode balanceAvailableAmountsIt = balanceAvailableAmountsIterator.next();
                    String currency = balanceAvailableAmountsIt.get("currency").textValue();
                    Double amount = balanceAvailableAmountsIt.get("amount").doubleValue();
                    if (!currentBalanceMap.containsKey(currency)) {
                        currentBalanceMap.put(currency, 0.0);
                    }
                    currentBalanceMap.put(currency, currentBalanceMap.get(currency) + amount);
                }
                String balanceTimestamp = listModel.get("balance").get("timestamp").textValue();
                Iterator<String> additionalMovementsFieldNamesIterator = listModel.get("additionalMovements").fieldNames();
                while (additionalMovementsFieldNamesIterator.hasNext()) {
                    String additionalMovementsFieldNamesIt = additionalMovementsFieldNamesIterator.next();
                    Iterator<JsonNode> currentAdditionalMovementsFieldNamesIterator = listModel.get("additionalMovements").get(additionalMovementsFieldNamesIt).iterator();
                    while (currentAdditionalMovementsFieldNamesIterator.hasNext()) {
                        JsonNode currentAdditionalMovementsFieldNamesIt = currentAdditionalMovementsFieldNamesIterator.next();
                        String currency = currentAdditionalMovementsFieldNamesIt.get("currency").textValue();
                        Double amount = currentAdditionalMovementsFieldNamesIt.get("amount").doubleValue();
                        if (!currentBalanceMap.containsKey(currency)) {
                            currentBalanceMap.put(currency, 0.0);
                        }
                        if (additionalMovementsFieldNamesIt.compareTo(balanceTimestamp) >= 0) {
                            currentBalanceMap.put(currency, currentBalanceMap.get(currency) + amount);
                        }
                        if (!additionalMovementsMap.containsKey(currency)) {
                            additionalMovementsMap.put(currency, 0.0);
                        }
                        additionalMovementsMap.put(currency, additionalMovementsMap.get(currency) + amount);
                    }
                }
                ArrayNode currentBalance = mapper.createArrayNode();
                for (String currency : currentBalanceMap.keySet()) {
                    JsonNode currentB = mapper.createObjectNode();
                    ((ObjectNode) currentB).put("currency", currency);
                    ((ObjectNode) currentB).put("amount", currentBalanceMap.get(currency));
                    currentBalance.add(currentB);
                }
                ((ObjectNode) listModel).putArray("currentBalance").addAll(currentBalance);
            }
            long currentHoldingPeriodInDays = TimeUnit.DAYS.convert(DateUtil.parseDate(DateUtil.getCurrentDate()).getTime() - DateUtil.parseDate(initialTimestamp).getTime(), TimeUnit.MILLISECONDS);
            Map<String, Double> yieldsMap = new HashMap<>();
            Map<String, Double> projectedYieldsMap = new HashMap<>();
            for (String currency : initialAmountsMap.keySet()) {
                Double initialAmount = initialAmountsMap.get(currency);
                Double finalAmount = initialAmount;
                if(additionalMovementsMap.containsKey(currency)){
                    finalAmount = finalAmount + additionalMovementsMap.get(currency);
                }
                Double yield = (finalAmount - initialAmount) / initialAmount;
                yieldsMap.put(currency, yield);
                if(currentHoldingPeriodInDays == 0){
                    continue;
                }
                Double projectedYield = yield * holdingPeriodInDays / currentHoldingPeriodInDays;
                projectedYieldsMap.put(currency, projectedYield);
            }
            ArrayNode yields = mapper.createArrayNode();
            for (String currency : yieldsMap.keySet()) {
                JsonNode yield = mapper.createObjectNode();
                ((ObjectNode) yield).put("currency", currency);
                ((ObjectNode) yield).put("amount", yieldsMap.get(currency));
                yields.add(yield);
            }
            ((ObjectNode) listModel).putArray("yields").addAll(yields);
            ArrayNode  projectedYields = mapper.createArrayNode();
            for (String currency : projectedYieldsMap.keySet()) {
                JsonNode projectedYield = mapper.createObjectNode();
                ((ObjectNode) projectedYield).put("currency", currency);
                ((ObjectNode) projectedYield).put("amount", projectedYieldsMap.get(currency));
                 projectedYields.add(projectedYield);
            }
            ((ObjectNode) listModel).putArray("projectedYields").addAll( projectedYields);
            modelsArrayNode.add(listModel);
        }
        ((ObjectNode) models).putArray("models").addAll(modelsArrayNode);
        return models;
    }

    public static ArrayNode getInitialAmounts(String userName) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> iAmounts = new HashMap<>();
        ArrayNode initialAmounts = mapper.createArrayNode();
        JsonNode list = list(userName);
        if (list.has("models")) {
            Iterator<JsonNode> modelsIterator = list.get("models").iterator();
            while (modelsIterator.hasNext()) {
                JsonNode modelsIt = modelsIterator.next();
                Iterator<JsonNode> modelsItInitialAmountsIterator = modelsIt.get("initialAmounts").iterator();
                while (modelsItInitialAmountsIterator.hasNext()) {
                    JsonNode modelsItInitialAmountsIt = modelsItInitialAmountsIterator.next();
                    String currency = modelsItInitialAmountsIt.get("currency").textValue();
                    Double amount = modelsItInitialAmountsIt.get("amount").doubleValue();
                    if (!iAmounts.containsKey(currency)) {
                        iAmounts.put(currency, 0.0);
                    }
                    iAmounts.put(currency, iAmounts.get(currency) + amount);
                }
            }
        }
        for (String currency : iAmounts.keySet()) {
            JsonNode initialAmount = mapper.createObjectNode();
            ((ObjectNode) initialAmount).put("currency", currency);
            ((ObjectNode) initialAmount).put("amount", iAmounts.get(currency));
            initialAmounts.add(initialAmount);
        }
        return initialAmounts;
    }

    public static JsonNode listAvailables(String userName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode availableModels = mapper.createObjectNode();
        File userFile = UsersFolderLocator.getConfigFile(userName);
        JsonNode user = null;
        try {
            user = new ObjectMapper().readTree(userFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user == null) {
            return availableModels;
        }
        UserType userType = UserType.valueOf(user.get("type").textValue());
        UserEnvironment userEnvironment = UserEnvironment.valueOf(user.get("environment").textValue());
        ArrayNode availableModelsArrayNode = mapper.createArrayNode();
        File modelsFolder = new File(ModelsFolderLocator.getFolder(), userType.name());
        switch (userType) {
            case PRO_TRADER:
                for (File modelsExchangeIdFolder : modelsFolder.listFiles()) {
                    if (!modelsExchangeIdFolder.isDirectory()) {
                        continue;
                    }
                    if (userEnvironment.equals(UserEnvironment.PRODUCTION) && !user.has(modelsExchangeIdFolder.getName() + "_loginAccount")) {
                        continue;
                    }
                    for (File modelExchangeIdFolder : modelsExchangeIdFolder.listFiles()) {
                        if (!modelExchangeIdFolder.isDirectory()) {
                            continue;
                        }
                        File modelFile = new File(modelExchangeIdFolder, "config.json");
                        if (!modelFile.exists() || !modelFile.isFile()) {
                            continue;
                        }
                        JsonNode model = null;
                        try {
                            model = new ObjectMapper().readTree(modelFile);
                        } catch (IOException ex) {
                            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        JsonNode listModel = mapper.createObjectNode();
                        ((ObjectNode) listModel).put("name", model.get("name").textValue());
                        ((ObjectNode) listModel).put("active", model.get("active").booleanValue());
                        ((ObjectNode) listModel).put("type", model.get("type").textValue());
                        ((ObjectNode) listModel).set("minimalAmounts", model.get("minimalAmounts"));
                        ((ObjectNode) listModel).set("optimalAmounts", model.get("optimalAmounts"));
                        ArrayNode modelSymbolsArrayNode = mapper.createArrayNode();
                        Iterator<JsonNode> modelSymbolRules = model.get("symbolRules").elements();
                        while (modelSymbolRules.hasNext()) {
                            JsonNode modelSymbolRule = modelSymbolRules.next();
                            JsonNode modelSymbol = mapper.createObjectNode();
                            ((ObjectNode) modelSymbol).put("exchangeId", modelSymbolRule.get("exchangeId").textValue());
                            ((ObjectNode) modelSymbol).put("symbol", modelSymbolRule.get("symbol").textValue());
                            ((ObjectNode) modelSymbol).put("active", modelSymbolRule.get("active").booleanValue());
                            ((ObjectNode) modelSymbol).put("startBasePercent", modelSymbolRule.get("startBasePercent").doubleValue());
                            ((ObjectNode) modelSymbol).put("baseCurrency", AccountBase.getSymbolBase(modelSymbolRule.get("symbol").textValue()));
                            modelSymbolsArrayNode.add(modelSymbol);
                        }
                        ((ObjectNode) listModel).putArray("modelSymbols").addAll(modelSymbolsArrayNode);
                        availableModelsArrayNode.add(listModel);
                    }
                }
                break;
            case NORMAL:
                Map<Integer, JsonNode> orderedModels = new TreeMap<>();
                for (File modelFile : modelsFolder.listFiles()) {
                    try {
                        JsonNode model = mapper.readTree(new File(modelFile, "config.json"));
                        boolean active = model.get("active").booleanValue();
                        if (!active) {
                            continue;
                        }
                        orderedModels.put(model.get("position").intValue(), model);
                    } catch (IOException ex) {
                        Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                for (Integer position : orderedModels.keySet()) {
                    JsonNode model = orderedModels.get(position);
                    availableModelsArrayNode.add(model);
                }
                break;
            default:
                break;
        }
        ((ObjectNode) availableModels).putArray("availableModels").addAll(availableModelsArrayNode);
        return availableModels;
    }

    public static ArrayNode getInvestedAmounts(String modelName, UserProfile userProfile) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode investedAmounts = mapper.createArrayNode();
        Set<String> userNames = new UserGetNames(userProfile).getResponse();
        Map<String, Double> investedAmountsMap = new HashMap<>();
        for (String userName : userNames) {
            int i = 1;
            boolean firstLoop = true;
            while (true) {
                String userModelName;
                if (firstLoop) {
                    userModelName = userName + "__" + modelName;
                    firstLoop = false;
                } else {
                    userModelName = userName + "__" + modelName + "____" + i;
                    i++;
                }
                File userModelFile = UsersFolderLocator.getModelFile(userModelName);
                if (!userModelFile.isFile()) {
                    break;
                }
                try {
                    JsonNode userModelInitialAmounts = mapper.readTree(userModelFile).get("initialAmounts");
                    Iterator<String> userModelInitialAmountsFieldNames = userModelInitialAmounts.fieldNames();
                    while (userModelInitialAmountsFieldNames.hasNext()) {
                        String userModelInitialAmountsFieldName = userModelInitialAmountsFieldNames.next();
                        if (!investedAmountsMap.containsKey(userModelInitialAmountsFieldName)) {
                            investedAmountsMap.put(userModelInitialAmountsFieldName, 0.0);
                        }
                        investedAmountsMap.put(userModelInitialAmountsFieldName, investedAmountsMap.get(userModelInitialAmountsFieldName) + userModelInitialAmounts.get(userModelInitialAmountsFieldName).doubleValue());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        investedAmountsMap.keySet().stream().map((currency) -> {
            JsonNode investedAmount = mapper.createObjectNode();
            ((ObjectNode) investedAmount).put("currency", currency);
            ((ObjectNode) investedAmount).put("amount", investedAmountsMap.get(currency));
            return investedAmount;
        }).forEach((investedAmount) -> {
            investedAmounts.add(investedAmount);
        });
        return investedAmounts;
    }

    public static void changeAmounts(String userModelName, Map<String, Double> initialAmounts) {
        File userModelFile = UsersFolderLocator.getModelFile(userModelName);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode userModel = mapper.readTree(userModelFile);
            JsonNode userModelInitialAmounts = mapper.createObjectNode();
            initialAmounts.keySet().stream().forEach((key) -> {
                ((ObjectNode) userModelInitialAmounts).put(key, initialAmounts.get(key));
            });
            ((ObjectNode) userModel).set("initialAmounts", userModelInitialAmounts);
            FileUtil.editFile(userModel, userModelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getTestStatus(String modelName) {
        File userModelsFolder = UsersFolderLocator.getModelsFolder(modelName);
        if (!userModelsFolder.isDirectory()) {
            return "USER DOES NOT EXIST";
        }
        File preUserModelFolder = new File(new File(userModelsFolder, "Test"), modelName);
        if (preUserModelFolder.isDirectory()) {
            return "WAITING TO START";
        }
        File userModelFolder = new File(userModelsFolder, modelName);
        if (!userModelFolder.isDirectory()) {
            return "USERMODEL DOES NOT EXIST";
        }
        if (!userModelFolder.getName().contains("test")) {
            return "USERMODEL IS NOT A TEST";
        }
        File userModelFile = new File(userModelFolder, "config.json");
        JsonNode userModel = null;
        try {
            userModel = new ObjectMapper().readTree(userModelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userModel == null) {
            return "USERMODEL JSON IS MALFORMED";
        }
        long currentTime = DateUtil.parseDate(DateUtil.getCurrentDate()).getTime();
        long writeTime = DateUtil.parseDate(DateUtil.getDate(userModelFolder.getName().split("____")[2])).getTime();
        long diffInMinutes = (currentTime - writeTime) / 60000;
        if (userModel.get("testStatus").textValue().equals("STARTED") && diffInMinutes >= 60) {
            ((ObjectNode) userModel).put("testStatus", "FAILED");
            FileUtil.editFile(userModel, userModelFile);
            return "FAILED";
        }
        return userModel.get("testStatus").textValue();
    }

    public static String process(String userModelName, ProcessType processType) {
        File userModelsFolder = UsersFolderLocator.getModelsFolder(userModelName);
        if (!userModelsFolder.exists()) {
            return "USER DOES NOT EXIST";
        }
        File userModelFolder = new File(userModelsFolder, userModelName);
        if (!userModelFolder.isDirectory()) {
            return "USERMODEL DOES NOT EXIST";
        }
        File userModelFile = new File(userModelFolder, "config.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userModel = null;
        boolean isMoneyClick = false;
        try {
            userModel = mapper.readTree(userModelFile);
            if(userModel.has("isMoneyClick")){
                isMoneyClick = userModel.get("isMoneyClick").booleanValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userModel == null) {
            return "USERMODEL DOES NOT EXIST";
        }
        switch (processType) {
            case ACTIVATE:
                if (isActive(userModelName, true)) {
                    return "USERMODEL IS ALREADY ACTIVE";
                }
                Map<String, Double> userModelInitialAmounts = mapper.convertValue(userModel.get("initialAmounts"), Map.class);
                for (String currency : userModelInitialAmounts.keySet()) {
                    String hasEnoughBalance = new UserHasEnoughBalance(userModelName.split("__")[0], currency, userModelInitialAmounts.get(currency), isMoneyClick).getResponse();
                    if (!hasEnoughBalance.equals("OK")) {
                        return hasEnoughBalance;
                    }
                }
                File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userModelName.split("__")[0]);
                if(isMoneyClick){
                    userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userModelName.split("__")[0]);
                }
                for (String currency : userModelInitialAmounts.keySet()) {
                    BaseOperation.substractToBalance(
                            userBalanceFolder,
                            currency,
                            userModelInitialAmounts.get(currency),
                            BalanceOperationType.MODEL_ACTIVATION,
                            BalanceOperationStatus.OK,
                            "USER MODEL NAME " + userModelName,
                            null,
                            false,
                            null,
                            false,
                            null
                    );
                }
                File userModelBalanceFile = UsersFolderLocator.getModelBalanceFile(userModelName);
                try {
                    JsonNode userModelBalance = mapper.readTree(userModelBalanceFile);
                    Iterator<JsonNode> userModelBalanceAvailableAmounts = userModelBalance.get("availableAmounts").elements();
                    while (userModelBalanceAvailableAmounts.hasNext()) {
                        JsonNode userModelBalanceAvailableAmount = userModelBalanceAvailableAmounts.next();
                        Double amount = userModelInitialAmounts.get(userModelBalanceAvailableAmount.get("currency").textValue());
                        if (amount == null) {
                            amount = 0.0;
                        }
                        ((ObjectNode) userModelBalanceAvailableAmount).put("amount", amount);
                    }
                    FileUtil.editFile(userModelBalance, userModelBalanceFile);
                    ((ObjectNode) userModel).put("active", true);
                    ((ObjectNode) userModel).put("lastActivationInactivationTimestamp", DateUtil.getCurrentDate());
                    FileUtil.editFile(userModel, userModelFile);
                    writeComment(userModelFolder, "activating model " + userModelName);
                } catch (IOException ex) {
                    Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case INACTIVATE:
                if (!isActive(userModelName, true)) {
                    return "USERMODEL IS ALREADY INACTIVE";
                }
                ((ObjectNode) userModel).put("active", false);
                FileUtil.editFile(userModel, userModelFile);
                writeComment(userModelFolder, "inactivating model " + userModelName);
                File userModelAdditionalMovementsFile = getUserModelAdditionalMovementsFile(userModelName);
                if (userModelAdditionalMovementsFile.isFile()) {
                    FileUtil.editFile(mapper.createObjectNode(), userModelAdditionalMovementsFile);
                }
                break;
        }
        return "OK";
    }

    public static void inactivate(String modelName, String exchangeId, String symbol) {
        File userModelFile = UsersFolderLocator.getModelFile(modelName);
        if (!userModelFile.isFile()) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userModel = null;
        try {
            userModel = mapper.readTree(userModelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userModel.get("active").booleanValue() || exchangeId == null || exchangeId.equals("") || symbol == null || symbol.equals("")) {
            return;
        }
        Iterator<JsonNode> modelSymbolRules = userModel.get("symbolRules").elements();
        while (modelSymbolRules.hasNext()) {
            JsonNode modelSymbolRule = modelSymbolRules.next();
            String modelSymbolRuleExchangeId = modelSymbolRule.get("exchangeId").textValue();
            String modelSymbolRuleSymbol = modelSymbolRule.get("symbol").textValue();
            if (exchangeId.equals(modelSymbolRuleExchangeId) && symbol.equals(modelSymbolRuleSymbol)) {
                ((ObjectNode) modelSymbolRule).put("active", false);
                FileUtil.editFile(userModel, userModelFile);
                break;
            }
        }
    }

    public static boolean isActive(String userModelName, boolean absolute) {
        File userModelFile = UsersFolderLocator.getModelFile(userModelName);
        if (!userModelFile.isFile()) {
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userModel = null;
        try {
            userModel = mapper.readTree(userModelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean active = userModel.get("active").booleanValue();
        if (absolute) {
            if (active) {
                return true;
            }
            Iterator<JsonNode> userModelSymbolRules = userModel.get("symbolRules").elements();
            while (userModelSymbolRules.hasNext()) {
                JsonNode userModelSymbolRule = userModelSymbolRules.next();
                if (userModelSymbolRule.get("active").booleanValue()) {
                    return true;
                }
            }
            return false;
        } else {
            return active;
        }
    }

    public static boolean isActive(String userModelName, String exchangeId, String symbol) {
        File userModelFile = UsersFolderLocator.getModelFile(userModelName);
        if (!userModelFile.isFile()) {
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userModel = null;
        try {
            userModel = mapper.readTree(userModelFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator<JsonNode> userModelSymbolRules = userModel.get("symbolRules").elements();
        while (userModelSymbolRules.hasNext()) {
            JsonNode userModelSymbolRule = userModelSymbolRules.next();
            if (exchangeId.equals(userModelSymbolRule.get("exchangeId").textValue())
                    && symbol.equals(userModelSymbolRule.get("symbol").textValue())) {
                return userModelSymbolRule.get("active").booleanValue();
            }
        }
        return false;
    }

    public static String sendToFreeNotifications(String userModelName, String exchangeId, String symbol, String name, String email, String phone) {
        File userModelFolder = UsersFolderLocator.getModelFolder(userModelName);
        if (!userModelFolder.isDirectory()) {
            return "USER MODEL DOES NOT EXIST";
        }
        String exchangeIdSymbol = exchangeId + "__" + symbol;
        File userModelExchangeIdSymbolFolder = new File(userModelFolder, exchangeIdSymbol);
        if (!userModelExchangeIdSymbolFolder.isDirectory()) {
            return "USER MODEL EXCHANGEID AND SYMBOL DOES NOT EXIST";
        }
        File userModelExchangeIdSymbolFreeNotificationsFolder = FileUtil.createFolderIfNoExist(userModelExchangeIdSymbolFolder, "FreeNotifications");
        JsonNode userModelExchangeIdSymbolFreeNotification = new ObjectMapper().createObjectNode();
        String timestamp = DateUtil.getCurrentDate();
        ((ObjectNode) userModelExchangeIdSymbolFreeNotification).put("timestamp", timestamp);
        ((ObjectNode) userModelExchangeIdSymbolFreeNotification).put("name", name);
        ((ObjectNode) userModelExchangeIdSymbolFreeNotification).put("email", email);
        ((ObjectNode) userModelExchangeIdSymbolFreeNotification).put("phone", phone);
        FileUtil.createFile(userModelExchangeIdSymbolFreeNotification, new File(userModelExchangeIdSymbolFreeNotificationsFolder, email + ".json"));
        return "OK";
    }

    public static void returnAmountsToUser(String userModelName, String reason) {
        ObjectMapper mapper = new ObjectMapper();
        File userModelFile = UsersFolderLocator.getModelFile(userModelName);
        JsonNode userModel = null;
        boolean isMoneyClick = false;
        try {
            userModel = mapper.readTree(userModelFile);
            if(userModel.has("isMoneyClick")){
                isMoneyClick = userModel.get("isMoneyClick").booleanValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userModel == null) {
            return;
        }
        JsonNode currentUserModelBalance = LocalData.getModelBalance(userModelName, false, true, isMoneyClick);
        File userModelBalanceFile = UsersFolderLocator.getModelBalanceFile(userModelName);
        JsonNode userModelBalance = null;
        try {
            userModelBalance = mapper.readTree(userModelBalanceFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userModelBalance == null) {
            return;
        }
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userModelName.split("__")[0]);
        if(isMoneyClick){
            userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userModelName.split("__")[0]);
        }
        String[] balanceTypes = new String[]{"availableAmounts"};
        for (String balanceType : balanceTypes) {
            Iterator<JsonNode> currentUserModelBalanceAmounts = currentUserModelBalance.get(balanceType).elements();
            while (currentUserModelBalanceAmounts.hasNext()) {
                JsonNode currentUserModelBalanceAmount = currentUserModelBalanceAmounts.next();
                if (currentUserModelBalanceAmount.get("amount").doubleValue() > 0) {
                    BaseOperation.addToBalance(
                            userBalanceFolder,
                            currentUserModelBalanceAmount.get("currency").textValue(),
                            currentUserModelBalanceAmount.get("amount").doubleValue(),
                            BalanceOperationType.MODEL_INACTIVATION,
                            BalanceOperationStatus.OK,
                            "USER MODEL NAME " + userModelName,
                            null,
                            null,
                            false,
                            null
                    );
                }
            }
            Iterator<JsonNode> userModelBalanceAmounts = userModelBalance.get(balanceType).elements();
            while (userModelBalanceAmounts.hasNext()) {
                JsonNode userModelBalanceAmount = userModelBalanceAmounts.next();
                ((ObjectNode) userModelBalanceAmount).put("amount", 0.0);
            }
        }
        FileUtil.editFile(userModelBalance, userModelBalanceFile);
        ((ObjectNode) userModel).put("lastActivationInactivationTimestamp", DateUtil.getCurrentDate());
        FileUtil.editFile(userModel, userModelFile);
    }

    public static List<String> getComments(String userModelName) {
        return FileUtil.readFile(UsersFolderLocator.getModelCommentsFile(userModelName), Charset.defaultCharset());
    }

    public static JsonNode getInAlgorithmsInfo(String userModelName, String exchangeId, String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode inAlgorithmsInfo = mapper.createObjectNode();
        ArrayNode inAlgorithmsInfosNode = mapper.createArrayNode();
        File inAlgorithmsFolder = new File(new File(UsersFolderLocator.getModelFolder(userModelName), exchangeId + "__" + symbol), "inAlgorithmsInfo");
        System.out.println("inAlgorithmsFolder: " + inAlgorithmsFolder);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inAlgorithmsFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id1.compareTo(id2);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                JsonNode inAlgorithms = mapper.readTree(it.toFile());
                inAlgorithmsInfosNode.add(inAlgorithms);
            }
        } catch (IOException ex) {
        }
        System.out.println("inAlgorithmsInfosNode.size(): " + inAlgorithmsInfosNode.size());
        ((ObjectNode) inAlgorithmsInfo).putArray("inAlgorithmsInfo").addAll(inAlgorithmsInfosNode);
        return inAlgorithmsInfo;
    }

    public static String modifyDescription(String userModelName, String description) {
        File userModelFile = UsersFolderLocator.getModelFile(userModelName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userModel;
        try {
            userModel = mapper.readTree(userModelFile);
            ((ObjectNode) userModel).put("description", description.replace("__", " "));
            FileUtil.editFile(userModel, userModelFile);
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }

    public static ModelGetDataResponse getData(String modelName) {
        File modelDataFile = ModelsFolderLocator.getDataFile(modelName);
        System.out.println("modelDataFile: " + modelDataFile.getAbsolutePath());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode modelData = null;
        try {
            modelData = mapper.readTree(modelDataFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (modelData == null) {
            return new ModelGetDataResponse(null, null, null);
        }
        String modelDataTitle = modelData.get("title").textValue();
        String modelDataModelTestName = modelData.get("modelTestName").textValue();
        String modelDataStartTestTimestamp = modelData.get("startTestTimestamp").textValue();
        String modelDataEndTestTimestamp = modelData.get("endTestTimestamp").textValue();
        ModelGetDataResponse modelGetDataResponse = new ModelGetDataResponse(
                modelDataTitle,
                modelDataStartTestTimestamp,
                modelDataEndTestTimestamp
        );
        modelGetDataResponse.setBalancePercent(modelData.get("balancePercent").doubleValue());
        File modelDataModelTestDataFile = UsersFolderLocator.getModelFile(modelDataModelTestName);
        JsonNode modelDataModelTestData = null;
        try {
            modelDataModelTestData = mapper.readTree(modelDataModelTestDataFile);
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (modelDataModelTestData == null) {
            return new ModelGetDataResponse(null, null, null);
        }
        List<String[]> modelDataModelTestDataExchangeIdSymbols = new ArrayList<>();
        Iterator<JsonNode> modelDataModelTestDataSymbolRules = modelDataModelTestData.get("symbolRules").elements();
        while (modelDataModelTestDataSymbolRules.hasNext()) {
            JsonNode modelDataModelTestDataSymbolRule = modelDataModelTestDataSymbolRules.next();
            if (!modelDataModelTestDataSymbolRule.get("active").booleanValue()) {
                continue;
            }
            modelDataModelTestDataExchangeIdSymbols.add(new String[]{modelDataModelTestDataSymbolRule.get("exchangeId").textValue(), modelDataModelTestDataSymbolRule.get("symbol").textValue()});
        }
        for (String[] modelDataModelTestDataExchangeIdSymbol : modelDataModelTestDataExchangeIdSymbols) {
            ModelGetDataResponse.ExchangeIdSymbolData exchangeIdSymbolData = new ModelGetDataResponse.ExchangeIdSymbolData(
                    modelDataModelTestDataExchangeIdSymbol[0],
                    modelDataModelTestDataExchangeIdSymbol[1],
                    OrderOperation.getOrders(modelDataModelTestDataExchangeIdSymbol[0], modelDataModelTestDataExchangeIdSymbol[1], modelDataModelTestName, modelDataStartTestTimestamp, modelDataEndTestTimestamp, CollectionOrderByDate.ASC),
                    OrderOperation.getOrderIntervals(modelDataModelTestDataExchangeIdSymbol[0], modelDataModelTestDataExchangeIdSymbol[1], modelDataModelTestName, modelDataStartTestTimestamp, modelDataEndTestTimestamp, CollectionOrderByDate.ASC)
            );
            modelGetDataResponse.getExchangeIdSymbolDatas().add(exchangeIdSymbolData);
        }
        return modelGetDataResponse;
    }

    public static String moveReserveToUser(String userModelName) {
        ObjectMapper mapper = new ObjectMapper();
        File userModelFile = UsersFolderLocator.getModelFile(userModelName);
        JsonNode userModel = null;
        boolean isMoneyClick = false;
        try {
            userModel = mapper.readTree(userModelFile);
            if(userModel.has("isMoneyClick")){
                isMoneyClick = userModel.get("isMoneyClick").booleanValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userModelName.split("__")[0]);
        if(isMoneyClick){
            userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userModelName.split("__")[0]);
        }
        JsonNode userModelBalance = LocalData.getModelBalance(userModelName, false, true, isMoneyClick);
        Iterator<JsonNode> userModelBalanceReservedAmounts = userModelBalance.get("reservedAmounts").elements();
        while (userModelBalanceReservedAmounts.hasNext()) {
            JsonNode userModelBalanceReservedAmount = userModelBalanceReservedAmounts.next();
            if (userModelBalanceReservedAmount.get("amount").doubleValue() > 0) {
                BaseOperation.addToBalance(
                        userBalanceFolder,
                        userModelBalanceReservedAmount.get("currency").textValue(),
                        userModelBalanceReservedAmount.get("amount").doubleValue(),
                        BalanceOperationType.MOVE_RESERVE,
                        BalanceOperationStatus.OK,
                        "USER MODEL NAME " + userModelName,
                        null,
                        null,
                        false,
                        null
                );
            }
        }
        return "OK";
    }

    public static Map<String, Double> getMovedToReserveAmount(String userModelName, String initTimestamp, String endTimestamp, boolean isMoneyClick) {
        JsonNode balanceMovements = new UserGetBalanceMovements(userModelName.split("__")[0], initTimestamp, endTimestamp, BalanceOperationType.MOVE_RESERVE, isMoneyClick).getResponse();
        Map<String, Double> moveReserveAmount = new TreeMap<>();
        Iterator<JsonNode> iter = balanceMovements.elements();
        while (iter.hasNext()) {
            JsonNode balanceMovement = iter.next();
            if (!balanceMovement.has("addedAmount")) {
                continue;
            }
            if (!balanceMovement.has("userModelName") || !balanceMovement.get("userModelName").textValue().equals(userModelName)) {
                continue;
            }
            if (!moveReserveAmount.containsKey(balanceMovement.get("addedAmount").get("currency").textValue())) {
                moveReserveAmount.put(balanceMovement.get("addedAmount").get("currency").textValue(), 0.0);
            }
            moveReserveAmount.put(balanceMovement.get("addedAmount").get("currency").textValue(), moveReserveAmount.get(balanceMovement.get("addedAmount").get("currency").textValue()) + balanceMovement.get("addedAmount").get("amount").doubleValue());
        }
        return moveReserveAmount;
    }

    public static String currencyChange(String modelName, String baseCurrency, String targetCurrency, BigDecimal marketPrice, BigDecimal baseAmount) {
        File userModelBalanceFile = UsersFolderLocator.getModelBalanceFile(modelName);
        if (!userModelBalanceFile.exists()) {
            return "USER MODEL BALANCE DOES NOT EXIST";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode userModelBalance = mapper.readTree(userModelBalanceFile);
            ArrayNode newModelBalanceAvailableAmounts = mapper.createArrayNode();
            double newBaseCurrencyAmount = 0.0;
            double newTargetCurrencyAmount = 0.0;
            Iterator<JsonNode> userModelBalanceAvailableAmountsIterator = userModelBalance.get("availableAmounts").elements();
            while (userModelBalanceAvailableAmountsIterator.hasNext()) {
                JsonNode userModelBalanceAvailableAmountsIt = userModelBalanceAvailableAmountsIterator.next();
                String userModelBalanceAvailableAmountsItCurrency = userModelBalanceAvailableAmountsIt.get("currency").textValue();
                double userModelBalanceAvailableAmountsItAmount = userModelBalanceAvailableAmountsIt.get("amount").doubleValue();
                if (userModelBalanceAvailableAmountsItCurrency.equals(baseCurrency) && userModelBalanceAvailableAmountsItAmount > 0) {
                    newBaseCurrencyAmount = userModelBalanceAvailableAmountsItAmount - baseAmount.doubleValue();
                    newTargetCurrencyAmount = newTargetCurrencyAmount + baseAmount.multiply(marketPrice).doubleValue();
                    continue;
                }
                if (userModelBalanceAvailableAmountsItCurrency.equals(targetCurrency)) {
                    newTargetCurrencyAmount = newTargetCurrencyAmount + userModelBalanceAvailableAmountsItAmount;
                    continue;
                }
                if (!userModelBalanceAvailableAmountsItCurrency.equals(baseCurrency) && !userModelBalanceAvailableAmountsItCurrency.equals(targetCurrency)) {
                    newModelBalanceAvailableAmounts.add(userModelBalanceAvailableAmountsIt);
                }
            }
            JsonNode newBaseCurrencyAmountIt = mapper.createObjectNode();
            ((ObjectNode) newBaseCurrencyAmountIt).put("currency", baseCurrency);
            ((ObjectNode) newBaseCurrencyAmountIt).put("amount", newBaseCurrencyAmount);
            newModelBalanceAvailableAmounts.add(newBaseCurrencyAmountIt);
            JsonNode newTargetCurrencyAmountIt = mapper.createObjectNode();
            ((ObjectNode) newTargetCurrencyAmountIt).put("currency", targetCurrency);
            ((ObjectNode) newTargetCurrencyAmountIt).put("amount", newTargetCurrencyAmount);
            newModelBalanceAvailableAmounts.add(newTargetCurrencyAmountIt);
            ((ObjectNode) userModelBalance).putArray("availableAmounts").addAll(newModelBalanceAvailableAmounts);
            FileUtil.editFile(userModelBalance, userModelBalanceFile);
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }

    private static void writeComment(File userModelFolder, String comment) {
        StringBuilder newComment = new StringBuilder(DateUtil.getCurrentDate());
        newComment.append(" ");
        newComment.append(comment);
        try {
            FileUtil.writeInFile(new File(userModelFolder, "comments.json"), newComment.toString());
        } catch (IOException ex) {
            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void firstTimeActivationModelProcess(String userModelName, JsonNode initialAmounts) {
        String timestamp = DateUtil.getCurrentDate();
        String fileDate = DateUtil.getFileDate(timestamp);
        File activeModelsFolder = ActiveModelsFolderLocator.getFolder();
        ObjectNode activeModel = new ObjectMapper().createObjectNode();
        activeModel.put("userName", userModelName.split("__")[0]);
        activeModel.put("userModelName", userModelName);
        activeModel.put("timestamp", timestamp);
        activeModel.set("initialAmounts", initialAmounts);
        FileUtil.createFile(activeModel, new File(activeModelsFolder, fileDate + "____" + userModelName + ".json"));
    }

    public static File getUserModelAdditionalMovementsFile(String userModelName) {
        File userModelAdditionalMovementsFile = new File(UsersFolderLocator.getModelFolder(userModelName), "additionalMovements.json");
        if (!userModelAdditionalMovementsFile.isFile()) {
            FileUtil.createFile(new ObjectMapper().createObjectNode(), userModelAdditionalMovementsFile);
        }
        return userModelAdditionalMovementsFile;
    }

    public static enum ProcessType {

        ACTIVATE, INACTIVATE;

    }

}
