package com.dollarbtc.backend.cryptocurrency.exchange.calculation.data;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Trade;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExchangeMarket {

    private final static HashMap<String, List<Trade>> TRADES_BY_EXCHANGEID_SYMBOL = new HashMap<>();
    private final static ArrayList<Period> PREVIOUS_PERIODS = new ArrayList<>();

    public static void addTrade(Trade trade, int periodDurationInSeconds) {
        if (PREVIOUS_PERIODS.isEmpty()) {
            // First trade
            PREVIOUS_PERIODS.add(new Period(trade, periodDurationInSeconds));
        } else {
            // Subsequent trades
            Period lastPeriod = PREVIOUS_PERIODS.get(PREVIOUS_PERIODS.size() - 1);
            if (!lastPeriod.inPeriod(trade.getTimestamp())) {
                PREVIOUS_PERIODS.add(new Period(lastPeriod.getStartTimestamp(), periodDurationInSeconds));
                lastPeriod = PREVIOUS_PERIODS.get(PREVIOUS_PERIODS.size() - 1);
            }
            lastPeriod.addTrade(trade);
        }
    }

    public static void resetPreviousPeriods() {
        PREVIOUS_PERIODS.clear();
    }

    public static boolean isEnoughPeriods(int nbPeriods) {
        return (PREVIOUS_PERIODS.size() >= nbPeriods);
    }

    public static boolean isEnoughTrades(int nbTrades) {
        return !PREVIOUS_PERIODS.isEmpty() && (PREVIOUS_PERIODS.get(0).getTrades().size() >= nbTrades);
    }

    public static ArrayList<Period> getPreviousPeriods() {
        return PREVIOUS_PERIODS;
    }

    public static ArrayList<Trade> getPreviousTrades() {
        return PREVIOUS_PERIODS.get(0).getTrades();
    }

    public static List<Trade> getTrades(String exchangeId, String symbol, String initDate, String endDate, int biggerSize) {
        boolean firstAdd = false;
        if (!TRADES_BY_EXCHANGEID_SYMBOL.containsKey(exchangeId + "__" + symbol) || TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol).size() < biggerSize) {
            firstAdd = true;
            TRADES_BY_EXCHANGEID_SYMBOL.put(exchangeId + "__" + symbol, LocalData.getTrades(exchangeId, symbol, initDate, endDate, biggerSize, "NORMAL", true));
        }
        if (!firstAdd) {
            String lastTimestamp = TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol).get(0).getTimestamp();
            int i = 0;
            List<Trade> newTrades = LocalData.getTrades(exchangeId, symbol, initDate, endDate, 10, "NORMAL", true);
            for (Trade trade : newTrades) {
                if (trade.getTimestamp().equals(lastTimestamp)) {
                    break;
                }
                TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol).add(i, trade);
                i++;
            }
            int actualSize = TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol).size();
            if (actualSize > biggerSize) {
                int j = 0;
                while (j < actualSize - biggerSize) {
                    TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol).remove(TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol).size() - 1);
                    j++;
                }
            }
        }
        return TRADES_BY_EXCHANGEID_SYMBOL.get(exchangeId + "__" + symbol);
    }

}
