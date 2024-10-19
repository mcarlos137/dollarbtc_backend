/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class BalanceOperationChangeMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        System.out.println("----------------------------------------------");
//        System.out.println("starting process");
//        System.out.println("----------------------------------------------");
//        File usersFolder = new File(ROOT_PATH, "Users");
//        ObjectMapper mapper = new ObjectMapper();
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
//        System.out.println("----------------------------------------------");
//        System.out.println("finishing process");
//        System.out.println("----------------------------------------------");
    }
    
    private static void changeBalance(File balanceFile, ObjectMapper mapper) {
        if (!balanceFile.isFile()) {
            return;
        }
        try {
            JsonNode balance = mapper.readTree(balanceFile);
            String balanceString = balance.toString();
            balanceString = balanceString.replace("INITIAL_DEPOSIT", "INITIAL_MOVEMENT");
            balanceString = balanceString.replace("WITHDRAW", "SEND");
            balanceString = balanceString.replace("DEPOSIT", "RECEIVE");
            balanceString = balanceString.replace("MODEL_ACTIVATION", "PLAN_ACTIVATION");
            balanceString = balanceString.replace("MODEL_INACTIVATION", "PLAN_INACTIVATION");
            balanceString = balanceString.replace("TRANSFER_TO_DOLLARBTC", "SEND");
            balanceString = balanceString.replace("TRANSFER_FROM_DOLLARBTC", "RECEIVE");
            balance = mapper.readTree(balanceString);
            FileUtil.editFile(balance, balanceFile);
        } catch (IOException ex) {
            Logger.getLogger(BalanceOperationChangeMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
