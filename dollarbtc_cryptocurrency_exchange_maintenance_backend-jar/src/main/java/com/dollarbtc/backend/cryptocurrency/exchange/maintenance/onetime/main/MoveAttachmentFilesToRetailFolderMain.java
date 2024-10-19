/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AttachmentsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class MoveAttachmentFilesToRetailFolderMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File moneyclickRetailsFolder = MoneyclickFolderLocator.getRetailsFolder();
        File attachmentsFolder = AttachmentsFolderLocator.getFolder();
        for(File moneyclickRetailFolder : moneyclickRetailsFolder.listFiles()){
            if(!moneyclickRetailFolder.isDirectory()){
                continue;
            }
            File moneyclickRetailConfigFile = new File(moneyclickRetailFolder, "config.json");
            File moneyclickRetailAttachmentsFolder = MoneyclickFolderLocator.getRetailAttachmentsFolder(moneyclickRetailFolder.getName());
            try {
                JsonNode moneyclickRetailConfig = new ObjectMapper().readTree(moneyclickRetailConfigFile);
                if(moneyclickRetailConfig.has("attachments")){
                    Iterator<JsonNode> moneyclickRetailConfigAttachmentsIterator = moneyclickRetailConfig.get("attachments").iterator();
                    while (moneyclickRetailConfigAttachmentsIterator.hasNext()) {
                        JsonNode moneyclickRetailConfigAttachmentsIt = moneyclickRetailConfigAttachmentsIterator.next();
                        String filePath = moneyclickRetailConfigAttachmentsIt.textValue().replace("https://attachment.dollarbtc.com", "");
                        File attachmentFile = new File(attachmentsFolder.getAbsolutePath() + filePath);
                        if(!attachmentFile.isFile()){
                            Logger.getLogger(MoveAttachmentFilesToRetailFolderMain.class.getName()).log(Level.INFO, "FILE DOES NOT EXIST");
                        } else {
                            File newFile = new File(moneyclickRetailAttachmentsFolder, attachmentFile.getName());
                            FileUtil.moveFileToFile(attachmentFile, newFile);
                            Logger.getLogger(MoveAttachmentFilesToRetailFolderMain.class.getName()).log(Level.INFO, "FROM " + attachmentFile.getAbsolutePath());
                            Logger.getLogger(MoveAttachmentFilesToRetailFolderMain.class.getName()).log(Level.INFO, "TO " + newFile.getAbsolutePath());
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MoveAttachmentFilesToRetailFolderMain.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
}
