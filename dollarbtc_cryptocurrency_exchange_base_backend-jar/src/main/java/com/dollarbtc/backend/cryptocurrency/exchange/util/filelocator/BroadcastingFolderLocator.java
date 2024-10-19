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
 * @author carlos molina
 */
public class BroadcastingFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Broadcasting"));
    }
            
    public static File getOverviewFile() {
        return new File(getFolder() , "overview.json");
    }
    
    public static File getFile(String id) {
        return new File(getFolder(), id + ".json");
    }
    
    public static File getAttachmentsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Attachments"));
    }
    
    public static File getCommentsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Comments"));
    }
    
    public static File getCommentsFile(String id, String episodeTrailerId, int page) {
        return new File(getCommentsFolder(), id + "__" + episodeTrailerId + "__" + page + ".json");
    }
    
    public static File getIndexesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Indexes"));
    }

    public static File getIndexFolder(String index) {
        return FileUtil.createFolderIfNoExist(new File(getIndexesFolder(), index));
    }
    
    public static File getIndexValueFolder(String index, String value) {
        return FileUtil.createFolderIfNoExist(new File(getIndexFolder(index), value));
    }
    
}
