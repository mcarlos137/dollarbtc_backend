/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserPostMessageRequest implements Serializable, Cloneable {
    
    private String userName, message, redirectionPath;
    
    public UserPostMessageRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirectionPath() {
        return redirectionPath;
    }

    public void setRedirectionPath(String redirectionPath) {
        this.redirectionPath = redirectionPath;
    }
                    
}
