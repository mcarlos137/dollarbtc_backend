package com.dollarbtc.backend.cryptocurrency.exchange.calculation.runner;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.MarketModulatorAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inactivateMarketModulator.InactivateMarketModulatorAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator.BasicModelModulator;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarketModulatorRunner {

    private final String exchangeId;
    private final String symbol;
    private final List<MarketModulatorAlgorithm> marketModulatorAlgorithms;

    public MarketModulatorRunner(
            String exchangeId,
            String symbol,
            List<MarketModulatorAlgorithm> marketModulatorAlgorithms
    ) {
        this.exchangeId = exchangeId;
        this.symbol = symbol;
        this.marketModulatorAlgorithms = marketModulatorAlgorithms;
    }

    public void run() {
        boolean activateMarket = true;
        for (MarketModulatorAlgorithm marketModulatorAlgorithm : marketModulatorAlgorithms) {
            if (inactivateMarketModulator(marketModulatorAlgorithm.inactivates, marketModulatorAlgorithm.periods)) {
                updateMarketActiveSymbolsFile(exchangeId, symbol, "INACTIVATE");
                activateMarket = false;
                break;
            }
        }
        if(activateMarket){
            updateMarketActiveSymbolsFile(exchangeId, symbol, "ACTIVATE");
        }
    }

    private static boolean inactivateMarketModulator(InactivateMarketModulatorAlgorithm[] inactivateMarketModulatorAlgoriths, ArrayList<Period> periods) {
        if (inactivateMarketModulatorAlgoriths != null && inactivateMarketModulatorAlgoriths.length > 0) {
            for (InactivateMarketModulatorAlgorithm inactivateMarketModulatorAlgorithm : inactivateMarketModulatorAlgoriths) {
                if (inactivateMarketModulatorAlgorithm == null) {
                    return false;
                }
                return inactivateMarketModulatorAlgorithm.inactivate(periods);
            }
        }
        return false;
    }

    public static void updateMarketActiveSymbolsFile(String exchangeId, String symbol, String operation) {
        File marketActiveSymbolsFile = new File(new File(OPERATOR_PATH, "MarketModulator"), "activeSymbols.json");
        if (!marketActiveSymbolsFile.isFile()) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode newMarketActiveSymbols = mapper.createObjectNode();
            ArrayNode newMarketActiveSymbolsArray = mapper.createArrayNode();
            JsonNode marketActiveSymbols = mapper.readTree(marketActiveSymbolsFile);
            Iterator<JsonNode> marketActiveSymbolsIterator = marketActiveSymbols.get("activeSymbols").elements();
            boolean marketActiveSymbolAlreadyAdded = false;
            while (marketActiveSymbolsIterator.hasNext()) {
                JsonNode marketActiveSymbolsIt = marketActiveSymbolsIterator.next();
                String marketActiveSymbolsItExchangeId = marketActiveSymbolsIt.get("exchangeId").textValue();
                String marketActiveSymbolsItSymbol = marketActiveSymbolsIt.get("symbol").textValue();
                switch (operation) {
                    case "ACTIVATE":
                        newMarketActiveSymbolsArray.add(marketActiveSymbolsIt);
                        if (marketActiveSymbolsItExchangeId.equals(exchangeId) && marketActiveSymbolsItSymbol.equals(symbol)) {
                            marketActiveSymbolAlreadyAdded = true;
                        }
                        continue;
                    case "INACTIVATE":
                        if (marketActiveSymbolsItExchangeId.equals(exchangeId) && marketActiveSymbolsItSymbol.equals(symbol)) {
                            continue;
                        }
                        newMarketActiveSymbolsArray.add(marketActiveSymbolsIt);
                }

            }
            if (!marketActiveSymbolAlreadyAdded) {
                JsonNode marketActiveSymbolToAdd = mapper.createObjectNode();
                ((ObjectNode) marketActiveSymbolToAdd).put("exchangeId", exchangeId);
                ((ObjectNode) marketActiveSymbolToAdd).put("symbol", symbol);
                newMarketActiveSymbolsArray.add(marketActiveSymbolToAdd);
            }
            ((ObjectNode) newMarketActiveSymbols).putArray("activeSymbols").addAll(newMarketActiveSymbolsArray);
            FileUtil.editFile(newMarketActiveSymbols, marketActiveSymbolsFile);
        } catch (IOException ex) {
            Logger.getLogger(BasicModelModulator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
