/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class TransferToBankCreateProcessRequest implements Serializable, Cloneable {
    
    private String userName, currency;
    private Set<String> ids;

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
    
    public Set<String> getIds() {
        return ids;
    }

    public void setIds(Set<String> ids) {
        this.ids = ids;
    }
    
}
