/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCUserCreateRequest implements Serializable, Cloneable {
    
    private String userName;
    private Map<String, Double> amounts = new HashMap<>();
    private Map<String, String> masterWalletIds = new HashMap<>();
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(Map<String, Double> amounts) {
        this.amounts = amounts;
    }

    public Map<String, String> getMasterWalletIds() {
        return masterWalletIds;
    }

    public void setMasterWalletIds(Map<String, String> masterWalletIds) {
        this.masterWalletIds = masterWalletIds;
    }
                        
}
