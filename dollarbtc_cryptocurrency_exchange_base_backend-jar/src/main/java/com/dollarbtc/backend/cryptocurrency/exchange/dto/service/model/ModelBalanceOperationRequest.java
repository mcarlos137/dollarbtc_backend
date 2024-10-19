/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class ModelBalanceOperationRequest implements Serializable, Cloneable {
    
    private String userModelName;
    private BalanceOperationType balanceOperationType;
    private Map<String, Double> amounts = new HashMap<>();
    
    public ModelBalanceOperationRequest() {
    }

    public String getUserModelName() {
        return userModelName;
    }

    public void setUserModelName(String userModelName) {
        this.userModelName = userModelName;
    }
        
    public BalanceOperationType getBalanceOperationType() {
        return balanceOperationType;
    }

    public void setBalanceOperationType(BalanceOperationType balanceOperationType) {
        this.balanceOperationType = balanceOperationType;
    }
    
    public Map<String, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(Map<String, Double> amounts) {
        this.amounts = amounts;
    }    
            
}
