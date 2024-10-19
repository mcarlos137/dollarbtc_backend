/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsViewRequest;
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
public class ShortsView extends AbstractOperation<String> {

    private final ShortsViewRequest shortsViewRequest;

    public ShortsView(ShortsViewRequest shortsViewRequest) {
        super(String.class);
        this.shortsViewRequest = shortsViewRequest;
    }

    @Override
    public void execute() {
        try {
            File shortsFile = ShortsFolderLocator.getFile(shortsViewRequest.getId());
            JsonNode shorts = mapper.readTree(shortsFile);
            ObjectNode view = mapper.createObjectNode();
            view.put("timestamp", DateUtil.getCurrentDate());
            view.put("userName", shortsViewRequest.getUserName());
            view.put("name", shortsViewRequest.getName());
            ((ArrayNode) shorts.get("views")).add(view);
            FileUtil.editFile(shorts, shortsFile);
        } catch (IOException ex) {
            Logger.getLogger(ShortsView.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
