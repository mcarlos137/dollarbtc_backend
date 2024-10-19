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
public class OTCGetAutomaticChatMessages extends AbstractOperation<ArrayNode> {
    
    private final String currency, language;
    private final OTCOperationType otcOTCOperationType;
    private final OTCOperationStatus otcOperationStatus;

    public OTCGetAutomaticChatMessages(String currency, String language, OTCOperationType otcOTCOperationType, OTCOperationStatus otcOperationStatus) {
        super(ArrayNode.class);
        this.currency = currency;
        this.language = language;
        this.otcOTCOperationType = otcOTCOperationType;
        this.otcOperationStatus = otcOperationStatus;
    }
    
    @Override
    protected void execute() {
        if(otcOTCOperationType == null && otcOperationStatus == null){
            super.response = method(currency, language);
        } else if(otcOTCOperationType != null && otcOperationStatus != null){
            super.response = method(currency, language, otcOTCOperationType, otcOperationStatus);
        }
    }
    
    private ArrayNode method(String currency, String language) {
        File otcCurrencyConfigFile = OTCFolderLocator.getCurrencyFile(null, currency);
        if (otcCurrencyConfigFile.isFile()) {
            try {
                JsonNode otcCurrencyConfig = mapper.readTree(otcCurrencyConfigFile);
                if (otcCurrencyConfig.has("automaticChatMessages") && otcCurrencyConfig.get("automaticChatMessages").has(language)) {
                    return (ArrayNode) otcCurrencyConfig.get("automaticChatMessages").get(language);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetAutomaticChatMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mapper.createArrayNode();
    }

    private ArrayNode method(String currency, String language, OTCOperationType otcOTCOperationType, OTCOperationStatus otcOperationStatus) {
        File otcCurrencyConfigFile = OTCFolderLocator.getCurrencyFile(null, currency);
        if (otcCurrencyConfigFile.isFile()) {
            try {
                JsonNode otcCurrencyConfig = mapper.readTree(otcCurrencyConfigFile);
                if (otcCurrencyConfig.has("automaticChatMessages") && otcCurrencyConfig.get("automaticChatMessages").has(language + "__" + otcOTCOperationType.name() + "__" + otcOperationStatus.name())) {
                    return (ArrayNode) otcCurrencyConfig.get("automaticChatMessages").get(language + "__" + otcOTCOperationType.name() + "__" + otcOperationStatus.name());
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetAutomaticChatMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mapper.createArrayNode();
    }

}
