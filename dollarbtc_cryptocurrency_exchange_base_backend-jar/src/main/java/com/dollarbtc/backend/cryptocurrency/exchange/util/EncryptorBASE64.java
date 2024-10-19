package com.dollarbtc.backend.cryptocurrency.exchange.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class EncryptorBASE64 {

    public static String encrypt(String data) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(data.getBytes("UTF-8"));
    }

    public static String decrypt(String encryptedData) throws UnsupportedEncodingException {
        return new String(Base64.getDecoder().decode(encryptedData.getBytes("UTF-8")), "UTF-8");
    }

    public static String encryptURL(String data) throws UnsupportedEncodingException {
        return Base64.getUrlEncoder().encodeToString(data.getBytes("UTF-8"));
    }

    public static String decryptURL(String encryptedData) throws UnsupportedEncodingException {
        return new String(Base64.getUrlDecoder().decode(encryptedData.getBytes("UTF-8")), "UTF-8");
    }

}
