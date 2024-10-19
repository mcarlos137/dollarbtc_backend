/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.main;

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
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class DeleteTradesNotIn1MinuteFileMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File exchangesFolder = ExchangesFolderLocator.getFolder();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        for (File exchangeFolder : exchangesFolder.listFiles()) {
            if (!exchangeFolder.isDirectory()) {
                return;
            }
            System.out.println("----------------------------------------------");
            System.out.println("starting " + exchangeFolder.getName());
            System.out.println("----------------------------------------------");
            for (File exchangeSymbolFolder : exchangeFolder.listFiles()) {
                if (!exchangeSymbolFolder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting " + exchangeSymbolFolder.getName());
                System.out.println("----------------------------------------------");
                File exchangeSymbolTradesFolder = new File(exchangeSymbolFolder, "Trades");
                for (File exchangeSymbolTradesPeriodsFolder : exchangeSymbolTradesFolder.listFiles()) {
                    if (!exchangeSymbolTradesPeriodsFolder.isDirectory()) {
                        continue;
                    }
                    System.out.println("----------------------------------------------");
                    System.out.println("starting " + exchangeSymbolTradesPeriodsFolder.getName());
                    System.out.println("----------------------------------------------");
                    File exchangeSymbolTradesPeriods1MinuteFile = new File(new File(exchangeSymbolTradesPeriodsFolder, "1M"), "files.json");
                    if (!exchangeSymbolTradesPeriods1MinuteFile.isFile()) {
                        continue;
                    }
                    JsonNode exchangeSymbolTradesPeriods1Minute = null;
                    try {
                        exchangeSymbolTradesPeriods1Minute = mapper.readTree(exchangeSymbolTradesPeriods1MinuteFile);
                    } catch (IOException ex) {
                        Logger.getLogger(DeleteTradesNotIn1MinuteFileMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (exchangeSymbolTradesPeriods1Minute == null) {
                        continue;
                    }
                    Set<String> minuteFileNames = new HashSet<>();
                    Iterator<JsonNode> exchangeSymbolTradesPeriods1MinuteFileNames = exchangeSymbolTradesPeriods1Minute.get("files").elements();
                    while (exchangeSymbolTradesPeriods1MinuteFileNames.hasNext()) {
                        JsonNode exchangeSymbolTradesPeriods1MinuteFileName = exchangeSymbolTradesPeriods1MinuteFileNames.next();
                        minuteFileNames.add(exchangeSymbolTradesPeriods1MinuteFileName.get("fileName").textValue());
                    }
                    long firstId = 0;
                    long lastId = 0;
                    try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(exchangeSymbolTradesPeriodsFolder.getPath()));) {
                        final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                                .filter(path -> Files.isRegularFile(path))
                                .sorted((o1, o2) -> {
                                    Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                                    Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                                    return id1.compareTo(id2);
                                })
                                .iterator();
                        while (iterator.hasNext()) {
                            Path it = iterator.next();
                            firstId = Long.parseLong(it.toFile().getName().replace(".json", ""));
                            break;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DeleteTradesNotIn1MinuteFileMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(exchangeSymbolTradesPeriodsFolder.getPath()));) {
                        final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                                .filter(path -> Files.isRegularFile(path))
                                .sorted((o1, o2) -> {
                                    Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                                    Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                                    return id2.compareTo(id1);
                                })
                                .iterator();
                        while (iterator.hasNext()) {
                            Path it = iterator.next();
                            lastId = Long.parseLong(it.toFile().getName().replace(".json", ""));
                            break;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DeleteTradesNotIn1MinuteFileMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    while (firstId <= lastId) {
                        File file = new File(exchangeSymbolTradesPeriodsFolder, firstId + ".json");
                        firstId++;
                        if (!file.isFile()) {
                            continue;
                        }
                        if (minuteFileNames.contains(file.getName())) {
                            continue;
                        }
                        FileUtil.deleteFile(file);
                        System.out.println("deleting file: " + file.getName());
                    }
                    System.out.println("----------------------------------------------");
                    System.out.println("finishing " + exchangeSymbolTradesPeriodsFolder.getName());
                    System.out.println("----------------------------------------------");
                }
                System.out.println("----------------------------------------------");
                System.out.println("finishing " + exchangeSymbolFolder.getName());
                System.out.println("----------------------------------------------");
            }
            System.out.println("----------------------------------------------");
            System.out.println("finishing " + exchangeFolder.getName());
            System.out.println("----------------------------------------------");
        }
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
    }

}
