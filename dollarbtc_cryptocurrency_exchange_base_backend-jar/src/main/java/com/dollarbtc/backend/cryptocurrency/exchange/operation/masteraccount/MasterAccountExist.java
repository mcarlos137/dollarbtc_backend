/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountExist extends AbstractOperation<Boolean> {
    
    private final String masterAccountName;

    public MasterAccountExist(String masterAccountName) {
        super(Boolean.class);
        this.masterAccountName = masterAccountName;
    }
        
    @Override
    public void execute() {
        File masterAccountFolder = MasterAccountFolderLocator.getFolderByMasterAccountName(masterAccountName);
        super.response = masterAccountFolder.isDirectory();
    } 
    
}
