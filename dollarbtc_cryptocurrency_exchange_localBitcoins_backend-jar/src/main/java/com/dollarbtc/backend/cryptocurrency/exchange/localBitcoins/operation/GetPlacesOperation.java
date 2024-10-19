/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto.Places;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author CarlosDaniel
 */
public class GetPlacesOperation extends AbstractRestClient<Places> {

    private static Client client;
    private static final String ENDPOINT = "/api/places/";
    private final Integer lat, lon;
    private final String countrycode, location_string;

    public GetPlacesOperation(Integer lat, Integer lon, String countrycode, String location_string) {
        super(Places.class);
        this.lat = lat;
        this.lon = lon;
        this.countrycode = countrycode;
        this.location_string = location_string;
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

    public Places getResponse() {
        String params = "?lat=" + lat + "&lon=" + lon;
        if (countrycode != null && !countrycode.equals("")) {
            params = params + "&countrycode=" + countrycode;
        }
        if (location_string != null && !location_string.equals("")) {
            params = params + "&location_string=" + location_string;
        }
        return new Places(super.getJsonNode(BasicLocalBitcoinsOperation.URL, ENDPOINT, params, RequestRestType.SYNC, null, null, 30));
    }

}
