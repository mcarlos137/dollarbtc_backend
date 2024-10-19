/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionEventType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class SubscriptionEventCreateRequest implements Serializable, Cloneable {

    private String baseUserName, targetUserName, baseName, targetName, publishTimestamp, timestamp;
    private JsonNode objectDetails;
    private SubscriptionEventType subscriptionEventType;

    public SubscriptionEventCreateRequest() {
    }

    public SubscriptionEventCreateRequest(String baseUserName, String targetUserName, String baseName, String targetName, JsonNode objectDetails, SubscriptionEventType subscriptionEventType) {
        this.baseUserName = baseUserName;
        this.targetUserName = targetUserName;
        this.baseName = baseName;
        this.targetName = targetName;
        this.objectDetails = objectDetails;
        this.subscriptionEventType = subscriptionEventType;
    }
        
    public String getId() {
        if(this.timestamp == null){
            this.timestamp = DateUtil.getCurrentDate();
        }
        return this.subscriptionEventType.name() + "__" + this.baseUserName + "__" + this.targetUserName + "__" + DateUtil.getFileDate(this.timestamp);
    }

    public String getBaseUserName() {
        return baseUserName;
    }

    public void setBaseUserName(String baseUserName) {
        this.baseUserName = baseUserName;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
    
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public JsonNode getObjectDetails() {
        return objectDetails;
    }

    public void setObjectDetails(JsonNode objectDetails) {
        this.objectDetails = objectDetails;
    }

    public String getPublishTimestamp() {
        return publishTimestamp;
    }

    public void setPublishTimestamp(String publishTimestamp) {
        this.publishTimestamp = publishTimestamp;
    }
        
    public SubscriptionEventType getSubscriptionEventType() {
        return subscriptionEventType;
    }

    public void setSubscriptionEventType(SubscriptionEventType subscriptionEventType) {
        this.subscriptionEventType = subscriptionEventType;
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("id", this.getId());
        ((ObjectNode) jsonNode).put("baseUserName", this.baseUserName);
        ((ObjectNode) jsonNode).put("targetUserName", this.targetUserName);
        if (this.baseName != null) {
            ((ObjectNode) jsonNode).put("baseName", this.baseName);
        }
        if (this.targetName != null) {
            ((ObjectNode) jsonNode).put("targetName", this.targetName);
        }
        ((ObjectNode) jsonNode).put("type", this.subscriptionEventType.name());
        if (this.objectDetails != null) {
            ((ObjectNode) jsonNode).put("objectDetails", this.objectDetails);
        }
        if (this.publishTimestamp != null) {
            ((ObjectNode) jsonNode).put("publishTimestamp", this.publishTimestamp);
        }
        if (this.timestamp != null) {
            ((ObjectNode) jsonNode).put("timestamp", this.timestamp);
        }
        return jsonNode;
    }

}
