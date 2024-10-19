/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.runner.MarketModulatorRunner;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.MarketModulatorAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.InactivateMarketModulatorAlgorithm;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author CarlosDaniel
 */
public class MarketModulatorModulator extends BasicMarketModulatorModulator {

    public MarketModulatorModulator(String threadName) {
        super(threadName);
    }

    @Override
    protected void runMarketModulatorModulator(
            String exchangeId,
            String symbol,
            String period,
            List<List<Object>> algorithmParams
    ) {
        System.out.println("----------------------------------------------");
        System.out.println("" + exchangeId + " " + symbol);
        List<MarketModulatorAlgorithm> marketModulatorAlgorithms = new ArrayList<>();
        for (List<Object> algorithmParam : algorithmParams) {
            MarketModulatorAlgorithm marketModulatorAlgorithm = new MarketModulatorAlgorithm(
                    (String) algorithmParam.get(0),
                    getInactivateMarketModulatorAlgorithms((String) algorithmParam.get(1), exchangeId),
                    exchangeId,
                    symbol,
                    period
            );
            marketModulatorAlgorithms.add(marketModulatorAlgorithm);
        }
        MarketModulatorRunner marketModulatorRunner = new MarketModulatorRunner(
                exchangeId,
                symbol,
                marketModulatorAlgorithms
        );
        marketModulatorRunner.run();
        System.out.println("----------------------------------------------");
    }

    public static InactivateMarketModulatorAlgorithm[] getInactivateMarketModulatorAlgorithms(String inactivateMarketModulatorAlgorithmParams, String exchangeId) {
        String[] inactivateMarketModulatorAlgoParams = inactivateMarketModulatorAlgorithmParams.split("____");
        InactivateMarketModulatorAlgorithm[] inactivateMarketModulatorAlgorithms = new InactivateMarketModulatorAlgorithm[inactivateMarketModulatorAlgoParams.length];
        int i = 0;
        for (String inactivateMarketModulatorAlgoParam : inactivateMarketModulatorAlgoParams) {
            switch (inactivateMarketModulatorAlgoParam.split("__")[0]) {
                case "ROC":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.ROCAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3])
                    );
                    break;
                case "RSI":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.RSIAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3])
                    );
                    break;
                case "EMA":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.EMAAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3].split("_")[0]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3].split("_")[1])
                    );
                    break;
                case "SMA":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.SMAAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3].split("_")[0]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3].split("_")[1])
                    );
                    break;
                case "SPREAD":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.SpreadAlgorithm(
                            exchangeId,
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2])
                    );
                    break;
                case "BAND_PRICE":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.BandPriceAlgorithm(
                            exchangeId,
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2])
                    );
                    break;
                case "BOLLINGER_BANDS":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.BollingerBandsAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.BollingerBandsAlgorithm.Type.valueOf(inactivateMarketModulatorAlgoParam.split("__")[2]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3])
                    );
                    break;
                case "ADL":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.ADLAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[2])
                    );
                    break;
                case "PPO":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.PPOAlgorithm(
                            inactivateMarketModulatorAlgoParam.split("__")[1],
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[0]),
                            Double.parseDouble(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[1]),
                            Boolean.parseBoolean(inactivateMarketModulatorAlgoParam.split("__")[2].split("_")[2]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3].split("_")[0]),
                            Integer.parseInt(inactivateMarketModulatorAlgoParam.split("__")[3].split("_")[1])
                    );
                    break;
                case "NONE":
                    inactivateMarketModulatorAlgorithms[i] = new com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.NoneAlgorithm();
                    break;
            }
            i++;
        }
        return inactivateMarketModulatorAlgorithms;
    }

}
