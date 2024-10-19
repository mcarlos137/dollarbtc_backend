/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class CopyDataToLocalBitcoinsFolderMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File websiteTickersFolder = new File(new File(OPERATOR_PATH, "Website"), "Tickers");
        File localBitcoinsTickersFolder = new File(new File(OPERATOR_PATH, "LocalBitcoins"), "Tickers");
        ObjectMapper mapper = new ObjectMapper();
        for (File websiteTickerSymbolFolder : websiteTickersFolder.listFiles()) {
            if (!websiteTickerSymbolFolder.isDirectory()) {
                continue;
            }
            System.out.println("websiteTickerSymbolFolder.getName(): " + websiteTickerSymbolFolder.getName());
            File localBitcoinsTickerSymbolFolder = FileUtil.createFolderIfNoExist(localBitcoinsTickersFolder, websiteTickerSymbolFolder.getName());
            for (File websiteTickerFolderOrFile : websiteTickerSymbolFolder.listFiles()) {
                if (websiteTickerFolderOrFile.isDirectory() && websiteTickerFolderOrFile.getName().equals("Old")) {
                    File localBitcoinsTickerFolderOrFile = FileUtil.createFolderIfNoExist(localBitcoinsTickerSymbolFolder, "Old");
                    for (File websiteTickerFile : websiteTickerFolderOrFile.listFiles()) {
                        System.out.println("websiteTickerFile.getName(): " + websiteTickerFile.getName());
                        if (!websiteTickerFile.isFile()) {
                            continue;
                        }
                        try {
                            JsonNode websiteTicker = mapper.readTree(websiteTickerFile);
                            ((ObjectNode) websiteTicker).remove("source");
                            JsonNode bid = websiteTicker.get("bid");
                            JsonNode ask = websiteTicker.get("ask");
                            ((ObjectNode) websiteTicker).remove("bid");
                            ((ObjectNode) websiteTicker).remove("ask");
                            ((ObjectNode) websiteTicker).put("bid", ask);
                            ((ObjectNode) websiteTicker).put("ask", bid);
                            FileUtil.createFile(websiteTicker, new File(localBitcoinsTickerFolderOrFile,  websiteTickerFile.getName()));
                        } catch (IOException ex) {
                            Logger.getLogger(CopyDataToLocalBitcoinsFolderMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                if (websiteTickerFolderOrFile.isFile()) {
                    System.out.println("websiteTickerFolderOrFile.getName(): " + websiteTickerFolderOrFile.getName());
                    try {
                        JsonNode websiteTicker = mapper.readTree(websiteTickerFolderOrFile);
                        ((ObjectNode) websiteTicker).remove("source");
                        JsonNode bid = websiteTicker.get("bid");
                        JsonNode ask = websiteTicker.get("ask");
                        ((ObjectNode) websiteTicker).remove("bid");
                        ((ObjectNode) websiteTicker).remove("ask");
                        ((ObjectNode) websiteTicker).put("bid", ask);
                        ((ObjectNode) websiteTicker).put("ask", bid);
                        FileUtil.createFile(websiteTicker, new File(localBitcoinsTickerSymbolFolder, websiteTickerFolderOrFile.getName()));
                    } catch (IOException ex) {
                        Logger.getLogger(CopyDataToLocalBitcoinsFolderMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

}
