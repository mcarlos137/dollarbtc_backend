/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.ModelOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class WebsocketsToFalseMain {

    public static void main(String[] args) {
        File exchangesFolder = ExchangesFolderLocator.getFolder();
        if (!exchangesFolder.exists()) {
            return;
        }
        for (File exchangeFolder : exchangesFolder.listFiles()) {
            if (exchangeFolder.isDirectory()) {
                for (File symbolFolder : exchangeFolder.listFiles()) {
                    if (!symbolFolder.exists()) {
                        continue;
                    }
                    if (symbolFolder.isDirectory()) {
                        File websocketFile = new File(new File(symbolFolder, "Trades"), "websocket.json");
                        if(!websocketFile.exists() || !websocketFile.isFile()){
                            continue;
                        }
                        JsonNode websocket = null;
                        try {
                            websocket = new ObjectMapper().readTree(websocketFile);
                        } catch (IOException ex) {
                            Logger.getLogger(ModelOperation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ((ObjectNode) websocket).put("active", false);
                        FileUtil.editFile(websocket, websocketFile);
                    }
                }
            }
        }
        System.exit(0);
    }

}
