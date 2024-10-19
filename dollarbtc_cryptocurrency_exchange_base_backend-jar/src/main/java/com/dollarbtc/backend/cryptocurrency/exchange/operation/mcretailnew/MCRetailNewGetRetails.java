/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewGetRetails extends AbstractOperation<ArrayNode> {
    
    private final String currency;
    private final MCRetailOperationType mcRetailOperationType;

    public MCRetailNewGetRetails(String currency, MCRetailOperationType mcRetailOperationType) {
        super(ArrayNode.class);
        this.currency = currency;
        this.mcRetailOperationType = mcRetailOperationType;
    }
    
    @Override
    public void execute() {
        ArrayNode moneyclickRetails = mapper.createArrayNode();
        File moneyclickRetailsFolder = MoneyclickFolderLocator.getRetailsFolder();
        for (File moneyclickRetailFolder : moneyclickRetailsFolder.listFiles()) {
            if (!moneyclickRetailFolder.isDirectory()) {
                continue;
            }
            File moneyclickRetailFile = new File(moneyclickRetailFolder, "config.json");
            if (!moneyclickRetailFile.isFile()) {
                continue;
            }
            boolean addMoneyclickRetail = false;
            try {
                JsonNode moneyclickRetail = mapper.readTree(moneyclickRetailFile);
                if (currency == null) {
                    addMoneyclickRetail = true;
                } else {
                    Iterator<JsonNode> moneyclickRetailOperationsIterator = moneyclickRetail.get("operations").iterator();
                    while (moneyclickRetailOperationsIterator.hasNext()) {
                        JsonNode moneyclickRetailOperationsIt = moneyclickRetailOperationsIterator.next();
                        if (mcRetailOperationType == null) {
                            if (moneyclickRetailOperationsIt.get("currency").textValue().equals(currency)) {
                                addMoneyclickRetail = true;
                            } else {
                                moneyclickRetailOperationsIterator.remove();
                            }
                        } else {
                            if (moneyclickRetailOperationsIt.get("currency").textValue().equals(currency)
                                    && MCRetailOperationType.valueOf(moneyclickRetailOperationsIt.get("type").textValue()).equals(mcRetailOperationType)) {
                                addMoneyclickRetail = true;
                            } else {
                                moneyclickRetailOperationsIterator.remove();
                            }
                        }
                    }
                }
                Iterator<JsonNode> moneyclickRetailOperationsIterator = moneyclickRetail.get("operations").iterator();
                while (moneyclickRetailOperationsIterator.hasNext()) {
                    JsonNode moneyclickRetailOperationsIt = moneyclickRetailOperationsIterator.next();
                    if (moneyclickRetailOperationsIt.get("currency").textValue().equals(currency)) {
                        BaseOperation.addFieldsToRetailOperation(moneyclickRetailOperationsIt, mapper);
                    }
                }
                if (addMoneyclickRetail) {
                    BaseOperation.addFieldsToRetail(moneyclickRetail, mapper);
                    moneyclickRetails.add(moneyclickRetail);
                }
            } catch (IOException ex) {
                Logger.getLogger(MCRetailNewGetRetails.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = moneyclickRetails;
    }
    
}
