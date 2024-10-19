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
public class SubscriptionsEventsFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "SubscriptionsEvents"));
    }

    public static File getFile(String id) {
        return new File(getFolder(), id + ".json");
    }

    public static File getIndexesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Indexes"));
    }
        
    public static File getIndexesSpecificFolder(String index){
        return FileUtil.createFolderIfNoExist(getIndexesFolder(), index);
    }
    
    public static File getIndexesSpecificFolder(String index, String value){
        return FileUtil.createFolderIfNoExist(getIndexesSpecificFolder(index), value);
    }

}
