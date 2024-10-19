/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author CarlosDaniel
 * @param <T>
 */
public abstract class AbstractRestClient<T> {

    protected final Class<T> responseClass;

    protected abstract Client getClient();

    protected abstract String getMediaType();
    protected MultivaluedMap<String, Object> headers;

    public AbstractRestClient(Class<T> responseClass) {
        this.responseClass = responseClass;
        headers = new MultivaluedHashMap<>();
    }

    protected T get(String url, RequestRestType requestRestType, SecurityType securityType, String[] securityParams, long timeoutInSeconds) {
        WebTarget webTarget = getClient().target(url).path("");
        Invocation.Builder invocationBuilder = webTarget.request(getMediaType());
        invocationBuilder.headers(headers);
        addSecurityHeaders(invocationBuilder, securityType, securityParams);
        if (requestRestType.equals(RequestRestType.SYNC)) {
            Response response = null;
            try {
                response = invocationBuilder.get();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            if (response != null) {
                try {
                    return new Gson().fromJson(response.readEntity(String.class), responseClass);
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            AsyncInvoker invoker = webTarget.request(getMediaType()).async();
            Future<Response> futureResponse = invoker.get();
            String resp = null;
            try {
                Response response;
                if (timeoutInSeconds == 0) {
                    response = futureResponse.get();
                } else {
                    response = futureResponse.get(timeoutInSeconds, TimeUnit.SECONDS);
                }
                futureResponse.cancel(true);
                if (response != null) {
                    resp = response.readEntity(String.class);
                    try {
                        return new Gson().fromJson(resp, responseClass);
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | IllegalStateException | JsonSyntaxException | BadRequestException | HTTPException ex) {
                if (ex instanceof JsonSyntaxException || ex instanceof IllegalStateException) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Exception message from service: " + resp);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                futureResponse.cancel(true);
            }
        }
        return null;
    }

    protected JsonNode getJsonNode(String baseUrl, String endpoint, String params, RequestRestType requestRestType, SecurityType securityType, String[] securityParams, long timeoutInSeconds) {
        WebTarget webTarget = getClient().target(baseUrl + endpoint + params).path("");
        Invocation.Builder invocationBuilder = webTarget.request(getMediaType());
        invocationBuilder.headers(headers);
        addSecurityHeaders(invocationBuilder, securityType, securityParams);
        if (requestRestType.equals(RequestRestType.SYNC)) {
            Response response = null;
            response = invocationBuilder.get();
            if (response != null) {
                try {
                    return new ObjectMapper().readTree(response.readEntity(String.class));
                } catch (IOException ex) {
                    Logger.getLogger(AbstractRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            AsyncInvoker invoker = webTarget.request(getMediaType()).async();
            Future<Response> futureResponse = invoker.get();
            String resp = null;
            try {
                Response response;
                if (timeoutInSeconds == 0) {
                    response = futureResponse.get();
                } else {
                    response = futureResponse.get(timeoutInSeconds, TimeUnit.SECONDS);
                }
                futureResponse.cancel(true);
                if (response != null) {
                    return new ObjectMapper().readTree(response.readEntity(String.class));
                }
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException | IllegalStateException | JsonSyntaxException | BadRequestException | HTTPException ex) {
                if (ex instanceof JsonSyntaxException || ex instanceof IllegalStateException) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Exception message from service: " + resp);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                futureResponse.cancel(true);
            }
        }
        return new ObjectMapper().createObjectNode();
    }

    protected T post(Object requestObject, String url, RequestRestType requestRestType, SecurityType securityType, String[] securityParams, long timeoutInSeconds) {
        WebTarget webTarget = getClient().target(url).path("");
        Invocation.Builder invocationBuilder = webTarget.request(getMediaType());
        invocationBuilder.headers(headers);
        addSecurityHeaders(invocationBuilder, securityType, securityParams);
        if (requestRestType.equals(RequestRestType.SYNC)) {
            Response response = null;
            try {
                response = invocationBuilder.post(Entity.entity(new Gson().toJson(requestObject), getMediaType()));
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            if (response != null) {
                try {
                    return new Gson().fromJson(response.readEntity(String.class), responseClass);
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            AsyncInvoker invoker = invocationBuilder.async();
            Future<Response> futureResponse = invoker.post(Entity.entity(new Gson().toJson(requestObject), getMediaType()));
            String resp = null;
            try {
                Response response;
                if (timeoutInSeconds == 0) {
                    response = futureResponse.get();
                } else {
                    response = futureResponse.get(timeoutInSeconds, TimeUnit.SECONDS);
                }
                futureResponse.cancel(true);
                if (response != null) {
                    resp = response.readEntity(String.class);
                    try {
                        return new Gson().fromJson(resp, responseClass);
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | IllegalStateException | BadRequestException | HTTPException ex) {
                if (ex instanceof JsonSyntaxException || ex instanceof IllegalStateException) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Exception message from service: " + resp);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                futureResponse.cancel(true);
            }
        }
        return null;
    }

    protected JsonNode postJsonNode(JsonNode requestJsonNode, String url, RequestRestType requestRestType, SecurityType securityType, String[] securityParams, long timeoutInSeconds) {
        WebTarget webTarget = getClient().target(url).path("");
        Invocation.Builder invocationBuilder = webTarget.request(getMediaType());
        invocationBuilder.headers(headers);
        addSecurityHeaders(invocationBuilder, securityType, securityParams);
        if (requestRestType.equals(RequestRestType.SYNC)) {
            Response response = null;
            try {
                response = invocationBuilder.post(Entity.entity(requestJsonNode, getMediaType()));
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            if (response != null) {
                String responseString = response.readEntity(String.class);
                if (responseString.equals("") && response.getStatus() == 201) {
                    JsonNode responseJsonNode = new ObjectMapper().createObjectNode();
                    ((ObjectNode) responseJsonNode).put("response", "OK");
                    ((ObjectNode) responseJsonNode).put("location", response.getLocation().getPath());
                    return responseJsonNode;
                }
                try {
                    return new ObjectMapper().readTree(responseString);
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            AsyncInvoker invoker = invocationBuilder.async();
            Future<Response> futureResponse = invoker.post(Entity.entity(requestJsonNode, getMediaType()));
            String resp = null;
            try {
                Response response;
                if (timeoutInSeconds == 0) {
                    response = futureResponse.get();
                } else {
                    response = futureResponse.get(timeoutInSeconds, TimeUnit.SECONDS);
                }
                futureResponse.cancel(true);
                if (response != null) {
                    try {
                        return new ObjectMapper().readTree(response.readEntity(String.class));
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | IllegalStateException | BadRequestException | HTTPException ex) {
                if (ex instanceof JsonSyntaxException || ex instanceof IllegalStateException) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Exception message from service: " + resp);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                futureResponse.cancel(true);
            }
        }
        return new ObjectMapper().createObjectNode();
    }

    protected JsonNode postJsonNode(MultivaluedMap<String, String> formData, String url, RequestRestType requestRestType, SecurityType securityType, String[] securityParams, long timeoutInSeconds) {
        WebTarget webTarget = getClient().target(url).path("");
        Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.headers(headers);
        addSecurityHeaders(invocationBuilder, securityType, securityParams);
        if (requestRestType.equals(RequestRestType.SYNC)) {
            Response response = null;
            try {
                response = invocationBuilder.post(Entity.form(formData));
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            if (response != null) {
                try {
                    return new ObjectMapper().readTree(response.readEntity(String.class));
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            AsyncInvoker invoker = invocationBuilder.async();
            Future<Response> futureResponse = invoker.post(Entity.form(formData));
            String resp = null;
            try {
                Response response;
                if (timeoutInSeconds == 0) {
                    response = futureResponse.get();
                } else {
                    response = futureResponse.get(timeoutInSeconds, TimeUnit.SECONDS);
                }
                futureResponse.cancel(true);
                if (response != null) {
                    try {
                        return new ObjectMapper().readTree(response.readEntity(String.class));
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | IllegalStateException | BadRequestException | HTTPException ex) {
                if (ex instanceof JsonSyntaxException || ex instanceof IllegalStateException) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Exception message from service: " + resp);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                futureResponse.cancel(true);
            }
        }
        return new ObjectMapper().createObjectNode();
    }

    protected JsonNode deleteJsonNode(String url, RequestRestType requestRestType, SecurityType securityType, String[] securityParams, long timeoutInSeconds) {
        WebTarget webTarget = getClient().target(url).path("");
        Invocation.Builder invocationBuilder = webTarget.request(getMediaType());
        invocationBuilder.headers(headers);
        addSecurityHeaders(invocationBuilder, securityType, securityParams);
        if (requestRestType.equals(RequestRestType.SYNC)) {
            Response response = null;
            try {
                response = invocationBuilder.delete();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            if (response != null) {
                String responseString = response.readEntity(String.class);
                try {
                    JsonNode responseJsonNode = new ObjectMapper().readTree(responseString);
                    if (response.getStatus() == 200) {
                        ((ObjectNode) responseJsonNode).put("response", "OK");
                    }
                    return responseJsonNode;
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            AsyncInvoker invoker = invocationBuilder.async();
            Future<Response> futureResponse = invoker.delete();
            String resp = null;
            try {
                Response response;
                if (timeoutInSeconds == 0) {
                    response = futureResponse.get();
                } else {
                    response = futureResponse.get(timeoutInSeconds, TimeUnit.SECONDS);
                }
                futureResponse.cancel(true);
                if (response != null) {
                    try {
                        return new ObjectMapper().readTree(response.readEntity(String.class));
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | IllegalStateException | BadRequestException | HTTPException ex) {
                if (ex instanceof JsonSyntaxException || ex instanceof IllegalStateException) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Exception message from service: " + resp);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                futureResponse.cancel(true);
            }
        }
        return new ObjectMapper().createObjectNode();
    }

    private static void addSecureContext() throws NoSuchAlgorithmException, KeyManagementException {
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
        Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
    }

    private static void addSecurityHeaders(Invocation.Builder invocationBuilder, SecurityType securityType, String[] securityParams) {
        if (securityType == null || securityParams == null) {
            return;
        }
        switch (securityType) {
            case BASIC:
                if (securityParams.length != 2) {
                    return;
                }
                try {
                    invocationBuilder.header("Authorization", "Basic " + EncryptorBASE64.encrypt(securityParams[0] + ":" + securityParams[1]));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AbstractRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case BEARER:
                if (securityParams.length != 1) {
                    return;
                }
                invocationBuilder.header("Authorization", "Bearer " + securityParams[0]);
                break;
            case HMAC:
                if (securityParams.length != 3) {
                    return;
                }
                try {
                    String nonceUnixTime = String.valueOf(System.currentTimeMillis() / 1000L);
                    String message = nonceUnixTime + securityParams[0] + securityParams[2];
                    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                    SecretKeySpec secret_key = new SecretKeySpec(securityParams[1].getBytes(), "HmacSHA256");
                    sha256_HMAC.init(secret_key);
                    byte[] hash = sha256_HMAC.doFinal(message.getBytes("UTF-8"));
                    String signature = DatatypeConverter.printHexBinary(hash).toUpperCase();
                    invocationBuilder.header("Apiauth-Key", securityParams[0]);
                    invocationBuilder.header("Apiauth-Nonce", nonceUnixTime);
                    invocationBuilder.header("Apiauth-Signature", signature);
                } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException ex) {
                    Logger.getLogger(AbstractRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
    }

    public static enum SecurityType {

        BASIC,
        BEARER,
        HMAC;

    }

}
