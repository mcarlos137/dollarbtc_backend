/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class ShortsCreate extends AbstractOperation<String> {

    private final ShortsCreateRequest shortsCreateRequest;

    public ShortsCreate(ShortsCreateRequest shortsCreateRequest) {
        super(String.class);
        this.shortsCreateRequest = shortsCreateRequest;
    }

    @Override
    public void execute() {
        String id = BaseOperation.getId();
        File shortsFile = new File(ShortsFolderLocator.getFolder(), id + ".json");
        JsonNode shorts = shortsCreateRequest.toJsonNode();
        ((ObjectNode) shorts).put("id", id);
        ((ObjectNode) shorts).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) shorts).put("status", "CREATED");
        ((ObjectNode) shorts).putArray("tags");
        ((ObjectNode) shorts).set("reactions", mapper.createObjectNode());
        ((ObjectNode) shorts).put("commentsCount", 0);
        ((ObjectNode) shorts).putArray("shares");
        ((ObjectNode) shorts).putArray("views");
        ((ObjectNode) shorts).put("donationsAmountUSD", 0.00);
        ((ObjectNode) shorts).put("status", "CREATED");
        FileUtil.createFile(shorts, shortsFile);
        createIndexesFolder(shorts);
        super.response = "OK";
    }

    private static void createIndexesFolder(JsonNode shorts) {
        ObjectNode index = new ObjectMapper().createObjectNode();
        String id = shorts.get("id").textValue();
        index.put("id", id);
        index.put("timestamp", shorts.get("timestamp").textValue());
        //UserName index
        FileUtil.createFile(index, new File(ShortsFolderLocator.getIndexValueFolder("UserNames", shorts.get("userName").textValue()), id + ".json"));
        //Title index
        FileUtil.createFile(index, new File(ShortsFolderLocator.getIndexValueFolder("Titles", shorts.get("title").textValue()), id + ".json"));
        //Tags index
        FileUtil.createFile(index, new File(ShortsFolderLocator.getIndexValueFolder("Tags", "not_defined"), id + ".json"));
        //Timestamp index
        FileUtil.createFile(index, new File(ShortsFolderLocator.getIndexValueFolder("Timestamps", shorts.get("timestamp").textValue()), id + ".json"));
        //Status index
        FileUtil.createFile(index, new File(ShortsFolderLocator.getIndexValueFolder("Statuses", shorts.get("status").textValue()), id + ".json"));
        //AssetIds index
        FileUtil.createFile(index, new File(ShortsFolderLocator.getIndexValueFolder("AssetIds", shorts.get("assetId").textValue()), id + ".json"));
    }

}
