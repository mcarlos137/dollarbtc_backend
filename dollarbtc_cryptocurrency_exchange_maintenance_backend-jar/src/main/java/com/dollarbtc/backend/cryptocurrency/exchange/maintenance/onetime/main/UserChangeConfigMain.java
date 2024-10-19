/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserChangeProfile;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public class UserChangeConfigMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        File usersFolder = new File(OPERATOR_PATH, "Users");
        ObjectMapper mapper = new ObjectMapper();
        //change User config file for all users
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (userFolder.getName().equals("molinabracho@gmail.com")) {
                continue;
            }
            File userFile = new File(userFolder, "config.json");
            System.out.println("----------------------------------------------");
            System.out.println("starting change user config file process for " + userFolder.getName());
            System.out.println("----------------------------------------------");
            changeConfig(userFolder.getName(), userFile, mapper);
            System.out.println("----------------------------------------------");
            System.out.println("finishing change user config file process for " + userFolder.getName());
            System.out.println("----------------------------------------------");
        }
        //change User balance files for all users
//        for (File userFolder : usersFolder.listFiles()) {
//            if (!userFolder.isDirectory()) {
//                continue;
//            }
//            File userBalanceFolder = new File(userFolder, "Balance");
//            if(!userBalanceFolder.isDirectory()){
//                continue;
//            }
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
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
    }

    private static void changeConfig(String userName, File userFile, ObjectMapper mapper) {
        if (!userFile.isFile()) {
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            if (!user.has("production")) {
                return;
            }
            if (!user.get("production").booleanValue()) {
                new UserChangeProfile(userName, UserProfile.PRO_TRADER_TESTER).getResponse();
            } else {
                new UserChangeProfile(userName, UserProfile.PRO_TRADER).getResponse();
            }
            if(!user.has("address")){
                return;
            }
            String address = user.get("address").textValue();
            File userWalletFile = new File(new File(OPERATOR_PATH, "UsersAddresses"), address + ".json");
            JsonNode userWallet = mapper.createObjectNode();
            ((ObjectNode) userWallet).put("address", address);
            if(user.has("privateKey")){
                ((ObjectNode) userWallet).put("privateKey", user.get("privateKey").textValue());
            }
            ((ObjectNode) userWallet).put("userName", userName);
            FileUtil.createFile(userWallet, userWalletFile);
        } catch (IOException ex) {
            Logger.getLogger(UserChangeConfigMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void changeBalance(File balanceFile, ObjectMapper mapper) {
        if (!balanceFile.isFile()) {
            return;
        }
        try {            
            JsonNode balance = mapper.readTree(balanceFile);
            String userModelName = null;
            Double changePrice = null;
            if(balance.has("userModelName")){
                userModelName = balance.get("userModelName").textValue();
            }
            if(balance.has("changePrice")){
                changePrice = balance.get("changePrice").doubleValue();
            }
            String balanceOperationType = balance.get("balanceOperationType").textValue();
            if(balanceOperationType.contains("PLAN")){
                ((ObjectNode) balance).put("balanceOperationType", balanceOperationType.replace("PLAN", "MODEL"));
            }
            if(userModelName != null){
                ((ObjectNode) balance).remove("userModelName");
                ((ObjectNode) balance).put("additionalInfo", "USER MODEL NAME " + userModelName);
            }
            if(changePrice != null){
                ((ObjectNode) balance).remove("changePrice");
                ((ObjectNode) balance).put("additionalInfo", "CURRENCY CHANGE RATE " + BigDecimal.valueOf(changePrice).toPlainString());
            }
            FileUtil.editFile(balance, balanceFile);
        } catch (IOException ex) {
            Logger.getLogger(ConceptChangesMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
