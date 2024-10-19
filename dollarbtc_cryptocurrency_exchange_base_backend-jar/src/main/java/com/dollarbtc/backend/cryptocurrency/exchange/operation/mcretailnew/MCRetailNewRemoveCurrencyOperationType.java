/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewRemoveCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class MCRetailNewRemoveCurrencyOperationType extends AbstractOperation<String> {
    
    private final MCRetailNewRemoveCurrencyOperationTypeRequest mcRetailNewRemoveCurrencyOperationTypeRequest;

    public MCRetailNewRemoveCurrencyOperationType(MCRetailNewRemoveCurrencyOperationTypeRequest mcRetailNewRemoveCurrencyOperationTypeRequest) {
        super(String.class);
        this.mcRetailNewRemoveCurrencyOperationTypeRequest = mcRetailNewRemoveCurrencyOperationTypeRequest;
    }
        
    @Override
    public void execute() {
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(mcRetailNewRemoveCurrencyOperationTypeRequest.getId());
        if (!moneyclickRetailConfigFile.isFile()) {
            this.response = "RETAIL ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            ArrayNode moneyclickRetailConfigOperations = (ArrayNode) moneyclickRetailConfig.get("operations");
            Iterator<JsonNode> moneyclickRetailConfigOperationsIterator = moneyclickRetailConfigOperations.iterator();
            while (moneyclickRetailConfigOperationsIterator.hasNext()) {
                JsonNode moneyclickRetailConfigOperationsIt = moneyclickRetailConfigOperationsIterator.next();
                if (moneyclickRetailConfigOperationsIt.get("currency").textValue().equals(mcRetailNewRemoveCurrencyOperationTypeRequest.getCurrency())
                        && MCRetailOperationType.valueOf(moneyclickRetailConfigOperationsIt.get("type").textValue()).equals(mcRetailNewRemoveCurrencyOperationTypeRequest.getMcRetailOperationType())) {
                    moneyclickRetailConfigOperationsIterator.remove();
                }
            }
            FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
            this.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewRemoveCurrencyOperationType.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.response = "FAIL";
    }
    
}
