/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class ReconversionDollarBTCPaymentBalanceMovementsVES20210930Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Starting ReconversionDollarBTCPaymentBalanceMovementsVES20210930Main");
        for (File currencyFolder : OTCFolderLocator.getFolder(null).listFiles()) {
            if (!currencyFolder.isDirectory()) {
                continue;
            }
            if (!currencyFolder.getName().equals("VES")) {
                continue;
            }
            System.out.println("currency: " + currencyFolder.getName());
            File currencyPaymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, currencyFolder.getName());
            for (File currencyPaymentFolder : currencyPaymentsFolder.listFiles()) {
                if (!currencyPaymentFolder.isDirectory()) {
                    continue;
                }
                String id = currencyPaymentFolder.getName();
                System.out.println("id: " + id);
                File additionalsFile = new File(currencyPaymentFolder, "additionals.json");
                if (additionalsFile.isFile()) {
                    try {
                        JsonNode additionals = mapper.readTree(additionalsFile);
                        if (additionals.has("reconverted20210930") && additionals.get("reconverted20210930").booleanValue()) {
                            continue;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ReconversionDollarBTCPaymentBalanceMovementsVES20210930Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                File currencyPaymentBalanceFolder = OTCFolderLocator.getCurrencyPaymentBalanceFolder(null, currencyFolder.getName(), id);
                for (File currencyPaymentBalanceMovementFile : currencyPaymentBalanceFolder.listFiles()) {
                    if (!currencyPaymentBalanceMovementFile.isFile()) {
                        continue;
                    }
                    if (currencyPaymentBalanceMovementFile.getName().contains("2021-10")
                            || currencyPaymentBalanceMovementFile.getName().contains("2021-11")) {
                        continue;
                    }
                    System.out.println("currencyPaymentBalanceMovementFile: " + currencyPaymentBalanceMovementFile.getName());
                    try {
                        JsonNode currencyPaymentBalanceMovement = mapper.readTree(currencyPaymentBalanceMovementFile);
                        if (currencyPaymentBalanceMovement.has("reconverted20210930") && currencyPaymentBalanceMovement.get("reconverted20210930").booleanValue()) {
                            System.out.println("reconverted20210930: " + currencyPaymentBalanceMovement.get("reconverted20210930").booleanValue());
                            continue;
                        }
                        if (currencyPaymentBalanceMovement.has("addedAmount")) {
                            String currency = currencyPaymentBalanceMovement.get("addedAmount").get("currency").textValue();
                            if (!currency.equals("VES")) {
                                continue;
                            }
                            System.out.println("Currency: " + currency);
                            if (currencyPaymentBalanceMovement.get("addedAmount").has("initialAmount")) {
                                Double initialAmount = currencyPaymentBalanceMovement.get("addedAmount").get("initialAmount").doubleValue();
                                System.out.println("Old initialAmount: " + initialAmount);
                                initialAmount = new BigDecimal(initialAmount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                System.out.println("New initialAmount: " + initialAmount);
                                ((ObjectNode) currencyPaymentBalanceMovement.get("addedAmount")).put("initialAmount", initialAmount);
                            }
                            Double amount = currencyPaymentBalanceMovement.get("addedAmount").get("amount").doubleValue();
                            System.out.println("Old amount: " + amount);
                            amount = new BigDecimal(amount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            System.out.println("New amount: " + amount);
                            ((ObjectNode) currencyPaymentBalanceMovement.get("addedAmount")).put("amount", amount);
                        }
                        if (currencyPaymentBalanceMovement.has("substractedAmount")) {
                            String currency = currencyPaymentBalanceMovement.get("substractedAmount").get("currency").textValue();
                            if (!currency.equals("VES")) {
                                continue;
                            }
                            System.out.println("Currency: " + currency);
                            if (currencyPaymentBalanceMovement.get("substractedAmount").has("initialAmount")) {
                                Double initialAmount = currencyPaymentBalanceMovement.get("substractedAmount").get("initialAmount").doubleValue();
                                System.out.println("Old initialAmount: " + initialAmount);
                                initialAmount = new BigDecimal(initialAmount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                System.out.println("New initialAmount: " + initialAmount);
                                ((ObjectNode) currencyPaymentBalanceMovement.get("substractedAmount")).put("initialAmount", initialAmount);
                            }
                            Double amount = currencyPaymentBalanceMovement.get("substractedAmount").get("amount").doubleValue();
                            System.out.println("Old amount: " + amount);
                            amount = new BigDecimal(amount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            System.out.println("New amount: " + amount);
                            ((ObjectNode) currencyPaymentBalanceMovement.get("substractedAmount")).put("amount", amount);
                        }
                        ((ObjectNode) currencyPaymentBalanceMovement).put("reconverted20210930", true);
                        FileUtil.editFile(currencyPaymentBalanceMovement, currencyPaymentBalanceMovementFile);
                    } catch (IOException ex) {
                        Logger.getLogger(ReconversionDollarBTCPaymentBalanceMovementsVES20210930Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                ObjectNode additionals = mapper.createObjectNode();
                additionals.put("reconverted20210930", true);
                FileUtil.editFile(additionals, additionalsFile);
            }
        }
        System.out.println("Finishing ReconversionDollarBTCPaymentBalanceMovementsVES20210930Main");
    }

}
