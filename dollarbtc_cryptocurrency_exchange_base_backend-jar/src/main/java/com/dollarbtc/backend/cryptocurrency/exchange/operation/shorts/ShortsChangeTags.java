/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsChangeTagsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsChangeTags extends AbstractOperation<String> {

    private final ShortsChangeTagsRequest shortsChangeTagsRequest;

    public ShortsChangeTags(ShortsChangeTagsRequest shortsChangeTagsRequest) {
        super(String.class);
        this.shortsChangeTagsRequest = shortsChangeTagsRequest;
    }

    @Override
    public void execute() {
        File shortsFile = ShortsFolderLocator.getFile(shortsChangeTagsRequest.getId());
        try {
            JsonNode shorts = mapper.readTree(shortsFile);
            if (shorts.has("tags")) {
                ((ArrayNode) shorts.get("tags")).removeAll();
            }
            for (String tag : shortsChangeTagsRequest.getTags()) {
                ((ArrayNode) shorts.get("tags")).add(tag);
            }
            FileUtil.editFile(shorts, shortsFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(ShortsChangeTags.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
