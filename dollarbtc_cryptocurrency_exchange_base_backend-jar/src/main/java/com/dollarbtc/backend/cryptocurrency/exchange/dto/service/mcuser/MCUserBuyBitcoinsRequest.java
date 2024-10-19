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
public class MCUserBuyBitcoinsRequest {
    
    private String userName, currency;
    private Double amount, btcAmount;

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

    public Double getBtcAmount() {
        return btcAmount;
    }

    public void setBtcAmount(Double btcAmount) {
        this.btcAmount = btcAmount;
    }
        
}
