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
public class MCUserTakeMessageOfferRequest {
    
    private String userName, pair, id, nickName;
    private Double amount, price;
    private OfferType type;
    private boolean bot, excludeMessage;

    public MCUserTakeMessageOfferRequest() {
    }    

    public MCUserTakeMessageOfferRequest(String userName, String pair, String id, String nickName, Double amount, Double price, OfferType type, boolean bot, boolean excludeMessage) {
        this.userName = userName;
        this.pair = pair;
        this.id = id;
        this.nickName = nickName;
        this.amount = amount;
        this.price = price;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
