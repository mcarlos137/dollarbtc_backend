/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts;

/**
 *
 * @author CarlosDaniel
 */
public class ShortsViewRequest {
        
    private String id, userName, name;

    public ShortsViewRequest() {
    }

    public ShortsViewRequest(String id, String userName, String name) {
        this.id = id;
        this.userName = userName;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
}
