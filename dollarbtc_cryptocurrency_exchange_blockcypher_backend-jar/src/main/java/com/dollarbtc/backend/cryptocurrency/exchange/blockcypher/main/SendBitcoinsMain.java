/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.blockcypher.main;

import com.blockcypher.context.BlockCypherContext;
import com.blockcypher.exception.BlockCypherException;
import com.blockcypher.model.transaction.Transaction;
import com.blockcypher.model.transaction.intermediary.IntermediaryTransaction;
import com.blockcypher.utils.gson.GsonFactory;
import com.blockcypher.utils.sign.SignUtils;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.blockcypher.BlockcypherGetInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListAddressesWithReceivedTransactions;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SendBitcoinsMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File blockcypherSendBitcoinsControllerFile = new File(OPERATOR_PATH, "blockcypherSendBitcoinsController.json");
        JsonNode blockcypherSendBitcoinsController = new ObjectMapper().createObjectNode();
        ((ObjectNode) blockcypherSendBitcoinsController).put("processActive", true);
        ((ObjectNode) blockcypherSendBitcoinsController).put("forcedToExit", false);
        ((ObjectNode) blockcypherSendBitcoinsController).put("sendMultiInputs", false);
        FileUtil.editFile(blockcypherSendBitcoinsController, blockcypherSendBitcoinsControllerFile);
        transactionLoop();
        ((ObjectNode) blockcypherSendBitcoinsController).put("processActive", false);
        ((ObjectNode) blockcypherSendBitcoinsController).put("forcedToExit", false);
        ((ObjectNode) blockcypherSendBitcoinsController).put("sendMultiInputs", false);
        FileUtil.editFile(blockcypherSendBitcoinsController, blockcypherSendBitcoinsControllerFile);
        System.exit(0);
    }

    private static void transactionLoop() {
        int totalMillisecs = 0;
        int millisecsPortion = 30000;
        while (true) {
            //look for new transactions
            checkProcessingOperations();
            if (checkForcedToExit()) {
                break;
            }
            try {
                totalMillisecs = totalMillisecs + millisecsPortion;
                Thread.sleep(millisecsPortion);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (totalMillisecs < 600000) {
                continue;
            }
            totalMillisecs = 0;
        }
    }

    private static void checkProcessingOperations() {
        Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.INFO, "----------- check processing operations -----------");
        ObjectMapper mapper = new ObjectMapper();
        File addressesCurrencyOperationsProcessingFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "PROCESSING");
        File addressesCurrencyOperationsOkFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "OK");
        File addressesCurrencyOperationsFailFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "FAIL");
//        File addressesCurrencyOperationsManualFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "MANUAL");
        for (File addressesCurrencyOperationsProcessingFile : addressesCurrencyOperationsProcessingFolder.listFiles()) {
            try {
                JsonNode operationProcessing = mapper.readTree(addressesCurrencyOperationsProcessingFile);
                String[] baseAddresses = null;
                if (operationProcessing.has("baseAddress")
                        && operationProcessing.get("baseAddress").textValue() != null
                        && !operationProcessing.get("baseAddress").textValue().equals("")) {
                    baseAddresses = new String[]{operationProcessing.get("baseAddress").textValue()};
                } else if(checkSendMultiInputs()) {
                    List<String> addressesList = new ArrayList<>();
                    Iterator<JsonNode> userListAddressesWithReceivedBTCTransactionsIterator = new UserListAddressesWithReceivedTransactions("BTC").getResponse().iterator();
                    while (userListAddressesWithReceivedBTCTransactionsIterator.hasNext()) {
                        JsonNode userListAddressesWithReceivedBTCTransactionsIt = userListAddressesWithReceivedBTCTransactionsIterator.next();
                        if(!userListAddressesWithReceivedBTCTransactionsIt.has("addresses")){
                            continue;
                        }
                        Iterator<String> userListAddressesWithReceivedBTCTransactionsItFieldNamesIterator = userListAddressesWithReceivedBTCTransactionsIt.fieldNames();
                        while (userListAddressesWithReceivedBTCTransactionsItFieldNamesIterator.hasNext()) {
                            String userListAddressesWithReceivedBTCTransactionsItFieldNamesIt = userListAddressesWithReceivedBTCTransactionsItFieldNamesIterator.next();
                            addressesList.add(userListAddressesWithReceivedBTCTransactionsItFieldNamesIt);
                        }
                    }
                    baseAddresses = new String[addressesList.size()];
                    addressesList.toArray(baseAddresses);
                }
                switch (operationProcessing.get("operation").textValue()) {
                    case "SEND_OUT":
                        try {
                        String result = sendBitcoins(baseAddresses, operationProcessing.get("targetAddress").textValue(), operationProcessing.get("amount").doubleValue());
                        ((ObjectNode) operationProcessing).put("result", result);
                        FileUtil.editFile(operationProcessing, addressesCurrencyOperationsProcessingFile);
                        FileUtil.moveFileToFolder(addressesCurrencyOperationsProcessingFile, addressesCurrencyOperationsOkFolder);
                    } catch (BlockCypherException | IOException ex) {
                        Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
                        ((ObjectNode) operationProcessing).put("error", ex.getMessage());
                        FileUtil.editFile(operationProcessing, addressesCurrencyOperationsProcessingFile);
                        FileUtil.moveFileToFolder(addressesCurrencyOperationsProcessingFile, addressesCurrencyOperationsFailFolder);
                    }
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String sendBitcoins(String[] baseAddresses, String targetAddress, Double bitcoinsAmount) throws BlockCypherException, FileNotFoundException, IOException {
        BlockCypherContext context = new BlockCypherContext("v1", "btc", "main", "b59c3adf5b414a0ab13242db49fa1b53");
        // WIF Format of your private Key
//        long satoshiAmount = new Double(bitcoinsAmount * 100000000).longValue();
//        IntermediaryTransaction unsignedTx = context.getTransactionService()
//                .newTransaction(
//                        new ArrayList<>(Arrays.asList(baseAddresses)),
//                        new ArrayList<>(Arrays.asList(targetAddress)),
//                        satoshiAmount
//                );
        IntermediaryTransaction unsignedTx = context.getTransactionService()
                .newTransaction(getTXSkeleton(baseAddresses, targetAddress, bitcoinsAmount).toString());
        File addressesCurrencyFolder = AddressesFolderLocator.getCurrencyFolder("BTC");
        File addressesCurrencyAddressFolder = new File(addressesCurrencyFolder, baseAddresses[0]);
        if (!addressesCurrencyAddressFolder.isDirectory()) {
            throw new FileNotFoundException();
        }
        String wif = new ObjectMapper().readTree(new File(addressesCurrencyAddressFolder, "config.json")).get("wif").textValue();
        SignUtils.signWithBase58KeyWithPubKey(unsignedTx, wif);
        Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.INFO, "unsignedTx: {0}", unsignedTx);
        Transaction tx = context.getTransactionService().sendTransaction(unsignedTx);
        String result = GsonFactory.getGsonPrettyPrint().toJson(tx);
        Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.INFO, "Sent transaction: {0}", result);
        return result;
    }

    private static boolean checkForcedToExit() {
        File blockcypherSendBitcoinsControllerFile = new File(OPERATOR_PATH, "blockcypherSendBitcoinsController.json");
        try {
            return new ObjectMapper().readTree(blockcypherSendBitcoinsControllerFile).get("forcedToExit").booleanValue();
        } catch (IOException ex) {
            Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private static boolean checkSendMultiInputs() {
        File blockcypherSendBitcoinsControllerFile = new File(OPERATOR_PATH, "blockcypherSendBitcoinsController.json");
        try {
            return new ObjectMapper().readTree(blockcypherSendBitcoinsControllerFile).get("sendMultiInputs").booleanValue();
        } catch (IOException ex) {
            Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private static JsonNode getTXSkeleton(String[] baseAddresses, String targetAddress, Double bitcoinsAmount) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode txSkeleton = mapper.createObjectNode();
        ArrayNode txSkeletonInputs = mapper.createArrayNode();
        for (String baseAddress : baseAddresses) {
            JsonNode txSkeletonInput = mapper.createObjectNode();
            ArrayNode txSkeletonInputAddresses = mapper.createArrayNode();
            txSkeletonInputAddresses.add(baseAddress);
            ((ObjectNode) txSkeletonInput).putArray("addresses").addAll(txSkeletonInputAddresses);
            txSkeletonInputs.add(txSkeletonInput);
        }
        ((ObjectNode) txSkeleton).putArray("inputs").addAll(txSkeletonInputs);
        ArrayNode txSkeletonOutputs = mapper.createArrayNode();
        JsonNode txSkeletonOutput = mapper.createObjectNode();
        ArrayNode txSkeletonOutputAddresses = mapper.createArrayNode();
        txSkeletonOutputAddresses.add(targetAddress);
        ((ObjectNode) txSkeletonOutput).putArray("addresses").addAll(txSkeletonOutputAddresses);
        long satoshiAmount = new Double(bitcoinsAmount * 100000000).longValue();
        ((ObjectNode) txSkeletonOutput).put("value", satoshiAmount);
        txSkeletonOutputs.add(txSkeletonOutput);
        ((ObjectNode) txSkeleton).putArray("outputs").addAll(txSkeletonOutputs);
        ((ObjectNode) txSkeleton).put("preference", getPreference());
        return txSkeleton;
    }
    
    private static String getPreference(){
        JsonNode blockcypherInfo = new BlockcypherGetInfo().getResponse();
        Double high_fee_per_kb = blockcypherInfo.get("high_fee_per_kb").doubleValue();
        Double medium_fee_per_kb = blockcypherInfo.get("medium_fee_per_kb").doubleValue();
        Double low_fee_per_kb = blockcypherInfo.get("low_fee_per_kb").doubleValue();
        Double fee = low_fee_per_kb;
        String preference = "low";
        if((medium_fee_per_kb - fee) / fee <= 0.1){
            fee = medium_fee_per_kb;
            preference = "medium";
        }
        if((high_fee_per_kb - fee) / fee <= 0.1){
            preference = "high";
        }
        return preference;
    }

}
