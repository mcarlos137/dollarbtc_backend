/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCChangeStatusesIndexInCentralFolder extends AbstractOperation<Void> {
    
    private final String userName, id; 
    private final OTCOperationStatus otcOperationStatusBase; 
    private final OTCOperationStatus otcOperationStatusTarget;

    public OTCChangeStatusesIndexInCentralFolder(String userName, String id, OTCOperationStatus otcOperationStatusBase, OTCOperationStatus otcOperationStatusTarget) {
        super(Void.class);
        this.userName = userName;
        this.id = id;
        this.otcOperationStatusBase = otcOperationStatusBase;
        this.otcOperationStatusTarget = otcOperationStatusTarget;
    }
        
    @Override
    public void execute() {
        File indexFile = new File(new File(OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Statuses"), otcOperationStatusBase.name()), id + ".json");
        File targetFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Statuses"), otcOperationStatusTarget.name());
        FileUtil.moveFileToFolder(indexFile, targetFolder);
        //CHECK INDEX
        File movedIndexFile = new File(targetFolder, indexFile.getName());
        if(!movedIndexFile.isFile()){
            try {
                FileUtil.createFile(mapper.readTree(new File(new File(OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "UserNames"), userName), id + ".json")), movedIndexFile);
            } catch (IOException ex) {
                Logger.getLogger(OTCChangeStatusesIndexInCentralFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
