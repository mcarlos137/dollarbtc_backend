/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class MoneyCallCreateRequest {
    
    private String type, createUserName, senderUserName, receiverUserName, currency, senderName, receiverName, scheduledTimestamp, createMessage;
    private Double rate;
    private Integer estimatedTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
    
    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getScheduledTimestamp() {
        return scheduledTimestamp;
    }

    public void setScheduledTimestamp(String scheduledTimestamp) {
        this.scheduledTimestamp = scheduledTimestamp;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getCreateMessage() {
        return createMessage;
    }

    public void setCreateMessage(String createMessage) {
        this.createMessage = createMessage;
    }
    
    public JsonNode toJsonNode() {        
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("type", this.type);
        ((ObjectNode) jsonNode).put("createUserName", this.createUserName);
        ((ObjectNode) jsonNode).put("senderUserName", this.senderUserName);
        ((ObjectNode) jsonNode).put("receiverUserName", this.receiverUserName);
        ((ObjectNode) jsonNode).put("currency", this.currency);
        if (this.senderName != null && !this.senderName.equals("")) {
            ((ObjectNode) jsonNode).put("senderName", this.senderName);
        }
        if (this.receiverName != null && !this.receiverName.equals("")) {
            ((ObjectNode) jsonNode).put("receiverName", this.receiverName);
        }
        ((ObjectNode) jsonNode).put("rate", this.rate);
        if (this.scheduledTimestamp != null) {
            ((ObjectNode) jsonNode).put("scheduledTimestamp", this.scheduledTimestamp);
        }
        if (this.estimatedTime != null) {
            ((ObjectNode) jsonNode).put("estimatedTime", this.estimatedTime);
        }
        if (this.createMessage != null) {
            ((ObjectNode) jsonNode).put("createMessage", this.createMessage);
        }
        return jsonNode;
    }
                   
}
