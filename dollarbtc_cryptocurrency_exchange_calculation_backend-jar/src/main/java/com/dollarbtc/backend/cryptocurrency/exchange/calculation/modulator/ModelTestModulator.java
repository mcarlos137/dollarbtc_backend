/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.runner.ModelTestRunner;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.IntervalAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.OrderBlockingCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author CarlosDaniel
 */
public class ModelTestModulator extends BasicModelTestModulator {
    
    public ModelTestModulator(String threadName) {
        super(threadName);
    }

    @Override
    protected void runModelTestModulator(
            String exchangeId,
            String symbol,
            int[] periodsDurationInSeconds,
            List<List<Object>> algorithmParams,
            double minimalAssetOrderAmount,
            String finalTimestamp,
            long testPastTimeInHours,
            double lastTradePriceSpread,
            int scanTimeInSeconds
    ) {
        System.out.println("----------------------------------------------");
        System.out.println("starting " + exchangeId + " " + symbol + " " + modelName);
        System.out.println("----------------------------------------------");
        ExchangeAccount exchangeAccount = new ExchangeAccount(exchangeId, symbol, modelName);
        exchangeAccount.setBtcTicker(null);
        List<IntervalAlgorithm> intervalAlgorithms = new ArrayList<>();
        for (List<Object> algorithmParam : algorithmParams) {
            if (exchangeAccount.getIntervalAlgorithmName() != null && !exchangeAccount.getIntervalAlgorithmName().equals(algorithmParam.get(0))) {
                continue;
            }
            IntervalAlgorithm intervalAlgorithm = new IntervalAlgorithm(
                    (String) algorithmParam.get(0),
                    ModelModulator.getTradingAlgorithms(exchangeAccount, minimalAssetOrderAmount, (String) algorithmParam.get(1)),
                    ModelModulator.getInOutAlgorithms(exchangeAccount, (String) algorithmParam.get(2)),
                    ModelModulator.getInOutAlgorithms(exchangeAccount, (String) algorithmParam.get(3))
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
        ModelTestRunner algorithmTestRunner = new ModelTestRunner(
                exchangeAccount,
                intervalAlgorithms
        );
        algorithmTestRunner.run(periodsDurationInSeconds, finalTimestamp, testPastTimeInHours, lastTradePriceSpread, scanTimeInSeconds);
        System.out.println("----------------------------------------------");
        System.out.println("finishing " + exchangeId + " " + symbol + " " + modelName);
        System.out.println("----------------------------------------------");
    }

}
