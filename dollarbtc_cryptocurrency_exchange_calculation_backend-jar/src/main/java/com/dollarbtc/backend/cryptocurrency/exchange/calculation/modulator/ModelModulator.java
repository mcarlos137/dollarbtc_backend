/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.runner.ModelRunner;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.IntervalAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.InOutAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.TradingAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.OrderBlockingCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author CarlosDaniel
 */
public class ModelModulator extends BasicModelModulator {

    public ModelModulator(String threadName) {
        super(threadName);
    }

    @Override
    protected void runModelModulator(
            String exchangeId,
            String symbol,
            String modelName,
            int[] periodsDurationInSeconds,
            List<List<Object>> algorithmParams,
            double minimalAssetOrderAmount,
            String initialTimestamp,
            boolean blockMarketStatus
    ) {
        System.out.println("----------------------------------------------");
        System.out.println("" + exchangeId + " " + symbol + " " + modelName);
        ExchangeAccount exchangeAccount = exchangeAccountsByExchangeIdModelNameSymbol.get(exchangeId + "__" + symbol + "__" + modelName);
        if (exchangeAccount == null) {
            exchangeAccount = new ExchangeAccount(exchangeId, symbol, modelName);
            if (exchangeAccount.getStartedTimestamp() == null && blockMarketStatus) {
                changeToProtectionCurrency(modelName);
                return;
            }
        }
        exchangeAccount.setBtcTicker(null);
        exchangeAccount.setModelActivationTimestamp(initialTimestamp);
        List<IntervalAlgorithm> intervalAlgorithms = new ArrayList<>();
        for (List<Object> algorithmParam : algorithmParams) {
            if (exchangeAccount.getIntervalAlgorithmName() != null && !exchangeAccount.getIntervalAlgorithmName().equals(algorithmParam.get(0))) {
                continue;
            }
            IntervalAlgorithm intervalAlgorithm = new IntervalAlgorithm(
                    (String) algorithmParam.get(0),
                    getTradingAlgorithms(exchangeAccount, minimalAssetOrderAmount, (String) algorithmParam.get(1)),
                    getInOutAlgorithms(exchangeAccount, (String) algorithmParam.get(2)),
                    getInOutAlgorithms(exchangeAccount, (String) algorithmParam.get(3))
            );
            intervalAlgorithm.earningCondition.setUsed((boolean) algorithmParam.get(4));
            intervalAlgorithm.earningCondition.setMinTransactionFactor(BigDecimal.valueOf((double) algorithmParam.get(5)));
            intervalAlgorithm.notEnoughBalanceCondition.setUsed((boolean) algorithmParam.get(6));
            intervalAlgorithm.notEnoughBalanceCondition.setInARowLimitToOrderQuantity((int) algorithmParam.get(7));
            intervalAlgorithm.orderBlockingCondition.setUsed((boolean) algorithmParam.get(8));
            intervalAlgorithm.orderBlockingCondition.setMaxLossMaxQuantity((int) algorithmParam.get(9));
            intervalAlgorithm.orderBlockingCondition.setMaxQuantity((int) algorithmParam.get(10));
            intervalAlgorithm.inPriceBandCondition.setUsed((boolean) algorithmParam.get(12));
            intervalAlgorithm.inPriceBandCondition.setUpPercent((double) algorithmParam.get(13));
            intervalAlgorithm.inPriceBandCondition.setDownPercent((double) algorithmParam.get(14));
            intervalAlgorithms.add(intervalAlgorithm);
            exchangeAccount.getOrderBlockingConditionActionByIntervalAlgorithmName().put((String) algorithmParam.get(0), OrderBlockingCondition.Action.valueOf((String) algorithmParam.get(11)));
        }
        ModelRunner algorithmRunner = new ModelRunner(
                exchangeAccount,
                intervalAlgorithms
        );
        exchangeAccount = algorithmRunner.run(periodsDurationInSeconds);
        exchangeAccountsByExchangeIdModelNameSymbol.put(exchangeId + "__" + symbol + "__" + modelName, exchangeAccount);
    }

    public static InOutAlgorithm[] getInOutAlgorithms(ExchangeAccount exchangeAccount, String inOutalgorithmParams) {
        String[] inOutalgoParams = inOutalgorithmParams.split("____");
        InOutAlgorithm[] inOutAlgorithms = new InOutAlgorithm[inOutalgoParams.length];
        int i = 0;
        for (String inOutalgoParam : inOutalgoParams) {
            switch (inOutalgoParam.split("__")[0]) {
                case "ROC":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.ROCAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[0]),
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[1]),
                            Boolean.parseBoolean(inOutalgoParam.split("__")[1].split("_")[2]),
                            Integer.parseInt(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "RSI":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.RSIAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[0]),
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[1]),
                            Boolean.parseBoolean(inOutalgoParam.split("__")[1].split("_")[2]),
                            Integer.parseInt(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "EMA":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.EMAAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[0]),
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[1]),
                            Boolean.parseBoolean(inOutalgoParam.split("__")[1].split("_")[2]),
                            Integer.parseInt(inOutalgoParam.split("__")[2].split("_")[0]),
                            Integer.parseInt(inOutalgoParam.split("__")[2].split("_")[1])
                    );
                    break;
                case "SMA":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.SMAAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[0]),
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[1]),
                            Boolean.parseBoolean(inOutalgoParam.split("__")[1].split("_")[2]),
                            Integer.parseInt(inOutalgoParam.split("__")[2].split("_")[0]),
                            Integer.parseInt(inOutalgoParam.split("__")[2].split("_")[1])
                    );
                    break;
                case "TIMEOUT":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.TimeoutAlgorithm(
                            exchangeAccount,
                            Integer.parseInt(inOutalgoParam.split("__")[1]),
                            Integer.parseInt(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "SPREAD":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.SpreadAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[0]),
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[1]),
                            Boolean.parseBoolean(inOutalgoParam.split("__")[1].split("_")[2])
                    );
                    break;
                case "STOPLOSS":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.StopLossAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1]),
                            Integer.parseInt(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "MAXLOSS":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.MaxLossAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1]),
                            Integer.parseInt(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "RESERVE":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.ReserveAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1]),
                            Double.parseDouble(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "BOLLINGER_BANDS":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.BollingerBandsAlgorithm(
                            exchangeAccount,
                            com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.BollingerBandsAlgorithm.Type.valueOf(inOutalgoParam.split("__")[1]),
                            Integer.parseInt(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "ADL":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.ADLAlgorithm(
                            exchangeAccount,
                            Integer.parseInt(inOutalgoParam.split("__")[1])
                    );
                    break;
                case "MAX_BUY_QUANTITY":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.MaxBuyQuantityAlgorithm(
                            exchangeAccount,
                            Integer.parseInt(inOutalgoParam.split("__")[1])
                    );
                    break;
                case "NO_BUY_TIMEOUT":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.NoBuyTimeoutAlgorithm(
                            exchangeAccount,
                            Integer.parseInt(inOutalgoParam.split("__")[1])
                    );
                    break;
                case "PPO":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.PPOAlgorithm(
                            exchangeAccount,
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[0]),
                            Double.parseDouble(inOutalgoParam.split("__")[1].split("_")[1]),
                            Boolean.parseBoolean(inOutalgoParam.split("__")[1].split("_")[2]),
                            Integer.parseInt(inOutalgoParam.split("__")[2].split("_")[0]),
                            Integer.parseInt(inOutalgoParam.split("__")[2].split("_")[1])
                    );
                    break;
                case "LAST_SELL_PRICE_VARIATION":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.LastSellPriceVariationAlgorithm(
                            exchangeAccount,
                            0.0,
                            Double.parseDouble(inOutalgoParam.split("__")[1]),
                            com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.LastSellPriceVariationAlgorithm.Type.valueOf(inOutalgoParam.split("__")[2])
                    );
                    break;
                case "NONE":
                    inOutAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.NoneAlgorithm();
                    break;
            }
            i++;
        }
        return inOutAlgorithms;
    }

    public static TradingAlgorithm[] getTradingAlgorithms(ExchangeAccount exchangeAccount, double baseOrderAmount, String algorithmParams) {
        String[] algoParams = algorithmParams.split("____");
        TradingAlgorithm[] tradingAlgorithms = new TradingAlgorithm[algoParams.length];
        int i = 0;
        for (String algoParam : algoParams) {
            switch (algoParam.split("__")[0]) {
                case "ROC":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.ROCAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[0]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[1]),
                            Boolean.parseBoolean(algoParam.split("__")[3].split("_")[2]),
                            Integer.parseInt(algoParam.split("__")[4])
                    );
                    break;
                case "PPO":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.PPOAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[0]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[1]),
                            Boolean.parseBoolean(algoParam.split("__")[3].split("_")[2]),
                            Integer.parseInt(algoParam.split("__")[4].split("_")[0]),
                            Integer.parseInt(algoParam.split("__")[4].split("_")[1])
                    );
                    break;
                case "EMA":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.EMAAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[0]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[1]),
                            Boolean.parseBoolean(algoParam.split("__")[3].split("_")[2]),
                            Integer.parseInt(algoParam.split("__")[4].split("_")[0]),
                            Integer.parseInt(algoParam.split("__")[4].split("_")[1])
                    );
                    break;
                case "SMA":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.SMAAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[0]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[1]),
                            Boolean.parseBoolean(algoParam.split("__")[3].split("_")[2]),
                            Integer.parseInt(algoParam.split("__")[4].split("_")[0]),
                            Integer.parseInt(algoParam.split("__")[4].split("_")[1])
                    );
                    break;
                case "RSI":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.RSIAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[0]),
                            Double.parseDouble(algoParam.split("__")[3].split("_")[1]),
                            Boolean.parseBoolean(algoParam.split("__")[3].split("_")[2]),
                            Integer.parseInt(algoParam.split("__")[4])
                    );
                    break;
                case "AROON":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.AroonAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            Integer.parseInt(algoParam.split("__")[3])
                    );
                    break;
                case "OPPOSITE_ORDER":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.OppositeOrderAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1])
                    );
                    break;
                case "BOLLINGER_BANDS":
                    tradingAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.BollingerBandsAlgorithm(
                            exchangeAccount,
                            Order.Type.valueOf(algoParam.split("__")[1]),
                            baseOrderAmount,
                            Double.parseDouble(algoParam.split("__")[2].split("_")[0]),
                            Boolean.parseBoolean(algoParam.split("__")[2].split("_")[1]),
                            com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.BollingerBandsAlgorithm.Type.valueOf(algoParam.split("__")[3]),
                            Integer.parseInt(algoParam.split("__")[4])
                    );
                    break;
            }
            i++;
        }
        return tradingAlgorithms;
    }

}
