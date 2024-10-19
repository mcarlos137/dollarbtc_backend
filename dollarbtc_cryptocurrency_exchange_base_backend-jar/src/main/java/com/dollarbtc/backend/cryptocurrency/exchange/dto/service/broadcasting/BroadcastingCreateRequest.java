/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BroadcastingType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class BroadcastingCreateRequest {
        
    private String userName, name, title, description, imageFileName;
    private Double subscriptionPrice;
    private BroadcastingType broadcastingType;

    public BroadcastingCreateRequest() {
    }

    public BroadcastingCreateRequest(String userName, String name, String title, String description, String imageFileName, Double subscriptionPrice, BroadcastingType broadcastingType) {
        this.userName = userName;
        this.name = name;
        this.title = title;
        this.description = description;
        this.imageFileName = imageFileName;
        this.subscriptionPrice = subscriptionPrice;
        this.broadcastingType = broadcastingType;
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

    public Double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(Double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public BroadcastingType getBroadcastingType() {
        return broadcastingType;
    }

    public void setBroadcastingType(BroadcastingType broadcastingType) {
        this.broadcastingType = broadcastingType;
    }
            
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("name", this.name);
        ((ObjectNode) jsonNode).put("title", this.title);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("imageFileName", this.imageFileName);
        ((ObjectNode) jsonNode).put("subscriptionPrice", this.subscriptionPrice);
        ((ObjectNode) jsonNode).put("type", this.broadcastingType.name());
        return jsonNode;
    }
                       
}
