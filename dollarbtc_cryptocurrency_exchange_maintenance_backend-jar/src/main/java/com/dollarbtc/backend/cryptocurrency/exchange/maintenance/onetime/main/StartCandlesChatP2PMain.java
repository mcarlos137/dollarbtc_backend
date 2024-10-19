/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CandlesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.PricesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class StartCandlesChatP2PMain {

    /**
     * @param args the command line arguments
     */
    private static final Map<String, Integer> PERIODS = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("----------- STARTING StartCandlesChatP2PMain");
        //PERIODS.put("1M", 60 * 60 * 24 * 30 * 1000);
        //PERIODS.put("1W", 60 * 60 * 24 * 7 * 1000);
        //PERIODS.put("1D", 60 * 24);
        //PERIODS.put("12H", 60 * 12);
        PERIODS.put("4H", 60 * 4);
        //PERIODS.put("1H", 60);
        //PERIODS.put("30m", 30);
        //PERIODS.put("15m", 15);
        //PERIODS.put("5m", 5);
        ObjectMapper mapper = new ObjectMapper();
        for (File pricesChatP2PPairFolder : PricesFolderLocator.getChatP2PFolder().listFiles()) {
            if (!pricesChatP2PPairFolder.isDirectory()) {
                continue;
            }
            System.out.println("----------- PAIR " + pricesChatP2PPairFolder.getName());
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(PricesFolderLocator.getChatP2POldFolder(pricesChatP2PPairFolder.getName()).getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> !Files.isDirectory(path))
                        .sorted((o1, o2) -> {
                            String da1 = o1.toFile().getName();
                            String da2 = o2.toFile().getName();
                            return da1.compareTo(da2);
                        })
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File pricesChatP2POldFile = it.toFile();
                    try {
                        JsonNode pricesChatP2POld = mapper.readTree(pricesChatP2POldFile);
                        setCandleValue(pricesChatP2POld, mapper);
                    } catch (IOException ex) {
                        Logger.getLogger(StartCandlesChatP2PMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
            }
        }
        System.out.println("----------- FINISHING StartCandlesChatP2PMain");
    }

    private static void setCandleValue(JsonNode value, ObjectMapper mapper) {
        String pair = value.get("pair").textValue();
        Double price = value.get("price").doubleValue();
        price = Double.parseDouble(String.format("%.2f", price));
        String timestamp = DateUtil.getDate(value.get("time").longValue());
        String startTimestamp = DateUtil.getDayStartDate(timestamp);
        System.out.println("----------- VALUE " + value);
        for (String period : PERIODS.keySet()) {
            System.out.println("----------- PERIOD " + period);
            String[] intervals = new String[]{startTimestamp, DateUtil.getDateMinutesAfter(startTimestamp, PERIODS.get(period))};
            String intervalFound = null;
            while (intervalFound == null) {
                if (timestamp.compareTo(intervals[0]) >= 0 && timestamp.compareTo(intervals[1]) < 0) {
                    intervalFound = intervals[0];
                } else {
                    intervals = new String[]{intervals[1], DateUtil.getDateMinutesAfter(intervals[1], PERIODS.get(period))};
                }
            }
            File candlesChatP2PFile = new File(CandlesFolderLocator.getChatP2PFolder(pair, period), DateUtil.getFileDate(intervalFound) + ".json");
            System.out.println("----------- candlesChatP2PFile " + candlesChatP2PFile.getName());
            if (!candlesChatP2PFile.isFile()) {
                for(File candlesChatP2PFoundedFile : CandlesFolderLocator.getChatP2PFolder(pair, period).listFiles()){
                    if(!candlesChatP2PFoundedFile.isFile()){
                        continue;
                    }
                    FileUtil.moveFileToFolder(candlesChatP2PFoundedFile, CandlesFolderLocator.getChatP2POldFolder(pair, period));
                }
                ObjectNode candlesChatP2P = mapper.createObjectNode();
                candlesChatP2P.put("shadowH", price);
                candlesChatP2P.put("shadowL", price);
                candlesChatP2P.put("open", price);
                candlesChatP2P.put("close", price);
                FileUtil.createFile(candlesChatP2P, candlesChatP2PFile);
            } else {
                try {
                    JsonNode candlesChatP2P = mapper.readTree(candlesChatP2PFile);
                    Double shadowH = candlesChatP2P.get("shadowH").doubleValue();
                    Double shadowL = candlesChatP2P.get("shadowL").doubleValue();
                    if (shadowH < price) {
                        ((ObjectNode) candlesChatP2P).put("shadowH", price);
                    }
                    if (shadowL > price) {
                        ((ObjectNode) candlesChatP2P).put("shadowL", price);
                    }
                    ((ObjectNode) candlesChatP2P).put("close", price);
                    FileUtil.editFile(candlesChatP2P, candlesChatP2PFile);
                } catch (IOException ex) {
                    Logger.getLogger(StartCandlesChatP2PMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
