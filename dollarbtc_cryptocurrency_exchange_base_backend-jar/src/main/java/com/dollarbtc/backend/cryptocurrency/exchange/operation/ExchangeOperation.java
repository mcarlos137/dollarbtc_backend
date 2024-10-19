/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class ExchangeOperation {

    public static JsonNode getCurrency(String currency) {
        File currenciesFile = ExchangesFolderLocator.getDollarBTCCurrenciesFile();
        ObjectMapper mapper = new ObjectMapper();
        if (!currenciesFile.isFile()) {
            return mapper.createObjectNode();
        }
        try {
            JsonNode currencies = mapper.readTree(currenciesFile);
            Iterator<JsonNode> currenciesIterator = currencies.elements();
            while (currenciesIterator.hasNext()) {
                JsonNode currencyy = currenciesIterator.next();
                if (currencyy.get("id").textValue().equals(currency)) {
                    return currencyy;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }

    public static JsonNode getCurrencies() {
        File currenciesFile = ExchangesFolderLocator.getDollarBTCCurrenciesFile();
        ObjectMapper mapper = new ObjectMapper();
        if (!currenciesFile.isFile()) {
            return mapper.createObjectNode();
        }
        try {
            return mapper.readTree(currenciesFile);
        } catch (IOException ex) {
            Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }

    public static JsonNode getSymbol(String symbol) {
        File symbolsFile = new File(new File(new File(OPERATOR_PATH, "Exchanges"), "DollarBTC"), "symbols.json");
        ObjectMapper mapper = new ObjectMapper();
        if (!symbolsFile.isFile()) {
            return mapper.createObjectNode();
        }
        try {
            JsonNode symbols = mapper.readTree(symbolsFile);
            Iterator<JsonNode> symbolsIterator = symbols.elements();
            while (symbolsIterator.hasNext()) {
                JsonNode symboll = symbolsIterator.next();
                if (symboll.get("id").textValue().equals(symbol)) {
                    return symboll;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }

    public static JsonNode getSymbols() {
        File symbolsFile = ExchangesFolderLocator.getDollarBTCSymbolsFile();
        ObjectMapper mapper = new ObjectMapper();
        if (!symbolsFile.isFile()) {
            return mapper.createObjectNode();
        }
        try {
            return mapper.readTree(symbolsFile);
        } catch (IOException ex) {
            Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createObjectNode();
    }
    
    public static String getSymbol(String baseCurrency, String targetCurrency) {
        String symbol = baseCurrency + targetCurrency;
        switch (symbol) {
            case "USDTBTC":
                symbol = "BTCUSDT";
                break;
            case "USDTETH":
                symbol = "ETHUSDT";
                break;
            case "BTCETH":
                symbol = "ETHBTC";
                break;
        }
        return symbol;
    }

    public static Object[] subscribeTicker(String symbol, Long lastId) {
        File tickerFolder = ExchangesFolderLocator.getDollarBTCSymbolTickerFolder(symbol);
        if (!tickerFolder.isDirectory()) {
            return null;
        }
        for (File tickerFile : tickerFolder.listFiles()) {
            if (tickerFile.isDirectory()) {
                continue;
            }
            try {
                Long lastIdd = Long.parseLong(tickerFile.getName().replace(".json", ""));
                if (lastId != null && Objects.equals(lastIdd, lastId)) {
                    return null;
                }
                return new Object[]{lastIdd, new ObjectMapper().readTree(tickerFile)};
            } catch (IOException ex) {
                Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static Object[] subscribeOrderbook(String symbol, Long lastId) {
        File orderbookFolder = ExchangesFolderLocator.getDollarBTCSymbolOrderbookFolder(symbol);
        if (!orderbookFolder.isDirectory()) {
            return null;
        }
        for (File orderbookFile : orderbookFolder.listFiles()) {
            if (orderbookFile.isDirectory()) {
                continue;
            }
            try {
                Long lastIdd = Long.parseLong(orderbookFile.getName().replace(".json", ""));
                if (lastId != null && Objects.equals(lastIdd, lastId)) {
                    return null;
                }
                return new Object[]{lastIdd, new ObjectMapper().readTree(orderbookFile)};
            } catch (IOException ex) {
                Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public static ArrayNode getOldTrades(String symbol) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        File oldTradesFolder = ExchangesFolderLocator.getDollarBTCSymbolTradesOldFolder(symbol);
        if (!oldTradesFolder.isDirectory()) {
            return null;
        }
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(oldTradesFolder.getPath()));) {
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
                result.add(mapper.readTree(it.toFile()));
                if (result.size() >= 2000) {
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return result;
    }

    public static Object[] subscribeTrades(String symbol, Long lastId) {
        File tradesFolder = ExchangesFolderLocator.getDollarBTCSymbolTradesFolder(symbol);
        if (!tradesFolder.isDirectory()) {
            return null;
        }
        for (File tradesFile : tradesFolder.listFiles()) {
            if (tradesFile.isDirectory()) {
                continue;
            }
            try {
                Long lastIdd = Long.parseLong(tradesFile.getName().replace(".json", ""));
                if (lastId != null && Objects.equals(lastIdd, lastId)) {
                    return null;
                }
                return new Object[]{lastIdd, new ObjectMapper().readTree(tradesFile)};
            } catch (IOException ex) {
                Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static ArrayNode getTrades(String symbol, int limit, String sort, String by) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        File tradesFolder = ExchangesFolderLocator.getDollarBTCSymbolTradesFolder(symbol);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(tradesFolder.getPath()));) {
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
                result.add(mapper.readTree(it.toFile()));
                if (result.size() >= limit) {
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return result;
    }

    public static Object[] subscribeCandles(String symbol, String period, Long lastId) {
        File candlesFolder = ExchangesFolderLocator.getDollarBTCSymbolCandlesPeriodFolder(symbol, period);
        if (!candlesFolder.isDirectory()) {
            return null;
        }
        for (File candlesFile : candlesFolder.listFiles()) {
            if (candlesFile.isDirectory()) {
                continue;
            }
            try {
                Long lastIdd = Long.parseLong(candlesFile.getName().replace(".json", ""));
                if (lastId != null && Objects.equals(lastIdd, lastId)) {
                    return null;
                }
                return new Object[]{lastIdd, new ObjectMapper().readTree(candlesFile)};
            } catch (IOException ex) {
                Logger.getLogger(ExchangeOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public static ArrayNode getOldCandles(String symbol, String period) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();
        File oldCandlesFolder = ExchangesFolderLocator.getDollarBTCSymbolCandlesPeriodOldFolder(symbol, period);
        if (!oldCandlesFolder.isDirectory()) {
            return null;
        }
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(oldCandlesFolder.getPath()));) {
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
                result.add(mapper.readTree(it.toFile()));
                if (result.size() >= 100) {
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return result;
    }

}
