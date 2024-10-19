/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserCheckSecurityQuestionsRequest implements Serializable, Cloneable {
        
    private String userName;
    private JsonNode securityQuestionsAndAnswers;
    
    public UserCheckSecurityQuestionsRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public JsonNode getSecurityQuestionsAndAnswers() {
        return securityQuestionsAndAnswers;
    }

    public void setSecurityQuestionsAndAnswers(JsonNode securityQuestionsAndAnswers) {
        this.securityQuestionsAndAnswers = securityQuestionsAndAnswers;
    }
    
}
