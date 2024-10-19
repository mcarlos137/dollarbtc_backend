/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.coinbase;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CoinbaseFolderLocator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class CoinbaseUpdatePrices extends AbstractOperation<Void> {

    private final String[] symbolBases;

    public CoinbaseUpdatePrices(String[] symbolBases) {
        super(Void.class);
        this.symbolBases = symbolBases;
    }

    @Override
    protected void execute() {
        method();
    }

    private synchronized void method() {
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        JsonNode trm = mapper.createObjectNode();
        String timestamp = DateUtil.getMinuteStartDate(null);
        String symbolAsset = "BTC";
        ((ObjectNode) trm).put("timestamp", timestamp);
        for (String symbolBase : symbolBases) {
            Double price = new CoinbaseGetPrice(symbolBase).getResponse();
            if (price == 0.0) {
                continue;
            }
            ((ObjectNode) trm).put("price", price);
            File pricesFolder = CoinbaseFolderLocator.getPricesSymbolFolder(symbolAsset + symbolBase);
            File pricesOldFolder = CoinbaseFolderLocator.getPricesSymbolOldFolder(symbolAsset + symbolBase);
            for (File priceFile : pricesFolder.listFiles()) {
                if (!priceFile.isFile()) {
                    continue;
                }
                FileUtil.moveFileToFolder(priceFile, pricesOldFolder);
            }
            FileUtil.createFile(trm, new File(pricesFolder, DateUtil.getFileDate(timestamp) + ".json"));
        }
    }

}

class CoinbaseGetPrice extends AbstractRestClient<Double> {

    private static Client client;
    private final String currency;

    public CoinbaseGetPrice(String currency) {
        super(Double.class);
        this.currency = currency;
    }

    @Override
    public Client getClient() {
        if (client == null) {
            client = ClientBuilder.newClient();
        }
        return client;
    }

    @Override
    public String getMediaType() {
        return MediaType.APPLICATION_JSON;
    }

    public Double getResponse() {
        String endpoint = "/prices/spot?currency=";
        JsonNode priceResponse = super.getJsonNode("https://api.coinbase.com/v2", endpoint, currency, RequestRestType.SYNC, null, null, 30);
        if (!priceResponse.has("data")) {
            return 0.0;
        }
        return Double.parseDouble(priceResponse.get("data").get("amount").textValue());
    }

}
