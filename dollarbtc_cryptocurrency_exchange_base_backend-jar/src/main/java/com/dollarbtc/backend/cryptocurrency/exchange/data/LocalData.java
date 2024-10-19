package com.dollarbtc.backend.cryptocurrency.exchange.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBaseInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.OrderInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.ModelOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author CarlosDaniel
 */
public class LocalData {

    public static List<Trade> getTrades(String exchangeId, String symbol, String initDate, String endDate, int maxQuantity, String type, boolean withNotInDayFolder) {
        List<Trade> trades = new ArrayList<>();
        File tradesFolder = ExchangesFolderLocator.getExchangeSymbolTradesFolder(exchangeId, symbol);
        boolean addMore = true;
        if (withNotInDayFolder) {
            addMore = addTrades(exchangeId, symbol, initDate, endDate, maxQuantity, tradesFolder, trades, "NORMAL");
        }
        if (addMore) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(tradesFolder.getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isDirectory(path))
                        .sorted((o1, o2) -> {
                            String da1 = DateUtil.getDate(o1.toFile().getName());
                            String da2 = DateUtil.getDate(o2.toFile().getName());
                            return DateUtil.parseDate(da2).compareTo(DateUtil.parseDate(da1));
                        })
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File dayTradesFolder = it.toFile();
                    if (!addTrades(exchangeId, symbol, initDate, endDate, maxQuantity, dayTradesFolder, trades, type)) {
                        break;
                    }
                }
            } catch (IOException ex) {
            }
        }
        return trades;
    }

    private static boolean addTrades(String exchangeId, String symbol, String initDate, String endDate, int maxQuantity, File tradesFolder, List<Trade> trades, String type) {
        ObjectMapper mapper = new ObjectMapper();
        File tradesTypeFolder = new File(tradesFolder, type);
        Long lastFileNameId = null;
        List<Trade> newTrades = new ArrayList<>();
        if (!type.equals("NORMAL") && tradesTypeFolder.isDirectory()) {
            File tradesTypeFilesFile = new File(tradesTypeFolder, "files.json");
            JsonNode tradesTypeFiles = null;
            try {
                tradesTypeFiles = mapper.readTree(tradesTypeFilesFile);
            } catch (IOException ex) {
                Logger.getLogger(LocalData.class.getName()).log(Level.SEVERE, null, ex);
            }
            Iterator<JsonNode> tradesTypeFilesElements = tradesTypeFiles.get("files").elements();
            while (tradesTypeFilesElements.hasNext()) {
                JsonNode tradesTypeFilesElement = tradesTypeFilesElements.next();
                String fileName = tradesTypeFilesElement.get("fileName").textValue();
                if (lastFileNameId == null) {
                    lastFileNameId = Long.parseLong(fileName.replace(".json", ""));
                }
                File tradeFile = new File(tradesFolder, fileName);
                JsonNode trade = null;
                try {
                    trade = mapper.readTree(tradeFile);
                } catch (IOException ex) {
                    Logger.getLogger(LocalData.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (trade == null) {
                    continue;
                }
                if (trade.get("timestamp") == null) {
                    continue;
                }
                String timestamp = trade.get("timestamp").textValue();
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (initDate != null && !initDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                        return false;
                    }
                }
                if (endDate != null && !endDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                        continue;
                    }
                }
                BigDecimal price = new BigDecimal(trade.get("price").textValue());
                BigDecimal tradableAmount = new BigDecimal(trade.get("quantity").textValue());
                String side = trade.get("side").textValue();
                Trade.Side tradeSide = null;
                switch (side) {
                    case "sell":
                        tradeSide = Trade.Side.SELL;
                        break;
                    case "buy":
                        tradeSide = Trade.Side.BUY;
                        break;
                }
                newTrades.add(new Trade.Builder(exchangeId, symbol, tradeSide, tradableAmount, price, timestamp).build());
                if (trades.size() + newTrades.size() >= maxQuantity) {
                    return false;
                }
            }
        }
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
            while (iterator.hasNext()) {
                Path it = iterator.next();
                JsonNode tradeData = mapper.readTree(it.toFile());
                if (tradeData == null) {
                    continue;
                }
                if (lastFileNameId != null && Long.parseLong(it.toFile().getName().replace(".json", "")) >= lastFileNameId) {
                    break;
                }
                if (tradeData.get("timestamp") == null) {
                    continue;
                }
                String timestamp = tradeData.get("timestamp").textValue();
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (initDate != null && !initDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                        return false;
                    }
                }
                if (endDate != null && !endDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                        continue;
                    }
                }
                BigDecimal price = new BigDecimal(tradeData.get("price").textValue());
                BigDecimal tradableAmount = new BigDecimal(tradeData.get("quantity").textValue());
                String side = tradeData.get("side").textValue();
                Trade.Side tradeSide = null;
                switch (side) {
                    case "sell":
                        tradeSide = Trade.Side.SELL;
                        break;
                    case "buy":
                        tradeSide = Trade.Side.BUY;
                        break;
                }
                trades.add(new Trade.Builder(exchangeId, symbol, tradeSide, tradableAmount, price, timestamp).build());
                if (trades.size() + newTrades.size() >= maxQuantity) {
                    return false;
                }
            }
        } catch (IOException ex) {
        }
        trades.addAll(newTrades);
        return true;
    }

    public static List<Order> getOrders(String exchangeId, String symbol, String userModelName, String initDate, String endDate, int maxQuantity) {
        List<Order> orders = new ArrayList<>();
        File ordersFolder = new File(new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), userModelName.split("__")[0]), "Models"), userModelName), exchangeId + "__" + symbol), "Orders");
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ordersFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id1.compareTo(id2);
                    })
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                JsonNode orderData = mapper.readTree(it.toFile());
                if (orderData == null) {
                    continue;
                }
                if (orderData.get("timestamp") == null) {
                    continue;
                }
                String timestamp = orderData.get("timestamp").textValue();
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (initDate != null && !initDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                        break;
                    }
                }
                if (endDate != null && !endDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                        continue;
                    }
                }
                BigDecimal price = new BigDecimal(orderData.get("price").doubleValue());
                BigDecimal tradableAmount = new BigDecimal(orderData.get("tradableAmount").doubleValue());
                Order.Type orderType = Order.Type.valueOf(orderData.get("type").textValue());
                Order order = new Order.Builder(exchangeId, symbol, orderType, timestamp).tradableAmount(tradableAmount).price(price).build();
                if (orderData.has("algorithmType")) {
                    String algorithmType = orderData.get("algorithmType").textValue();
                    if (algorithmType.equals("TIME")) {
                        algorithmType = "TIMEOUT";
                    }
                    order.setIndicatorType(Order.AlgorithmType.valueOf(algorithmType));
                }
                if (orderData.has("tradingType")) {
                    order.setTradingType(Order.TradingType.valueOf(orderData.get("tradingType").textValue()));
                }
                orders.add(order);
                if (orders.size() == maxQuantity) {
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return orders;
    }

    public static List<Order> getLastOrders(String exchangeId, String symbol, String userModelName, String initDate, String endDate, int maxQuantity, boolean deleteFiles) {
        List<Order> orders = new ArrayList<>();
        File ordersFolder = new File(new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), userModelName.split("__")[0]), "Models"), userModelName), exchangeId + "__" + symbol), "LastOrders");
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ordersFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id2.compareTo(id1);
                    })
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                File file = it.toFile();
                JsonNode orderData = mapper.readTree(file);
                if (orderData == null) {
                    continue;
                }
                if (orderData.get("timestamp") == null) {
                    continue;
                }
                String timestamp = orderData.get("timestamp").textValue();
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (initDate != null && !initDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                        break;
                    }
                }
                if (endDate != null && !endDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                        continue;
                    }
                }
                BigDecimal price = new BigDecimal(orderData.get("price").doubleValue());
                BigDecimal tradableAmount = new BigDecimal(orderData.get("tradableAmount").doubleValue());
                Order.Type orderType = Order.Type.valueOf(orderData.get("type").textValue());
                Order order = new Order.Builder(exchangeId, symbol, orderType, timestamp).tradableAmount(tradableAmount).price(price).build();
                if (orderData.has("algorithmType")) {
                    order.setIndicatorType(Order.AlgorithmType.valueOf(orderData.get("algorithmType").textValue()));
                }
                if (orderData.has("tradingType")) {
                    order.setTradingType(Order.TradingType.valueOf(orderData.get("tradingType").textValue()));
                }
                orders.add(order);
                if (deleteFiles) {
                    file.delete();
                }
                if (orders.size() == maxQuantity) {
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return orders;
    }

    public static List<OrderInterval> getOrderIntervals(String exchangeId, String symbol, String userModelName, String initDate, String endDate, int maxOrderIntervalQuantity, int maxOrderQuantity) {
        List<OrderInterval> orderIntervals = new ArrayList<>();
        File ordersFolder = new File(new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), userModelName.split("__")[0]), "Models"), userModelName), exchangeId + "__" + symbol), "Orders");
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ordersFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isDirectory(path))
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                List<File> files = Arrays.asList(it.toFile().listFiles());
                files.sort((File o1, File o2) -> {
                    Long id1 = Long.parseLong(o1.getName().replace(".json", ""));
                    Long id2 = Long.parseLong(o2.getName().replace(".json", ""));
                    return id1.compareTo(id2);
                });
                List<Order> orders = new ArrayList<>();
                for (File file : files) {
                    JsonNode orderData = mapper.readTree(file);
                    if (orderData == null) {
                        continue;
                    }
                    if (orderData.get("timestamp") == null) {
                        continue;
                    }
                    String timestamp = orderData.get("timestamp").textValue();
                    if (timestamp == null || timestamp.equals("")) {
                        continue;
                    }
                    if (initDate != null && !initDate.equals("")) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                            break;
                        }
                    }
                    if (endDate != null && !endDate.equals("")) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                            continue;
                        }
                    }
                    BigDecimal price = new BigDecimal(orderData.get("price").doubleValue());
                    BigDecimal tradableAmount = new BigDecimal(orderData.get("tradableAmount").doubleValue());
                    Order.Type orderType = Order.Type.valueOf(orderData.get("type").textValue());
                    Order order = new Order.Builder(exchangeId, symbol, orderType, timestamp).tradableAmount(tradableAmount).price(price).build();
                    if (orderData.has("algorithmType")) {
                        String algorithmType = orderData.get("algorithmType").textValue();
                        if (algorithmType.equals("TIME")) {
                            algorithmType = "TIMEOUT";
                        }
                        order.setIndicatorType(Order.AlgorithmType.valueOf(algorithmType));
                    }
                    if (orderData.has("tradingType")) {
                        order.setTradingType(Order.TradingType.valueOf(orderData.get("tradingType").textValue()));
                    }
                    orders.add(order);
                    if (orders.size() == maxOrderQuantity) {
                        break;
                    }
                }
                String startTimestamp = null;
                String endTimestamp = null;
                if (!orders.isEmpty()) {
                    startTimestamp = orders.get(orders.size() - 1).getTimestamp();
                    endTimestamp = orders.get(0).getTimestamp();
                }
                String intervalAlgorithmName = it.toFile().getName().split("____")[1];
                Map<String, Integer> orderSummary = new HashMap<>();
                //Order.Type__Order.AlgorithmType__Order.TradingType
                for (Order order : orders) {
                    String key;
                    if (order.getTradingType() == null) {
                        key = order.getType().toString() + "__" + order.getAlgorithmType().toString();
                    } else if (order.getAlgorithmType() == null) {
                        key = order.getType().toString() + "__" + order.getTradingType().toString();
                    } else {
                        key = order.getType().toString() + "__" + order.getAlgorithmType().toString() + "__" + order.getTradingType().toString();
                    }
                    if (!orderSummary.containsKey(key)) {
                        orderSummary.put(key, 0);
                    }
                    orderSummary.put(key, orderSummary.get(key) + 1);
                    if (orderSummary.size() == maxOrderIntervalQuantity) {
                        break;
                    }
                }
                orderIntervals.add(new OrderInterval(startTimestamp, endTimestamp, intervalAlgorithmName, orderSummary, orders));
            }
        } catch (IOException ex) {
        }
        return orderIntervals;
    }

    public static List<AccountBase> getAccounts(String exchangeId, String symbol, String userModelName, String initDate, String endDate, int maxQuantity, AccountBase.Retrieve accountBaseRetrieve) {
        List<AccountBase> accounts = new ArrayList<>();
        File accountFolder = new File(new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), userModelName.split("__")[0]), "Models"), userModelName), exchangeId + "__" + symbol), "Accounts");
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(accountFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        if (accountBaseRetrieve.equals(AccountBase.Retrieve.ONLY_FIRST)) {
                            return id2.compareTo(id1);
                        }
                        return id1.compareTo(id2);
                    })
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                JsonNode accountData = mapper.readTree(it.toFile());
                if (accountData == null) {
                    continue;
                }
                String timestamp = accountData.get("accountBase").get("timestamp").textValue();
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (initDate != null && !initDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                        break;
                    }
                }
                if (endDate != null && !endDate.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                        continue;
                    }
                }
                accounts.add(new AccountBase.Builder(
                        new BigDecimal(accountData.get("accountBase").get("reservedBaseBalance").doubleValue()),
                        new BigDecimal(accountData.get("accountBase").get("initialBaseBalance").doubleValue()),
                        new BigDecimal(accountData.get("accountBase").get("currentBaseBalance").doubleValue()),
                        new BigDecimal(accountData.get("accountBase").get("initialAssetBalance").doubleValue()),
                        new BigDecimal(accountData.get("accountBase").get("currentAssetBalance").doubleValue()),
                        accountData.get("accountBase").get("exchangeId").textValue(),
                        accountData.get("accountBase").get("symbol").textValue(),
                        accountData.get("accountBase").get("modelName").textValue(),
                        timestamp
                )
                        .lastAskPrice(new BigDecimal(accountData.get("accountBase").get("lastAskPrice").doubleValue()))
                        .lastBidPrice(new BigDecimal(accountData.get("accountBase").get("lastBidPrice").doubleValue()))
                        .info(accountData.get("accountBase").get("info").textValue())
                        .build());
                if (accounts.size() == maxQuantity || !accountBaseRetrieve.equals(AccountBase.Retrieve.ALL)) {
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return accounts;
    }

    public static List<AccountBaseInterval> getAccountIntervals(String exchangeId, String symbol, String userModelName, String initDate, String endDate, int maxQuantity, AccountBase.Retrieve accountBaseRetrieve) {
        List<AccountBaseInterval> accountIntervals = new ArrayList<>();
        File accountFolder = new File(new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), userModelName.split("__")[0]), "Models"), userModelName), exchangeId + "__" + symbol), "Accounts");
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(accountFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isDirectory(path))
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                List<File> files = Arrays.asList(it.toFile().listFiles());
                files.sort((File o1, File o2) -> {
                    Long id1 = Long.parseLong(o1.getName().replace(".json", ""));
                    Long id2 = Long.parseLong(o2.getName().replace(".json", ""));
                    if (accountBaseRetrieve.equals(AccountBase.Retrieve.ONLY_FIRST)) {
                        return id2.compareTo(id1);
                    }
                    return id1.compareTo(id2);
                });
                List<AccountBase> accounts = new ArrayList<>();
                String startTimestamp = null;
                String endTimestamp = null;
                for (File file : files) {
                    JsonNode accountData = mapper.readTree(file);
                    if (accountData == null) {
                        continue;
                    }
                    if (!accountData.has("accountBase")) {
                        continue;
                    }
                    if (startTimestamp == null) {
                        startTimestamp = accountData.get("startedTimestamp").textValue();
                    }
                    String timestamp = accountData.get("accountBase").get("timestamp").textValue();
                    if (endTimestamp == null) {
                        endTimestamp = timestamp;
                    }
                    if (timestamp == null || timestamp.equals("")) {
                        continue;
                    }
                    if (initDate != null && !initDate.equals("")) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initDate)) < 0) {
                            break;
                        }
                    }
                    if (endDate != null && !endDate.equals("")) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endDate)) > 0) {
                            continue;
                        }
                    }
                    accounts.add(new AccountBase.Builder(
                            new BigDecimal(accountData.get("accountBase").get("reservedBaseBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("initialBaseBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("currentBaseBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("initialAssetBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("currentAssetBalance").doubleValue()),
                            accountData.get("accountBase").get("exchangeId").textValue(),
                            accountData.get("accountBase").get("symbol").textValue(),
                            accountData.get("accountBase").get("modelName").textValue(),
                            timestamp
                    )
                            .lastAskPrice(new BigDecimal(accountData.get("accountBase").get("lastAskPrice").doubleValue()))
                            .lastBidPrice(new BigDecimal(accountData.get("accountBase").get("lastBidPrice").doubleValue()))
                            .info(accountData.get("accountBase").get("info").textValue())
                            .build());
                    if (accounts.size() == maxQuantity || !accountBaseRetrieve.equals(AccountBase.Retrieve.ALL)) {
                        break;
                    }
                }
                String intervalAlgorithmName = null;
                if (it.toFile().getName().contains("____")) {
                    intervalAlgorithmName = it.toFile().getName().split("____")[1];
                }
                accountIntervals.add(new AccountBaseInterval(startTimestamp, endTimestamp, intervalAlgorithmName, accounts));
            }
        } catch (IOException ex) {
        }
        return accountIntervals;
    }

    public static JsonNode getModelBalance(String modelName, boolean excludeInSymbolBaseBalances, boolean excludeEstimatedValues, boolean isMoneyClick) {
        ObjectMapper mapper = new ObjectMapper();
        File initialUserModelFolder = new File(new File(new File(new File(OPERATOR_PATH, "Users"), modelName.split("__")[0]), "Models"), modelName);
        File initialUserModelBalanceFile = new File(initialUserModelFolder, "balance.json");
        JsonNode initialUserModelBalance = null;
        try {
            initialUserModelBalance = mapper.readTree(initialUserModelBalanceFile);
        } catch (IOException ex) {
            Logger.getLogger(LocalData.class.getName()).log(Level.SEVERE, null, ex);
        }
        File userModelFile = new File(initialUserModelFolder, "config.json");
        JsonNode userModel = null;
        try {
            userModel = mapper.readTree(userModelFile);
        } catch (IOException ex) {
            Logger.getLogger(LocalData.class.getName()).log(Level.SEVERE, null, ex);
        }
        String initialTimestamp;
        if (userModel.has("lastActivationInactivationTimestamp")) {
            initialTimestamp = userModel.get("lastActivationInactivationTimestamp").textValue();
        } else if (userModel.has("copyTimestamp")) {
            initialTimestamp = userModel.get("copyTimestamp").textValue();
        } else {
            initialTimestamp = null;
        }
        List<String> exchangeIdSymbols = new ArrayList<>();
        List<String> initialExchangeIdSymbols = ExchangeUtil.getExchangeIdSymbols(null, null);
        for (File initialUserModelF : initialUserModelFolder.listFiles()) {
            if (!initialUserModelF.isDirectory() || !initialUserModelF.getName().contains("__")) {
                continue;
            }
            String exchangeIdSymbol = initialUserModelF.getName();
            if (initialExchangeIdSymbols.contains(exchangeIdSymbol)) {
                exchangeIdSymbols.add(exchangeIdSymbol);
            }
        }
        Map<String, Double> availableBalance = new HashMap<>();
        Map<String, Double> reservedBalance = new HashMap<>();
        Set<String> inSymbols = new HashSet<>();
        exchangeIdSymbols.stream().forEach((exchangeIdSymbol) -> {
            String exchangeId = exchangeIdSymbol.split("__")[0];
            String symbol = exchangeIdSymbol.split("__")[1];
            String symbolBase = AccountBase.getSymbolBase(symbol);
            String symbolAsset = AccountBase.getSymbolAsset(symbol);
            List<AccountBase> accounts = getAccounts(exchangeId, symbol, modelName, initialTimestamp, null, 2000, AccountBase.Retrieve.ALL);
            if (!accounts.isEmpty()) {
                AccountBase initialAccount = accounts.get(accounts.size() - 1);
                AccountBase finalAccount = accounts.get(0);
                if (!availableBalance.containsKey(symbolBase)) {
                    availableBalance.put(symbolBase, 0.0);
                }
                if (!availableBalance.containsKey(symbolAsset)) {
                    availableBalance.put(symbolAsset, 0.0);
                }
                if (!reservedBalance.containsKey(symbolBase)) {
                    reservedBalance.put(symbolBase, 0.0);
                }
                if (excludeInSymbolBaseBalances) {
                    availableBalance.put(symbolBase, availableBalance.get(symbolBase) - finalAccount.getInitialBaseBalance().doubleValue());
                } else {
                    availableBalance.put(symbolBase, availableBalance.get(symbolBase) + finalAccount.getCurrentBaseBalance().doubleValue() - finalAccount.getInitialBaseBalance().doubleValue());
                    availableBalance.put(symbolAsset, availableBalance.get(symbolAsset) + finalAccount.getCurrentAssetBalance().doubleValue() - finalAccount.getInitialAssetBalance().doubleValue());
                }
                reservedBalance.put(symbolBase, reservedBalance.get(symbolBase) + finalAccount.getReservedBaseBalance().doubleValue());
                inSymbols.add(exchangeId + "__" + symbol + "__" + initialAccount.getTimestamp());
            }
            List<AccountBaseInterval> accountIntervals = getAccountIntervals(exchangeId, symbol, modelName, initialTimestamp, null, 2000, AccountBase.Retrieve.ONLY_LAST);
            accountIntervals.stream().filter((accountInterval) -> !(accountInterval.getAccounts().isEmpty())).map((accountInterval) -> accountInterval.getAccounts().get(accountInterval.getAccounts().size() - 1)).map((finalAccount) -> {
                if (!availableBalance.containsKey(symbolBase)) {
                    availableBalance.put(symbolBase, 0.0);
                }
                if (!availableBalance.containsKey(symbolAsset)) {
                    availableBalance.put(symbolAsset, 0.0);
                }
                if (!reservedBalance.containsKey(symbolBase)) {
                    reservedBalance.put(symbolBase, 0.0);
                }
                return finalAccount;
            }).map((finalAccount) -> {
                availableBalance.put(symbolBase, availableBalance.get(symbolBase) + finalAccount.getCurrentBaseBalance().doubleValue() - finalAccount.getInitialBaseBalance().doubleValue());
                availableBalance.put(symbolAsset, availableBalance.get(symbolAsset) + finalAccount.getCurrentAssetBalance().doubleValue() - finalAccount.getInitialAssetBalance().doubleValue());
                return finalAccount;
            }).forEach((finalAccount) -> {
                reservedBalance.put(symbolBase, reservedBalance.get(symbolBase) + finalAccount.getReservedBaseBalance().doubleValue());
            });
        });
        Iterator<JsonNode> initialModelBalanceAvailableAmounts = initialUserModelBalance.get("availableAmounts").elements();
        while (initialModelBalanceAvailableAmounts.hasNext()) {
            JsonNode initialModelBalanceAvailableAmount = initialModelBalanceAvailableAmounts.next();
            if (!availableBalance.containsKey(initialModelBalanceAvailableAmount.get("currency").textValue())) {
                availableBalance.put(initialModelBalanceAvailableAmount.get("currency").textValue(), 0.0);
            }
            availableBalance.put(initialModelBalanceAvailableAmount.get("currency").textValue(), availableBalance.get(initialModelBalanceAvailableAmount.get("currency").textValue()) + initialModelBalanceAvailableAmount.get("amount").doubleValue());
        }
        if (initialUserModelBalance.has("reservedAmounts")) {
            Iterator<JsonNode> initialModelBalanceReservedAmounts = initialUserModelBalance.get("reservedAmounts").elements();
            while (initialModelBalanceReservedAmounts.hasNext()) {
                JsonNode initialModelBalanceReservedAmount = initialModelBalanceReservedAmounts.next();
                if (!reservedBalance.containsKey(initialModelBalanceReservedAmount.get("currency").textValue())) {
                    reservedBalance.put(initialModelBalanceReservedAmount.get("currency").textValue(), 0.0);
                }
                reservedBalance.put(initialModelBalanceReservedAmount.get("currency").textValue(), reservedBalance.get(initialModelBalanceReservedAmount.get("currency").textValue()) + initialModelBalanceReservedAmount.get("amount").doubleValue());
            }
        }
        JsonNode modelBalance = mapper.createObjectNode();
        ((ObjectNode) modelBalance).put("modelName", modelName);
        if (userModel.has("description")) {
            ((ObjectNode) modelBalance).put("description", userModel.get("description").textValue());
        }
        ((ObjectNode) modelBalance).put("initialTimestamp", initialTimestamp);
        if (!excludeEstimatedValues) {
            Map<String, Double> estimatedValues = new HashMap<>();
            estimatedValues.put("BTC", 0.0);
            estimatedValues.put("USDT", 0.0);
            estimatedValues.put("ETH", 0.0);
            Set<String> keys = new HashSet<>();
            keys.add("BTC");
            keys.add("USDT");
            keys.add("ETH");
            availableBalance.keySet().stream().filter((key) -> (!keys.contains(key))).forEach((key) -> {
                keys.add(key);
            });
            reservedBalance.keySet().stream().filter((key) -> (!keys.contains(key))).forEach((key) -> {
                keys.add(key);
            });
            for (String key : estimatedValues.keySet()) {
                if (availableBalance.containsKey(key)) {
                    estimatedValues.put(key, estimatedValues.get(key) + availableBalance.get(key));
                }
                if (reservedBalance.containsKey(key)) {
                    estimatedValues.put(key, estimatedValues.get(key) + reservedBalance.get(key));
                }
                for (String k : keys) {
                    if (k.equals(key)) {
                        continue;
                    }
                    boolean invert = false;
                    List<Trade> lastTrade = getTrades("HitBTC", k + key, null, null, 1, "NORMAL", true);
                    if (lastTrade.isEmpty()) {
                        lastTrade = getTrades("HitBTC", key + k, null, null, 1, "NORMAL", true);
                        if (lastTrade.isEmpty()) {
                            continue;
                        }
                        invert = true;
                    }
                    if (availableBalance.containsKey(k)) {
                        if (invert) {
                            estimatedValues.put(key, estimatedValues.get(key) + availableBalance.get(k) / lastTrade.get(0).getPrice().doubleValue());
                        } else {
                            estimatedValues.put(key, estimatedValues.get(key) + availableBalance.get(k) * lastTrade.get(0).getPrice().doubleValue());
                        }
                    }
                    if (reservedBalance.containsKey(k)) {
                        if (invert) {
                            estimatedValues.put(key, estimatedValues.get(key) + reservedBalance.get(k) / lastTrade.get(0).getPrice().doubleValue());
                        } else {
                            estimatedValues.put(key, estimatedValues.get(key) + reservedBalance.get(k) * lastTrade.get(0).getPrice().doubleValue());
                        }
                    }
                }
            }
            Set<String> allowedEstimatedValues = new HashSet<>();
            allowedEstimatedValues.add("BTC");
            allowedEstimatedValues.add("USDT");
            allowedEstimatedValues.add("ETH");
            ArrayNode estimatedValuesArrayNode = mapper.createArrayNode();
            estimatedValues.keySet().stream().filter((currency) -> !(!allowedEstimatedValues.contains(currency))).map((currency) -> {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("amount", estimatedValues.get(currency));
                objectNode.put("currency", currency);
                return objectNode;
            }).forEach((objectNode) -> {
                estimatedValuesArrayNode.add(objectNode);
            });
            ((ObjectNode) modelBalance).putArray("estimatedValues").addAll(estimatedValuesArrayNode);
        }
        ArrayNode availableAmountsArrayNode = mapper.createArrayNode();
        availableBalance.keySet()
                .stream().map((currency) -> {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("amount", availableBalance.get(currency));
                    objectNode.put("currency", currency);
                    return objectNode;
                }
                ).forEach(
                        (objectNode) -> {
                            availableAmountsArrayNode.add(objectNode);
                        }
                );
        ((ObjectNode) modelBalance).putArray("availableAmounts").addAll(availableAmountsArrayNode);
        Map<String, Double> movedToReserveAmount = ModelOperation.getMovedToReserveAmount(modelName, initialTimestamp, null, isMoneyClick);
        Map<String, Double> newReservedBalance = new HashMap<>();
        for (String key
                : reservedBalance.keySet()) {
            Double newReservedAmount = reservedBalance.get(key);
            if (movedToReserveAmount != null && movedToReserveAmount.containsKey(key)) {
                newReservedAmount = newReservedAmount - movedToReserveAmount.get(key);
            }
            newReservedBalance.put(key, newReservedAmount);
        }
        ArrayNode reservedAmountsArrayNode = mapper.createArrayNode();
        newReservedBalance.keySet()
                .stream().map((currency) -> {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("amount", newReservedBalance.get(currency));
                    objectNode.put("currency", currency);
                    return objectNode;
                }
                ).forEach(
                        (objectNode) -> {
                            reservedAmountsArrayNode.add(objectNode);
                        }
                );
        ((ObjectNode) modelBalance).putArray("reservedAmounts").addAll(reservedAmountsArrayNode);
        ArrayNode inSymbolsArrayNode = mapper.createArrayNode();
        inSymbols.stream()
                .map((inSymbol) -> {
                    ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("exchangeId", inSymbol.split("__")[0]);
                    objectNode.put("symbol", inSymbol.split("__")[1]);
                    objectNode.put("startedTimestamp", inSymbol.split("__")[2]);
                    return objectNode;
                }
                ).forEach(
                        (objectNode) -> {
                            inSymbolsArrayNode.add(objectNode);
                        }
                );
        ((ObjectNode) modelBalance).putArray("inSymbols").addAll(inSymbolsArrayNode);
        return modelBalance;
    }

    public static JsonNode getUserBalance(String userName, boolean withModelBalances, boolean excludeTest, boolean excludeEstimatedValues, String finalTimestamp, boolean isMoneyClick) {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> modelNames = ExchangeUtil.getModelNames(userName, excludeTest);
        Map<String, Double> availableBalance = new HashMap<>();
        Map<String, Double> reservedBalance = new HashMap<>();
        Map<String, Double> deferredBalance = new HashMap<>();
        String userBalanceFolderName = "Balance";
        if(isMoneyClick){
            userBalanceFolderName = "MCBalance";
        }
        File userBalanceFolder = new File(new File(new File(OPERATOR_PATH, "Users"), userName), userBalanceFolderName);
        if (!userBalanceFolder.exists()) {
            return mapper.createObjectNode();
        }
        for (File userBalanceFile : userBalanceFolder.listFiles()) {
            if (!userBalanceFile.isFile()) {
                continue;
            }
            JsonNode userBalance = null;
            try {
                userBalance = mapper.readTree(userBalanceFile);

            } catch (IOException ex) {
                Logger.getLogger(LocalData.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            if (userBalance == null) {
                continue;
            }
//            DateUtil.parseDate(DateUtil.getDate(userBalance.get("timestamp").textValue()));
//            DateUtil.getDate(userBalance.get("timestamp").textValue());
//            if (finalTimestamp != null && !finalTimestamp.equals("") && DateUtil.parseDate(DateUtil.getDate(userBalance.get("timestamp").textValue())).after(DateUtil.parseDate(finalTimestamp))) {
//
//            }
            if (!userBalance.has("balanceOperationStatus")) {
                continue;
            }
            BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.valueOf(userBalance.get("balanceOperationStatus").textValue());
            if (balanceOperationStatus.equals(BalanceOperationStatus.FAIL)) {
                continue;
            }
            if (userBalanceFile.getName().contains("add")) {
                if (!userBalance.has("addedAmount")) {
                    continue;
                }
                JsonNode userBalanceAddedAmount = userBalance.get("addedAmount");
                if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING)) {
                    if (!deferredBalance.containsKey(userBalanceAddedAmount.get("currency").textValue())) {
                        deferredBalance.put(userBalanceAddedAmount.get("currency").textValue(), 0.0);
                    }
                    deferredBalance.put(userBalanceAddedAmount.get("currency").textValue(), deferredBalance.get(userBalanceAddedAmount.get("currency").textValue()) + userBalanceAddedAmount.get("amount").doubleValue());
                } else if (balanceOperationStatus.equals(BalanceOperationStatus.OK)) {
                    if (!availableBalance.containsKey(userBalanceAddedAmount.get("currency").textValue())) {
                        availableBalance.put(userBalanceAddedAmount.get("currency").textValue(), 0.0);
                    }
                    availableBalance.put(userBalanceAddedAmount.get("currency").textValue(), availableBalance.get(userBalanceAddedAmount.get("currency").textValue()) + userBalanceAddedAmount.get("amount").doubleValue());
                }
            } else if (userBalanceFile.getName().contains("substract")) {
                if (!userBalance.has("substractedAmount")) {
                    continue;
                }
                JsonNode userBalanceSubstractedAmount = userBalance.get("substractedAmount");
                if (!availableBalance.containsKey(userBalanceSubstractedAmount.get("currency").textValue())) {
                    availableBalance.put(userBalanceSubstractedAmount.get("currency").textValue(), 0.0);
                }
                availableBalance.put(userBalanceSubstractedAmount.get("currency").textValue(), availableBalance.get(userBalanceSubstractedAmount.get("currency").textValue()) - userBalanceSubstractedAmount.get("amount").doubleValue());
            }
        }
        Map<String, Double> estimatedValues = new HashMap<>();
        if (!excludeEstimatedValues) {
            estimatedValues.put("BTC", 0.0);
            estimatedValues.put("USDT", 0.0);
            estimatedValues.put("ETH", 0.0);
            Set<String> keys = new HashSet<>();
            keys.add("BTC");
            keys.add("USDT");
            keys.add("ETH");
            availableBalance.keySet().stream().filter((key) -> (!keys.contains(key))).forEach((key) -> {
                keys.add(key);
            });
            reservedBalance.keySet().stream().filter((key) -> (!keys.contains(key))).forEach((key) -> {
                keys.add(key);
            });
            for (String key : estimatedValues.keySet()) {
                Double availableBalanceAmount = availableBalance.get(key);
                Double reservedBalanceAmount = reservedBalance.get(key);
                Double estimatedValuesAmount = estimatedValues.get(key);
                if (availableBalanceAmount != null) {
                    estimatedValuesAmount = estimatedValuesAmount + availableBalanceAmount;
                }
                if (reservedBalanceAmount != null) {
                    estimatedValuesAmount = estimatedValuesAmount + reservedBalanceAmount;
                }
                estimatedValues.put(key, estimatedValuesAmount);
                for (String k : keys) {
                    if (k.equals(key)) {
                        continue;
                    }
                    boolean invert = false;
                    List<Trade> lastTrade = getTrades("HitBTC", k + key, null, null, 1, "NORMAL", true);
                    if (lastTrade.isEmpty()) {
                        lastTrade = getTrades("HitBTC", key + k, null, null, 1, "NORMAL", true);
                        if (lastTrade.isEmpty()) {
                            continue;
                        }
                        invert = true;
                    }
                    if (availableBalance.containsKey(k) && availableBalance.get(key) != null) {
                        if (invert) {
                            if(lastTrade.get(0).getPrice() != null){
                                estimatedValues.put(k, estimatedValues.get(k) + availableBalance.get(key) * lastTrade.get(0).getPrice().doubleValue());
                            } 
                        } else {
                            if(lastTrade.get(0).getPrice() != null){
                                estimatedValues.put(k, estimatedValues.get(k) + availableBalance.get(key) / lastTrade.get(0).getPrice().doubleValue());
                            } 
                        }
                    }
                    if (reservedBalance.containsKey(k)) {
                        if (invert) {
                            estimatedValues.put(k, estimatedValues.get(k) + reservedBalance.get(key) * lastTrade.get(0).getPrice().doubleValue());
                        } else {
                            estimatedValues.put(k, estimatedValues.get(k) + reservedBalance.get(key) / lastTrade.get(0).getPrice().doubleValue());
                        }
                    }
                }
            }
        }
        List<JsonNode> modelBalances = new ArrayList<>();
        if (withModelBalances) {
            modelNames.stream().map((modelName) -> getModelBalance(modelName, false, excludeEstimatedValues, isMoneyClick)).map((modelBalance) -> {
                modelBalances.add(modelBalance);
                return modelBalance;
            }).map((modelBalance) -> {
                if (!excludeEstimatedValues) {
                    Iterator<JsonNode> modelBalanceEstimatedValues = modelBalance.get("estimatedValues").elements();
                    while (modelBalanceEstimatedValues.hasNext()) {
                        JsonNode modelBalanceEstimatedValue = modelBalanceEstimatedValues.next();
                        if (!estimatedValues.containsKey(modelBalanceEstimatedValue.get("currency").textValue())) {
                            estimatedValues.put(modelBalanceEstimatedValue.get("currency").textValue(), 0.0);
                        }
                        estimatedValues.put(modelBalanceEstimatedValue.get("currency").textValue(), estimatedValues.get(modelBalanceEstimatedValue.get("currency").textValue()) + modelBalanceEstimatedValue.get("amount").doubleValue());
                    }
                }
                Iterator<JsonNode> modelBalanceReservedAmounts = modelBalance.get("reservedAmounts").elements();
                return modelBalanceReservedAmounts;
            }).forEach((modelBalanceReservedAmounts) -> {
            });
        }
        JsonNode userBalance = mapper.createObjectNode();
        ((ObjectNode) userBalance).put("userName", userName);
        if (!excludeEstimatedValues) {
            Set<String> allowedEtimatedValues = new HashSet<>();
            allowedEtimatedValues.add("BTC");
            allowedEtimatedValues.add("USDT");
            ArrayNode estimatedValuesArrayNode = mapper.createArrayNode();
            estimatedValues.keySet().stream().filter((currency) -> !(!allowedEtimatedValues.contains(currency))).map((currency) -> {
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("amount", estimatedValues.get(currency));
                objectNode.put("currency", currency);
                return objectNode;
            }).forEach((objectNode) -> {
                estimatedValuesArrayNode.add(objectNode);
            });
            ((ObjectNode) userBalance).putArray("estimatedValues").addAll(estimatedValuesArrayNode);
        }
        ArrayNode availableAmountsArrayNode = mapper.createArrayNode();
        availableBalance.keySet().stream().map((currency) -> {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("amount", availableBalance.get(currency));
            objectNode.put("currency", currency);
            return objectNode;
        }).forEach((objectNode) -> {
            availableAmountsArrayNode.add(objectNode);
        });
        ((ObjectNode) userBalance).putArray("availableAmounts").addAll(availableAmountsArrayNode);
        ArrayNode deferredAmountsArrayNode = mapper.createArrayNode();
        deferredBalance.keySet().stream().map((currency) -> {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("amount", deferredBalance.get(currency));
            objectNode.put("currency", currency);
            return objectNode;
        }).forEach((objectNode) -> {
            deferredAmountsArrayNode.add(objectNode);
        });
        ((ObjectNode) userBalance).putArray("deferredAmounts").addAll(deferredAmountsArrayNode);
        if (withModelBalances) {
            ArrayNode modelBalancesArrayNode = mapper.createArrayNode();
            modelBalances.stream().forEach((modelBalance) -> {
                modelBalancesArrayNode.add(modelBalance);
            });
            ((ObjectNode) userBalance).putArray("modelBalances").addAll(modelBalancesArrayNode);
        }
        return userBalance;
    }

}
