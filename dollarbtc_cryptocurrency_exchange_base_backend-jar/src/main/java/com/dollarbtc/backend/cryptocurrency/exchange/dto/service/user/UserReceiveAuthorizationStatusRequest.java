/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.ReceiveAuthorizationStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserReceiveAuthorizationStatusRequest implements Serializable, Cloneable {
        
    private String userName, receiveAuthorizationId;
    private ReceiveAuthorizationStatus receiveAuthorizationStatus;
    
    public UserReceiveAuthorizationStatusRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReceiveAuthorizationId() {
        return receiveAuthorizationId;
    }

    public void setReceiveAuthorizationId(String receiveAuthorizationId) {
        this.receiveAuthorizationId = receiveAuthorizationId;
    }    

    public ReceiveAuthorizationStatus getReceiveAuthorizationStatus() {
        return receiveAuthorizationStatus;
    }

    public void setReceiveAuthorizationStatus(ReceiveAuthorizationStatus receiveAuthorizationStatus) {
        this.receiveAuthorizationStatus = receiveAuthorizationStatus;
    }
            
}
