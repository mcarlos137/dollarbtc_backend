/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyOrdersFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyOrderList extends AbstractOperation<ArrayNode> {

    private final String status; //PROCESSING, OK, FAILED

    public MoneyOrderList(String status) {
        super(ArrayNode.class);
        this.status = status;
    }

    @Override
    public void execute() {
        File moneyOrdersOperationsStatusFolder = new File(MoneyOrdersFolderLocator.getOperationsFolder(), status);
        ArrayNode moneyOrderList = mapper.createArrayNode();
        for(File moneyOrderOperationFile : moneyOrdersOperationsStatusFolder.listFiles()){
            if(!moneyOrderOperationFile.isFile()){
                continue;
            }
            try {
                moneyOrderList.add(mapper.readTree(moneyOrderOperationFile));
            } catch (IOException ex) {
                Logger.getLogger(MoneyOrderList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = moneyOrderList;
    }

}
