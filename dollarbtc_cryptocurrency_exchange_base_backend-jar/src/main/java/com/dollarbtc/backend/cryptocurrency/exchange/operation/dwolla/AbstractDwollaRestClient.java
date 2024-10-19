/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.dwolla;

import com.dollarbtc.backend.cryptocurrency.exchange.util.*;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 *
 * @author CarlosDaniel
 * @param <T>
 */
public abstract class AbstractDwollaRestClient<T> extends AbstractRestClient<T> {

    private static Client client;
    protected final String url;
    protected final String clientKey;
    protected final String clientSecret;
    protected final String accessToken;
        
    public AbstractDwollaRestClient(Class<T> responseClass, boolean accessTokenNeeded) throws IOException {
        super(responseClass);
        File dwollaFile = BaseFilesLocator.getDwollaFile();
        JsonNode dwolla = new ObjectMapper().readTree(dwollaFile);
        this.url = dwolla.get("url").textValue();
        this.clientKey = dwolla.get("clientKey").textValue();
        this.clientSecret = dwolla.get("clientSecret").textValue();
        if(accessTokenNeeded){
            this.accessToken = new GetTokenOperation().getResponse().get("access_token").textValue();
        } else {
            this.accessToken = null;
        }
    }

    @Override
    public Client getClient() {
        if (client == null) {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }}, new java.security.SecureRandom());
                client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                Logger.getLogger(AbstractDwollaRestClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return client;
    }

    @Override
    public String getMediaType() {
        return "application/vnd.dwolla.v1.hal+json";
    }

}
