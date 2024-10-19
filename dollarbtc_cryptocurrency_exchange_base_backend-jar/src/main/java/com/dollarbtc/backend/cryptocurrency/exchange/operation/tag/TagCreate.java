/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.tag;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.tag.TagCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.TagsFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class TagCreate extends AbstractOperation<String> {

    private final TagCreateRequest tagCreateRequest;

    public TagCreate(TagCreateRequest tagCreateRequest) {
        super(String.class);
        this.tagCreateRequest = tagCreateRequest;
    }

    @Override
    protected void execute() {
        File tagConfigFile = TagsFolderLocator.getConfigFile();
        ArrayNode tagConfig = mapper.createArrayNode();
        if (tagConfigFile.isFile()) {
            try {
                tagConfig = (ArrayNode) mapper.readTree(tagConfigFile);
            } catch (IOException ex) {
                Logger.getLogger(TagCreate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        tagConfig.add(tagCreateRequest.getName().toUpperCase());
        FileUtil.editFile(tagConfig, tagConfigFile);
        super.response  = "OK";
    }
  
}
