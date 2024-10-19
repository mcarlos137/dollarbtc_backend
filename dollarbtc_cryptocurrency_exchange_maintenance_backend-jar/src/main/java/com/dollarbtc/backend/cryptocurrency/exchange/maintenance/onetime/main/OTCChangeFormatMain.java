/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class OTCChangeFormatMain extends BaseOperation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        File otcFolder = OTCFolderLocator.getFolder(null);
        for (File otcCurrencyFolder : otcFolder.listFiles()) {
            if (!otcCurrencyFolder.isDirectory()) {
                continue;
            }
            if (otcCurrencyFolder.getName().equals("Operations")) {
                // operations
                for (File otcCurrencyOperationFolder : otcCurrencyFolder.listFiles()) {
                    if (!otcCurrencyOperationFolder.isDirectory()) {
                        continue;
                    }
                    if (otcCurrencyOperationFolder.getName().equals("Indexes")) {
                        // indexes
                        File otcCurrencyOperationIndexPaidFolder = new File(new File(otcCurrencyOperationFolder, "Statuses"), "PAID");
                        if (otcCurrencyOperationIndexPaidFolder.isDirectory()) {
                            System.out.println("moving all files from folder " + otcCurrencyOperationIndexPaidFolder.getAbsolutePath());
                            FileUtil.moveAllFilesToFolder(otcCurrencyOperationIndexPaidFolder, FileUtil.createFolderIfNoExist(new File(new File(otcCurrencyOperationFolder, "Statuses"), "PAY_VERIFICATION")));
                            System.out.println("deleting folder " + otcCurrencyOperationIndexPaidFolder.getAbsolutePath());
                            FileUtil.deleteFolder(otcCurrencyOperationIndexPaidFolder);
                        }
                        File otcCurrencyOperationIndexFinishedFolder = new File(new File(otcCurrencyOperationFolder, "Statuses"), "FINISHED");
                        if (otcCurrencyOperationIndexFinishedFolder.isDirectory()) {
                            System.out.println("moving all files from folder " + otcCurrencyOperationIndexFinishedFolder.getAbsolutePath());
                            FileUtil.moveAllFilesToFolder(otcCurrencyOperationIndexFinishedFolder, FileUtil.createFolderIfNoExist(new File(new File(otcCurrencyOperationFolder, "Statuses"), "SUCCESS")));
                            System.out.println("deleting folder " + otcCurrencyOperationIndexFinishedFolder.getAbsolutePath());
                            FileUtil.deleteFolder(otcCurrencyOperationIndexFinishedFolder);
                        }
                    } else {
                        // real operations
                        File otcCurrencyOperationFile = new File(otcCurrencyOperationFolder, "operation.json");
                        if (otcCurrencyOperationFile.isFile()) {
                            try {
                                JsonNode otcCurrencyOperation = mapper.readTree(otcCurrencyOperationFile);
                                if (otcCurrencyOperation.has("otcOperationStatus") && otcCurrencyOperation.get("otcOperationStatus").textValue().equals("PAID")) {
                                    ((ObjectNode) otcCurrencyOperation).put("otcOperationStatus", "PAY_VERIFICATION");
                                }
                                if (otcCurrencyOperation.has("otcOperationStatus") && otcCurrencyOperation.get("otcOperationStatus").textValue().equals("FINISHED")) {
                                    ((ObjectNode) otcCurrencyOperation).put("otcOperationStatus", "SUCCESS");
                                }
                                if (otcCurrencyOperation.has("otcOperationType")
                                        && otcCurrencyOperation.has("payment")
                                        && otcCurrencyOperation.get("otcOperationType").textValue().equals("SELL")) {
                                    ((ObjectNode) otcCurrencyOperation).put("clientPayment", otcCurrencyOperation.get("payment"));
                                    ((ObjectNode) otcCurrencyOperation).remove("payment");
                                }
                                if (otcCurrencyOperation.has("otcOperationType")
                                        && otcCurrencyOperation.has("payment")
                                        && otcCurrencyOperation.get("otcOperationType").textValue().equals("BUY")) {
                                    ((ObjectNode) otcCurrencyOperation).put("dollarBTCPayment", otcCurrencyOperation.get("payment"));
                                    ((ObjectNode) otcCurrencyOperation).remove("payment");
                                }
                                System.out.println("editing file " + otcCurrencyOperationFile.getAbsolutePath());
                                FileUtil.editFile(otcCurrencyOperation, otcCurrencyOperationFile);
                            } catch (IOException ex) {
                                Logger.getLogger(OTCChangeFormatMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            } else {
                // currencies
                // deleting offers
                File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
                if (otcCurrencyOffersFolder.isDirectory()) {
                    for (File otcCurrencyOfferFolder : otcCurrencyOffersFolder.listFiles()) {
                        if (otcCurrencyOfferFolder.getName().contains("ASK") || otcCurrencyOfferFolder.getName().contains("BID")) {
                            System.out.println("deleting folder " + otcCurrencyOfferFolder.getAbsolutePath());
                            FileUtil.deleteFolder(otcCurrencyOfferFolder);
                        }
                    }
                }
                // changing PAID status to PAY_VERIFICATION and FINISHED to SUCCESS
                File otcCurrencyConfigFile = new File(otcCurrencyFolder, "config.json");
                if (otcCurrencyConfigFile.isFile()) {
                    try {
                        JsonNode otcCurrencyConfig = mapper.readTree(otcCurrencyConfigFile);
                        String otcCurrencyConfigString = otcCurrencyConfig.toString();
                        otcCurrencyConfigString = otcCurrencyConfigString.replace("PAID", "PAY_VERIFICATION");
                        otcCurrencyConfigString = otcCurrencyConfigString.replace("FINISHED", "SUCCESS");
                        otcCurrencyConfig = mapper.readTree(otcCurrencyConfigString);
                        System.out.println("editing file " + otcCurrencyConfigFile.getAbsolutePath());
                        FileUtil.editFile(otcCurrencyConfig, otcCurrencyConfigFile);
                    } catch (IOException ex) {
                        Logger.getLogger(OTCChangeFormatMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        File usersFolder = UsersFolderLocator.getFolder();
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            File userOTCFolder = new File(userFolder, "OTC");
            if (userOTCFolder.isDirectory()) {
                for (File userOTCCurrencyFolder : userOTCFolder.listFiles()) {
                    if (!userOTCCurrencyFolder.isDirectory()) {
                        continue;
                    }
                    File userOTCCurrencySellFolder = new File(userOTCCurrencyFolder, "SELL");
                    if (userOTCCurrencySellFolder.isDirectory()) {
                        for (File userOTCCurrencySellOperationFile : userOTCCurrencySellFolder.listFiles()) {
                            if (!userOTCCurrencySellOperationFile.isFile()) {
                                continue;
                            }
                            try {
                                JsonNode userOTCCurrencySellOperation = mapper.readTree(userOTCCurrencySellOperationFile);
                                if (userOTCCurrencySellOperation.get("otcOperationStatus").textValue().equals("PAID")) {
                                    ((ObjectNode) userOTCCurrencySellOperation).put("otcOperationStatus", "PAY_VERIFICATION");
                                }
                                if (userOTCCurrencySellOperation.get("otcOperationStatus").textValue().equals("FINISHED")) {
                                    ((ObjectNode) userOTCCurrencySellOperation).put("otcOperationStatus", "SUCCESS");
                                }
                                if (userOTCCurrencySellOperation.has("payment")) {
                                    ((ObjectNode) userOTCCurrencySellOperation).put("clientPayment", userOTCCurrencySellOperation.get("payment"));
                                    ((ObjectNode) userOTCCurrencySellOperation).remove("payment");
                                }
                                System.out.println("editing file " + userOTCCurrencySellOperationFile.getAbsolutePath());
                                FileUtil.editFile(userOTCCurrencySellOperation, userOTCCurrencySellOperationFile);
                            } catch (IOException ex) {
                                Logger.getLogger(OTCChangeFormatMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    File userOTCCurrencyBuyFolder = new File(userOTCCurrencyFolder, "BUY");
                    if (userOTCCurrencyBuyFolder.isDirectory()) {
                        for (File userOTCCurrencyBuyOperationFile : userOTCCurrencyBuyFolder.listFiles()) {
                            if (!userOTCCurrencyBuyOperationFile.isFile()) {
                                continue;
                            }
                            try {
                                JsonNode userOTCCurrencyBuyOperation = mapper.readTree(userOTCCurrencyBuyOperationFile);
                                if (userOTCCurrencyBuyOperation.get("otcOperationStatus").textValue().equals("PAID")) {
                                    ((ObjectNode) userOTCCurrencyBuyOperation).put("otcOperationStatus", "PAY_VERIFICATION");
                                }
                                if (userOTCCurrencyBuyOperation.get("otcOperationStatus").textValue().equals("FINISHED")) {
                                    ((ObjectNode) userOTCCurrencyBuyOperation).put("otcOperationStatus", "SUCCESS");
                                }
                                if (userOTCCurrencyBuyOperation.has("payment")) {
                                    ((ObjectNode) userOTCCurrencyBuyOperation).put("dollarBTCPayment", userOTCCurrencyBuyOperation.get("payment"));
                                    ((ObjectNode) userOTCCurrencyBuyOperation).remove("payment");
                                }
                                System.out.println("editing file " + userOTCCurrencyBuyOperationFile.getAbsolutePath());
                                FileUtil.editFile(userOTCCurrencyBuyOperation, userOTCCurrencyBuyOperationFile);
                            } catch (IOException ex) {
                                Logger.getLogger(OTCChangeFormatMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
    }

}
