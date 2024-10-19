/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto.Currencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author CarlosDaniel
 */
public class GetCurrenciesOperation extends AbstractRestClient<Currencies> {

    private static Client client;
    private static final String ENDPOINT = "/api/currencies/";

    public GetCurrenciesOperation() {
        super(Currencies.class);
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

    public Currencies getResponse() {
        return new Currencies(super.getJsonNode(BasicLocalBitcoinsOperation.URL, ENDPOINT, "", RequestRestType.SYNC, null, null, 30));
    }

}
