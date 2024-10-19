/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BalanceGetMovementsBetweenTimestampMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String initialTimestamp = "2020-09-12T01:17:23.680Z";
        String finalTimestamp = null;
        File usersFolder = UsersFolderLocator.getFolder();
        ObjectMapper mapper = new ObjectMapper();
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            String userName = userFolder.getName();
            
            File mcBalanceUserFolder = UsersFolderLocator.getMCBalanceFolder(userName);
            for (File mcBalanceUserFile : mcBalanceUserFolder.listFiles()) {
                if (!mcBalanceUserFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode mcBalanceUser = mapper.readTree(mcBalanceUserFile);
                    String timestamp = DateUtil.getDate(mcBalanceUser.get("timestamp").textValue());
                    
                    boolean show = true;
                    if(initialTimestamp != null && initialTimestamp.compareTo(timestamp) > 0){
                        show = false;
                    }
                    if(finalTimestamp != null && finalTimestamp.compareTo(timestamp) < 0){
                        show = false;
                    }
                    if(show){
                        System.out.println("userName: " + userName);
                        System.out.println("mcBalanceUserFile: " + mcBalanceUserFile.getAbsolutePath());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BalanceGetMovementsBetweenTimestampMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
