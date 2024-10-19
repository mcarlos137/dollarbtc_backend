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
public class NotificationsFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Notifications"));
    }
    
    public static File getTopicsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Topics"));
    }
    
    public static File getTopicFile(String id) {
        return new File(getTopicsFolder(), id + ".json");
    }
    
    public static File getMessagesFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Messages"));
    }
    
    public static File getGroupsFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Groups"));
    }
    
    public static File getGroupFile(String name) {
        return new File(getGroupsFolder(), name + ".json");
    }
    
    public static File getFirebaseFile() {
        return new File(getFolder(), "firebase.json");
    }
    
    public static File getKaikaiFirebaseFile() {
        return new File(getFolder(), "firebase_kaikai.json");
    }
    
}
