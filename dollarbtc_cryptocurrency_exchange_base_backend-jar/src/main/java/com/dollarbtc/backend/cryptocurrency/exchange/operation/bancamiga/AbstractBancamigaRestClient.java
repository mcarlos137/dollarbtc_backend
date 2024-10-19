/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga;

import com.dollarbtc.backend.cryptocurrency.exchange.util.*;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author CarlosDaniel
 * @param <T>
 */
public abstract class AbstractBancamigaRestClient<T> extends AbstractRestClient<T> {

    private static Client client;
    public static final String URL = "https://200.74.230.26";
    public static final String API_KEY = "18354e0f-81c5-4b1f-951f-a5ee096269cc";
    protected MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

    public AbstractBancamigaRestClient(Class<T> responseClass) {
        super(responseClass);
        super.headers.add("apiKey", API_KEY);
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
                Logger.getLogger(AbstractBancamigaRestClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return client;
    }

    @Override
    public String getMediaType() {
        return MediaType.MULTIPART_FORM_DATA;
    }

}
