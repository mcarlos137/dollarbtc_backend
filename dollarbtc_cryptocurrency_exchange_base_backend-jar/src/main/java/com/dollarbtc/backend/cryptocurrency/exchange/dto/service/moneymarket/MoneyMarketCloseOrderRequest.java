/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket;

/**
 *
 * @author CarlosDaniel
 */
public class MoneyMarketCloseOrderRequest {
    
    private String userName, id;
    private boolean bot;

    public MoneyMarketCloseOrderRequest() {
    }    
    
    public MoneyMarketCloseOrderRequest(String userName, String id) {
        this.userName = userName;
        this.id = id;
    }

    public MoneyMarketCloseOrderRequest(String userName, String id, boolean bot) {
        this.userName = userName;
        this.id = id;
        this.bot = bot;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }    
    
}
