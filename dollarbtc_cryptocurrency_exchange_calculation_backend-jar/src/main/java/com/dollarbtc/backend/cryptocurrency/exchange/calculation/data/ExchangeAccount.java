package com.dollarbtc.backend.cryptocurrency.exchange.calculation.data;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Ticker;
import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.PrimaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.SecundaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.IntervalAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.OrderBlockingCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ExchangeAccount {

    //GENERAL
    private String modelActivationTimestamp;
    private String startedTimestamp;
    private String intervalAlgorithmName;
    private AccountBase accountBase;
    private BigDecimal currentReservedBaseBalance = BigDecimal.ZERO;
    private BigDecimal feeBalance = BigDecimal.ZERO;
    private int tradeCounter = 0;
    private int stopLossInARowCounter = 0;
    private int maxLossInARowCounter = 0;
    private Map<String, OrderBlockingCondition.Action> orderBlockingConditionActionByIntervalAlgorithmName = new HashMap<>();
    private Buy buy = new Buy();
    private int maxBuyQuantity = 100;
    private int buyCounter;
    private String lastBuyTimestamp;
    private File ordersFolder;
    private File lastOrdersFolder;
    private File accountsFolder;
    private File modelExchangeIdSymbolFolder;
    private Ticker btcTicker;

    public ExchangeAccount(String exchangeId, String symbol, String modelName) {
        if (modelName != null && !modelName.equals("")) {
            modelExchangeIdSymbolFolder = FileUtil.createFolderIfNoExist(new File(new File(new File(new File(OPERATOR_PATH, "Users"), modelName.split("__")[0]), "Models"), modelName), exchangeId + "__" + symbol);
            ordersFolder = FileUtil.createFolderIfNoExist(modelExchangeIdSymbolFolder, "Orders");
            lastOrdersFolder = FileUtil.createFolderIfNoExist(modelExchangeIdSymbolFolder, "LastOrders");
            accountsFolder = FileUtil.createFolderIfNoExist(modelExchangeIdSymbolFolder, "Accounts");
        }
        String firstFileInExchangeAccountsFolderName = ExchangeUtil.getFirstFileInFolderName(accountsFolder);
        if (firstFileInExchangeAccountsFolderName != null) {
            ExchangeUtil.accountsFileId = Long.parseLong(firstFileInExchangeAccountsFolderName.replace(".json", "")) - 1L;
            String firstFileInOrdersFolderName = ExchangeUtil.getFirstFileInFolderName(ordersFolder);
            if (firstFileInOrdersFolderName != null) {
                ExchangeUtil.ordersFileId = Long.parseLong(firstFileInOrdersFolderName.replace(".json", "")) - 1L;
            }
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(accountsFolder.getPath()));) {
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
                    JsonNode accountData = mapper.readTree(it.toFile());
                    if (accountData == null) {
                        continue;
                    }
                    if (accountData.get("accountBase") == null) {
                        continue;
                    }
                    this.startedTimestamp = accountData.get("startedTimestamp").textValue();
                    this.intervalAlgorithmName = accountData.get("intervalAlgorithmName").textValue();
                    this.accountBase = new AccountBase.Builder(
                            new BigDecimal(accountData.get("accountBase").get("reservedBaseBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("initialBaseBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("currentBaseBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("initialAssetBalance").doubleValue()),
                            new BigDecimal(accountData.get("accountBase").get("currentAssetBalance").doubleValue()),
                            exchangeId,
                            symbol,
                            modelName,
                            accountData.get("accountBase").get("timestamp").textValue()
                    ).build();
                    if (accountData.get("buy").has("orders")) {
                        Iterator<JsonNode> ordersData = accountData.get("buy").get("orders").elements();
                        while (ordersData.hasNext()) {
                            JsonNode orderData = ordersData.next();
                            BigDecimal price = new BigDecimal(orderData.get("price").doubleValue());
                            BigDecimal tradableAmount = new BigDecimal(orderData.get("tradableAmount").doubleValue());
                            Order.Type orderType = Order.Type.valueOf(orderData.get("type").textValue());
                            String orderTimestamp = orderData.get("timestamp").textValue();
                            Order order = new Order.Builder(exchangeId, symbol, orderType, orderTimestamp).tradableAmount(tradableAmount).price(price).build();
                            this.buy.orders.add(order);
                        }
                    }
                    this.buyCounter = accountData.get("buyCounter").intValue();
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(ExchangeAccount.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.accountBase = new AccountBase.Builder(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    exchangeId,
                    symbol,
                    modelName,
                    null
            ).build();
        }
    }

    public String getModelActivationTimestamp() {
        return modelActivationTimestamp;
    }

    public void setModelActivationTimestamp(String modelActivationTimestamp) {
        this.modelActivationTimestamp = modelActivationTimestamp;
    }

    public String getIntervalAlgorithmName() {
        return intervalAlgorithmName;
    }

    public String getStartedTimestamp() {
        return startedTimestamp;
    }

    public AccountBase getAccountBase() {
        return accountBase;
    }

    public BigDecimal getCurrentReservedBaseBalance() {
        return currentReservedBaseBalance;
    }

    public void setCurrentReservedBaseBalance(BigDecimal currentReservedBaseBalance) {
        this.currentReservedBaseBalance = currentReservedBaseBalance;
    }

    public int getStopLossInARowCounter() {
        return stopLossInARowCounter;
    }

    public void setStopLossInARowCounter(int stopLossInARowCounter) {
        this.stopLossInARowCounter = stopLossInARowCounter;
    }
    
    public int getMaxLossInARowCounter() {
        return maxLossInARowCounter;
    }

    public void setMaxLossInARowCounter(int maxLossInARowCounter) {
        this.maxLossInARowCounter = maxLossInARowCounter;
    }

    public Map<String, OrderBlockingCondition.Action> getOrderBlockingConditionActionByIntervalAlgorithmName() {
        return orderBlockingConditionActionByIntervalAlgorithmName;
    }

    public Buy getBuy() {
        return buy;
    }

    public void setBuy(Buy buy) {
        this.buy = buy;
    }

    public int getMaxBuyQuantity() {
        return maxBuyQuantity;
    }

    public void setMaxBuyQuantity(int maxBuyQuantity) {
        this.maxBuyQuantity = maxBuyQuantity;
    }

    public int getBuyCounter() {
        return buyCounter;
    }

    public String getLastBuyTimestamp() {
        return lastBuyTimestamp;
    }

    public File getOrdersFolder() {
        return ordersFolder;
    }

    public File getAccountsFolder() {
        return accountsFolder;
    }

    public File getModelExchangeIdSymbolFolder() {
        return modelExchangeIdSymbolFolder;
    }

    public double getBaseBalance(BigDecimal currentPrice) {
        return this.accountBase.getCurrentBaseBalance().add(this.accountBase.getCurrentAssetBalance().multiply(currentPrice)).doubleValue();
    }

    public double getOverallDeductedFees() {
        return feeBalance.doubleValue();
    }

    public Ticker getBtcTicker() {
        return btcTicker;
    }

    public void setBtcTicker(Ticker btcTicker) {
        this.btcTicker = btcTicker;
    }

    public void setTradeCounter(int tradeCounter) {
        this.tradeCounter = tradeCounter;
    }

    public void setBuyCounter(int buyCounter) {
        this.buyCounter = buyCounter;
    }

    public void setLastBuyTimestamp(String lastBuyTimestamp) {
        this.lastBuyTimestamp = lastBuyTimestamp;
    }

    public void setStartedTimestamp(String startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public void setIntervalAlgorithmName(String intervalAlgorithmName) {
        this.intervalAlgorithmName = intervalAlgorithmName;
    }

    public boolean startCoin(IntervalAlgorithm intervalAlgorithm, String timestamp, String modelName) {
        String symbolBase = this.accountBase.getSymbolBase();
        BigDecimal initialBaseAmount = this.getInitialBaseAmount(this.accountBase.getSymbolBase());
        System.out.println("initialBaseAmount: " + initialBaseAmount);
        switch (symbolBase) {
            case "BTC":
                if (initialBaseAmount.compareTo(new BigDecimal(0.01)) < 0) {
                    System.out.println("do not have enough balance to start coin " + modelName + " " + this.accountBase.getExchangeId() + " " + this.accountBase.getSymbol());
                    return false;
                }
                break;
            case "USDT":
                if (initialBaseAmount.compareTo(new BigDecimal(35)) < 0) {
                    System.out.println("do not have enough balance to start coin " + modelName + " " + this.accountBase.getExchangeId() + " " + this.accountBase.getSymbol());
                    return false;
                }
                break;
            case "ETH":
                if (initialBaseAmount.compareTo(new BigDecimal(0.4)) < 0) {
                    System.out.println("do not have enough balance to start coin " + modelName + " " + this.accountBase.getExchangeId() + " " + this.accountBase.getSymbol());
                    return false;
                }
                break;
        }
        this.accountBase.setInitialBaseBalance(initialBaseAmount);
        this.accountBase.setCurrentBaseBalance(initialBaseAmount);
        this.startedTimestamp = timestamp;
        this.intervalAlgorithmName = intervalAlgorithm.name;
        this.resetInConditions(timestamp, intervalAlgorithm);
        return true;
    }

    public void stopCoin(String timestamp) {
        if (intervalAlgorithmName == null || intervalAlgorithmName.equals("")) {
            intervalAlgorithmName = "NONE";
        }
        File newOrdersFolder = new File(ordersFolder, DateUtil.getFileDate(timestamp) + "____" + intervalAlgorithmName);
        FileUtil.moveAllFilesToFolder(ordersFolder, newOrdersFolder);
        File newAccountsFolder = new File(accountsFolder, DateUtil.getFileDate(timestamp) + "____" + intervalAlgorithmName);
        FileUtil.moveAllFilesToFolder(accountsFolder, newAccountsFolder);
    }

    public boolean isEnoughBase(Order order, IntervalAlgorithm intervalAlgorithm) {
        if (order.getType() == Order.Type.BUY) {
            boolean enoughBase = (order.getTradableAmount().multiply(order.getPrice()).multiply(BigDecimal.ONE.add(ExchangeUtil.FEE_TRANSACTION_FACTOR)).compareTo(this.accountBase.getCurrentBaseBalance()) <= 0);
            if (!enoughBase) {
                intervalAlgorithm.notEnoughBalanceCondition.addToBuyInARowCounter();
                intervalAlgorithm.notEnoughBalanceCondition.restartToSellInARowCounter();
                if (intervalAlgorithm.notEnoughBalanceCondition.getToBuyInARowHighestSellPrice() == null) {
                    intervalAlgorithm.notEnoughBalanceCondition.setToBuyInARowHighestSellPrice(order.getPrice());
                } else {
                    if (order.getPrice().compareTo(intervalAlgorithm.notEnoughBalanceCondition.getToBuyInARowHighestSellPrice()) > 0) {
                        intervalAlgorithm.notEnoughBalanceCondition.setToBuyInARowHighestSellPrice(order.getPrice());
                        intervalAlgorithm.notEnoughBalanceCondition.setToBuyInARowConsistentDownTrend(false);
                    } else {
                        if (intervalAlgorithm.notEnoughBalanceCondition.getToBuyInARowConsistentDownTrend() == null) {
                            intervalAlgorithm.notEnoughBalanceCondition.setToBuyInARowConsistentDownTrend(true);
                        }
                    }
                }
                System.out.println("Account with no enoughBase");
            } else {
                intervalAlgorithm.notEnoughBalanceCondition.restartToSellInARowCounter();
                intervalAlgorithm.notEnoughBalanceCondition.restartToBuyInARowCounter();
            }
            return enoughBase;
        }
        return true;
    }

    public boolean isEnoughAsset(Order order, IntervalAlgorithm intervalAlgorithm) {
        if (order.getType() == Order.Type.SELL) {
            boolean enoughBtc = (order.getTradableAmount().compareTo(this.accountBase.getCurrentAssetBalance()) <= 0);
            if (!enoughBtc) {
                intervalAlgorithm.notEnoughBalanceCondition.addToSellInARowCounter();
                intervalAlgorithm.notEnoughBalanceCondition.restartToBuyInARowCounter();
                if (intervalAlgorithm.notEnoughBalanceCondition.getToSellInARowLowestBuyPrice() == null) {
                    intervalAlgorithm.notEnoughBalanceCondition.setToSellInARowLowestBuyPrice(order.getPrice());
                } else {
                    if (order.getPrice().compareTo(intervalAlgorithm.notEnoughBalanceCondition.getToSellInARowLowestBuyPrice()) < 0) {
                        intervalAlgorithm.notEnoughBalanceCondition.setToSellInARowLowestBuyPrice(order.getPrice());
                        intervalAlgorithm.notEnoughBalanceCondition.setToSellInARowConsistentUpTrend(false);
                    } else {
                        if (intervalAlgorithm.notEnoughBalanceCondition.getToSellInARowConsistentUpTrend() == null) {
                            intervalAlgorithm.notEnoughBalanceCondition.setToSellInARowConsistentUpTrend(true);
                        }
                    }
                }
                System.out.println("Account with no enoughBtc");
            } else {
                intervalAlgorithm.notEnoughBalanceCondition.restartToSellInARowCounter();
                intervalAlgorithm.notEnoughBalanceCondition.restartToBuyInARowCounter();
            }
            return enoughBtc;
        }
        return true;
    }

    public boolean buy(Order order, String timestamp) {
        if (!PrimaryOperation.userNewOrder(this.accountBase.getExchangeId(), this.accountBase.getModelName().split("__")[0], this.accountBase.getSymbol(), order)) {
            return false;
        }
//        BigDecimal baseAmount = BigDecimal.ZERO;
//        BigDecimal tradableAmount = BigDecimal.ZERO;
//        if (order.getMarketTrades().isEmpty()) {
//            baseAmount = addFee(order.getTradableAmount().multiply(order.getPrice()));
//            tradableAmount = order.getTradableAmount();
//        } else {
//            for (Order.MarketTrade orderMarketTrade : order.getMarketTrades()) {
//                baseAmount = baseAmount.add(addFee(orderMarketTrade.getTradableAmount().multiply(orderMarketTrade.getPrice()), orderMarketTrade.getFeeAmount()));
//                tradableAmount = tradableAmount.add(orderMarketTrade.getTradableAmount());
//            }
//        }
        BigDecimal baseAmount = addFee(order.getTradableAmount().multiply(order.getPrice()));
        BigDecimal tradableAmount = order.getTradableAmount();
        this.accountBase.setCurrentBaseBalance(this.accountBase.getCurrentBaseBalance().subtract(baseAmount));
        this.accountBase.setCurrentAssetBalance(this.accountBase.getCurrentAssetBalance().add(tradableAmount));
        buy.addOrder(order);
        buyCounter++;
        lastBuyTimestamp = timestamp;
        // Updating the trade counter
        tradeCounter++;
        ExchangeUtil.createFile(order.toJsonNode(), new File[]{ordersFolder, lastOrdersFolder}, "order");
        return true;
    }

    public boolean sell(Order order) {
        if (!PrimaryOperation.userNewOrder(this.accountBase.getExchangeId(), this.accountBase.getModelName().split("__")[0], this.accountBase.getSymbol(), order)) {
            return false;
        }
        this.accountBase.setCurrentAssetBalance(this.accountBase.getCurrentAssetBalance().subtract(order.getTradableAmount()));
        // Deducting transaction fee
        BigDecimal baseAmount = deductFee(order.getTradableAmount().multiply(order.getPrice()));
        this.accountBase.setCurrentBaseBalance(this.accountBase.getCurrentBaseBalance().add(baseAmount));
        // Updating the trade counter
        tradeCounter++;
        ExchangeUtil.createFile(order.toJsonNode(), new File[]{ordersFolder, lastOrdersFolder}, "order");
        return true;
    }

    public double getOverallEarnings(BigDecimal currentPrice) {
        BigDecimal baseDifference = this.accountBase.getCurrentBaseBalance().subtract(this.accountBase.getInitialBaseBalance());
        BigDecimal btcDifference = this.accountBase.getCurrentAssetBalance().subtract(this.accountBase.getInitialAssetBalance());
        BigDecimal overallEarnings = baseDifference.add(btcDifference.multiply(currentPrice));
        return overallEarnings.doubleValue();
    }

    public boolean addToReservedBaseBalance(BigDecimal currentPrice, BigDecimal amountToReserveBase, BigDecimal reserveBaseLimitAmount) {
        BigDecimal baseDifference = this.accountBase.getCurrentBaseBalance().subtract(this.accountBase.getInitialBaseBalance());
        BigDecimal btcDifference = this.accountBase.getCurrentAssetBalance().subtract(this.accountBase.getInitialAssetBalance());
        BigDecimal overallEarnings = baseDifference.add(btcDifference.multiply(currentPrice));
        if (overallEarnings.compareTo(amountToReserveBase) > 0) {
            if (this.accountBase.getCurrentBaseBalance().compareTo(overallEarnings) > 0) {
                if (currentReservedBaseBalance.equals(BigDecimal.ZERO)) {
                    currentReservedBaseBalance = currentReservedBaseBalance.add(overallEarnings);
                }
                if (this.accountBase.getReservedBaseBalance().add(currentReservedBaseBalance).compareTo(reserveBaseLimitAmount) >= 0) {
                    return true;
                }
            } else {
                if (this.accountBase.getReservedBaseBalance().add(overallEarnings).compareTo(reserveBaseLimitAmount) >= 0) {
                    currentReservedBaseBalance = currentReservedBaseBalance.add(overallEarnings);
                    return true;
                }
            }
        }
        return false;
    }

    public void resetInConditions(String timestamp, IntervalAlgorithm intervalAlgorithm) {
        if (intervalAlgorithm.orderBlockingCondition.isUsed()) {
            OrderBlockingCondition.Action action = orderBlockingConditionActionByIntervalAlgorithmName.get(intervalAlgorithmName);
            if (action.equals(OrderBlockingCondition.Action.IN_RESET)) {
                intervalAlgorithm.orderBlockingCondition.reset(modelExchangeIdSymbolFolder, timestamp);
            }
        }
        if (intervalAlgorithm.inPriceBandCondition.isUsed()) {
            intervalAlgorithm.inPriceBandCondition.reset(modelExchangeIdSymbolFolder);
        }
    }

    private BigDecimal deductFee(BigDecimal amount) {
        BigDecimal feeAmount = amount.multiply(ExchangeUtil.FEE_TRANSACTION_FACTOR);
        feeBalance = feeBalance.add(feeAmount);
        return amount.subtract(feeAmount);
    }

    private BigDecimal addFee(BigDecimal amount) {
        BigDecimal feeAmount = amount.multiply(ExchangeUtil.FEE_TRANSACTION_FACTOR);
        feeBalance = feeBalance.add(feeAmount);
        return amount.add(feeAmount);
    }

    private BigDecimal addFee(BigDecimal amount, BigDecimal feeAmount) {
        feeBalance = feeBalance.add(feeAmount);
        return amount.add(feeAmount);
    }

    private BigDecimal getInitialBaseAmount(String symbolBase) {
        ObjectMapper mapper = new ObjectMapper();
        File modelFile = new File(new File(new File(new File(new File(OPERATOR_PATH, "Users"), this.getAccountBase().getModelName().split("__")[0]), "Models"), this.getAccountBase().getModelName()), "config.json");
        JsonNode model = null;
        try {
            model = mapper.readTree(modelFile);
        } catch (IOException ex) {
            Logger.getLogger(ExchangeAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model == null) {
            return null;
        }
        Double startBasePercent = null;
        Iterator<JsonNode> modelSymbolRules = model.get("symbolRules").elements();
        while (modelSymbolRules.hasNext()) {
            JsonNode modelSymbolRule = modelSymbolRules.next();
            String modelSymbolRuleExchangeId = modelSymbolRule.get("exchangeId").textValue();
            if (!modelSymbolRuleExchangeId.equals(this.getAccountBase().getExchangeId())) {
                continue;
            }
            String modelSymbolRuleSymbol = modelSymbolRule.get("symbol").textValue();
            if (!modelSymbolRuleSymbol.equals(this.getAccountBase().getSymbol())) {
                continue;
            }
            boolean modelSymbolRuleActive = modelSymbolRule.get("active").booleanValue();
            if (!modelSymbolRuleActive) {
                continue;
            }
            startBasePercent = modelSymbolRule.get("startBasePercent").doubleValue();
            break;
        }
        if (startBasePercent == null) {
            return BigDecimal.ZERO;
        }
        JsonNode modelBalance = LocalData.getModelBalance(this.getAccountBase().getModelName(), true, false, false);
        Iterator<JsonNode> modelBalanceEstimatedValues = modelBalance.get("estimatedValues").elements();
        Double estimatedValues = null;
        while (modelBalanceEstimatedValues.hasNext()) {
            JsonNode modelBalanceEstimatedValue = modelBalanceEstimatedValues.next();
            if (modelBalanceEstimatedValue.get("currency").textValue().equals(this.accountBase.getSymbolBase())) {
                estimatedValues = modelBalanceEstimatedValue.get("amount").doubleValue();
                break;
            }
        }
        Double initialBaseAmount = estimatedValues * startBasePercent / 100;
        Double availableBaseAmount = 0.0;
        Map<String, Double> availableAmounts = new HashMap<>();
        Iterator<JsonNode> modelBalanceAvailableAmounts = modelBalance.get("availableAmounts").elements();
        while (modelBalanceAvailableAmounts.hasNext()) {
            JsonNode modelBalanceAvailableAmount = modelBalanceAvailableAmounts.next();
            if (modelBalanceAvailableAmount.get("currency").textValue().equals(this.accountBase.getSymbolBase())) {
                availableBaseAmount = availableBaseAmount + modelBalanceAvailableAmount.get("amount").doubleValue();
            } else {
                availableAmounts.put(modelBalanceAvailableAmount.get("currency").textValue(), modelBalanceAvailableAmount.get("amount").doubleValue());
            }
        }
        if (initialBaseAmount > availableBaseAmount) {
            boolean out = false;
            for (String key : availableAmounts.keySet()) {
                double portionFactor = 0;
                Double amountToChange = availableAmounts.get(key) * 0.05;
                while (portionFactor <= 1) {
                    portionFactor = portionFactor + 0.05;
                    SecundaryOperation.currencyChangeModel(this.getAccountBase().getModelName(), key, symbolBase, BigDecimal.valueOf(amountToChange));
                    modelBalance = LocalData.getModelBalance(this.getAccountBase().getModelName(), true, true, false);
                    modelBalanceAvailableAmounts = modelBalance.get("availableAmounts").elements();
                    while (modelBalanceAvailableAmounts.hasNext()) {
                        JsonNode modelBalanceAvailableAmount = modelBalanceAvailableAmounts.next();
                        if (modelBalanceAvailableAmount.get("currency").textValue().equals(this.accountBase.getSymbolBase())) {
                            if (modelBalanceAvailableAmount.get("amount").doubleValue() >= initialBaseAmount) {
                                out = true;
                                break;
                            }
                        }
                        if (out) {
                            break;
                        }
                    }
                    if (out) {
                        break;
                    }
                }
                if (out) {
                    break;
                }
            }
        }
        return BigDecimal.valueOf(initialBaseAmount);
    }

    @Override
    public String toString() {
        return "ExchangeAccount ["
                + "startedTimestamp=" + startedTimestamp
                + ", intervalAlgorithmName=" + intervalAlgorithmName
                + ", accountBase=" + accountBase
                + ", feeBalance=" + feeBalance
                + ", tradeCounter=" + tradeCounter
                + ", maxLossInARowCounter=" + maxLossInARowCounter
                + ", maxBuyQuantity=" + maxBuyQuantity
                + ", buyCounter=" + buyCounter
                + ", lastBuyTimestamp=" + lastBuyTimestamp
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("startedTimestamp", this.startedTimestamp);
        ((ObjectNode) jsonNode).put("intervalAlgorithmName", this.intervalAlgorithmName);
        ((ObjectNode) jsonNode).put("accountBase", this.accountBase.toJsonNode());
        ((ObjectNode) jsonNode).put("feeBalance", this.feeBalance);
        ((ObjectNode) jsonNode).put("tradeCounter", this.tradeCounter);
        ((ObjectNode) jsonNode).put("stopLossInARowCounter", this.stopLossInARowCounter);
        ((ObjectNode) jsonNode).put("maxLossInARowCounter", this.maxLossInARowCounter);
        ((ObjectNode) jsonNode).put("buy", this.buy.toJsonNode());
        ((ObjectNode) jsonNode).put("maxBuyQuantity", this.maxBuyQuantity);
        ((ObjectNode) jsonNode).put("buyCounter", this.buyCounter);
        ((ObjectNode) jsonNode).put("lastBuyTimestamp", this.lastBuyTimestamp);
        return jsonNode;
    }

    public static class Buy {

        private final List<Order> orders = new ArrayList<>();

        public Order getLastOrder() {
            if (orders.isEmpty()) {
                return null;
            }
            return orders.get(orders.size() - 1);
        }

        public Order getFirstOrder() {
            if (orders.isEmpty()) {
                return null;
            }
            return orders.get(0);
        }

        public void addOrder(Order order) {
            orders.add(order);
        }

        public int getQuantity() {
            return orders.size();
        }

        public void clear() {
            orders.clear();
        }

        public Order getSellOrder(Order sellOrder, BigDecimal earningConditionMinTransactionFactor) {
            if (orders.isEmpty()) {
                return sellOrder;
            }
            BigDecimal minTransactionFactor = ExchangeUtil.FEE_TRANSACTION_FACTOR.multiply(BigDecimal.valueOf(2)).add(earningConditionMinTransactionFactor);
            BigDecimal finalSellOrderTradableAmount = BigDecimal.ZERO;
            List<Order> ordersToRemove = new ArrayList<>();
            for (Order order : orders) {
                if (order.getPrice().multiply(BigDecimal.ONE.add(minTransactionFactor)).compareTo(sellOrder.getPrice()) < 0) {
                    finalSellOrderTradableAmount = finalSellOrderTradableAmount.add(order.getTradableAmount());
                    if (finalSellOrderTradableAmount.compareTo(sellOrder.getTradableAmount()) > 0) {
                        finalSellOrderTradableAmount = finalSellOrderTradableAmount.subtract(order.getTradableAmount());
                    } else {
                        ordersToRemove.add(order);
                    }
                }
            }
            orders.removeAll(ordersToRemove);
            sellOrder.setTradableAmount(finalSellOrderTradableAmount);
            if (sellOrder.getTradableAmount().compareTo(BigDecimal.ZERO) > 0) {
                return sellOrder;
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            return "[orders=" + orders
                    + "]";
        }

        public JsonNode toJsonNode() {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.createObjectNode();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (Order order : orders) {
                arrayNode.add(order.toJsonNode());
            }
            ((ObjectNode) jsonNode).putArray("orders").addAll(arrayNode);
            return jsonNode;
        }

    }

}
