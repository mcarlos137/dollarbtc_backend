/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.filter;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.hmac.HmacGetSecretKey;
import com.dollarbtc.backend.cryptocurrency.exchange.service.hmac.HMACFilter;
import com.dollarbtc.backend.cryptocurrency.exchange.service.hmac.SecretKeyException;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class SecurityFilter extends HMACFilter {

    @Override
    protected String getSecretKey(String accessKey, String realm, String deviceId) throws SecretKeyException {
        if (realm.equals("Admin")) {
            File hmacFile = BaseFilesLocator.getHmacFile();
            try {
                JsonNode hmac = new ObjectMapper().readTree(hmacFile);
                if (hmac.has(accessKey)) {
                    return hmac.get(accessKey).textValue();
                }
                throw new SecretKeyException(SecretKeyException.NOT_FOUND);
            } catch (IOException ex) {
                Logger.getLogger(SecurityFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode newHmacFile = mapper.readTree(BaseFilesLocator.getNewHmacFile());
                if (!newHmacFile.has(accessKey) || !newHmacFile.get(accessKey).get("active").booleanValue()) {
                    throw new SecretKeyException(SecretKeyException.CANNOT_RETRIEVE);
                }
                String secretKey = new HmacGetSecretKey(realm, deviceId).getResponse();
                if(secretKey == null){
                    throw new SecretKeyException(SecretKeyException.NOT_FOUND);
                }
                return secretKey;
            } catch (IOException ex) {
                Logger.getLogger(SecurityFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new SecretKeyException(SecretKeyException.NOT_FOUND);
        }
        throw new SecretKeyException(SecretKeyException.CANNOT_RETRIEVE);
    }

}
