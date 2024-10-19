/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

/**
 *
 * @author CarlosDaniel
 */
public class SecretKeyException extends Exception {

    private static final long serialVersionUID = -8918809922281463624L;

    public static final String NOT_FOUND = "No secret key is associated with the given access key.";
    public static final String CANNOT_RETRIEVE = "Fail to obtain secret/private key from the authorization server.";

    public SecretKeyException(String message) {
        super(message);
    }

}