/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class MoneyMarketFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "MoneyMarket"));
    }
       
    public static File getTopTradersFile() {
        return new File(getFolder(), "topTraders.json");
    }
    
    public static File getUserNameFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), userName));
    }
    
    public static File getUserNameOldFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getUserNameFolder(userName), "Old"));
    }
            
    public static File getUserNameIndexFile(String userName, String id) {
        return new File(getUserNameFolder(userName), id + ".json");
    }
        
    public static File getOrdersFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Orders"));
    }
        
    public static File getOrderFile(String id) {
        return new File(getOrdersFolder(), id + ".json");
    }
        
    public static File getPairsFile() {
        return new File(getFolder(), "pairs.json");
    }

    public static File getPairFolder(String pair) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            boolean pairExist = false;
            Iterator<String> pairsIterator = mapper.readTree(getPairsFile()).fieldNames();
            while (pairsIterator.hasNext()) {
                String pairsIt = pairsIterator.next();
                if (pairsIt.equals(pair)) {
                    pairExist = true;
                    break;
                }
            }
            if (pairExist) {
                return FileUtil.createFolderIfNoExist(new File(getFolder(), pair));
            }
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketFolderLocator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static File getPairTypeFolder(String pair, String type) {
        return FileUtil.createFolderIfNoExist(new File(getPairFolder(pair), type));
    }
    
    public static File getPairTypeOldFolder(String pair, String type) {
        return FileUtil.createFolderIfNoExist(new File(getPairTypeFolder(pair, type), "Old"));
    }
    
    public static File getPairTypeIndexFile(String pair, String type, String id) {
        return new File(getPairTypeFolder(pair, type), id + ".json");
    }
        
    public static File getBotsFile() {
        return new File(getFolder(), "bots.json");
    }
    
    public static File getBotsActivityFile() {
        return new File(getFolder(), "botsActivity.json");
    }
    
}
