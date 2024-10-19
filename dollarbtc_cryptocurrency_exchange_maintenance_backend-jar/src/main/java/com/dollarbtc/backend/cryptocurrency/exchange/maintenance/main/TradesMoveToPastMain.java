/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class TradesMoveToPastMain {

    public static void main(String[] args) {
        List<String> exchangeIdSymbols = ExchangeUtil.getExchangeIdSymbols(null, null);
//        long maxTradesQuantity = 500;
        long maxTradesQuantity = Long.parseLong(args[0]);
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        for (String exchangeIdSymbol : exchangeIdSymbols) {
            String exchangeId = exchangeIdSymbol.split("__")[0];
            String symbol = exchangeIdSymbol.split("__")[1];
            System.out.println("----------------------------------------------");
            System.out.println("starting " + exchangeId + " " + symbol);
            System.out.println("----------------------------------------------");
            long lastId = Long.MAX_VALUE;
            File tradesFolder = ExchangesFolderLocator.getExchangeSymbolTradesFolder(exchangeId, symbol);
            File pastTradesFolder = FileUtil.createFolderIfNoExist(tradesFolder, "Past");
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(tradesFolder.getPath()));) {
                final long tradesQuantity = StreamSupport.stream(stream.spliterator(), false).count();
                if (tradesQuantity == 0 || tradesQuantity + 2 <= maxTradesQuantity) {
                    continue;
                }
                long tradesToMoveQuantity = tradesQuantity - maxTradesQuantity - 2;
                System.out.println("maxTradesQuantity: " + maxTradesQuantity);
                System.out.println("tradesToMoveQuantity: " + tradesToMoveQuantity);
                while (tradesToMoveQuantity > 0) {
                    String tradeFileName = String.valueOf(lastId) + ".json";
                    File tradeToMoveFile = new File(tradesFolder, tradeFileName);
                    if (tradeToMoveFile.exists()) {
                        System.out.println("moving to past: " + tradeFileName);
                        FileUtil.moveFileToFolder(tradeToMoveFile, pastTradesFolder);
                        tradesToMoveQuantity--;
                    }
                    lastId--;
                }
            } catch (IOException ex) {
                Logger.getLogger(TradesMoveToPastMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("----------------------------------------------");
            System.out.println("finishing " + exchangeId + " " + symbol);
            System.out.println("----------------------------------------------");
        }
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
        System.exit(0);
    }

}
