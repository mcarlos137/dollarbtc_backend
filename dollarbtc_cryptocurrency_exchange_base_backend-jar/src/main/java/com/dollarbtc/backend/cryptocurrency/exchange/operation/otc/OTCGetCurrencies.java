/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetCurrencies extends AbstractOperation<Object> {

    private final Boolean onlyBasic, withoutUSDTAndETH;
    
    public OTCGetCurrencies(Boolean onlyBasic) {
        super(Object.class);
        this.onlyBasic = onlyBasic;
        this.withoutUSDTAndETH = true;
    }

    public OTCGetCurrencies(Boolean onlyBasic, Boolean withoutUSDTAndETH) {
        super(Object.class);
        this.onlyBasic = onlyBasic;
        this.withoutUSDTAndETH = withoutUSDTAndETH;
    }

    @Override
    protected void execute() {
        if (onlyBasic == null) {
            super.response = method();
        } else {
            super.response = method(onlyBasic);
        }
    }

    private ArrayNode method(Boolean onlyBasic) {
        ArrayNode currencies = mapper.createArrayNode();
        Set<String> currenciesAdded = new HashSet<>();
        if(!withoutUSDTAndETH){
            ObjectNode btcCurrency = mapper.createObjectNode();
            btcCurrency.put("shortName", "BTC");
            btcCurrency.put("fullName", "Bitcoin");
            btcCurrency.put("active", true);
            btcCurrency.put("countryPhoneCode", "997");
            currencies.add(btcCurrency);
        }
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operatorName = operatorsIt.textValue();
            File otcFolder = OTCFolderLocator.getFolder(operatorName);
            for (File otcCurrencyFolder : otcFolder.listFiles()) {
                if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                    continue;
                }
                if (withoutUSDTAndETH && otcCurrencyFolder.getName().equals("USDT")) {
                    continue;
                }
                if (withoutUSDTAndETH && otcCurrencyFolder.getName().equals("ETH")) {
                    continue;
                }
                File otcCurrencyConfigFile = new File(otcCurrencyFolder, "config.json");
                if (!otcCurrencyConfigFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode currency = mapper.readTree(otcCurrencyConfigFile);
                    String shortName = currency.get("shortName").textValue();
                    if (!currenciesAdded.contains(shortName)) {
                        currenciesAdded.add(shortName);
                        if (onlyBasic) {
                            if (currency.has("clientPaymentTypes")) {
                                ((ObjectNode) currency).remove("clientPaymentTypes");
                            }
                            if (currency.has("automaticChatMessages")) {
                                ((ObjectNode) currency).remove("automaticChatMessages");
                            }
                            if (currency.has("operationCheckList")) {
                                ((ObjectNode) currency).remove("operationCheckList");
                            }
                        }
                        currencies.add(currency);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(OTCGetCurrencies.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return currencies;
    }

    private List<String> method() {
        List<String> currencies = new ArrayList<>();
        for (File otcCurrencyFolder : OTCFolderLocator.getFolder(null).listFiles()) {
            if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                continue;
            }
            currencies.add(otcCurrencyFolder.getName());
        }
        return currencies;
    }

}
