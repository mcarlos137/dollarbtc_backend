/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;

/**
 *
 * @author CarlosDaniel
 */
public class MCUserPostMessageOfferRequest {
    
    private String userName, pair, timeUnit, nickName;
    private Double amount, price;
    private int time;
    private OfferType type;
    private boolean bot, excludeMessage;

    public MCUserPostMessageOfferRequest() {
    }
    
    public MCUserPostMessageOfferRequest(String userName, String pair, String nickName, Double amount, Double price, int time, String timeUnit, OfferType type, boolean bot, boolean excludeMessage) {
        this.userName = userName;
        this.pair = pair;
        this.timeUnit = timeUnit;
        this.nickName = nickName;
        this.amount = amount;
        this.price = price;
        this.time = time;
        this.type = type;
        this.bot = bot;
        this.excludeMessage = excludeMessage;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }
    
    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
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
    
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public OfferType getType() {
        return type;
    }

    public void setType(OfferType type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }    

    public boolean isExcludeMessage() {
        return excludeMessage;
    }

    public void setExcludeMessage(boolean excludeMessage) {
        this.excludeMessage = excludeMessage;
    }
            
}
