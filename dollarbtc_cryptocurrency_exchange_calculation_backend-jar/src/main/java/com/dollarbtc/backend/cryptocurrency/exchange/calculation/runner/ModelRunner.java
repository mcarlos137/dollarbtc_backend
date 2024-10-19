package com.dollarbtc.backend.cryptocurrency.exchange.calculation.runner;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.InOutAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.TradingAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeMarket;
import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.PrimaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.IntervalAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.ModelOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.FEE_TRANSACTION_FACTOR;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModelRunner {

    private ExchangeAccount exchangeAccount;
    private final List<IntervalAlgorithm> intervalAlgorithms;

    public ModelRunner(ExchangeAccount exchangeAccount, List<IntervalAlgorithm> intervalAlgorithms) {
        this.exchangeAccount = exchangeAccount;
        this.intervalAlgorithms = intervalAlgorithms;
    }

    public ExchangeAccount run(int[] periodsDurationInSeconds) {
        String timestamp = DateUtil.getCurrentDate();
        int highestPeriod = 0;
        for (int periodDurationInSeconds : periodsDurationInSeconds) {
            if (periodDurationInSeconds > highestPeriod) {
                highestPeriod = periodDurationInSeconds;
            }
        }
        int tradesQuantity = highestPeriod * 25;
        List<Trade> allTrades = ExchangeMarket.getTrades(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), null, null, tradesQuantity);
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
                System.out.println("allTrades.size(): " + allTrades.size());
                for (Trade trade : allTrades) {
                    ExchangeMarket.addTrade(trade, periodDurationInSeconds);
                    if (tradingAlgoritmsEnoughPeriods(intervalAlgorithm.tradings) && inOutAlgoritmsEnoughPeriods(intervalAlgorithm.ins) && inOutAlgoritmsEnoughPeriods(intervalAlgorithm.outs)) {
                        ArrayList<Period> previousPeriods = new ArrayList<>();
                        previousPeriods.addAll(ExchangeMarket.getPreviousPeriods());
                        periodsWithResultBidAskPricesPreviousPeriods.put(periodDurationInSeconds, previousPeriods);
                        break;
                    }
                }
            }
            if (periodsWithResultBidAskPricesPreviousPeriods.isEmpty()) {
                System.out.println("ExchangeMarket.getPreviousPeriods().size(): " + ExchangeMarket.getPreviousPeriods().size());
                System.out.println("not enough periods");
                continue;
            }
            boolean coinStarted = false;
            Map<Integer, Order.AlgorithmType> periodsWithResultOrderIndicatorType = new HashMap<>();
            // GET BID - ASK PRICE TICKER
            BigDecimal[] tickerPrices = PrimaryOperation.getTickerPrices(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol());
            if (tickerPrices == null) {
                continue;
            }
            BigDecimal lastBidPrice = tickerPrices[0];
            BigDecimal lastAskPrice = tickerPrices[1];
            exchangeAccount.getAccountBase().setLastAskPrice(lastAskPrice);
            exchangeAccount.getAccountBase().setLastBidPrice(lastBidPrice);
            // CHECK CLIENT INACTIVATION, MARKET INACTIVATION AND HOLDING PERIOD INACTIVATION
            Order.AlgorithmType outOrderAlgorithmType = null;
            boolean userModelActive = ModelOperation.isActive(exchangeAccount.getAccountBase().getModelName(), false);
            if (!userModelActive) {
                outOrderAlgorithmType = Order.AlgorithmType.CLIENT_INACTIVATION;
                ModelOperation.inactivate(exchangeAccount.getAccountBase().getModelName(), exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol());
            } else {
                boolean userModelSymbolActive = ModelOperation.isActive(exchangeAccount.getAccountBase().getModelName(), exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol());
                if(!userModelSymbolActive){
                    outOrderAlgorithmType = Order.AlgorithmType.MARKET_INACTIVATION;
                }
            }
            // CHECK IN CONDITIONS
            if (outOrderAlgorithmType == null) {
                if (exchangeAccount.getStartedTimestamp() == null || exchangeAccount.getStartedTimestamp().equals("")) {
                    for (Integer periodDurationInSeconds : periodsWithResultBidAskPricesPreviousPeriods.keySet()) {
                        periodsWithResultOrderIndicatorType.put(periodDurationInSeconds, getInAlgorithmType(intervalAlgorithm.ins, (ArrayList<Period>) periodsWithResultBidAskPricesPreviousPeriods.get(periodDurationInSeconds), timestamp));
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
            }
            // CHECK OUT CONDITIONS
            boolean out = false;
            if (outOrderAlgorithmType == null) {
                periodsWithResultOrderIndicatorType.clear();
                for (Integer periodDurationInSeconds : periodsWithResultBidAskPricesPreviousPeriods.keySet()) {
                    periodsWithResultOrderIndicatorType.put(periodDurationInSeconds, getOutAlgorithmType(intervalAlgorithm.outs, (ArrayList<Period>) periodsWithResultBidAskPricesPreviousPeriods.get(periodDurationInSeconds), timestamp));
                }
                outOrderAlgorithmType = checkPeriodsOrderAlgorithmType(periodsWithResultOrderIndicatorType);
            }
            boolean exchangeError = false;
            if (outOrderAlgorithmType != null) {
                BigDecimal assetBalance = exchangeAccount.getAccountBase().getCurrentAssetBalance();
                Order outOrder = new Order.Builder(exchangeAccount.getAccountBase().getExchangeId(), exchangeAccount.getAccountBase().getSymbol(), Order.Type.SELL, timestamp).tradableAmount(assetBalance).price(lastBidPrice).algorithmType(outOrderAlgorithmType).build();
                if (assetBalance.compareTo(BigDecimal.ZERO) == 1) {
                    if (exchangeAccount.sell(outOrder)) {
                        System.out.println("" + outOrder.getTimestamp() + " " + outOrder.getType() + " Amount: " + outOrder.getTradableAmount() + " Price: " + outOrder.getPrice() + " " + outOrder.getSymbol());
                        System.out.println("sell all");
                    } else {
                        System.out.println("problem with exchange account");
                        exchangeError = true;
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
            exchangeAccount.getAccountBase().setLastAskPrice(lastAskPrice);
            exchangeAccount.getAccountBase().setLastBidPrice(lastBidPrice);
            ExchangeUtil.createFile(exchangeAccount.toJsonNode(), new File[]{exchangeAccount.getAccountsFolder()}, "account");
            exchangeAccount.getAccountBase().setInfo("");
            System.out.println("Overall earnings: $"
                    + exchangeAccount.getOverallEarnings(lastBidPrice)
                    + "\n\tBase Balance: $"
                    + exchangeAccount.getBaseBalance(lastBidPrice)
                    + "\n\tAccount infos: "
                    + exchangeAccount
                    + "\n\tAlgorithm infos: "
                    + intervalAlgorithm);
            if (out && !exchangeError) {
                exchangeAccount.stopCoin(timestamp);
                if (outOrderAlgorithmType != null && outOrderAlgorithmType.equals(Order.AlgorithmType.CLIENT_INACTIVATION)) {
                    ModelOperation.returnAmountsToUser(exchangeAccount.getAccountBase().getModelName(), "client inactivation");
                }
                exchangeAccount = null;
            }
            if (coinStarted) {
                break;
            }
        }
        return exchangeAccount;
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
