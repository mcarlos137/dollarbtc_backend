/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AttachmentsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class MoveAttachmentFilesToUserFolderMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        File usersFolder = UsersFolderLocator.getFolder();
        File attachmentsFolder = AttachmentsFolderLocator.getFolder();
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (!userFolder.getName().equals("alphaprofits@protonmail.com")) {
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "continue");
                continue;
            }
            Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
            Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "user " + userFolder.getName());
            File userConfigFile = new File(userFolder, "config.json");
            File userAttachmentsFolder = UsersFolderLocator.getAttachmentsFolder(userFolder.getName());
            try {
                JsonNode userConfig = mapper.readTree(userConfigFile);
                String userConfigString = userConfig.toString();
                Set<String> stringsToRemove = new HashSet<>();
                while (true) {
                    if (userConfigString.contains("https://attachment.dollarbtc.com")) {
                        Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
                        userConfigString = userConfigString.substring(userConfigString.indexOf("https://attachment.dollarbtc.com") + 33);
                        String attachmentSubFolderPath = userConfigString.substring(0, userConfigString.indexOf("/"));
                        userConfigString = userConfigString.substring(attachmentSubFolderPath.length() + 1);
                        String attachmentFileName;
                        if (userConfigString.contains(",")) {
                            attachmentFileName = userConfigString.substring(0, userConfigString.indexOf(",") - 1);
                        } else {
                            attachmentFileName = userConfigString.substring(0, userConfigString.indexOf("}") - 1);
                        }
                        File attachmentFile = new File(attachmentsFolder.getAbsolutePath() + "/" + attachmentSubFolderPath + "/" + attachmentFileName);
                        stringsToRemove.add("https://attachment.dollarbtc.com" + "/" + attachmentSubFolderPath + "/");
                        File newFile = new File(userAttachmentsFolder, attachmentFileName);
                        FileUtil.moveFileToFile(attachmentFile, newFile);
                        Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "FROM " + attachmentFile.getAbsolutePath());
                        Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "TO " + newFile.getAbsolutePath());
                        Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
                    } else {
                        break;
                    }
                }
                userConfigString = userConfig.toString();
                for (String stringToRemove : stringsToRemove) {
                    userConfigString = userConfigString.replace(stringToRemove, "");
                    Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "REMOVE " + stringToRemove);
                }
                if (!stringsToRemove.isEmpty()) {
                    FileUtil.editFile(mapper.readTree(userConfigString), userConfigFile);
                }
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
            } catch (IOException ex) {
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            File userGoogleAuthenticatorFile = UsersFolderLocator.getGoogleAuthenticatorFile(userFolder.getName());
            if (!userGoogleAuthenticatorFile.isFile()) {
                continue;
            }
            try {
                JsonNode userGoogleAuthenticator = mapper.readTree(userGoogleAuthenticatorFile);
                if(!userGoogleAuthenticator.has("qrCodeUrl")){
                    continue;
                }
                String qrCodeUrl = userGoogleAuthenticator.get("qrCodeUrl").textValue();
                File attachmentFile = new File(attachmentsFolder.getAbsolutePath() + qrCodeUrl.replace("https://attachment.dollarbtc.com", ""));
                File userGAQRCodeFile = UsersFolderLocator.getGAQRCodeFile(userFolder.getName());
                FileUtil.moveFileToFile(attachmentFile, userGAQRCodeFile);
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "FROM " + attachmentFile.getAbsolutePath());
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "TO " + userGAQRCodeFile.getAbsolutePath());
                ((ObjectNode) userGoogleAuthenticator).remove("qrCodeUrl");
                FileUtil.editFile(userGoogleAuthenticator, userGoogleAuthenticatorFile);
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.INFO, "REMOVE " + "https://attachment.dollarbtc.com/gaQRCodes/");
            } catch (IOException ex) {
                Logger.getLogger(MoveAttachmentFilesToUserFolderMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
