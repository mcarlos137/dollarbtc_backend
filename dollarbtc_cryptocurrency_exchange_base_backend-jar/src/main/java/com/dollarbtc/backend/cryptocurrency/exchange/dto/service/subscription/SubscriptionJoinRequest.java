/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionType;
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
public class SubscriptionJoinRequest implements Serializable, Cloneable {
    
    private String baseUserName, targetUserName, baseName, targetName;
    private JsonNode objectDetails;
    private Integer periodInMonths;
    private Double amount, price;
    private boolean automaticRenewal;
    private SubscriptionType subscriptionType;

    public SubscriptionJoinRequest() {
    }

    public String getId() {
        if (objectDetails != null) {
            return subscriptionType.name() + "__" + baseUserName + "__" + targetUserName + "__" + objectDetails.get("id").textValue();
        }
        return subscriptionType.name() + "__" + baseUserName + "__" + targetUserName;
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
    
    public Integer getPeriodInMonths() {
        return periodInMonths;
    }

    public void setPeriodInMonths(Integer periodInMonths) {
        this.periodInMonths = periodInMonths;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }    

    public boolean isAutomaticRenewal() {
        return automaticRenewal;
    }

    public void setAutomaticRenewal(boolean automaticRenewal) {
        this.automaticRenewal = automaticRenewal;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
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
        if (this.objectDetails != null) {
            ((ObjectNode) jsonNode).set("objectDetails", this.objectDetails);
        }
        if (this.amount != null) {
            ((ObjectNode) jsonNode).put("amount", this.amount);
        }
        if (this.price != null) {
            ((ObjectNode) jsonNode).put("price", this.price);
        }
        ((ObjectNode) jsonNode).put("automaticRenewal", this.automaticRenewal);
        ((ObjectNode) jsonNode).put("type", subscriptionType.name());
        return jsonNode;
    }

}
