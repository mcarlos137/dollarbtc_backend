/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author CarlosDaniel
 */
public class EncryptorCipher {

    private final static String KEY = "Lav42645Tel17325"; // 128 bit key
    private Cipher cipher;
    private Key aesKey;

    public EncryptorCipher(String algorithm) {
        try {
            aesKey = new SecretKeySpec(KEY.getBytes(), algorithm);
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(EncryptorCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String encrypt(String baseString) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return new String(cipher.doFinal(baseString.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptorCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String decrypt(String encryptedString) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(encryptedString.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptorCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
