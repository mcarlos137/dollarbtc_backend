/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.hmac;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class HmacGetAccessKey extends AbstractOperation<String> {

    public HmacGetAccessKey() {
        super(String.class);
    }
    
    @Override
    protected void execute() {
        File hmacFile = BaseFilesLocator.getNewHmacFile();
        try {
            super.response = mapper.readTree(hmacFile).get("accessKey").textValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(HmacGetAccessKey.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = null;
    }
    
}
