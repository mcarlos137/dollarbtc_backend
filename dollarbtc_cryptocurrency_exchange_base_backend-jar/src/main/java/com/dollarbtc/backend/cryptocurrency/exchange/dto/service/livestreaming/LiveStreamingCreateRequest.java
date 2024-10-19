/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.LiveStreamingType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class LiveStreamingCreateRequest {

    private String userName, name, title, description, imageFileName, publishTimestamp;
    private LiveStreamingType liveStreamingType;
    private Double ppvPrice;

    public LiveStreamingCreateRequest() {
    }

    public LiveStreamingCreateRequest(String userName, String name, String title, String description, String imageFileName, String publishTimestamp, Double ppvPrice, LiveStreamingType liveStreamingType) {
        this.userName = userName;
        this.name = name;
        this.title = title;
        this.description = description;
        this.imageFileName = imageFileName;
        this.ppvPrice = ppvPrice;
        this.publishTimestamp = publishTimestamp;
        this.liveStreamingType = liveStreamingType;
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

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getPublishTimestamp() {
        return publishTimestamp;
    }

    public void setPublishTimestamp(String publishTimestamp) {
        this.publishTimestamp = publishTimestamp;
    }

    public Double getPpvPrice() {
        return ppvPrice;
    }

    public void setPpvPrice(Double ppvPrice) {
        this.ppvPrice = ppvPrice;
    }

    public LiveStreamingType getLiveStreamingType() {
        return liveStreamingType;
    }

    public void setLiveStreamingType(LiveStreamingType liveStreamingType) {
        this.liveStreamingType = liveStreamingType;
    }
        
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("name", this.name);
        ((ObjectNode) jsonNode).put("title", this.title);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("type", this.liveStreamingType.name());
        ((ObjectNode) jsonNode).put("imageFileName", this.imageFileName);
        ((ObjectNode) jsonNode).put("publishTimestamp", this.publishTimestamp);
        ((ObjectNode) jsonNode).put("ppvPrice", this.ppvPrice);
        return jsonNode;
    }

}
