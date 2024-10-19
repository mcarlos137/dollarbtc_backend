/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public final class ExchangeUtil {

    public static final String OPERATOR_PATH = "/MAIN";
    public static final String OPERATOR_NAME = new File(OPERATOR_PATH).getName();
    public static final String CENTRAL_PATH = new File(OPERATOR_PATH).getParent();
    public static final BigDecimal FEE_TRANSACTION_FACTOR = BigDecimal.valueOf(0.001);
    public static long ordersFileId = Long.MAX_VALUE;
    public static long accountsFileId = Long.MAX_VALUE;
    public static long tradesFileId = Long.MAX_VALUE;
    public static long candlesFileId = Long.MAX_VALUE;
    public static long orderBooksFileId = Long.MAX_VALUE;
    public static long tickersFileId = Long.MAX_VALUE;
    public static long inAlgorithmsInfoFileId = Long.MAX_VALUE;

    public static void createFile(JsonNode jsonNode, File[] folders, String type) {
        switch (type) {
            case "order":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, ordersFileId + ".json");
                }
                ordersFileId--;
                break;
            case "account":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, accountsFileId + ".json");
                }
                accountsFileId--;
                break;
            case "trade":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, tradesFileId + ".json");
                }
                tradesFileId--;
                break;
            case "candle":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, candlesFileId + ".json");
                }
                candlesFileId--;
                break;
            case "orderBook":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, orderBooksFileId + ".json");
                }
                orderBooksFileId--;
                break;
            case "ticker":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, tickersFileId + ".json");
                }
                tickersFileId--;
                break;
            case "inAlgorithmInfo":
                for (File folder : folders) {
                    FileUtil.createFile(jsonNode, folder, inAlgorithmsInfoFileId + ".json");
                }
                inAlgorithmsInfoFileId--;
                break;
        }
    }

    public static List<String> getExchangeIdSymbols(String symbolAsset, String symbolBase) {
        List<String> exchangeIdSymbols = new ArrayList<>();
        File exchangesFolder = ExchangesFolderLocator.getFolder();
        if (!exchangesFolder.exists()) {
            return exchangeIdSymbols;
        }
        for (File exchangeFolder : exchangesFolder.listFiles()) {
            if (exchangeFolder.isDirectory()) {
                for (File symbolFolder : exchangeFolder.listFiles()) {
                    if (!symbolFolder.exists()) {
                        continue;
                    }
                    if (symbolFolder.isDirectory()) {
                        if (symbolAsset == null && symbolBase == null) {
                            exchangeIdSymbols.add(exchangeFolder.getName() + "__" + symbolFolder.getName());
                        } else if (symbolAsset != null && symbolBase == null && symbolFolder.getName().startsWith(symbolAsset)) {
                            exchangeIdSymbols.add(exchangeFolder.getName() + "__" + symbolFolder.getName());
                        } else if (symbolAsset == null && symbolBase != null && !symbolFolder.getName().startsWith(symbolBase) && symbolFolder.getName().contains(symbolBase)) {
                            exchangeIdSymbols.add(exchangeFolder.getName() + "__" + symbolFolder.getName());
                        } else if (symbolAsset != null && symbolBase != null && symbolFolder.getName().startsWith(symbolAsset + symbolBase)) {
                            exchangeIdSymbols.add(exchangeFolder.getName() + "__" + symbolFolder.getName());
                        }
                    }
                }
            }
        }
        return exchangeIdSymbols;
    }

    public static Set<String> getModelNames(String userName, boolean excludeTest) {
        Set<String> modelNames = new HashSet<>();
        File userModelsFolder = new File(new File(new File(OPERATOR_PATH, "Users"), userName), "Models");
        if (!userModelsFolder.isDirectory() || userModelsFolder.list().length == 0) {
            return modelNames;
        }
        for (File userModelFolder : userModelsFolder.listFiles()) {
            if (!userModelFolder.isDirectory() || userModelFolder.getName().equals("Test")) {
                continue;
            }
            String modelName = userModelFolder.getName();
            if (excludeTest && modelName.contains("test")) {
                continue;
            }
            modelNames.add(modelName);
        }
        return modelNames;
    }

    public static void deleteModelData(String modelName) {
        File userModelFolder = new File(new File(new File(new File(OPERATOR_PATH, "Users"), modelName.split("__")[0]), "Models"), modelName);
        if (!userModelFolder.isDirectory()) {
            return;
        }
        for (File userModelF : userModelFolder.listFiles()) {
            if (userModelF.isDirectory()) {
                FileUtil.deleteFolder(userModelF);
            }
        }
    }

    public static String getFirstFileInFolderName(File folder) {
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(o -> (!o.getFileName().toFile().getName().contains("websocket")))
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id1.compareTo(id2);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                return it.toFile().getName();
            }
        } catch (IOException ex) {
        }
        return null;
    }

    public static String getSymbol(String exchangeId, String symbol) {
        if (exchangeId.equals("HitBTC")) {
            if (symbol.contains("USDT")) {
                symbol = symbol.replace("USDT", "USD");
                return symbol;
            }
        }
        return symbol;
    }

}
