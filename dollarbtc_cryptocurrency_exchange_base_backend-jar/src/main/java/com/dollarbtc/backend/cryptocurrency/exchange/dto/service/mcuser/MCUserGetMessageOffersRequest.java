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
public class MCUserGetMessageOffersRequest {
    
    private String userName, pair, id;
    private OfferType type;
    private boolean botInfo, old;

    public MCUserGetMessageOffersRequest() {
    }
    
    public MCUserGetMessageOffersRequest(String userName, String pair, String id, OfferType type, boolean botInfo, boolean old) {
        this.userName = userName;
        this.pair = pair;
        this.id = id;
        this.type = type;
        this.botInfo = botInfo;
        this.old = old;
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

    public boolean isBotInfo() {
        return botInfo;
    }

    public void setBotInfo(boolean botInfo) {
        this.botInfo = botInfo;
    }

    public boolean isOld() {
        return old;
    }

    public void setOld(boolean old) {
        this.old = old;
    }
        
}
