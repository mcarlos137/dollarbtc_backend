/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccountnew.MasterAccountNewGetProfitsAndChargesBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPaymentBalances;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountNewGetOTCMasterAccountProfitsAndChargesBalance extends AbstractOperation<JsonNode> {
    
    private final MasterAccountNewGetProfitsAndChargesBalanceRequest masterAccountNewGetProfitsAndChargesBalanceRequest;

    public MasterAccountNewGetOTCMasterAccountProfitsAndChargesBalance(MasterAccountNewGetProfitsAndChargesBalanceRequest masterAccountNewGetProfitsAndChargesBalanceRequest) {
        super(JsonNode.class);
        this.masterAccountNewGetProfitsAndChargesBalanceRequest = masterAccountNewGetProfitsAndChargesBalanceRequest;
    }
        
    @Override
    public void execute() {
        JsonNode profitsAndChargesBalances = mapper.createObjectNode();
        File chargesFolder = new File(new File(ExchangeUtil.OPERATOR_PATH, "Charges"), "MASTER_ACCOUNT__" + masterAccountNewGetProfitsAndChargesBalanceRequest.getMasterAccountName());
        if (chargesFolder.isDirectory()) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(chargesFolder.getPath()))) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> Files.isRegularFile(path))
                        .sorted((o1, o2) -> {
                            String id1 = o1.toFile().getName().replace(".json", "");
                            String id2 = o2.toFile().getName().replace(".json", "");
                            return id1.compareTo(id2);
                        })
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    JsonNode charge = mapper.readTree(it.toFile());
                    if (charge == null) {
                        continue;
                    }
                    if (!charge.has("timestamp") || charge.get("timestamp") == null) {
                        continue;
                    }
                    if (!charge.has("status") || charge.get("status").textValue().equals("FAIL")) {
                        continue;
                    }
                    String timestamp = charge.get("timestamp").textValue();
                    if (timestamp == null || timestamp.equals("")) {
                        continue;
                    }
                    if (masterAccountNewGetProfitsAndChargesBalanceRequest.getInitTimestamp() != null && !masterAccountNewGetProfitsAndChargesBalanceRequest.getInitTimestamp().equals("")) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(masterAccountNewGetProfitsAndChargesBalanceRequest.getInitTimestamp())) < 0) {
                            break;
                        }
                    }
                    if (masterAccountNewGetProfitsAndChargesBalanceRequest.getFinalTimestamp() != null && !masterAccountNewGetProfitsAndChargesBalanceRequest.getFinalTimestamp().equals("")) {
                        if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(masterAccountNewGetProfitsAndChargesBalanceRequest.getFinalTimestamp())) > 0) {
                            continue;
                        }
                    }
                    String type = charge.get("type").textValue();
                    String currency = charge.get("currency").textValue();
                    Double amount = charge.get("amount").doubleValue();
                    if (!profitsAndChargesBalances.has(currency)) {
                        ((ObjectNode) profitsAndChargesBalances).set(currency, mapper.createObjectNode());
                    }
                    if (!profitsAndChargesBalances.get(currency).has("operationBalance")) {
                        ((ObjectNode) profitsAndChargesBalances.get(currency)).put("operationBalance", 0.0);
                    }
                    if (!profitsAndChargesBalances.get(currency).has("charges")) {
                        ((ObjectNode) profitsAndChargesBalances.get(currency)).set("charges", mapper.createObjectNode());
                    }
                    if (!profitsAndChargesBalances.get(currency).get("charges").has(type)) {
                        ((ObjectNode) profitsAndChargesBalances.get(currency).get("charges")).put(type, 0.0);
                    }
                    ((ObjectNode) profitsAndChargesBalances.get(currency).get("charges")).put(type, profitsAndChargesBalances.get(currency).get("charges").get(type).doubleValue() + amount);
                }
            } catch (IOException ex) {
            }
        }
        Double btcBalance = 0.0;
        Iterator<JsonNode> btcBalanceIterator = BaseOperation.getBalance(MasterAccountFolderLocator.getBalanceFolder(masterAccountNewGetProfitsAndChargesBalanceRequest.getMasterAccountName()), masterAccountNewGetProfitsAndChargesBalanceRequest.getInitTimestamp(), masterAccountNewGetProfitsAndChargesBalanceRequest.getFinalTimestamp()).iterator();
        while (btcBalanceIterator.hasNext()) {
            JsonNode btcBalanceIt = btcBalanceIterator.next();
            if (!btcBalanceIt.get("currency").textValue().equals("BTC")) {
                continue;
            }
            btcBalance = btcBalance + btcBalanceIt.get("amount").doubleValue();
        }
        if (!profitsAndChargesBalances.has("BTC")) {
            ((ObjectNode) profitsAndChargesBalances).set("BTC", mapper.createObjectNode());
        }
        ((ObjectNode) profitsAndChargesBalances.get("BTC")).put("operationBalance", btcBalance);
        Set<String> currencies = getOTCMasterAccountCurrencies(masterAccountNewGetProfitsAndChargesBalanceRequest.getMasterAccountName());
        for (String currency : currencies) {
            Double currencyBalance = 0.0;
            Iterator<JsonNode> dollarBTCPaymentBalancesIterator = new OTCGetDollarBTCPaymentBalances(currency, masterAccountNewGetProfitsAndChargesBalanceRequest.getInitTimestamp(), masterAccountNewGetProfitsAndChargesBalanceRequest.getFinalTimestamp()).getResponse().iterator();
            while (dollarBTCPaymentBalancesIterator.hasNext()) {
                JsonNode dollarBTCPaymentBalancesIt = dollarBTCPaymentBalancesIterator.next();
                Iterator<JsonNode> balanceIterator = dollarBTCPaymentBalancesIt.get("balance").iterator();
                while (balanceIterator.hasNext()) {
                    JsonNode balanceIt = balanceIterator.next();
                    if (!balanceIt.get("currency").textValue().equals(currency)) {
                        continue;
                    }
                    currencyBalance = currencyBalance + balanceIt.get("amount").doubleValue();
                }
            }
            if (!profitsAndChargesBalances.has(currency)) {
                ((ObjectNode) profitsAndChargesBalances).set(currency, mapper.createObjectNode());
            }
            ((ObjectNode) profitsAndChargesBalances.get(currency)).put("operationBalance", currencyBalance);
        }
        Logger.getLogger(MasterAccountNewGetOTCMasterAccountProfitsAndChargesBalance.class.getName()).log(Level.INFO, "{0}", profitsAndChargesBalances);
        super.response = profitsAndChargesBalances;
    }
    
    private static Set<String> getOTCMasterAccountCurrencies(String otcMasterAccount) {
        Set<String> otcMasterAccountCurrencies = new HashSet<>();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            File masterAccountConfigFile = MasterAccountFolderLocator.getConfigFile(operator);
            if (masterAccountConfigFile.isFile()) {
                try {
                    Iterator<JsonNode> masterAccountConfigIterator = new ObjectMapper().readTree(masterAccountConfigFile).iterator();
                    while (masterAccountConfigIterator.hasNext()) {
                        JsonNode masterAccountConfigIt = masterAccountConfigIterator.next();
                        if (masterAccountConfigIt.get("name").textValue().equals(otcMasterAccount)) {
                            Iterator<JsonNode> masterAccountConfigCurrenciesIterator = masterAccountConfigIt.get("currencies").iterator();
                            while (masterAccountConfigCurrenciesIterator.hasNext()) {
                                JsonNode masterAccountConfigCurrenciesIt = masterAccountConfigCurrenciesIterator.next();
                                otcMasterAccountCurrencies.add(masterAccountConfigCurrenciesIt.textValue());
                            }
                            break;

                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MasterAccountNewGetOTCMasterAccountProfitsAndChargesBalance.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return otcMasterAccountCurrencies;
    }
    
}
