/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOperationCheckList extends AbstractOperation<ArrayNode> {

    private final String currency;
    private final OTCOperationType otcOTCOperationType;
    private final OTCOperationStatus otcOperationStatus;

    public OTCGetOperationCheckList(String currency, OTCOperationType otcOTCOperationType, OTCOperationStatus otcOperationStatus) {
        super(ArrayNode.class);
        this.currency = currency;
        this.otcOTCOperationType = otcOTCOperationType;
        this.otcOperationStatus = otcOperationStatus;
    }

    @Override
    public void execute() {
        File otcCurrencyConfigFile = OTCFolderLocator.getCurrencyFile(null, currency);
        if (otcCurrencyConfigFile.isFile()) {
            try {
                JsonNode otcCurrencyConfig = mapper.readTree(otcCurrencyConfigFile);
                String chectListType = otcOTCOperationType.name() + "__" + otcOperationStatus.name();
                if (otcCurrencyConfig.has("operationCheckList") && otcCurrencyConfig.get("operationCheckList").has(chectListType)) {
                    super.response = (ArrayNode) otcCurrencyConfig.get("operationCheckList").get(chectListType);
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetOperationCheckList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = mapper.createArrayNode();
    }

}
