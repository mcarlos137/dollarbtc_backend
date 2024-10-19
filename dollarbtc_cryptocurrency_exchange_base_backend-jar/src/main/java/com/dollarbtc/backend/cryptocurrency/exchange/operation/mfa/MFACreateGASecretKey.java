/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFACreateGASecretKeyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base32;

/**
 *
 * @author carlosmolina
 */
public class MFACreateGASecretKey extends AbstractOperation<String> {
    
    private final MFACreateGASecretKeyRequest mfaCreateGASecretKeyRequest;

    public MFACreateGASecretKey(MFACreateGASecretKeyRequest mfaCreateGASecretKeyRequest) {
        super(String.class);
        this.mfaCreateGASecretKeyRequest = mfaCreateGASecretKeyRequest;
    }
    
    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(mfaCreateGASecretKeyRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File userGoogleAuthenticatorFile = UsersFolderLocator.getGoogleAuthenticatorFile(mfaCreateGASecretKeyRequest.getUserName());
        if (userGoogleAuthenticatorFile.isFile()) {
            super.response = "GOOGLE AUTHENTICATOR ALREADY EXIST";
            return;
        }
        String secretKey = generateSecretKey();
        String googleAuthenticatorBarCode = getGoogleAuthenticatorBarCode(secretKey, "support@moneyclick.com", "moneyclick.com");
        JsonNode userGoogleAuthenticator = mapper.createObjectNode();
        ((ObjectNode) userGoogleAuthenticator).put("secretKey", secretKey);
        ((ObjectNode) userGoogleAuthenticator).put("barCode", googleAuthenticatorBarCode);
        try {
            UsersFolderLocator.getGAQRCodeFile(mfaCreateGASecretKeyRequest.getUserName());
            createQRCode(googleAuthenticatorBarCode, UsersFolderLocator.getGAQRCodeFile(mfaCreateGASecretKeyRequest.getUserName()).getAbsolutePath(), 300, 300);
        } catch (WriterException | IOException ex) {
            Logger.getLogger(MFACreateGASecretKey.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtil.createFile(userGoogleAuthenticator, userGoogleAuthenticatorFile);
        super.response = "OK";
    }
    
    private static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
    
    private static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private static void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

}
