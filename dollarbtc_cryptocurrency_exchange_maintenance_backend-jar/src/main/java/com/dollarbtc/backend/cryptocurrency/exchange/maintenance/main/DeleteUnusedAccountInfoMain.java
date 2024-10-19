/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.main;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class DeleteUnusedAccountInfoMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File usersFolder = new File(OPERATOR_PATH, "Users");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("----------------------------------------------");
        System.out.println("starting process");
        System.out.println("----------------------------------------------");
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                return;
            }
            System.out.println("----------------------------------------------");
            System.out.println("starting " + userFolder.getName());
            System.out.println("----------------------------------------------");
            File userModelsFolder = new File(userFolder, "Models");
            if(!userModelsFolder.isDirectory()){
                continue;
            }
            for (File userModelFolder : userModelsFolder.listFiles()) {
                if (!userModelFolder.isDirectory() || userModelFolder.getName().equals("Test")) {
                    continue;
                }
                System.out.println("----------------------------------------------");
                System.out.println("starting " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
                for (File userModelExchangeIdSymbolFolder : userModelFolder.listFiles()) {
                    if (!userModelExchangeIdSymbolFolder.isDirectory()) {
                        continue;
                    }
                    System.out.println("----------------------------------------------");
                    System.out.println("starting " + userModelExchangeIdSymbolFolder.getName());
                    System.out.println("----------------------------------------------");
                    File userModelExchangeIdSymbolAccountsFolder = new File(userModelExchangeIdSymbolFolder, "Accounts");
                    for (File userModelExchangeIdSymbolAccountPeriodsFolder : userModelExchangeIdSymbolAccountsFolder.listFiles()) {
                        if (!userModelExchangeIdSymbolAccountPeriodsFolder.isDirectory()) {
                            continue;
                        }
                        System.out.println("----------------------------------------------");
                        System.out.println("starting " + userModelExchangeIdSymbolAccountPeriodsFolder.getName());
                        System.out.println("----------------------------------------------");
                        File fistFile = null;
                        File lastFile = null;
                        File lowestPriceFile = null;
                        File highestPriceFile = null;
                        Set<File> filesToRemove = new HashSet<>();
                        double lowestLastAskPrice = 0;
                        double highestLastAskPrice = 0;
                        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(userModelExchangeIdSymbolAccountPeriodsFolder.getPath()));) {
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
                                File file = it.toFile();
                                if (fistFile == null) {
                                    fistFile = file;
                                }
                                if (lowestPriceFile == null) {
                                    lowestPriceFile = file;
                                }
                                if (highestPriceFile == null) {
                                    highestPriceFile = file;
                                }
                                lastFile = file;
                                JsonNode jsonNode = null;
                                try {
                                    jsonNode = mapper.readTree(file);
                                } catch (IOException ex) {
                                    Logger.getLogger(DeleteUnusedAccountInfoMain.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if(!jsonNode.has("accountBase") || !jsonNode.get("accountBase").has("lastAskPrice")){
                                    filesToRemove.add(file);
                                    continue;
                                }
                                double lastAskPrice = jsonNode.get("accountBase").get("lastAskPrice").doubleValue();
                                if (lowestLastAskPrice == 0) {
                                    lowestLastAskPrice = lastAskPrice;
                                }
                                if (highestLastAskPrice == 0) {
                                    highestLastAskPrice = lastAskPrice;
                                }
                                if (highestLastAskPrice < lastAskPrice) {
                                    highestPriceFile = file;
                                    highestLastAskPrice = lastAskPrice;
                                }
                                if (lowestLastAskPrice > lastAskPrice) {
                                    lowestPriceFile = file;
                                    lowestLastAskPrice = lastAskPrice;
                                }
                            }
                            if(fistFile != null){
                                System.out.println("fistFile: " + fistFile.getName());
                            }
                            if(lastFile != null){
                                System.out.println("lastFile: " + lastFile.getName());
                            }
                            if(lowestPriceFile != null){
                                System.out.println("lowestPriceFile: " + lowestPriceFile.getName());
                            }
                            if(highestPriceFile != null){
                                System.out.println("highestPriceFile: " + highestPriceFile.getName());
                            }
                        } catch (IOException ex) {
                        }
                        for (File userModelExchangeIdSymbolAccountPeriodsFile : userModelExchangeIdSymbolAccountPeriodsFolder.listFiles()) {
                            if(userModelExchangeIdSymbolAccountPeriodsFile.equals(fistFile) || 
                                    userModelExchangeIdSymbolAccountPeriodsFile.equals(lastFile) ||
                                    userModelExchangeIdSymbolAccountPeriodsFile.equals(lowestPriceFile) ||
                                    userModelExchangeIdSymbolAccountPeriodsFile.equals(highestPriceFile)){
                                continue;
                            }
                            filesToRemove.add(userModelExchangeIdSymbolAccountPeriodsFile);
                        }
                        Iterator<File> filesToRemoveIterator = filesToRemove.iterator();
                        while(filesToRemoveIterator.hasNext()){
                            FileUtil.deleteFile(filesToRemoveIterator.next());
                        }
                        System.out.println("----------------------------------------------");
                        System.out.println("finishing " + userModelExchangeIdSymbolAccountPeriodsFolder.getName());
                        System.out.println("----------------------------------------------");
                    }
                    System.out.println("----------------------------------------------");
                    System.out.println("finishing " + userModelExchangeIdSymbolFolder.getName());
                    System.out.println("----------------------------------------------");
                }
                System.out.println("----------------------------------------------");
                System.out.println("finishing " + userModelFolder.getName());
                System.out.println("----------------------------------------------");
            }
            System.out.println("----------------------------------------------");
            System.out.println("finishing " + userFolder.getName());
            System.out.println("----------------------------------------------");
        }
        System.out.println("----------------------------------------------");
        System.out.println("finishing process");
        System.out.println("----------------------------------------------");
    }

}
