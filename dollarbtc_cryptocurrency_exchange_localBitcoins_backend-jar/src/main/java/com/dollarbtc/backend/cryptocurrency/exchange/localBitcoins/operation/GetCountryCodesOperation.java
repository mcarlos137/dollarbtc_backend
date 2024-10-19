/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto.CountryCodes;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author CarlosDaniel
 */
public class GetCountryCodesOperation extends AbstractRestClient<CountryCodes> {

    private static Client client;
    private static final String ENDPOINT = "/api/countrycodes/";

    public GetCountryCodesOperation() {
        super(CountryCodes.class);
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

    public CountryCodes getResponse() {
        return new CountryCodes(super.getJsonNode(BasicLocalBitcoinsOperation.URL, ENDPOINT, "", RequestRestType.SYNC, null, null, 30));
    }

}
