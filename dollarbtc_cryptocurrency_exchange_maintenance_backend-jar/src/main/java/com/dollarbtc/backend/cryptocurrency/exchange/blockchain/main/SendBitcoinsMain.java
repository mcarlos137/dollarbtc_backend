/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.blockchain.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.blockchain.BlockchainMakePayment;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        File blockchainSendBitcoinsControllerFile = new File(OPERATOR_PATH, "blockchainSendBitcoinsController.json");
        JsonNode blockchainSendBitcoinsController = new ObjectMapper().createObjectNode();
        ((ObjectNode) blockchainSendBitcoinsController).put("processActive", true);
        ((ObjectNode) blockchainSendBitcoinsController).put("forcedToExit", false);
        FileUtil.editFile(blockchainSendBitcoinsController, blockchainSendBitcoinsControllerFile);
        transactionLoop();
        ((ObjectNode) blockchainSendBitcoinsController).put("processActive", false);
        ((ObjectNode) blockchainSendBitcoinsController).put("forcedToExit", false);
        FileUtil.editFile(blockchainSendBitcoinsController, blockchainSendBitcoinsControllerFile);
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
                String baseAddress = null;
                if (operationProcessing.has("baseAddress")
                        && operationProcessing.get("baseAddress").textValue() != null
                        && !operationProcessing.get("baseAddress").textValue().equals("")) {
                    baseAddress = operationProcessing.get("baseAddress").textValue();
                } else {
//                    List<String> addressesList = new ArrayList<>();
//                    Iterator<JsonNode> userListAddressesWithReceivedBTCTransactionsIterator = new UserListAddressesWithReceivedBTCTransactions().getResponse().iterator();
//                    while (userListAddressesWithReceivedBTCTransactionsIterator.hasNext()) {
//                        JsonNode userListAddressesWithReceivedBTCTransactionsIt = userListAddressesWithReceivedBTCTransactionsIterator.next();
//                        if(!userListAddressesWithReceivedBTCTransactionsIt.has("addresses")){
//                            continue;
//                        }
//                        Iterator<String> userListAddressesWithReceivedBTCTransactionsItFieldNamesIterator = userListAddressesWithReceivedBTCTransactionsIt.fieldNames();
//                        while (userListAddressesWithReceivedBTCTransactionsItFieldNamesIterator.hasNext()) {
//                            String userListAddressesWithReceivedBTCTransactionsItFieldNamesIt = userListAddressesWithReceivedBTCTransactionsItFieldNamesIterator.next();
//                            addressesList.add(userListAddressesWithReceivedBTCTransactionsItFieldNamesIt);
//                        }
//                    }
//                    baseAddresses = new String[addressesList.size()];
//                    addressesList.toArray(baseAddresses);
                }
                switch (operationProcessing.get("operation").textValue()) {
                    case "SEND_OUT":
                        long satoshiAmount = new Double(operationProcessing.get("amount").doubleValue() * 100000000).longValue();
                        File addressesCurrencyFolder = AddressesFolderLocator.getCurrencyFolder("BTC");
                        File addressesCurrencyAddressFolder = new File(addressesCurrencyFolder, baseAddress);
                        if (!addressesCurrencyAddressFolder.isDirectory()) {
                            throw new FileNotFoundException();
                        }
                        String guid = new ObjectMapper().readTree(new File(addressesCurrencyAddressFolder, "config.json")).get("guid").textValue();
                        JsonNode result = new BlockchainMakePayment(guid, operationProcessing.get("targetAddress").textValue(), satoshiAmount, 10000L).getResponse();
                        if (result.has("success") && result.get("success").booleanValue()) {
                            ((ObjectNode) operationProcessing).set("result", result);
                            FileUtil.editFile(operationProcessing, addressesCurrencyOperationsProcessingFile);
                            FileUtil.moveFileToFolder(addressesCurrencyOperationsProcessingFile, addressesCurrencyOperationsOkFolder);
                        } else {
                            ((ObjectNode) operationProcessing).set("error", result);
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

    private static boolean checkForcedToExit() {
        File blockcypherSendBitcoinsControllerFile = new File(OPERATOR_PATH, "blockcypherSendBitcoinsController.json");
        try {
            return new ObjectMapper().readTree(blockcypherSendBitcoinsControllerFile).get("forcedToExit").booleanValue();
        } catch (IOException ex) {
            Logger.getLogger(SendBitcoinsMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
