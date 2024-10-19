/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;

/**
 *
 * @author ricardo torres
 */
public class MoneyOrdersFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "MoneyOrders"));
    }
    
    public static File getOperationsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Operations"));
    }
    
    public static File getAttachmentsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Attachments"));
    }
        
    public static File getConfigFile() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "config.json"));
    }    
    
}
