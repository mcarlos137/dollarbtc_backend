/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Ticker;
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
public class GetTickerOperation extends AbstractRestClient<Ticker> {

    private static Client client;
    private static final String URL = "https://api.hitbtc.com/api/2/public/ticker/";
    private final String symbol;

    public GetTickerOperation(String symbol) {
        super(Ticker.class);
        this.symbol = symbol;
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
    
    public Ticker getResponse() {
        return super.get(URL + ExchangeUtil.getSymbol("HitBTC", symbol), RequestRestType.SYNC, null, null, 30);
    }
    
}
