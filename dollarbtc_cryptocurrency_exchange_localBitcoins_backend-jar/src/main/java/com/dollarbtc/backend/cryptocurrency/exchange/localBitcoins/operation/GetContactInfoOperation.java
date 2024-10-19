/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto.ContactInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author CarlosDaniel
 */
public class GetContactInfoOperation extends AbstractRestClient<ContactInfo> {

    private static Client client;
    private static final String ENDPOINT = "/api/contact_info/";
    private final String contactId, key, secret;

    public GetContactInfoOperation(String contactId, String key, String secret) {
        super(ContactInfo.class);
        this.contactId = contactId;
        this.key = key;
        this.secret = secret;
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

    public ContactInfo getResponse() {
        String endPoint = ENDPOINT + contactId + "/";
        return new ContactInfo(super.getJsonNode(BasicLocalBitcoinsOperation.URL, endPoint, "", RequestRestType.SYNC, SecurityType.HMAC, new String[]{key, secret, endPoint}, 30));
    }

}
