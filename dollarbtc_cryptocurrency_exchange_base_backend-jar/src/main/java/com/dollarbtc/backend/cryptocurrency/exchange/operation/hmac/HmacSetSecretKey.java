/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.hmac;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.hmac.HmacSetSecretKeyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class HmacSetSecretKey extends AbstractOperation<String> {

    private final HmacSetSecretKeyRequest hmacSetSecretKeyRequest;

    public HmacSetSecretKey(HmacSetSecretKeyRequest hmacSetSecretKeyRequest) {
        super(String.class);
        this.hmacSetSecretKeyRequest = hmacSetSecretKeyRequest;
    }

    @Override
    protected void execute() {
        if (!UsersFolderLocator.getFolder(hmacSetSecretKeyRequest.getUserName()).isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        if (hmacSetSecretKeyRequest.getDeviceId() != null && !hmacSetSecretKeyRequest.getDeviceId().equals("")) {
            File userHmacNewFile = UsersFolderLocator.getHmacNewFile(hmacSetSecretKeyRequest.getUserName());
            JsonNode userHmacNew = mapper.createObjectNode();
            ObjectNode device = mapper.createObjectNode();
            device.put("timestamp", DateUtil.getCurrentDate());
            device.put("secretKey", hmacSetSecretKeyRequest.getSecretKey());
            ((ObjectNode) userHmacNew).set(hmacSetSecretKeyRequest.getDeviceId(), device);
            FileUtil.editFile(userHmacNew, userHmacNewFile);
            super.response = "OK";
            return;
        }
        File userHmacFile = UsersFolderLocator.getHmacFile(hmacSetSecretKeyRequest.getUserName());
        JsonNode userHmac = mapper.createObjectNode();
        ((ObjectNode) userHmac).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) userHmac).put("secretKey", hmacSetSecretKeyRequest.getSecretKey());
        FileUtil.editFile(userHmac, userHmacFile);
        super.response = "OK";
    }

}
