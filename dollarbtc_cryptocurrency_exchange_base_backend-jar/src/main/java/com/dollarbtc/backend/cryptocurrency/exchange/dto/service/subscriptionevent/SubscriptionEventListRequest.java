/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionEventType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class SubscriptionEventListRequest implements Serializable, Cloneable {

    private String baseUserName, targetUserName, initialTimestamp, finalTimestamp;
    private SubscriptionEventType subscriptionEventType;

    public SubscriptionEventListRequest() {
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

    public SubscriptionEventType getSubscriptionEventType() {
        return subscriptionEventType;
    }

    public void setSubscriptionEventType(SubscriptionEventType subscriptionEventType) {
        this.subscriptionEventType = subscriptionEventType;
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
