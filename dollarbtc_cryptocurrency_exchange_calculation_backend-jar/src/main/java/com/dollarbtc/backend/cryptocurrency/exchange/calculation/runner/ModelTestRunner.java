package com.dollarbtc.backend.cryptocurrency.exchange.calculation.runner;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.IntervalAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.InOutAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.TradingAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.FEE_TRANSACTION_FACTOR;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModelTestRunner {

    private final ExchangeAccount exchangeAccount;
    private final List<IntervalAlgorithm> intervalAlgorithms;

    public ModelTestRunner(ExchangeAccount exchangeAccount, List<IntervalAlgorithm> intervalAlgorithms) {
        this.exchangeAccount = exchangeAccount;
        this.intervalAlgorithms = intervalAlgorithms;
    }

    public void run(int[] periodsDurationInSeconds, String finalTimestamp, Long testPastTimeInHours, double lastTradePriceSpread, int scanTimeInSeconds) {
        if (lastTradePriceSpread < 0 || lastTradePriceSpread >= 1) {
            return;
        }
        int tradesQuantity = testPastTimeInHours.intValue() * 100;
        DateUtil.getDate(DateUtil.parseDate(finalTimestamp).getTime() - testPastTimeInHours * 60 * 60 * 1000);
        String timestamp = DateUtil.getDate(DateUtil.parseDate(finalTimestamp).getTime() - testPastTimeInHours * 60 * 60 * 1000);
        System.out.println("timestamp0: " + timestamp);
        List<Trade> fullTrades = LocalData.getTrades(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), null, finalTimestamp, tradesQuantity, "NORMAL", true);
        if (fullTrades.isEmpty()) {
            return;
        }
        int timestampTradePosition = fullTrades.size() - 1;
        boolean coinStarted = false;
        while (true) {
            boolean exit = false;
            while (true) {
                if (exit) {
                    break;
                }
                if (timestampTradePosition <= 0) {
                    return;
                }
                while (true) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) > 0) {
                        timestampTradePosition--;
                        if (timestampTradePosition <= 0) {
                            exit = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (exit) {
                    break;
                }
                while (true) {
                    while (true) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) > 0) {
                            timestampTradePosition--;
                            if (timestampTradePosition <= 0) {
                                exit = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (exit) {
                        break;
                    }
                    String newScanTimestamp = DateUtil.getDate(DateUtil.parseDate(timestamp).getTime() + scanTimeInSeconds * 1000);
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) <= 0
                            && DateUtil.parseDate(newScanTimestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) >= 0) {
                        while (true) {
                            if (timestampTradePosition <= 0) {
                                exit = true;
                                break;
                            }
                            if (DateUtil.parseDate(newScanTimestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition - 1).getTimestamp())) < 0) {
                                if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) <= 0
                                        && DateUtil.parseDate(newScanTimestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) >= 0) {
                                    exit = true;
                                    break;
                                }
                            }
                            timestampTradePosition--;
                            if (timestampTradePosition <= 0) {
                                exit = true;
                                break;
                            }
                        }
                    } else {
                        while (true) {
                            if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(fullTrades.get(timestampTradePosition).getTimestamp())) > 0) {
                                timestampTradePosition--;
                                if (timestampTradePosition <= 0) {
                                    exit = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    timestamp = DateUtil.getDate(DateUtil.parseDate(timestamp).getTime() + scanTimeInSeconds * 1000);
                    if (exit) {
                        break;
                    }
                }
                if (exit) {
                    break;
                }
            }
            List<Trade> trades = fullTrades.subList(timestampTradePosition, fullTrades.size() - 1);
            System.out.println("trades firtsTradePosition: " + timestampTradePosition);
            System.out.println("timestamp: " + timestamp);
            System.out.println("fullTrades.get(timestampTradePosition).getTimestamp(): " + fullTrades.get(timestampTradePosition).getTimestamp());
            if (timestampTradePosition > 0) {
                System.out.println("fullTrades.get(timestampTradePosition - 1).getTimestamp(): " + fullTrades.get(timestampTradePosition - 1).getTimestamp());
            }
            boolean alreadyCheckInPriceBandCondition = false;
            for (IntervalAlgorithm intervalAlgorithm : intervalAlgorithms) {
                if (exchangeAccount == null) {
                    break;
                }
                if (exchangeAccount.getIntervalAlgorithmName() != null && !exchangeAccount.getIntervalAlgorithmName().equals(intervalAlgorithm.name)) {
                    continue;
                }
                // CREATE PERIODS
                Map<Integer, ArrayList<Period>> periodsWithResultBidAskPricesPreviousPeriods = new HashMap<>();
                for (int periodDurationInSeconds : periodsDurationInSeconds) {
                    ExchangeMarket.resetPreviousPeriods();
                    for (Trade trade : trades) {
                        ExchangeMarket.addTrade(trade, periodDurationInSeconds);
                        ExchangeMarket.getPreviousPeriods();
                        if (tradingAlgoritmsEnoughPeriods(intervalAlgorithm.tradings) && inOutAlgoritmsEnoughPeriods(intervalAlgorithm.ins) && inOutAlgoritmsEnoughPeriods(intervalAlgorithm.outs)) {
                            ArrayList<Period> previousPeriods = new ArrayList<>();
                            previousPeriods.addAll(ExchangeMarket.getPreviousPeriods());
                            periodsWithResultBidAskPricesPreviousPeriods.put(periodDurationInSeconds, previousPeriods);
                            break;
                        }
                    }
                }
                if (periodsWithResultBidAskPricesPreviousPeriods.isEmpty()) {
                    System.out.println("not enough periods");
                    continue;
                }
                Map<Integer, Order.AlgorithmType> periodsWithResultOrderIndicatorType = new HashMap<>();
                // RECREATE BID - ASK PRICE TICKER
                BigDecimal lastTradePrice = trades.get(0).getPrice();
                BigDecimal upperTradePrice = lastTradePrice.divide(BigDecimal.ONE.subtract(new BigDecimal(lastTradePriceSpread / 2)), 8, RoundingMode.UP);
                BigDecimal diffTradePrice = upperTradePrice.subtract(lastTradePrice);
                BigDecimal lastBidPrice = lastTradePrice.subtract(diffTradePrice);
                BigDecimal lastAskPrice = lastTradePrice.add(diffTradePrice);
                exchangeAccount.getAccountBase().setLastAskPrice(lastAskPrice);
                exchangeAccount.getAccountBase().setLastBidPrice(lastBidPrice);
                // CHECK IN CONDITIONS
                if (exchangeAccount.getStartedTimestamp() == null || exchangeAccount.getStartedTimestamp().equals("")) {
                    for (Integer periodDurationInSeconds : periodsWithResultBidAskPricesPreviousPeriods.keySet()) {
                        periodsWithResultOrderIndicatorType.put(periodDurationInSeconds, getInAlgorithmType(intervalAlgorithm.ins, (ArrayList<Period>) periodsWithResultBidAskPricesPreviousPeriods.get(periodDurationInSeconds), timestamp));
//                        printInAlgorithmType(intervalAlgorithm.ins, (ArrayList<Period>) periodsWithResultBidAskPricesPreviousPeriods.get(periodDurationInSeconds), timestamp);
                    }
                    Order.AlgorithmType inOrderAlgorithmType = checkPeriodsOrderAlgorithmType(periodsWithResultOrderIndicatorType);
                    if (inOrderAlgorithmType != null) {
                        // CHECK ORDER BLOCKING CONDITION
                        if (!intervalAlgorithm.orderBlockingCondition.pass(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), exchangeAccount.getAccountBase().getModelName(), intervalAlgorithm.name, exchangeAccount.getOrderBlockingConditionActionByIntervalAlgorithmName(), exchangeAccount.getModelExchangeIdSymbolFolder())) {
                            System.out.println("not in by order blocking condition");
                            continue;
                        }
                        // CHECK IN PRICE BAND CONDITION
                        if (alreadyCheckInPriceBandCondition) {
                            continue;
                        }
                        if (!intervalAlgorithm.inPriceBandCondition.pass(intervalAlgorithm.name, lastAskPrice, exchangeAccount.getModelExchangeIdSymbolFolder())) {
                            System.out.println("not in by price band condition");
                            alreadyCheckInPriceBandCondition = true;
                            continue;
                        }

                        if (!exchangeAccount.startCoin(intervalAlgorithm, timestamp, exchangeAccount.getAccountBase().getModelName())) {
                            continue;
                        } else {
                            coinStarted = true;
                        }

                    } else {
                        System.out.println("not in by any algorithm");
                        continue;
                    }
                }
                // CHECK OUT CONDITIONS
                boolean out = false;
                Order.AlgorithmType outOrderAlgorithmType = null;
                if (outOrderAlgorithmType == null) {
                    periodsWithResultOrderIndicatorType.clear();
                    for (Integer periodDurationInSeconds : periodsWithResultBidAskPricesPreviousPeriods.keySet()) {
                        periodsWithResultOrderIndicatorType.put(periodDurationInSeconds, getOutAlgorithmType(intervalAlgorithm.outs, (ArrayList<Period>) periodsWithResultBidAskPricesPreviousPeriods.get(periodDurationInSeconds), timestamp));
                    }
                    outOrderAlgorithmType = checkPeriodsOrderAlgorithmType(periodsWithResultOrderIndicatorType);
                }
                if (outOrderAlgorithmType != null) {
                    BigDecimal assetBalance = exchangeAccount.getAccountBase().getCurrentAssetBalance();
                    Order outOrder = new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.SELL, timestamp).tradableAmount(assetBalance).price(lastBidPrice).algorithmType(outOrderAlgorithmType).build();
                    if (assetBalance.compareTo(BigDecimal.ZERO) == 1) {
                        if (exchangeAccount.sell(outOrder)) {
                            System.out.println("" + outOrder.getTimestamp() + " " + outOrder.getType() + " Amount: " + outOrder.getTradableAmount() + " Price: " + outOrder.getPrice() + " " + outOrder.getSymbol());
                            System.out.println("sell all");
                        } else {
                            System.out.println("problem with exchange account");
                        }
                    } else {
                        ExchangeUtil.createFile(outOrder.toJsonNode(), new File[]{exchangeAccount.getOrdersFolder()}, "order");
                    }
                    out = true;
                }
                // TRADE
                if (!out) {
                    System.out.println("intervalAlgorithm name: " + exchangeAccount.getIntervalAlgorithmName());
                    Map<Integer, Order> periodsWithResultOrder = new HashMap<>();
                    for (Integer periodDurationInSeconds : periodsWithResultBidAskPricesPreviousPeriods.keySet()) {
                        periodsWithResultOrder.put(periodDurationInSeconds, processOrder(exchangeAccount, intervalAlgorithm.tradings, (ArrayList<Period>) periodsWithResultBidAskPricesPreviousPeriods.get(periodDurationInSeconds), timestamp));
                    }
                    Order order = checkPeriodsOrder(periodsWithResultOrder);
                    if (order != null) {
                        processOrder(exchangeAccount, lastBidPrice, lastAskPrice, order, timestamp, intervalAlgorithm);
                    } else {
                        System.out.println("" + timestamp + " no action because no trend conclusion at ask price : " + lastAskPrice + " and bid price: " + lastBidPrice + " " + exchangeAccount.getAccountBase().getSymbol());
                    }
                }
                exchangeAccount.getAccountBase().setCurrentBaseBalance(exchangeAccount.getAccountBase().getCurrentBaseBalance().subtract(exchangeAccount.getCurrentReservedBaseBalance()));
                exchangeAccount.getAccountBase().setReservedBaseBalance(exchangeAccount.getAccountBase().getReservedBaseBalance().add(exchangeAccount.getCurrentReservedBaseBalance()));
                exchangeAccount.setCurrentReservedBaseBalance(BigDecimal.ZERO);
                exchangeAccount.getAccountBase().setTimestamp(timestamp);
                ExchangeUtil.createFile(exchangeAccount.toJsonNode(), new File[]{exchangeAccount.getAccountsFolder()}, "account");
                exchangeAccount.getAccountBase().setInfo("");
                System.out.println("Overall earnings: $"
                        + exchangeAccount.getOverallEarnings(lastBidPrice)
                        + "\n\tBase Balance: $"
                        + exchangeAccount.getBaseBalance(lastBidPrice)
                        + "\n\tAccount infos: "
                        + exchangeAccount);
                if (out) {
                    exchangeAccount.stopCoin(timestamp);
                    exchangeAccount.getAccountBase().setReservedBaseBalance(BigDecimal.ZERO);
                    exchangeAccount.getAccountBase().setCurrentAssetBalance(BigDecimal.ZERO);
                    exchangeAccount.getAccountBase().setCurrentBaseBalance(exchangeAccount.getAccountBase().getInitialBaseBalance());
                    exchangeAccount.setStartedTimestamp(null);
                    exchangeAccount.getBuy().clear();
                    exchangeAccount.setTradeCounter(0);
                    exchangeAccount.setMaxLossInARowCounter(0);
                    exchangeAccount.setBuyCounter(0);
                    exchangeAccount.setLastBuyTimestamp(null);
                    exchangeAccount.setBtcTicker(null);
                    exchangeAccount.setIntervalAlgorithmName(null);
                    coinStarted = false;
                    break;
                }
                if (coinStarted) {
                    break;
                }
            }
        }
    }

    private Order.AlgorithmType getInAlgorithmType(InOutAlgorithm[] inOutAlgorithms, ArrayList<Period> previousPeriods, String timestamp) {
        Order.AlgorithmType algorithmType = null;
        if (inOutAlgorithms != null && inOutAlgorithms.length > 0) {
            for (InOutAlgorithm inOutAlgorithm : inOutAlgorithms) {
                algorithmType = inOutAlgorithm.in(previousPeriods, timestamp, true);
                if (algorithmType == null) {
                    return null;
                }
            }
        }
        return algorithmType;
    }
    
    private void printInAlgorithmType(InOutAlgorithm[] inOutAlgorithms, ArrayList<Period> previousPeriods, String timestamp) {
        if (inOutAlgorithms != null && inOutAlgorithms.length > 0) {
            for (InOutAlgorithm inOutAlgorithm : inOutAlgorithms) {
                inOutAlgorithm.in(previousPeriods, timestamp, true);
            }
        }
        ExchangeUtil.createFile(exchangeAccount.getAccountBase().infoToJsonNode(), new File[]{FileUtil.createFolderIfNoExist(exchangeAccount.getModelExchangeIdSymbolFolder(), "inAlgorithmsInfo")}, "inAlgorithmInfo");
        exchangeAccount.getAccountBase().setInfo("");
    }

    private Order.AlgorithmType getOutAlgorithmType(InOutAlgorithm[] inOutAlgorithms, ArrayList<Period> previousPeriods, String timestamp) {
        Order.AlgorithmType algorithmType = null;
        if (inOutAlgorithms != null && inOutAlgorithms.length > 0) {
            for (InOutAlgorithm inOutAlgorithm : inOutAlgorithms) {
                if (algorithmType != null) {
                    break;
                }
                algorithmType = inOutAlgorithm.out(previousPeriods, timestamp, true);
            }
        }
        return algorithmType;
    }

    private Order processOrder(ExchangeAccount exchangeAccount, TradingAlgorithm[] tradingAlgorithms, ArrayList<Period> previousPeriods, String timestamp) {
        Order order = null;
        if (tradingAlgorithms != null && tradingAlgorithms.length > 0) {
            for (TradingAlgorithm tradingAlgorithm : tradingAlgorithms) {
                if (order != null) {
                    break;
                }
                order = tradingAlgorithm.placeOrder(previousPeriods, timestamp, true);
                if (order != null && order.getType().equals(Order.Type.BUY) && exchangeAccount.getBuyCounter() == exchangeAccount.getMaxBuyQuantity()) {
                    order = null;
                    break;
                }
            }
        }
        return order;
    }

    private void processOrder(ExchangeAccount exchangeAccount, BigDecimal lastBidPrice, BigDecimal lastAskPrice, Order order, String timestamp, IntervalAlgorithm intervalAlgorithm) {
        boolean process = false;
        if (order.getType() == Order.Type.BUY) {
            // Buy
            if (order.getPrice() == null) {
                order.setPrice(lastAskPrice);
            }
            if (exchangeAccount.isEnoughBase(order, intervalAlgorithm)) {
                order.setTradingType(Order.TradingType.NORMAL);
                if (exchangeAccount.buy(order, timestamp)) {
                    process = true;
                    System.out.println("" + order.getTimestamp() + " " + order.getType() + " Amount: " + order.getTradableAmount() + " Price: " + order.getPrice() + " " + order.getSymbol());
                    System.out.println("normal buy");
                } else {
                    System.out.println("problem with exchange account");
                }
            } else if (intervalAlgorithm.notEnoughBalanceCondition.sellToBuy(lastBidPrice)) {
                // Sell to buy
                if (order.getTradableAmount().compareTo(exchangeAccount.getAccountBase().getCurrentAssetBalance()) > 0) {
                    order.setTradableAmount(exchangeAccount.getAccountBase().getCurrentAssetBalance());
                }
                Order newOrder = new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.SELL, timestamp).tradableAmount(order.getTradableAmount()).price(lastBidPrice).priceType(Order.PriceType.MARKET).tradingType(Order.TradingType.SELL_TO_BUY).build();
                if (exchangeAccount.sell(newOrder)) {
                    process = true;
                    System.out.println("" + newOrder.getTimestamp() + " " + newOrder.getType() + " Amount: " + newOrder.getTradableAmount() + " Price: " + newOrder.getPrice() + " " + newOrder.getSymbol());
                    System.out.println("sell to buy");
                } else {
                    System.out.println("problem with exchange account");
                }
            }
        } else if (order.getType() == Order.Type.SELL) {
            // Sell
            if (order.getPrice() == null) {
                order.setPrice(lastBidPrice);
            }
            if (exchangeAccount.isEnoughAsset(order, intervalAlgorithm)) {
                if (intervalAlgorithm.earningCondition.isUsed()) {
                    order = exchangeAccount.getBuy().getSellOrder(order, intervalAlgorithm.earningCondition.getMinTransactionFactor());
                } else {
                    order = exchangeAccount.getBuy().getSellOrder(order, BigDecimal.valueOf(0.001));
                }
                if (order != null) {
                    order.setTradingType(Order.TradingType.NORMAL);
                    exchangeAccount.sell(order);
                    process = true;
                    System.out.println("" + order.getTimestamp() + " " + order.getType() + " Amount: " + order.getTradableAmount() + " Price: " + order.getPrice() + " " + order.getSymbol());
                    System.out.println("normal sell");
                } else {
                    process = true;
                    System.out.println("" + timestamp + " no action because no earning condition at price : " + lastBidPrice + " " + exchangeAccount.getAccountBase().getSymbol());
                }
            } else if (intervalAlgorithm.notEnoughBalanceCondition.buyToSell(lastAskPrice)) {
                // Buy to sell
                if (order.getTradableAmount().multiply(lastAskPrice).multiply(BigDecimal.ONE.add(FEE_TRANSACTION_FACTOR)).compareTo(exchangeAccount.getAccountBase().getCurrentBaseBalance()) > 0) {
                    BigDecimal assetToBeBought = exchangeAccount.getAccountBase().getCurrentBaseBalance().divide(lastAskPrice, 8, RoundingMode.DOWN).multiply(BigDecimal.ONE.subtract(ExchangeUtil.FEE_TRANSACTION_FACTOR));
                    order.setTradableAmount(assetToBeBought);
                }
                Order newOrder = new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.BUY, timestamp).tradableAmount(order.getTradableAmount()).price(lastAskPrice).priceType(Order.PriceType.MARKET).tradingType(Order.TradingType.BUY_TO_SELL).build();
                exchangeAccount.buy(newOrder, timestamp);
                process = true;
                System.out.println("" + newOrder.getTimestamp() + " " + newOrder.getType() + " Amount: " + newOrder.getTradableAmount() + " Price: " + newOrder.getPrice() + " " + newOrder.getSymbol());
                System.out.println("buy to sell");
            }
        }
        if (!process) {
            System.out.println("" + order.getTimestamp() + " no action at price: " + order.getPrice() + " " + order.getSymbol());
        }
    }

    private static Order checkPeriodsOrder(Map<Integer, Order> periodsWithResultOrder) {
        boolean createOrder = true;
        Order order = null;
        for (Integer key : periodsWithResultOrder.keySet()) {
            if (order == null) {
                order = periodsWithResultOrder.get(key);
                if (order == null) {
                    createOrder = false;
                    break;
                }
            } else {
                if (periodsWithResultOrder.get(key) == null) {
                    createOrder = false;
                    break;
                }
                if (periodsWithResultOrder.get(key).getTradableAmount().compareTo(order.getTradableAmount()) > 0) {
                    order = periodsWithResultOrder.get(key);
                }
            }
            if (periodsWithResultOrder.get(key) != null) {
                if (order.getType() != periodsWithResultOrder.get(key).getType()) {
                    createOrder = false;
                    break;
                }
            }
        }
        if (createOrder) {
            return order;
        } else {
            return null;
        }
    }

    private static Order.AlgorithmType checkPeriodsOrderAlgorithmType(Map<Integer, Order.AlgorithmType> periodsWithResultOrderIndicatorType) {
        Order.AlgorithmType algorithmType = null;
        for (Integer key : periodsWithResultOrderIndicatorType.keySet()) {
            algorithmType = periodsWithResultOrderIndicatorType.get(key);
            if (algorithmType == null) {
                return null;
            }
        }
        return algorithmType;
    }

    private static boolean tradingAlgoritmsEnoughPeriods(TradingAlgorithm[] tradingAlgorithms) {
        for (TradingAlgorithm tradingAlgorithm : tradingAlgorithms) {
            if (!tradingAlgorithm.isEnoughPeriods()) {
                return false;
            }
        }
        return true;
    }

    private static boolean inOutAlgoritmsEnoughPeriods(InOutAlgorithm[] inOutAlgorithms) {
        for (InOutAlgorithm inOutAlgorithm : inOutAlgorithms) {
            if (!inOutAlgorithm.isEnoughPeriods()) {
                return false;
            }
        }
        return true;
    }

}
