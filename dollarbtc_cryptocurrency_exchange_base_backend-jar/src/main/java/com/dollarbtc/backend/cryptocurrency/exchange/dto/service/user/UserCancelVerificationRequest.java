/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserCancelVerificationRequest implements Serializable, Cloneable {
    
    private String userName, verificationOperationId;
    private UserVerificationType userVerificationType;
    
    public UserCancelVerificationRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVerificationOperationId() {
        return verificationOperationId;
    }

    public void setVerificationOperationId(String verificationOperationId) {
        this.verificationOperationId = verificationOperationId;
    }
    
    public UserVerificationType getUserVerificationType() {
        return userVerificationType;
    }

    public void setUserVerificationType(UserVerificationType userVerificationType) {
        this.userVerificationType = userVerificationType;
    }

}
