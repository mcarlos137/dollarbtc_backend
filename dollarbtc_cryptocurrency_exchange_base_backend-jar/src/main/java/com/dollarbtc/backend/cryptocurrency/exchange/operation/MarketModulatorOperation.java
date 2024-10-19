/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.marketmodulator.MarketModulatorModifyAutomaticRulesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.marketmodulator.MarketModulatorModifyManualRulesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MarketModulatorFolderLocator;
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
public class MarketModulatorOperation {

    public static String modifyAutomaticRules(MarketModulatorModifyAutomaticRulesRequest marketModulatorModifyAutomaticRulesRequest) {
        File marketModulatorAutomaticRulesFile = MarketModulatorFolderLocator.getAutomaticRulesFile();
        FileUtil.editFile(marketModulatorModifyAutomaticRulesRequest.toJsonNode(), marketModulatorAutomaticRulesFile);
        return "OK";
    }
    
    public static String modifyManualRules(MarketModulatorModifyManualRulesRequest marketModulatorModifyManualRulesRequest) {
        File marketModulatorManualRulesFile = MarketModulatorFolderLocator.getManualRulesFile();
        FileUtil.editFile(marketModulatorModifyManualRulesRequest.toJsonNode(), marketModulatorManualRulesFile);
        return "OK";
    }
    
    public static JsonNode getAutomaticRules(){
        File marketModulatorAutomaticRulesFile = MarketModulatorFolderLocator.getAutomaticRulesFile();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(marketModulatorAutomaticRulesFile);
        } catch (IOException ex) {
            Logger.getLogger(MarketModulatorOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }
    
    public static JsonNode getManualRules(){
        File marketModulatorManualRulesFile = MarketModulatorFolderLocator.getManualRulesFile();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(marketModulatorManualRulesFile);
        } catch (IOException ex) {
            Logger.getLogger(MarketModulatorOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }
    
    public static JsonNode getActiveSymbols() {
        File marketModulatorActiveSymbolsFile = MarketModulatorFolderLocator.getActiveSymbolsFile();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(marketModulatorActiveSymbolsFile);
        } catch (IOException ex) {
            Logger.getLogger(MarketModulatorOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }

}
