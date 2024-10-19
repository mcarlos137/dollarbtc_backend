/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.binance.dto.Candles;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author conamerica02
 */
public class GetCandlesOperation extends AbstractRestClient<Candles> {

    private static Client client;
    private static final String ENDPOINT = "/api/v1/klines";
    private final String symbol;
    private final String period;

    public GetCandlesOperation(String symbol, String period) {
        super(Candles.class);
        this.symbol = symbol;
        this.period = getPeriod(period);
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

    public Candles getResponse() {
        return new Candles(super.getJsonNode(BasicBinanceOperation.URL, ENDPOINT, "?symbol=" + ExchangeUtil.getSymbol("Binance", symbol) + "&interval=" + period + "&limit=3", RequestRestType.SYNC, null, null, 30));
    }

    private static String getPeriod(String period) {
        switch (period) {
            case "M5":
                return "5m";
            case "M15":
                return "15m";
            case "M30":
                return "30m";
            case "H1":
                return "1h";
            case "H4":
                return "4h";
            case "D1":
                return "1d";
            case "W1":
                return "7d";
            case "1M":
                return "1M";
        }
        return "1M";
    }

}
