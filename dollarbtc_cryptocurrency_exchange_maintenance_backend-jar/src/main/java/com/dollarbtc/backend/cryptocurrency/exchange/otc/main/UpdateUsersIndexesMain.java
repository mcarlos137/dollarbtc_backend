/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersIndexesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class UpdateUsersIndexesMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> usersIndexesNames = new ArrayList<>();
        usersIndexesNames.add("name");
//        usersIndexesNames.add("active"); 
        usersIndexesNames.add("type");
        usersIndexesNames.add("environment");
        usersIndexesNames.add("operationAccount");
        usersIndexesNames.add("phone");
        usersIndexesNames.add("firstName");
        usersIndexesNames.add("typeDocumentIdentity");
        usersIndexesNames.add("gender");
//        usersIndexesNames.add("questionSecurity");
//        usersIndexesNames.add("answerSecurity");
        usersIndexesNames.add("lastName");
        usersIndexesNames.add("numberDocumentIdentity");
        usersIndexesNames.add("birthdate");
        usersIndexesNames.add("birthplace");
        usersIndexesNames.add("userDirection");
        usersIndexesNames.add("email");
        usersIndexesNames.add("familyName");
        usersIndexesNames.add("familyEmail");
        usersIndexesNames.add("userLocalBitcoin");
        usersIndexesNames.add("userFacebook");
        usersIndexesNames.add("nickname");
        usersIndexesNames.add("address");
        File usersFolder = UsersFolderLocator.getFolder();
        int i = 0;
        for (File userFolder : usersFolder.listFiles()) {
            if (i >= 100) {
                break;
            }
            if (!userFolder.isDirectory()) {
                continue;
            }
//            if (!userFolder.getName().equals("584245522788")) {
//                continue;
//            }
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "INIT USERNAME {0}", userFolder.getName());
            if (updateUserIndexes(mapper, userFolder.getName(), usersIndexesNames)) {
                i++;
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "FINISH USERNAME {0}", userFolder.getName());
            } else {
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "SKIP USERNAME {0}", userFolder.getName());
            }
        }
    }

    private static boolean updateUserIndexes(ObjectMapper mapper, String userName, List<String> usersIndexesNames) {
        File userIndexesFile = UsersIndexesFolderLocator.getFile(userName);
        JsonNode userIndexes = mapper.createObjectNode();
        if (userIndexesFile.isFile()) {
            try {
                userIndexes = mapper.readTree(userIndexesFile);
            } catch (IOException ex) {
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (userIndexes.has("update") && !userIndexes.get("update").booleanValue()) {
            return false;
        }
        Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "update");
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        JsonNode userConfig = null;
        try {
            userConfig = mapper.readTree(userConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (userConfig == null) {
            return false;
        }
        ((ObjectNode) userIndexes).put("update", false);
        Iterator<String> userIndexNamesIterator = userIndexes.fieldNames();
        while (userIndexNamesIterator.hasNext()) {
            String userIndexNameIt = userIndexNamesIterator.next();
            if (userIndexNameIt.equals("update")) {
                continue;
            }
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "index name {0}", userIndexNameIt);
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "index value {0}", userIndexes.get(userIndexNameIt).textValue().replaceAll("'", "").trim().toUpperCase());
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "DELETE");
            File userIndexValueFolder = new File(UsersIndexesFolderLocator.getFolder(userIndexNameIt), userIndexes.get(userIndexNameIt).textValue().replaceAll("'", "").trim().toUpperCase().replace(" ", "_"));
            FileUtil.deleteFile(new File(userIndexValueFolder, userName + ".json"));
            if (userIndexValueFolder.isDirectory() && userIndexValueFolder.list().length == 0) {
                FileUtil.deleteFolder(userIndexValueFolder);
            }
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "file deleted");
            userIndexNamesIterator.remove();
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "field deleted");
        }
        String timestamp = DateUtil.getCurrentDate();
        for (String userIndexName : usersIndexesNames) {
            Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "index name {0}", userIndexName);
            if (userConfig.has(userIndexName) && userConfig.get(userIndexName) != null && userConfig.get(userIndexName).textValue() != null && !userConfig.get(userIndexName).textValue().equals("")) {
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "index value {0}", userConfig.get(userIndexName).textValue().replaceAll("'", "").trim().toUpperCase());
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "CREATE");
                ((ObjectNode) userIndexes).put(userIndexName, userConfig.get(userIndexName).textValue().replaceAll("'", "").trim().toUpperCase());
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "field created");
                File usersIndexValueFolder = FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(UsersIndexesFolderLocator.getFolder(userIndexName)), userConfig.get(userIndexName).textValue().replaceAll("'", "").trim().toUpperCase().replace(" ", "_"));
                ObjectNode userIndexValue = mapper.createObjectNode();
                ((ObjectNode) userIndexValue).put("timestamp", timestamp);
                FileUtil.createFile(userIndexValue, new File(usersIndexValueFolder, userName + ".json"));
                Logger.getLogger(UpdateUsersIndexesMain.class.getName()).log(Level.INFO, "file created");
            }
        }
        FileUtil.editFile(userIndexes, userIndexesFile);
        return true;
    }

}
