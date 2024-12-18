/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.hmac;

import java.security.SignatureException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author CarlosDaniel
 */
public class SHAHMACAlgorithm implements HMACAlgorithm {

    /**
     * The name of the algorithm. See Java Cryptography Architecture Reference Guide for valid names.
     */
    String algorithm = null;

    /**
     * Constructs a new SHAHMACAlgorithm with the given size.
     * 
     * @param shaSize key size
     */
    public SHAHMACAlgorithm(int shaSize) {
        if (shaSize != 1 && shaSize != 256 && shaSize != 384 && shaSize != 512) {
            throw new IllegalArgumentException("Size " + shaSize
                    + " not supported (only 1, 256, 384 and 512 are supported)");
        }
        algorithm = "HmacSHA" + Integer.toString(shaSize);
    }

    @Override
    public String encryptMessage(String secretKey, String message) throws SignatureException {
        String result;
        try {
            Mac mac = Mac.getInstance(algorithm);
            byte[] decodedSecretKey = Base64.decodeBase64(secretKey);
            SecretKeySpec signingKey = new SecretKeySpec(decodedSecretKey, algorithm);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes());
            result = Base64.encodeBase64String(rawHmac);

        } catch(Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

}
