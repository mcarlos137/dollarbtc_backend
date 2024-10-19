/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetOperatorName extends AbstractOperation<String> {

    private final String masterAccountName;

    public MasterAccountGetOperatorName(String masterAccountName) {
        super(String.class);
        this.masterAccountName = masterAccountName;
    }

    @Override
    public void execute() {
        try {
            Iterator<JsonNode> operatorsIterator = new ObjectMapper().readTree(BaseFilesLocator.getOperatorsFile()).iterator();
            while (operatorsIterator.hasNext()) {
                JsonNode operatorsIt = operatorsIterator.next();
                String operatorName = operatorsIt.textValue();
                File masterAccountFolder = new File(MasterAccountFolderLocator.getFolder(operatorName), masterAccountName);
                if (masterAccountFolder.isDirectory()) {
                    super.response = operatorName;
                    return;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MasterAccountGetOperatorName.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = null;
    }

}
