/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewProcessOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewProcessOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class CancelMCRetailOperationsMain {

    public static void main(String[] args) {
        System.out.println("Starting CancelMCRetailOperationsMain");
        String baseTimestamp = DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), 60);
        File moneyclickOperationsStatusesFolder = MoneyclickFolderLocator.getOperationsIndexFolder("Statuses");
        if(!moneyclickOperationsStatusesFolder.isDirectory()){
            return;
        }
        File moneyclickOperationsStatusProcessingFolder = new File(moneyclickOperationsStatusesFolder, MCRetailOperationStatus.PROCESSING.name());
        if(!moneyclickOperationsStatusProcessingFolder.isDirectory()){
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        for(File moneyclickOperationsStatusProcessingFile : moneyclickOperationsStatusProcessingFolder.listFiles()){
            if(!moneyclickOperationsStatusProcessingFile.isFile()){
                continue;
            }
            try {
                JsonNode moneyclickOperationsStatusProcessing = mapper.readTree(moneyclickOperationsStatusProcessingFile);
                String timestamp = moneyclickOperationsStatusProcessing.get("timestamp").textValue();
                String id = moneyclickOperationsStatusProcessing.get("id").textValue();
                if(DateUtil.parseDate(timestamp).before(DateUtil.parseDate(baseTimestamp))){
                    System.out.println("OPERATION: " + id + " CANCELED");
                    new MCRetailNewProcessOperation(new MCRetailNewProcessOperationRequest("AUTOMATIC", id, MCRetailOperationStatus.CANCELED, false)).getResponse();
                }
            } catch (IOException ex) {
                Logger.getLogger(CancelMCRetailOperationsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Finishing CancelMCRetailOperationsMain");
    }

}
