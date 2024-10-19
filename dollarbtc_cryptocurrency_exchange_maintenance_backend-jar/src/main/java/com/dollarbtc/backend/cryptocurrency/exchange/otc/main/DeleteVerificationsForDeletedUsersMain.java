/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersVerificationFolderLocator;
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
public class DeleteVerificationsForDeletedUsersMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        for (File usersVerificationStatusFolder : UsersVerificationFolderLocator.getFolder().listFiles()) {
            if (!usersVerificationStatusFolder.isDirectory()) {
                continue;
            }
            for (File userVerificationFile : usersVerificationStatusFolder.listFiles()) {
                if (!userVerificationFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userVerification = mapper.readTree(userVerificationFile);
                    String userName = userVerification.get("userName").textValue();
                    File userConfigFile = UsersFolderLocator.getConfigFile(userName);
                    if (!userConfigFile.isFile()) {
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "USERNAME {0}", userName);
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "1");
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "DELETE {0}", userVerificationFile);
                        FileUtil.deleteFile(userVerificationFile);
                        continue;
                    }
                    JsonNode userConfig = mapper.readTree(userConfigFile);
                    if (!userConfig.has("verification")) {
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "USERNAME {0}", userName);
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "2");
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "DELETE {0}", userVerificationFile);
                        FileUtil.deleteFile(userVerificationFile);
                        continue;
                    }
                    boolean delete = true;
                    String verificationOperationId = userVerification.get("verificationOperationId").textValue();
                    if (userConfig.get("verification").has("C") && verificationOperationId.equals(userConfig.get("verification").get("C").get("verificationOperationId").textValue())) {
                        delete = false;
                    }
                    if (userConfig.get("verification").has("D") && verificationOperationId.equals(userConfig.get("verification").get("D").get("verificationOperationId").textValue())) {
                        delete = false;
                    }
                    if (delete) {
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "USERNAME {0}", userName);
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "3");
                        Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.INFO, "DELETE {0}", userVerificationFile);
                        FileUtil.deleteFile(userVerificationFile);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DeleteVerificationsForDeletedUsersMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
