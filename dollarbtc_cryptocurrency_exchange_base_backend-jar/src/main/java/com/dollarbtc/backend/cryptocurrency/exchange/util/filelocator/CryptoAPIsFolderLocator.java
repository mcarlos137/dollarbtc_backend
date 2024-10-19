/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class CryptoAPIsFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "CryptoAPIs"));
    }

    public static File getEventsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Events"));
    }

    public static File getEventsFolder(String blockchain, String network, String event) {
        return FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(getEventsFolder(), blockchain), network), event);
    }

    public static File getEventsOldFolder(String blockchain, String network, String event) {
        return FileUtil.createFolderIfNoExist(getEventsFolder(blockchain, network, event), "Old");
    }

    public static File getEventsFile(String blockchain, String network, String event, String id) {
        return new File(getEventsFolder(blockchain, network, event), id + ".json");
    }
    
    public static File getEventSubscriptionsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "EventSubscriptions"));
    }
    
    public static File getEventSubscriptionsFolder(String blockchain, String network, String event) {
        return FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(getEventSubscriptionsFolder(), blockchain), network), event);
    }

    public static File getEventSubscriptionsOldFolder(String blockchain, String network, String event) {
        return FileUtil.createFolderIfNoExist(getEventSubscriptionsFolder(blockchain, network, event), "Old");
    }

    public static File getEventSubscriptionsErrorFolder(String blockchain, String network, String event) {
        return FileUtil.createFolderIfNoExist(getEventSubscriptionsFolder(blockchain, network, event), "Error");
    }
    
    public static File getEventSubscriptionsFile(String blockchain, String network, String event, String id) {
        return new File(getEventSubscriptionsFolder(blockchain, network, event), id + ".json");
    }
    
}
