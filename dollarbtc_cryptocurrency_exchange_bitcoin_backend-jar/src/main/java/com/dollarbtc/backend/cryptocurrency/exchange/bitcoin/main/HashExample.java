/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bitcoin.main;

import java.security.*;
import org.bitcoinj.core.Base58;
/**
 *
 * @author CarlosDaniel
 */
public class HashExample {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String string = "37750fc299a391fc993dd02fba8162eceae1e7c9ae270abd0b8a9cdd54218ed2";
        System.out.println(string);
        String baseString = "80" + string;
        System.out.println(baseString);
        string = getSha256(baseString);
        System.out.println(string);
        string = getSha256(string);
        System.out.println(string);
        System.out.println(string.substring(0, 8));
        baseString = baseString + string.substring(0, 8);
        System.out.println(baseString);
        String finalString = Base58.encode(hexStringToByteArray(baseString));
        System.out.println(finalString);
        
    }
    
    private static String getSha256(String string) throws NoSuchAlgorithmException{
        return bytesToHex(MessageDigest.getInstance("SHA-256").digest(hexStringToByteArray(string)));
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
