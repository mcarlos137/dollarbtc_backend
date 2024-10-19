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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class TradesTo1HourFileMain {

    public static void main(String[] args) {
        args = new String[2];
        args[0] = "1H";
        args[1] = "100";
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
            String lastTimestamp = DateUtil.getDayStartDate(null);
            List<String> timestamps = new ArrayList<>();
            timestamps.add(lastTimestamp);
            int daysBefore = 1;
            while (daysBefore < Integer.parseInt(args[1])) {
                lastTimestamp = DateUtil.getDateDaysBefore(lastTimestamp, 1);
                timestamps.add(lastTimestamp);
                daysBefore++;
            }
            File tradesFolder = ExchangesFolderLocator.getExchangeSymbolTradesFolder(exchangeId, symbol);
            ObjectMapper mapper = new ObjectMapper();
            boolean continueWithPastFolders = true;
            for (String timestamp : timestamps) {
                String folderDate = DateUtil.getFileDate(timestamp);
                File folder = new File(tradesFolder, folderDate);
                if (!folder.exists() || !folder.isDirectory()) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting " + timestamp);
                System.out.println("----------------------------------------------");
                File folderPeriod = new File(folder, args[0]);
                File filesFile = new File(folderPeriod, "files.json");
                if (folderPeriod.exists() && folderPeriod.isDirectory() && filesFile.exists() && filesFile.isFile()) {
                    continueWithPastFolders = false;
                } else {
                    FileUtil.createFolderIfNoExist(folder, args[0]);
                }
                JsonNode files = mapper.createObjectNode();
                ArrayNode idsArrayNode = mapper.createArrayNode();
                List<String> startHourTimestamps = new ArrayList<>();
                try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder.getPath()));) {
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
                        File tradeFile = it.toFile();
                        JsonNode tradeData = mapper.readTree(tradeFile);
                        if (tradeData == null) {
                            continue;
                        }
                        if (tradeData.get("timestamp") == null) {
                            continue;
                        }
                        String startHourTimestamp = DateUtil.getHourStartDate(tradeData.get("timestamp").textValue());
                        if (!startHourTimestamps.contains(startHourTimestamp)) {
                            startHourTimestamps.add(startHourTimestamp);
                            ObjectNode objectNode = mapper.createObjectNode();
                            objectNode.put("fileName", tradeFile.getName());
                            idsArrayNode.add(objectNode);
                            System.out.println("" + tradeData.get("timestamp").textValue());
                        }
                    }
                } catch (IOException ex) {
                }
                ((ObjectNode) files).putArray("files").addAll(idsArrayNode);
                FileUtil.editFile(files, filesFile);
                System.out.println("----------------------------------------------");
                System.out.println("finishing " + timestamp);
                System.out.println("----------------------------------------------");
                if (!continueWithPastFolders) {
                    break;
                }
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
