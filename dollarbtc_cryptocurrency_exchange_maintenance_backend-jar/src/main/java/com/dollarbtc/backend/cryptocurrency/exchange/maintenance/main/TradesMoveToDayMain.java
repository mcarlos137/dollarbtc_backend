/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class TradesMoveToDayMain {

    public static void main(String[] args) {
        List<String> exchangeIdSymbols = ExchangeUtil.getExchangeIdSymbols(null, null);
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        for (String exchangeIdSymbol : exchangeIdSymbols) {
            String exchangeId = exchangeIdSymbol.split("__")[0];
            String symbol = exchangeIdSymbol.split("__")[1];
            System.out.println("----------------------------------------------");
            System.out.println("starting " + exchangeId + " " + symbol);
            System.out.println("----------------------------------------------");

            File tradesFolder = ExchangesFolderLocator.getExchangeSymbolTradesFolder(exchangeId, symbol);
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(tradesFolder.getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(o -> (!o.getFileName().toFile().getName().contains("websocket")))
                        .filter(path -> Files.isRegularFile(path))
                        .sorted((o1, o2) -> {
                            Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                            Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                            return id1.compareTo(id2);
                        })
                        .iterator();
                ObjectMapper mapper = new ObjectMapper();
                boolean doNotMoveFirst = true;
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    if(doNotMoveFirst){
                        doNotMoveFirst = false;
                        continue;
                    }
                    File tradeFile = it.toFile();
                    JsonNode tradeData = mapper.readTree(tradeFile);
                    if (tradeData == null) {
                        continue;
                    }
                    if (tradeData.get("timestamp") == null) {
                        continue;
                    }
                    File dayTradesFolder = FileUtil.createFolderIfNoExist(tradesFolder, DateUtil.getFileDate(DateUtil.getDayStartDate(tradeData.get("timestamp").textValue())));
                    FileUtil.moveFileToFolder(tradeFile, dayTradesFolder);
                    System.out.println("moving to day: " + tradeFile.getName());
                }
            } catch (IOException ex) {
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
