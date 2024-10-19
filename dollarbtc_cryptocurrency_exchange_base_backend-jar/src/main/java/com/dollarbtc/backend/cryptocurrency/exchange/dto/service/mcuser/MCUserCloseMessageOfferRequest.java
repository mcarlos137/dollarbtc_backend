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
public class MCUserCloseMessageOfferRequest {
    
    private String userName, pair, id;
    private OfferType type;
    private boolean bot, excludeMessage;

    public MCUserCloseMessageOfferRequest() {
    }    
    
    public MCUserCloseMessageOfferRequest(String userName, String pair, String id, OfferType type, boolean excludeMessage) {
        this.userName = userName;
        this.pair = pair;
        this.id = id;
        this.type = type;
        this.excludeMessage = excludeMessage;
    }

    public MCUserCloseMessageOfferRequest(String userName, String pair, String id, OfferType type, boolean bot, boolean excludeMessage) {
        this.userName = userName;
        this.pair = pair;
        this.id = id;
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
