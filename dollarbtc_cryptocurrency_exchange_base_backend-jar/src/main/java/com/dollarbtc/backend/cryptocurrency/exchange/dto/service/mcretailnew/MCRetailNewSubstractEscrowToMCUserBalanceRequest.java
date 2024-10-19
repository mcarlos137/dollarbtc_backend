/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCRetailNewSubstractEscrowToMCUserBalanceRequest implements Serializable, Cloneable {

    private String userName, currency, retailId;
    private Double amount;
    
    public MCRetailNewSubstractEscrowToMCUserBalanceRequest() {
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
    
    public String getRetailId() {
        return retailId;
    }

    public void setRetailId(String retailId) {
        this.retailId = retailId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
            
}
