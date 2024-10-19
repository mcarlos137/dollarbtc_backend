/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsShareRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsShare extends AbstractOperation<String> {

    private final ShortsShareRequest shortsShareRequest;

    public ShortsShare(ShortsShareRequest shortsShareRequest) {
        super(String.class);
        this.shortsShareRequest = shortsShareRequest;
    }

    @Override
    public void execute() {
        try {
            File shortsFile = ShortsFolderLocator.getFile(shortsShareRequest.getId());
            JsonNode shorts = mapper.readTree(shortsFile);
            ObjectNode share = mapper.createObjectNode();
            share.put("timestamp", DateUtil.getCurrentDate());
            share.put("userName", shortsShareRequest.getUserName());
            share.put("name", shortsShareRequest.getName());
            ((ArrayNode) shorts.get("shares")).add(share);
            FileUtil.editFile(shorts, shortsFile);
        } catch (IOException ex) {
            Logger.getLogger(ShortsShare.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
