/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author CarlosDaniel
 */
public class Currencies {
    
    private final List<Currency> currencies = new ArrayList<>();
    
    public Currencies(JsonNode jsonNode) {
        JsonNode dataCurrencies = jsonNode.get("data").get("currencies");
        Iterator<String> dataCurrenciesFieldNames = dataCurrencies.fieldNames();
        while (dataCurrenciesFieldNames.hasNext()) {
            String dataCurrenciesFieldName = dataCurrenciesFieldNames.next();
            currencies.add(new Currency(dataCurrenciesFieldName, dataCurrencies.get(dataCurrenciesFieldName).get("name").textValue(), dataCurrencies.get(dataCurrenciesFieldName).get("altcoin").booleanValue()));
        }
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }
        
    public static class Currency {

        private final String shortName, name;
        private final boolean altcoin;

        public Currency(String shortName, String name, boolean altcoin) {
            this.shortName = shortName;
            this.name = name;
            this.altcoin = altcoin;
        }
        
        public String getShortName() {
            return shortName;
        }

        public String getName() {
            return name;
        }

        public boolean isAltcoin() {
            return altcoin;
        }

        @Override
        public String toString() {
            return "Currency{" + "shortName=" + shortName + ", name=" + name + ", altcoin=" + altcoin + '}';
        }
        
    }
            
}
