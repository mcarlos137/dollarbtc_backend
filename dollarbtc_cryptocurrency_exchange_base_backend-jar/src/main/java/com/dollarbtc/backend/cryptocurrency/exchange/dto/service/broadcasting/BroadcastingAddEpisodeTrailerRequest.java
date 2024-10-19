/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class BroadcastingAddEpisodeTrailerRequest {
        
    private String broadcastingId, title, description, type, imageFileName, videoFileName, publishTimestamp;

    public BroadcastingAddEpisodeTrailerRequest() {
    }

    public BroadcastingAddEpisodeTrailerRequest(String broadcastingId, String title, String description, String type, String imageFileName, String videoFileName, String publishTimestamp) {
        this.broadcastingId = broadcastingId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.imageFileName = imageFileName;
        this.videoFileName = videoFileName;
        this.publishTimestamp = publishTimestamp;
    }
    
    public String getBroadcastingId() {
        return broadcastingId;
    }

    public void setBroadcastingId(String broadcastingId) {
        this.broadcastingId = broadcastingId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
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
            
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("broadcastingId", this.broadcastingId);
        ((ObjectNode) jsonNode).put("title", this.title);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("type", this.type);
        ((ObjectNode) jsonNode).put("imageFileName", this.imageFileName);
        ((ObjectNode) jsonNode).put("videoFileName", this.videoFileName);
        ((ObjectNode) jsonNode).put("publishTimestamp", this.publishTimestamp);
        return jsonNode;
    }
                       
}
