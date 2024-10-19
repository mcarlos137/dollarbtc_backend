/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserTransferBTCRequest implements Serializable, Cloneable {
        
    private String userName;
    private Double amount;
    private BalanceOperationType balanceOperationType;
    
    public UserTransferBTCRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public BalanceOperationType getBalanceOperationType() {
        return balanceOperationType;
    }

    public void setBalanceOperationType(BalanceOperationType balanceOperationType) {
        this.balanceOperationType = balanceOperationType;
    }
            
}
