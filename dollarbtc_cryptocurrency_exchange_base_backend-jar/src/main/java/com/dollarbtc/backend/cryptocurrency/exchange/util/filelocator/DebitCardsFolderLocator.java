/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class DebitCardsFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "DebitCards"));
    }
    
    public static File getFolder(String id) {
        return new File(getFolder(), id);
    }
    
    public static File getBalanceFolder(String id) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(id), "Balance"));
    }
    
    public static File getConfigFile(String id) {
        return new File(getFolder(id), "config.json");
    }
    
    public static File getModelsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Models"));
    }

    public static File getIndexesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Indexes"));
    }

    public static File getIndexesSpecificFolder(String index) {
        return FileUtil.createFolderIfNoExist(getIndexesFolder(), index);
    }
    
    public static File getIndexesSpecificValueFolder(String index, String value) {
        return FileUtil.createFolderIfNoExist(getIndexesSpecificFolder(index), value);
    }
    
}
