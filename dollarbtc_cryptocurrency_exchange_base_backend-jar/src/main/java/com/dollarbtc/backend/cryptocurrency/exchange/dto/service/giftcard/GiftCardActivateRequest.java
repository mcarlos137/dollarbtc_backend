/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard;

/**
 *
 * @author carlosmolina
 */
public class GiftCardActivateRequest {
    
    private String id, userName, currency, language, email, source;
    private Double amount;
    private boolean upfrontCommission;
    
    public GiftCardActivateRequest() {
    }

    public GiftCardActivateRequest(String id, String userName, String currency, Double amount, String source, boolean upfrontCommission) {
        this.id = id;
        this.userName = userName;
        this.currency = currency;
        this.amount = amount;
        this.source = source;
        this.upfrontCommission = upfrontCommission;
    }  
    
    public GiftCardActivateRequest(String id, String userName, String currency, Double amount, String language, String email, String source, boolean upfrontCommission) {
        this.id = id;
        this.userName = userName;
        this.currency = currency;
        this.amount = amount;
        this.language = language;
        this.email = email;
        this.source = source;
        this.upfrontCommission = upfrontCommission;
    } 
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
        
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUpfrontCommission() {
        return upfrontCommission;
    }

    public void setUpfrontCommission(boolean upfrontCommission) {
        this.upfrontCommission = upfrontCommission;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
}
