/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

import java.security.SignatureException;
/**
 *
 * @author CarlosDaniel
 */
public interface HMACAlgorithm {
    
    /**
     * Encrypt the given message using the given secret key.
     * 
     * @param secretKey Secret Key
     * @param message Message
     * @return One-way encrypted message
     * @throws SignatureException If there is an error or the system doesn't support the encryption method
     */
    String encryptMessage( String secretKey, String message ) throws SignatureException;
    
}