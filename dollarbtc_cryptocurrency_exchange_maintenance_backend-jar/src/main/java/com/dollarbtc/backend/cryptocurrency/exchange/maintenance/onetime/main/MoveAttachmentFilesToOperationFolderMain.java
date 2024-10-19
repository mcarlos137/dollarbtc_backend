/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AdminFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AttachmentsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class MoveAttachmentFilesToOperationFolderMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        File otcOperationsFolder = OTCFolderLocator.getOperationsFolder(null);
        File attachmentsFolder = AttachmentsFolderLocator.getFolder();
        for (File otcOperationFolder : otcOperationsFolder.listFiles()) {
            if (!otcOperationFolder.isDirectory() || otcOperationFolder.getName().equals("Indexes") || otcOperationFolder.getName().equals("ChangeStatuses")) {
                continue;
            }
            Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
            Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "OPERATION ID {0}", otcOperationFolder.getName());
            File otcOperationAttachmentsFolder = OTCFolderLocator.getOperationIdAttachmentsFolder(null, otcOperationFolder.getName());
            List<File> otcOperationMessagesFolders = new ArrayList<>();
            File otcOperationIdMessagesFolder = OTCFolderLocator.getOperationIdMessagesSideFolder(null, otcOperationFolder.getName(), "User");
            if (otcOperationIdMessagesFolder.isDirectory()) {
                otcOperationMessagesFolders.add(otcOperationIdMessagesFolder);
            }
            otcOperationIdMessagesFolder = new File(otcOperationIdMessagesFolder, "Old");
            if (otcOperationIdMessagesFolder.isDirectory()) {
                otcOperationMessagesFolders.add(otcOperationIdMessagesFolder);
            }
            otcOperationIdMessagesFolder = OTCFolderLocator.getOperationIdMessagesSideFolder(null, otcOperationFolder.getName(), "Admin");
            if (otcOperationIdMessagesFolder.isDirectory()) {
                otcOperationMessagesFolders.add(otcOperationIdMessagesFolder);
            }
            otcOperationIdMessagesFolder = new File(otcOperationIdMessagesFolder, "Old");
            if (otcOperationIdMessagesFolder.isDirectory()) {
                otcOperationMessagesFolders.add(otcOperationIdMessagesFolder);
            }
            for (File otcOperationMessagesFolder : otcOperationMessagesFolders) {
                for (File otcOperationMessageFile : otcOperationMessagesFolder.listFiles()) {
                    if (!otcOperationMessageFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode otcOperationMessage = mapper.readTree(otcOperationMessageFile);
                        if (!otcOperationMessage.has("attachmentURL")) {
                            continue;
                        }
                        String attachmentPath = otcOperationMessage.get("attachmentURL").textValue().replace("https://attachment.dollarbtc.com", "");
                        File attachmentFile = new File(attachmentsFolder.getAbsolutePath() + attachmentPath);
                        ((ObjectNode) otcOperationMessage).remove("attachmentURL");
                        ((ObjectNode) otcOperationMessage).put("attachment", attachmentFile.getName());
                        FileUtil.editFile(otcOperationMessage, otcOperationMessageFile);
                        Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "REMOVE FIELD attachmentURL AND ADDING FIELD attachment " + attachmentFile.getName());
                        if (!attachmentFile.isFile()) {
                            continue;
                        }
                        File newFile = new File(otcOperationAttachmentsFolder, attachmentFile.getName());
                        FileUtil.moveFileToFile(attachmentFile, newFile);
                        Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "FROM " + attachmentFile.getAbsolutePath());
                        Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "TO " + newFile.getAbsolutePath());
                    } catch (IOException ex) {
                        Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
        }
        List<File> adminOperationMessagesFolders = new ArrayList<>();
        File adminOperationMessagesFolder = AdminFolderLocator.getOperationMessagesFolder();
        if (adminOperationMessagesFolder.isDirectory()) {
            adminOperationMessagesFolders.add(adminOperationMessagesFolder);
        }
        adminOperationMessagesFolder = new File(adminOperationMessagesFolder, "Old");
        if (adminOperationMessagesFolder.isDirectory()) {
            adminOperationMessagesFolders.add(adminOperationMessagesFolder);
        }
        for (File adminOperationMsFolder : adminOperationMessagesFolders) {
            for (File adminOperationMsFile : adminOperationMsFolder.listFiles()) {
                if (!adminOperationMsFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode adminOperationMs = mapper.readTree(adminOperationMsFile);
                    if (!adminOperationMs.has("attachmentURL")) {
                        continue;
                    }
                    String id = adminOperationMs.get("id").textValue();
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "OPERATION ID {0}", id);
                    String attachmentPath = adminOperationMs.get("attachmentURL").textValue().replace("https://attachment.dollarbtc.com", "");
                    File attachmentFile = new File(attachmentsFolder.getAbsolutePath() + attachmentPath);
                    ((ObjectNode) adminOperationMs).remove("attachmentURL");
                    ((ObjectNode) adminOperationMs).put("attachment", attachmentFile.getName());
                    FileUtil.editFile(adminOperationMs, adminOperationMsFile);
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "REMOVE FIELD attachmentURL AND ADDING FIELD attachment {0}", attachmentFile.getName());
                    if (!attachmentFile.isFile()) {
                        Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
                        continue;
                    }
                    File otcOperationAttachmentsFolder = OTCFolderLocator.getOperationIdAttachmentsFolder(null, id);
                    File newFile = new File(otcOperationAttachmentsFolder, attachmentFile.getName());
                    FileUtil.moveFileToFile(attachmentFile, newFile);
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "FROM {0}", attachmentFile.getAbsolutePath());
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "TO {0}", newFile.getAbsolutePath());
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.INFO, "---------------------------------------");
                } catch (IOException ex) {
                    Logger.getLogger(MoveAttachmentFilesToOperationFolderMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
