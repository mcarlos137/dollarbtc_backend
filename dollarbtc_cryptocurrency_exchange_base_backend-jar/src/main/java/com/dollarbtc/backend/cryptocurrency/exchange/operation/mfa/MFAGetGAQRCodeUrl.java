/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MFAGetGAQRCodeUrl extends AbstractOperation<String> {
    
    private final String userName;

    public MFAGetGAQRCodeUrl(String userName) {
        super(String.class);
        this.userName = userName;
    }
        
    @Override
    protected void execute() {
        try {
            super.response = mapper.readTree(UsersFolderLocator.getGoogleAuthenticatorFile(userName)).get("qrCodeUrl").textValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(MFAGetGAQRCodeUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "";
    }
    
}
