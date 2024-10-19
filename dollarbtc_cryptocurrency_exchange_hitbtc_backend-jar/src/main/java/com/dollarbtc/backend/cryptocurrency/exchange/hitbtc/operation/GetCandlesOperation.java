/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Candle;
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
public class GetCandlesOperation extends AbstractRestClient<Candle[]> {

    private static Client client;
    private static final String URL = "https://api.hitbtc.com/api/2/public/candles/";
    private final String symbol;
    private final String period;

    public GetCandlesOperation(String symbol, String period) {
        super(Candle[].class);
        this.symbol = symbol;
        this.period = period;
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
    
    public Candle[] getResponse() {
        return super.get(URL + ExchangeUtil.getSymbol("HitBTC", symbol) + "?period=" + period, RequestRestType.SYNC, null, null, 30);
    }    
    
}
