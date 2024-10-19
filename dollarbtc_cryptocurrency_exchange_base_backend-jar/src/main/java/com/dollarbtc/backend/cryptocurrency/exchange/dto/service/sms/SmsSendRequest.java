/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.sms;

import java.io.Serializable;
import java.util.List;


/**
 *
 * @author carlosmolina
 */
public class SmsSendRequest implements Serializable, Cloneable {
    
    //private String phonePrefix, message;
    private String message;
    private List<String> phones;
    private boolean testing;

    public SmsSendRequest() {
    }

    public SmsSendRequest(String message, List<String> phones, boolean testing) {
        this.message = message;
        this.phones = phones;
        this.testing = testing;
    }

    /*public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }*/

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }
        
}
