/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;

/**
 *
 * @author CarlosDaniel
 */
public class MoneyMarketGetOrdersRequest {
    
    private String userName, pair, id;
    private OrderType[] types;
    private boolean botInfo, old;

    public MoneyMarketGetOrdersRequest() {
    }
    
    public MoneyMarketGetOrdersRequest(String userName) {
        this.userName = userName;
    }
    
    public MoneyMarketGetOrdersRequest(String pair, OrderType[] types) {
        this.pair = pair;
        this.types = types;
    }
    
    public MoneyMarketGetOrdersRequest(String userName, String pair, String id, OrderType[] types, boolean botInfo, boolean old) {
        this.userName = userName;
        this.pair = pair;
        this.id = id;
        this.types = types;
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

    public OrderType[] getTypes() {
        return types;
    }

    public void setTypes(OrderType[] types) {
        this.types = types;
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
