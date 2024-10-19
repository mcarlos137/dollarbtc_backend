/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class SubscriptionListRequest implements Serializable, Cloneable {

    private String baseUserName, targetUserName, objectDetailsId, initialTimestamp, finalTimestamp;
    private SubscriptionType subscriptionType;
    private SubscriptionStatus subscriptionStatus;

    public SubscriptionListRequest() {
    }
    
    public SubscriptionListRequest(String baseUserName, SubscriptionType subscriptionType, SubscriptionStatus subscriptionStatus) {
        this.baseUserName = baseUserName;
        this.subscriptionType = subscriptionType;
        this.subscriptionStatus = subscriptionStatus;
    }
    
    public SubscriptionListRequest(String baseUserName, String targetUserName, SubscriptionType subscriptionType, SubscriptionStatus subscriptionStatus) {
        this.baseUserName = baseUserName;
        this.targetUserName = targetUserName;
        this.subscriptionType = subscriptionType;
        this.subscriptionStatus = subscriptionStatus;
    }

    public SubscriptionListRequest(String baseUserName, String targetUserName, String objectDetailsId, SubscriptionType subscriptionType, SubscriptionStatus subscriptionStatus) {
        this.baseUserName = baseUserName;
        this.targetUserName = targetUserName;
        this.objectDetailsId = objectDetailsId;
        this.subscriptionType = subscriptionType;
        this.subscriptionStatus = subscriptionStatus;
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

    public String getObjectDetailsId() {
        return objectDetailsId;
    }

    public void setObjectDetailsId(String objectDetailsId) {
        this.objectDetailsId = objectDetailsId;
    }
    
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }    

    public String getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(String initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

    public String getFinalTimestamp() {
        return finalTimestamp;
    }

    public void setFinalTimestamp(String finalTimestamp) {
        this.finalTimestamp = finalTimestamp;
    }    
        
}
