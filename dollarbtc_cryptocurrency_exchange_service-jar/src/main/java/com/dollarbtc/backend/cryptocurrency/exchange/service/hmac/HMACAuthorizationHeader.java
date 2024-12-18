/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
public class HMACAuthorizationHeader {

    public static final String PROVIDER = "acquia-http-hmac";

    public static final String DELIMITER_AUTHORIZATION_HEADER = ",";
    public static final String GLUE_AUTHORIZATION_HEADER_PAIR = "=";

    public static final String DELIMITER_CUSTOM_SUBHEADER = ";";
    public static final String GLUE_CUSTOM_SUBHEADER_PAIR = ":";

    private String realm;
    private String id;
    private String nonce;
    private String version;
    private List<String> headers;
    private String signature;
    private String deviceId;

    /**
     * This method converts an Authorization string into a key-value pair Map
     * 
     * The input format:
     * acquia-http-hmac realm="Example",id="client-id",nonce="random-uuid",version="2.0",headers="custom1;custom2",signature="Signature"
     * 
     * The result of this call will be a Map in <key,value> pair of the parameters section (2nd token)
     * This will discard the 1st token (i.e.: acquia-http-hmac will be discarded)
     * 
     * @param authString; in the format specified above
     * @return
     */
    public static HMACAuthorizationHeader getAuthorizationHeaderObject(String authString) {
        int indexSpace = authString.indexOf(" ");
        String authContent = authString.substring(indexSpace + 1);
        String[] authParams = authContent.split(",");
        Map<String, String> theMap = new HashMap<>();
        for (String param : authParams) {
            int indexDelimiter = param.indexOf("="); //first index of delimiter
            String key = param.substring(0, indexDelimiter);
            String val = param.substring(indexDelimiter + 1);
            theMap.put(key.toLowerCase(), val.substring(1, val.length() - 1)); //remove "" from val
        }
        HMACAuthorizationHeader result = new HMACAuthorizationHeader(theMap.get("realm"),
            theMap.get("id"), theMap.get("nonce"), theMap.get("version"));
        //check headers
        String headers = theMap.get("headers");
        if (headers != null && headers.length() > 0) {
            result.setHeaders(Arrays.asList(headers.split(DELIMITER_CUSTOM_SUBHEADER)));
        }
        //check signature
        String signature = theMap.get("signature");
        if (signature != null && signature.length() > 0) {
            result.setSignature(signature);
        }
        String deviceId = theMap.get("deviceid");
        if (deviceId != null && deviceId.length() > 0) {
            result.setDeviceId(deviceId);
        }
        if (result.isAuthorizationHeaderValid()) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * Constructor with the essential parameters
     * 
     * @param realm
     * @param id
     * @param nonce
     * @param version
     */
    public HMACAuthorizationHeader(String realm, String id, String nonce, String version) {
        this(realm, id, nonce, version, null, null);
    }

    /**
     * Constructor with all the parameters specified
     * 
     * @param realm
     * @param id
     * @param nonce
     * @param version
     * @param headers
     * @param signature
     */
    public HMACAuthorizationHeader(String realm, String id, String nonce, String version,
            List<String> headers, String signature) {
        super();
        this.realm = realm;
        this.id = id;
        this.nonce = nonce;
        this.version = version;
        this.headers = headers;
        this.signature = signature;
    }

    /**
     * These parameters must be specified for an Authorization to be valid:
     *  realm, id, nonce, version
     * 
     * @return
     */
    public boolean isAuthorizationHeaderValid() {
        return this.realm != null && this.realm.length() > 0 && this.id != null
                && this.id.length() > 0 && this.nonce != null && this.nonce.length() > 0
                && this.version != null && this.version.length() > 0;
    }

    /**
     * Get realm of this Authorization
     * 
     * @return
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Set realm of this Authorization
     * 
     * @param realm
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * Get id (accessKey) of this Authorization
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set id (accessKey) of this Authorization
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get nonce of this Authorization
     * 
     * @return
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * Set nonce of this Authorization
     * 
     * @param nonce
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /**
     * Get version of this Authorization
     * 
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set version of this Authorization
     * 
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get custom headers of this Authorization
     * 
     * @return
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * Set custom headers of this Authorization
     * 
     * @param headers
     */
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    /**
     * Get encrypted signature of this Authorization
     * 
     * @return
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Set encrypted signature of this Authorization
     * 
     * @param signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }    

    @Override
    public String toString() {
        return this.getAuthorizationString().toString();
    }

    /**
     * Construct Authorization content
     * 
     * @param realm
     * @param id
     * @param nonce
     * @param version
     * @param headers; optional: omitted if null or size 0
     * @param signature; optional: omitted if null or length 0
     * @return
     */
    public StringBuilder getAuthorizationString() {
        StringBuilder authBuilder = new StringBuilder();
        authBuilder.append(PROVIDER).append(" ");
        authBuilder.append("realm=\"").append(this.realm).append("\",");
        authBuilder.append("id=\"").append(this.id).append("\",");
        authBuilder.append("nonce=\"").append(this.nonce).append("\",");
        authBuilder.append("version=\"").append(this.version).append("\"");
        authBuilder.append("deviceId=\"").append(this.deviceId).append("\"");

        if (this.headers != null && this.headers.size() > 0) {
            authBuilder.append(",headers=\"").append(
                this.implodeStringArray(this.headers, DELIMITER_CUSTOM_SUBHEADER)).append("\"");
        }

        if (this.signature != null && this.signature.length() > 0) {
            authBuilder.append(",signature=\"").append(this.signature).append("\"");
        }

        return authBuilder;
    }

    /**
     * Helper method to implode a list of Strings into a String
     * For example: List [ "a", "b" ] delimiter "," will become "a,b"
     * @param theList
     * @param glue
     * @return
     */
    private String implodeStringArray(List<String> theList, String glue) {
        StringBuilder sBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String aString : theList) {
            if (!isFirst) {
                sBuilder.append(glue);
            }
            sBuilder.append(aString);
            isFirst = false;
        }
        return sBuilder.toString();
    }

}
