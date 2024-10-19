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
public class UserStartVerificationRequest implements Serializable, Cloneable {
    
    private String userName, info;
    private String[] fieldNames;
    private UserVerificationType userVerificationType;
    
    public UserStartVerificationRequest() {
    }

    public UserStartVerificationRequest(String userName, String info, String[] fieldNames, UserVerificationType userVerificationType) {
        this.userName = userName;
        this.info = info;
        this.fieldNames = fieldNames;
        this.userVerificationType = userVerificationType;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public UserVerificationType getUserVerificationType() {
        return userVerificationType;
    }

    public void setUserVerificationType(UserVerificationType userVerificationType) {
        this.userVerificationType = userVerificationType;
    }

}
