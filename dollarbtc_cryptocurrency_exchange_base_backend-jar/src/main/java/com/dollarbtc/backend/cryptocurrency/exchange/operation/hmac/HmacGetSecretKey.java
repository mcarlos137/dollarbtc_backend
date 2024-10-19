/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.hmac;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class HmacGetSecretKey extends AbstractOperation<String> {

    private final String userName, deviceId;

    public HmacGetSecretKey(String userName, String deviceId) {
        super(String.class);
        this.userName = userName;
        this.deviceId = deviceId;
    }

    @Override
    protected void execute() {
        if (deviceId == null) {
            File userHmacFile = UsersFolderLocator.getHmacFile(userName);
            try {
                super.response = mapper.readTree(userHmacFile).get("secretKey").textValue();
                return;
            } catch (IOException ex) {
                Logger.getLogger(HmacGetSecretKey.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            File userHmacNewFile = UsersFolderLocator.getHmacNewFile(userName);
            try {
                super.response = mapper.readTree(userHmacNewFile).get(deviceId).get("secretKey").textValue();
                return;
            } catch (IOException ex) {
                Logger.getLogger(HmacGetSecretKey.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        super.response = null;
    }

}
