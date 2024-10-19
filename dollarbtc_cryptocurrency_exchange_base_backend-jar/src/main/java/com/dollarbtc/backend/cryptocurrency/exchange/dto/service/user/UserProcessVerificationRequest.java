/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserProcessVerificationRequest implements Serializable, Cloneable {
    
    private String userName, timestamp;
    private boolean success;
    private UserVerificationType userVerificationType;
    private UserVerificationStatus lastUserVerificationStatus;
    
    public UserProcessVerificationRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserVerificationType getUserVerificationType() {
        return userVerificationType;
    }

    public void setUserVerificationType(UserVerificationType userVerificationType) {
        this.userVerificationType = userVerificationType;
    }
    
    public UserVerificationStatus getLastUserVerificationStatus() {
        return lastUserVerificationStatus;
    }

    public void setLastUserVerificationStatus(UserVerificationStatus lastUserVerificationStatus) {
        this.lastUserVerificationStatus = lastUserVerificationStatus;
    }
                
}
