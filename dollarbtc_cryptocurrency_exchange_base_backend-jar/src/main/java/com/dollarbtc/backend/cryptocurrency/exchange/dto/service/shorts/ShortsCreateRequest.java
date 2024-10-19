/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class ShortsCreateRequest {
        
    private String userName, name, title, description, videoFileName, publishTimestamp, assetId;

    public ShortsCreateRequest() {
    }

    public ShortsCreateRequest(String userName, String name, String title, String description, String videoFileName, String publishTimestamp, String assetId) {
        this.userName = userName;
        this.name = name;
        this.title = title;
        this.description = description;
        this.videoFileName = videoFileName;
        this.publishTimestamp = publishTimestamp;
        this.assetId = assetId;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public String getPublishTimestamp() {
        return publishTimestamp;
    }

    public void setPublishTimestamp(String publishTimestamp) {
        this.publishTimestamp = publishTimestamp;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
            
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("name", this.name);
        ((ObjectNode) jsonNode).put("title", this.title);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("videoFileName", this.videoFileName);
        ((ObjectNode) jsonNode).put("publishTimestamp", this.publishTimestamp);
        ((ObjectNode) jsonNode).put("assetId", this.assetId);
        return jsonNode;
    }
                       
}
