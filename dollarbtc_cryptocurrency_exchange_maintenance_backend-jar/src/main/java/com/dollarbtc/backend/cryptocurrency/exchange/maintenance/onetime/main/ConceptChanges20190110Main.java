/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class ConceptChanges20190110Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        File usersFolder = new File(OPERATOR_PATH, "Users");
        //change Models config 
        ObjectMapper mapper = new ObjectMapper();
        File modelsFolder = new File(OPERATOR_PATH, "Models");
        for (File modelsTypeFolder : modelsFolder.listFiles()) {
            if (!modelsTypeFolder.isDirectory() || !modelsTypeFolder.getName().equals("PRO_TRADER")) {
                continue;
            }
            for (File modelsTypeExchangeFolder : modelsTypeFolder.listFiles()) {
                if (!modelsTypeExchangeFolder.isDirectory()) {
                    continue;
                }
                for (File modelFolder : modelsTypeExchangeFolder.listFiles()) {
                    if (!modelFolder.isDirectory()) {
                        continue;
                    }
                    System.out.println("----------------------------------------------");
                    System.out.println("starting change model config process for " + modelFolder.getName());
                    System.out.println("----------------------------------------------");
                    changeModelConfig(new File(modelFolder, "config.json"), mapper);
                    System.out.println("----------------------------------------------");
                    System.out.println("finishing change model config process for " + modelFolder.getName());
                    System.out.println("----------------------------------------------");
                }
            }
        }
        //change Models config for all users
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (userFolder.getName().equals("molinabracho@gmail.com")) {
                continue;
            }
            File userModelsFolder = new File(userFolder, "Models");
            if(!userModelsFolder.isDirectory()){
                continue;
            }
            for (File userModelFolder : userModelsFolder.listFiles()) {
                if (!userModelFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting change model config process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
                changeModelConfig(new File(userModelFolder, "config.json"), mapper);
                System.out.println("----------------------------------------------");
                System.out.println("finishing change model config process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
            }
        }
        //change Models Accounts and Orders for all users
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (userFolder.getName().equals("molinabracho@gmail.com")) {
                continue;
            }
            File userModelsFolder = new File(userFolder, "Models");
            if(!userModelsFolder.isDirectory()){
                continue;
            }
            for (File userModelFolder : userModelsFolder.listFiles()) {
                if (!userModelFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting change Models Accounts data for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
                changeAccountsData(userModelFolder, mapper);
                System.out.println("----------------------------------------------");
                System.out.println("finishing change Models Accounts data for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");

                System.out.println("----------------------------------------------");
                System.out.println("starting change Models Orders data for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
                changeOrdersData(userModelFolder, mapper);
                System.out.println("----------------------------------------------");
                System.out.println("finishing change Models Orders data for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");

                System.out.println("----------------------------------------------");
                System.out.println("starting modify in bid price band file for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
                modifyInBidPriceBand(userModelFolder, mapper);
                System.out.println("----------------------------------------------");
                System.out.println("finishing modify in bid price band file for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");

                System.out.println("----------------------------------------------");
                System.out.println("starting delete last orders for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
                deleteLastOrders(userModelFolder);
                System.out.println("----------------------------------------------");
                System.out.println("finishing delete last orders for all users process for " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
            }
        }
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
    }

    private static void changeModelConfig(File modelFile, ObjectMapper mapper) {
        if (!modelFile.isFile()) {
            return;
        }
        try {
            JsonNode model = mapper.readTree(modelFile);
            String modelString = model.toString();
            modelString = modelString.replace("nBidPriceBand", "nPriceBand");
            modelString = modelString.replace("BID", "BUY");
            modelString = modelString.replace("ASK", "SELL");
            model = mapper.readTree(modelString);
            FileUtil.editFile(model, modelFile);
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
                    orderDataString = orderDataString.replace("BID", "BUY");
                    orderDataString = orderDataString.replace("ASK", "SELL");
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
                        orderDataString = orderDataString.replace("BID", "BUY");
                        orderDataString = orderDataString.replace("ASK", "SELL");
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
                    accountDataString = accountDataString.replace("BID", "BUY");
                    accountDataString = accountDataString.replace("ASK", "SELL");
                    accountDataString = accountDataString.replace("lastBidTimestamp", "lastBuyTimestamp");
                    accountDataString = accountDataString.replace("bidCounter", "buyCounter");
                    accountDataString = accountDataString.replace("maxBidQuantity", "maxBuyQuantity");
                    accountDataString = accountDataString.replace("bid", "buy");
                    accountDataString = accountDataString.replace("toBidInARowCounter", "toBuyInARowCounter");
                    accountDataString = accountDataString.replace("toBidInARowHighestAskPrice", "toBuyInARowHighestSellPrice");
                    accountDataString = accountDataString.replace("toBidInARowConsistentDownTrend", "toBuyInARowConsistentDownTrend");
                    accountDataString = accountDataString.replace("toAskInARowCounter", "toSellInARowCounter");
                    accountDataString = accountDataString.replace("toAskInARowLowestBidPrice", "toSellInARowLowestBuyPrice");
                    accountDataString = accountDataString.replace("toAskInARowConsistentUpTrend", "toSellInARowConsistentUpTrend");
                    accountDataString = accountDataString.replace("inBidPriceBandCondition", "inPriceBandCondition");
                    accountData = mapper.readTree(accountDataString);
                    double lastBidPrice = accountData.get("accountBase").get("lastAskPrice").doubleValue();
                    double lastAskPrice = accountData.get("accountBase").get("lastBidPrice").doubleValue();
                    ((ObjectNode) accountData.get("accountBase")).put("lastBidPrice", lastBidPrice);
                    ((ObjectNode) accountData.get("accountBase")).put("lastAskPrice", lastAskPrice);
                    ((ObjectNode) accountData).remove("lastBidPrice");
                    ((ObjectNode) accountData).remove("lastAskPrice");
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
                        accountDataString = accountDataString.replace("BID", "BUY");
                        accountDataString = accountDataString.replace("ASK", "SELL");
                        accountDataString = accountDataString.replace("lastBidTimestamp", "lastBuyTimestamp");
                        accountDataString = accountDataString.replace("bidCounter", "buyCounter");
                        accountDataString = accountDataString.replace("maxBidQuantity", "maxBuyQuantity");
                        accountDataString = accountDataString.replace("bid", "buy");
                        accountDataString = accountDataString.replace("toBidInARowCounter", "toBuyInARowCounter");
                        accountDataString = accountDataString.replace("toBidInARowHighestAskPrice", "toBuyInARowHighestSellPrice");
                        accountDataString = accountDataString.replace("toBidInARowConsistentDownTrend", "toBuyInARowConsistentDownTrend");
                        accountDataString = accountDataString.replace("toAskInARowCounter", "toSellInARowCounter");
                        accountDataString = accountDataString.replace("toAskInARowLowestBidPrice", "toSellInARowLowestBuyPrice");
                        accountDataString = accountDataString.replace("toAskInARowConsistentUpTrend", "toSellInARowConsistentUpTrend");
                        accountDataString = accountDataString.replace("inBidPriceBandCondition", "inPriceBandCondition");
                        accountData = mapper.readTree(accountDataString);
                        double lastBidPrice = accountData.get("accountBase").get("lastAskPrice").doubleValue();
                        double lastAskPrice = accountData.get("accountBase").get("lastBidPrice").doubleValue();
                        ((ObjectNode) accountData.get("accountBase")).put("lastBidPrice", lastBidPrice);
                        ((ObjectNode) accountData.get("accountBase")).put("lastAskPrice", lastAskPrice);
                        ((ObjectNode) accountData).remove("lastBidPrice");
                        ((ObjectNode) accountData).remove("lastAskPrice");
                        FileUtil.editFile(accountData, accountDataFile);
                    }
                }
            } catch (IOException ex) {
            }
        }
    }

    private static void modifyInBidPriceBand(File modelFolder, ObjectMapper mapper) {
        for (File modelExchangeIdSymbolFolder : modelFolder.listFiles()) {
            if (!modelExchangeIdSymbolFolder.isDirectory()) {
                continue;
            }
            File inBidPriceBandConditionFile = new File(modelExchangeIdSymbolFolder, "inBidPriceBandCondition.json");
            if (!inBidPriceBandConditionFile.isFile()) {
                continue;
            }
            try {
                JsonNode inBidPriceBandCondition = mapper.readTree(inBidPriceBandConditionFile);
                String inBidPriceBandConditionString = inBidPriceBandCondition.toString();
                inBidPriceBandConditionString = inBidPriceBandConditionString.replace("bidPrice", "price");
                inBidPriceBandCondition = mapper.readTree(inBidPriceBandConditionString);
                FileUtil.editFile(inBidPriceBandCondition, inBidPriceBandConditionFile);
                FileUtil.moveFileToFile(inBidPriceBandConditionFile, new File(modelExchangeIdSymbolFolder, "inPriceBandCondition.json"));
            } catch (IOException ex) {
                Logger.getLogger(ConceptChanges20190110Main.class.getName()).log(Level.SEVERE, null, ex);
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
