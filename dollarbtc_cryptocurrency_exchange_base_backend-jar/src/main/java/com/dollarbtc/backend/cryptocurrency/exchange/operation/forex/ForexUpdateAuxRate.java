/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.forex;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ForexFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ForexUpdateAuxRate extends AbstractOperation<Void> {

    private final String[] baseSymbolsAndTargetSymbols;

    public ForexUpdateAuxRate(String[] baseSymbolsAndTargetSymbols) {
        super(Void.class);
        this.baseSymbolsAndTargetSymbols = baseSymbolsAndTargetSymbols;
    }
        
    @Override
    protected void execute() {
        this.method();
    }    
    
    public synchronized void method() {
        String timestamp = DateUtil.getMinuteStartDate(null);
        for (String baseSymbolAndTargetSymbol : baseSymbolsAndTargetSymbols) {
            String baseSymbol = baseSymbolAndTargetSymbol.split("__")[0];
            String targetSymbol = baseSymbolAndTargetSymbol.split("__")[1];
            File baseForexRatesFolder = ForexFolderLocator.getRatesSymbolFolder(baseSymbol);
            JsonNode baseRate = null;
            for (File rateFile : baseForexRatesFolder.listFiles()) {
                if (!rateFile.isFile()) {
                    continue;
                }
                try {
                    baseRate = mapper.readTree(rateFile);
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(ForexUpdateAuxRate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (baseRate == null) {
                continue;
            }
            String baseRateTimestamp = baseRate.get("timestamp").textValue();
            ((ObjectNode) baseRate).put("baseTimestamp", baseRateTimestamp);
            File targetRatesFolder = ForexFolderLocator.getRatesSymbolFolder(targetSymbol);
            File targetRatesOldFolder = FileUtil.createFolderIfNoExist(targetRatesFolder, "Old");
            boolean processed = false;
            for (File rateFile : targetRatesFolder.listFiles()) {
                if (!rateFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode rate = mapper.readTree(rateFile);
                    processed = true;
                    if (baseRateTimestamp.equals(rate.get("baseTimestamp").textValue())) {
                        break;
                    }
                    FileUtil.moveFileToFolder(rateFile, targetRatesOldFolder);
                    ((ObjectNode) baseRate).put("timestamp", timestamp);
                    FileUtil.createFile(baseRate, new File(targetRatesFolder, DateUtil.getFileDate(timestamp) + ".json"));
                } catch (IOException ex) {
                    Logger.getLogger(ForexUpdateAuxRate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!processed) {
                ((ObjectNode) baseRate).put("timestamp", timestamp);
                FileUtil.createFile(baseRate, new File(targetRatesFolder, DateUtil.getFileDate(timestamp) + ".json"));
            }
        }
    }

}
