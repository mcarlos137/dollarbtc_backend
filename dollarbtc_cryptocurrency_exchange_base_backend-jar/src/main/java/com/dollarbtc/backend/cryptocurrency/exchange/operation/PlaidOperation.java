/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsGetRequest;
import com.plaid.client.request.ItemDwollaProcessorTokenCreateRequest;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.AccountsGetResponse;
import com.plaid.client.response.ItemDwollaProcessorTokenCreateResponse;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;

/**
 *
 * @author ricardo torres
 */
public class PlaidOperation {

    private final PlaidClient plaidClient;

    public PlaidOperation() throws IOException {
        File plaidFile = BaseFilesLocator.getPlaidFile();
        JsonNode plaid = new ObjectMapper().readTree(plaidFile);
        String publicKey = plaid.get("publicKey").textValue();
        String clientId = plaid.get("clientId").textValue();
        String secret = plaid.get("secret").textValue();
        plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret(clientId, secret)
                .publicKey(publicKey)
                .sandboxBaseUrl() // sandbox Plaid environment
                .logLevel(HttpLoggingInterceptor.Level.BODY)
                .build();
    }

    public String[] getAccessTokenAndItemId(String publicToken) throws IOException {
        String[] accessTokenAndItemId = new String[2];
        Response<ItemPublicTokenExchangeResponse> plaidResponse = plaidClient.service().itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(publicToken)).execute();
        Logger.getLogger(PlaidOperation.class.getName()).log(Level.INFO, "publicToken: {0}", publicToken);
        if(plaidResponse.body() != null){
            accessTokenAndItemId[0] = plaidResponse.body().getAccessToken();
            accessTokenAndItemId[1] = plaidResponse.body().getItemId();
        }
        return accessTokenAndItemId;
    }
    
    public String getProcessorToken(String accessToken, String accountId) throws IOException {
        Response<ItemDwollaProcessorTokenCreateResponse> plaidResponse = plaidClient.service().itemDwollaProcessorTokenCreate(new ItemDwollaProcessorTokenCreateRequest(accessToken, accountId)).execute();
        if(plaidResponse.body() != null){
            return plaidResponse.body().getProcessorToken();
        }
        return null;
    }
    
    public ObjectNode getAccount(String accessToken) throws IOException {
        Response<AccountsGetResponse> plaidResponse = plaidClient.service().accountsGet(new AccountsGetRequest(accessToken)).execute();
       
        if(plaidResponse.body() == null || plaidResponse.body().getAccounts() == null){
            return new ObjectMapper().createObjectNode();
        }
        return new ObjectMapper().convertValue(plaidResponse.body().getAccounts().get(0), ObjectNode.class);
    }

}
