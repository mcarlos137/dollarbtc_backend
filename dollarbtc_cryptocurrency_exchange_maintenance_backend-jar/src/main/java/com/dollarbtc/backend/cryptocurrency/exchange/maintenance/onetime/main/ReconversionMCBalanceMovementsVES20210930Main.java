/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class ReconversionMCBalanceMovementsVES20210930Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Starting ReconversionMCBalanceMovementsVES20210930Main");
        int i = 0;
        for (File userFolder : UsersFolderLocator.getFolder().listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
//            if (!(userFolder.getName().equals("584166955176")
//                    || userFolder.getName().equals("584263798319")
//                    || userFolder.getName().equals("584125726857")
//                    || userFolder.getName().equals("584247112188")
//                    || userFolder.getName().equals("584166214068")
//                    || userFolder.getName().equals("584165296031")
//                    || userFolder.getName().equals("584120249597")
//                    || userFolder.getName().equals("584245105809")
//                    || userFolder.getName().equals("584245522788"))) {
//                continue;
//            }
            File additionalsFile = new File(userFolder, "additionals.json");
            if(additionalsFile.isFile()){
                try {
                    JsonNode additionals = mapper.readTree(additionalsFile);
                    if(additionals.has("reconverted20210930") && additionals.get("reconverted20210930").booleanValue()){
                        continue;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ReconversionMCBalanceMovementsVES20210930Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("user: " + userFolder.getName());
            File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userFolder.getName());
            for (File userMCBalanceMovementFile : userMCBalanceFolder.listFiles()) {
                if (!userMCBalanceMovementFile.isFile()) {
                    continue;
                }
                System.out.println("userMCBalanceMovementFile: " + userMCBalanceMovementFile.getName());
                try {
                    JsonNode userMCBalanceMovement = mapper.readTree(userMCBalanceMovementFile);
                    if (userMCBalanceMovement.has("reconverted20210930") && userMCBalanceMovement.get("reconverted20210930").booleanValue()) {
                        System.out.println("reconverted20210930: " + userMCBalanceMovement.get("reconverted20210930").booleanValue());
                        continue;
                    }
                    if (userMCBalanceMovement.has("addedAmount")) {
                        String currency = userMCBalanceMovement.get("addedAmount").get("currency").textValue();
                        if (!currency.equals("VES")) {
                            continue;
                        }
                        System.out.println("Currency: " + currency);
                        if (userMCBalanceMovement.get("addedAmount").has("initialAmount")) {
                            Double initialAmount = userMCBalanceMovement.get("addedAmount").get("initialAmount").doubleValue();
                            System.out.println("Old initialAmount: " + initialAmount);
                            initialAmount = new BigDecimal(initialAmount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            System.out.println("New initialAmount: " + initialAmount);
                            ((ObjectNode) userMCBalanceMovement.get("addedAmount")).put("initialAmount", initialAmount);
                        }
                        Double amount = userMCBalanceMovement.get("addedAmount").get("amount").doubleValue();
                        System.out.println("Old amount: " + amount);
                        amount = new BigDecimal(amount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        System.out.println("New amount: " + amount);
                        ((ObjectNode) userMCBalanceMovement.get("addedAmount")).put("amount", amount);
                    }
                    if (userMCBalanceMovement.has("substractedAmount")) {
                        String currency = userMCBalanceMovement.get("substractedAmount").get("currency").textValue();
                        if (!currency.equals("VES")) {
                            continue;
                        }
                        System.out.println("Currency: " + currency);
                        if (userMCBalanceMovement.get("substractedAmount").has("initialAmount")) {
                            Double initialAmount = userMCBalanceMovement.get("substractedAmount").get("initialAmount").doubleValue();
                            System.out.println("Old initialAmount: " + initialAmount);
                            initialAmount = new BigDecimal(initialAmount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            System.out.println("New initialAmount: " + initialAmount);
                            ((ObjectNode) userMCBalanceMovement.get("substractedAmount")).put("initialAmount", initialAmount);
                        }
                        Double amount = userMCBalanceMovement.get("substractedAmount").get("amount").doubleValue();
                        System.out.println("Old amount: " + amount);
                        amount = new BigDecimal(amount / 1000000).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        System.out.println("New amount: " + amount);
                        ((ObjectNode) userMCBalanceMovement.get("substractedAmount")).put("amount", amount);
                    }
                    ((ObjectNode) userMCBalanceMovement).put("reconverted20210930", true);
                    FileUtil.editFile(userMCBalanceMovement, userMCBalanceMovementFile);
                } catch (IOException ex) {
                    Logger.getLogger(ReconversionMCBalanceMovementsVES20210930Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ObjectNode additionals = mapper.createObjectNode();
            additionals.put("reconverted20210930", true);
            FileUtil.editFile(additionals, additionalsFile);
            i++;
            if (i >= 5000) {
                break;
            }
        }
        System.out.println("Finishing ReconversionMCBalanceMovementsVES20210930Main");
    }

}
