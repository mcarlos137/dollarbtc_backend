/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author CarlosDaniel
 */
public class ConceptChangesMain {

    public static void main(String[] args) {
//        System.out.println("----------------------------------------------");
//        System.out.println("starting process");
//        System.out.println("----------------------------------------------");
//        File usersFolder = new File(ROOT_PATH, "Users");
//        //change Models config 
//        ObjectMapper mapper = new ObjectMapper();
//        File modelsFolder = new File(ROOT_PATH, "Models");
//        for (File modelFolder : modelsFolder.listFiles()) {
//            if (!modelFolder.isDirectory()) {
//                continue;
//            }
//            System.out.println("----------------------------------------------");
//            System.out.println("starting change model config process for " + modelFolder.getName());
//            System.out.println("----------------------------------------------");
//            changeModelConfig(new File(modelFolder, "config.json"), mapper);
//            System.out.println("----------------------------------------------");
//            System.out.println("finishing change model config process for " + modelFolder.getName());
//            System.out.println("----------------------------------------------");
//        }
//        //change User balance files for all users
//        for (File userFolder : usersFolder.listFiles()) {
//            if (!userFolder.isDirectory()) {
//                continue;
//            }
//            File userBalanceFolder = new File(userFolder, "Balance");
//            for (File userBalanceFile : userBalanceFolder.listFiles()) {
//                if (!userBalanceFile.isFile()) {
//                    continue;
//                }
//                System.out.println("----------------------------------------------");
//                System.out.println("starting change user balance files process for " + userFolder.getName());
//                System.out.println("----------------------------------------------");
//                changeBalance(userBalanceFile, mapper);
//                System.out.println("----------------------------------------------");
//                System.out.println("finishing change user balance files process for " + userFolder.getName());
//                System.out.println("----------------------------------------------");
//            }
//
//        }
//        //change Models config and balance for all users
//        for (File userFolder : usersFolder.listFiles()) {
//            if (!userFolder.isDirectory()) {
//                continue;
//            }
//            File userModelsFolder = new File(userFolder, "Models");
//            for (File userModelFolder : userModelsFolder.listFiles()) {
//                if (!userModelFolder.isDirectory()) {
//                    continue;
//                }
//                System.out.println("----------------------------------------------");
//                System.out.println("starting change model config process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//                changeModelConfig(new File(userModelFolder, "config.json"), mapper);
//                changeBalance(new File(userModelFolder, "balance.json"), mapper);
//                System.out.println("----------------------------------------------");
//                System.out.println("finishing change model config process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//            }
//        }
//        //change Models Orders algorithmType for all users
//        for (File userFolder : usersFolder.listFiles()) {
//            if (!userFolder.isDirectory()) {
//                continue;
//            }
//            File userModelsFolder = new File(userFolder, "Models");
//            for (File userModelFolder : userModelsFolder.listFiles()) {
//                if (!userModelFolder.isDirectory()) {
//                    continue;
//                }
//                System.out.println("----------------------------------------------");
//                System.out.println("starting change Models Accounts data for all users process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//                changeAccountsData(userModelFolder, mapper);
//                System.out.println("----------------------------------------------");
//                System.out.println("finishing change Models Accounts data for all users process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//                System.out.println("----------------------------------------------");
//                System.out.println("starting change Models Orders data for all users process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//                changeOrdersData(userModelFolder, mapper);
//                System.out.println("----------------------------------------------");
//                System.out.println("finishing change Models Orders data for all users process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//                System.out.println("----------------------------------------------");
//                System.out.println("starting delete last orders for all users process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//                deleteLastOrders(userModelFolder);
//                System.out.println("----------------------------------------------");
//                System.out.println("finishing delete last orders for all users process for " + userModelFolder.getName());
//                System.out.println("----------------------------------------------");
//
//            }
//        }
//        System.out.println("----------------------------------------------");
//        System.out.println("finishing process");
//        System.out.println("----------------------------------------------");
    }

    private static void changeModelConfig(File modelFile, ObjectMapper mapper) {
        if (!modelFile.isFile()) {
            return;
        }
        try {
            JsonNode model = mapper.readTree(modelFile);
            boolean modelGeneralRulesUseEarningCondition = model.get("generalRules").get("useEarningCondition").booleanValue();
            Double modelGeneralRulesEarningConditionMinTransactionFactor = model.get("generalRules").get("earningConditionMinTransactionFactor").doubleValue();
            boolean modelGeneralRulesUseNotEnoughBalanceCondition = model.get("generalRules").get("useNotEnoughBalanceCondition").booleanValue();
            Integer modelGeneralRulesNotEnoughBalanceConditionInARowLimitToOrderQuantity = model.get("generalRules").get("notEnoughBalanceConditionInARowLimitToOrderQuantity").intValue();
            boolean modelGeneralRulesUseOrderBlockingCondition = model.get("generalRules").get("useOrderBlockingCondition").booleanValue();
            Integer modelGeneralRulesOrderBlockingConditionStopLossMaxQuantity = model.get("generalRules").get("orderBlockingConditionStopLossMaxQuantity").intValue();
            Integer modelGeneralRulesOrderBlockingConditionMaxQuantity = model.get("generalRules").get("orderBlockingConditionMaxQuantity").intValue();
            boolean modelGeneralRulesUseInBidPriceBandCondition = model.get("generalRules").get("useInBidPriceBandCondition").booleanValue();
            Double modelGeneralRulesInBidPriceBandConditionUpPercent = model.get("generalRules").get("inBidPriceBandConditionUpPercent").doubleValue();
            Double modelGeneralRulesInBidPriceBandConditionDownPercent = model.get("generalRules").get("inBidPriceBandConditionDownPercent").doubleValue();
            ((ObjectNode) model).remove("generalRules");
            ((ObjectNode) model).remove("testStatus");
            Iterator<JsonNode> modelSymbolRules = model.get("symbolRules").elements();
            while (modelSymbolRules.hasNext()) {
                JsonNode modelSymbolRule = modelSymbolRules.next();
                ((ObjectNode) modelSymbolRule).put("startBasePercent", 100);
                ((ObjectNode) modelSymbolRule).remove("startBaseAmount");
                Iterator<JsonNode> modelSymbolRuleAlgorithmRules = modelSymbolRule.get("algorithmRules").elements();
                while (modelSymbolRuleAlgorithmRules.hasNext()) {
                    JsonNode modelSymbolRuleAlgorithmRule = modelSymbolRuleAlgorithmRules.next();
                    String modelSymbolRuleAlgorithmRuleOutParams = modelSymbolRuleAlgorithmRule.get("outParams").textValue();
                    modelSymbolRuleAlgorithmRuleOutParams = modelSymbolRuleAlgorithmRuleOutParams.replace("STOPLOSS", "MAXLOSS");
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("outParams", modelSymbolRuleAlgorithmRuleOutParams);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("useEarningCondition", modelGeneralRulesUseEarningCondition);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("earningConditionMinTransactionFactor", modelGeneralRulesEarningConditionMinTransactionFactor);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("useNotEnoughBalanceCondition", modelGeneralRulesUseNotEnoughBalanceCondition);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("notEnoughBalanceConditionInARowLimitToOrderQuantity", modelGeneralRulesNotEnoughBalanceConditionInARowLimitToOrderQuantity);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("useOrderBlockingCondition", modelGeneralRulesUseOrderBlockingCondition);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("orderBlockingConditionMaxLossMaxQuantity", modelGeneralRulesOrderBlockingConditionStopLossMaxQuantity);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("orderBlockingConditionMaxQuantity", modelGeneralRulesOrderBlockingConditionMaxQuantity);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("useInBidPriceBandCondition", modelGeneralRulesUseInBidPriceBandCondition);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("inBidPriceBandConditionUpPercent", modelGeneralRulesInBidPriceBandConditionUpPercent);
                    ((ObjectNode) modelSymbolRuleAlgorithmRule).put("inBidPriceBandConditionDownPercent", modelGeneralRulesInBidPriceBandConditionDownPercent);
                }
            }
            String modelString = model.toString();
            if (modelString.contains("HitBTC")) {
                modelString = modelString.replace("USD", "USDT");
                model = mapper.readTree(modelString);
            }
            FileUtil.editFile(model, modelFile);
        } catch (IOException ex) {
            Logger.getLogger(ConceptChangesMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void changeBalance(File balanceFile, ObjectMapper mapper) {
        if (!balanceFile.isFile()) {
            return;
        }
        try {
            JsonNode balance = mapper.readTree(balanceFile);
            String balanceString = balance.toString();
            balanceString = balanceString.replace("USD", "USDT");
            balance = mapper.readTree(balanceString);
            FileUtil.editFile(balance, balanceFile);
        } catch (IOException ex) {
            Logger.getLogger(ConceptChangesMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void changeOrdersData(File modelFolder, ObjectMapper mapper) {
        for (File modelExchangeIdSymbolFolder : modelFolder.listFiles()) {
            if (!modelExchangeIdSymbolFolder.isDirectory()) {
                continue;
            }
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(new File(modelExchangeIdSymbolFolder, "Orders").getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isRegularFile(path))
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File orderDataFile = it.toFile();
                    JsonNode orderData = mapper.readTree(orderDataFile);
                    if (orderData == null) {
                        continue;
                    }
                    String orderDataString = orderData.toString();
                    orderDataString = orderDataString.replace("STOP_LOSS", "MAX_LOSS");
                    if (orderDataString.contains("HitBTC")) {
                        orderDataString = orderDataString.replace("USD", "USDT");
                    }
                    orderData = mapper.readTree(orderDataString);
                    FileUtil.editFile(orderData, orderDataFile);
                }
            } catch (IOException ex) {
            }
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(new File(modelExchangeIdSymbolFolder, "Orders").getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isDirectory(path))
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    List<File> files = Arrays.asList(it.toFile().listFiles());
                    for (File orderDataFile : files) {
                        JsonNode orderData = mapper.readTree(orderDataFile);
                        if (orderData == null) {
                            continue;
                        }
                        String orderDataString = orderData.toString();
                        orderDataString = orderDataString.replace("STOP_LOSS", "MAX_LOSS");
                        if (orderDataString.contains("HitBTC")) {
                            orderDataString = orderDataString.replace("USD", "USDT");
                        }
                        orderData = mapper.readTree(orderDataString);
                        FileUtil.editFile(orderData, orderDataFile);
                    }
                }
            } catch (IOException ex) {
            }
        }
    }

    private static void changeAccountsData(File modelFolder, ObjectMapper mapper) {
        for (File modelExchangeIdSymbolFolder : modelFolder.listFiles()) {
            if (!modelExchangeIdSymbolFolder.isDirectory()) {
                continue;
            }
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(new File(modelExchangeIdSymbolFolder, "Accounts").getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isRegularFile(path))
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File accountDataFile = it.toFile();
                    JsonNode accountData = mapper.readTree(accountDataFile);
                    if (accountData == null) {
                        continue;
                    }
                    String accountDataString = accountData.toString();
                    if (accountDataString.contains("HitBTC")) {
                        accountDataString = accountDataString.replace("USD", "USDT");
                    }
                    accountData = mapper.readTree(accountDataString);
                    FileUtil.editFile(accountData, accountDataFile);
                }
            } catch (IOException ex) {
            }
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(new File(modelExchangeIdSymbolFolder, "Accounts").getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isDirectory(path))
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    List<File> files = Arrays.asList(it.toFile().listFiles());
                    for (File accountDataFile : files) {
                        JsonNode accountData = mapper.readTree(accountDataFile);
                        if (accountData == null) {
                            continue;
                        }
                        String accountDataString = accountData.toString();
                        if (accountDataString.contains("HitBTC")) {
                            accountDataString = accountDataString.replace("USD", "USDT");
                        }
                        accountData = mapper.readTree(accountDataString);
                        FileUtil.editFile(accountData, accountDataFile);
                    }
                }
            } catch (IOException ex) {
            }
        }
    }

    private static void deleteLastOrders(File modelFolder) {
        for (File modelExchangeIdSymbolFolder : modelFolder.listFiles()) {
            if (!modelExchangeIdSymbolFolder.isDirectory()) {
                continue;
            }
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(new File(modelExchangeIdSymbolFolder, "LastOrders").getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isRegularFile(path))
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File lastOrderDataFile = it.toFile();
                    FileUtil.deleteFile(lastOrderDataFile);
                }
            } catch (IOException ex) {
            }
        }
    }

}
