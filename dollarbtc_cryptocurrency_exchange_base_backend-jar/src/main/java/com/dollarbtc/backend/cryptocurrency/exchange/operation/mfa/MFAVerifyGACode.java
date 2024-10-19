/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFAVerifyGACodeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import de.taimos.totp.TOTP;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author carlosmolina
 */
public class MFAVerifyGACode extends AbstractOperation<String> {
    
    private final MFAVerifyGACodeRequest mfaVerifyGACodeRequest;

    public MFAVerifyGACode(MFAVerifyGACodeRequest mfaVerifyGACodeRequest) {
        super(String.class);
        this.mfaVerifyGACodeRequest = mfaVerifyGACodeRequest;
    }
    
    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(mfaVerifyGACodeRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            String userGoogleAuthenticatorSecretKey = mapper.readTree(UsersFolderLocator.getGoogleAuthenticatorFile(mfaVerifyGACodeRequest.getUserName())).get("secretKey").textValue();
            if (mfaVerifyGACodeRequest.getCode().equals(getTOTPCode(userGoogleAuthenticatorSecretKey))) {
                super.response = "OK";
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(MFAVerifyGACode.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
    private static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
    
}
