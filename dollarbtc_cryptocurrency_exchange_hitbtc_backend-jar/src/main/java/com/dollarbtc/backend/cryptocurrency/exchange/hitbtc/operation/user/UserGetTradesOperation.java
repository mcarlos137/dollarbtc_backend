/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.MarketTrade;
import static com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasicUser.getUserCredentialJsonFile;
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
public class UserGetTradesOperation extends AbstractRestClient<MarketTrade[]> {

    private static Client client;
    private static final String URL = "https://api.hitbtc.com/api/2/history/trades";
    private final String loginAccount, symbol;
    private final int limit;

    public UserGetTradesOperation(String loginAccount, String symbol, int limit) {
        super(MarketTrade[].class);
        this.loginAccount = loginAccount;
        this.symbol = ExchangeUtil.getSymbol("HitBTC", symbol);
        this.limit = limit;
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
    
    public MarketTrade[] getResponse() {
        String[] userCredential = getUserCredentialJsonFile(loginAccount);
        return super.get(URL + "?" + "symbol=" + symbol + "&limit=" + limit, RequestRestType.SYNC, SecurityType.BASIC, userCredential, 30);
    }
    
}
