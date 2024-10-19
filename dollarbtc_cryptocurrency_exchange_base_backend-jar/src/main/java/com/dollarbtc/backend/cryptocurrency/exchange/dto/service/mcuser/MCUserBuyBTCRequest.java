/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser;

/**
 *
 * @author CarlosDaniel
 */
public class MCUserBuyBTCRequest {
    
    private String userName, currency, description;
    private Double amount, price;

    public MCUserBuyBTCRequest() {
    }

    public MCUserBuyBTCRequest(String userName, String currency, String description, Double amount, Double price) {
        this.userName = userName;
        this.currency = currency;
        this.description = description;
        this.amount = amount;
        this.price = price;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }    
                   
}
