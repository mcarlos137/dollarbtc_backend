/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class SubscriptionUnjoinRequest implements Serializable, Cloneable {
    
    private String baseUserName, targetUserName, typeId;
    private SubscriptionType subscriptionType;
    
    public SubscriptionUnjoinRequest() {
    }

    public String getId() {
        if(typeId == null){
            return subscriptionType.name() + "__" + baseUserName + "__" + targetUserName;
        }
        return subscriptionType.name() + "__" + baseUserName + "__" + targetUserName + "__" + typeId;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
        
}
