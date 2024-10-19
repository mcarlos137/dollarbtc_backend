/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserAddMasterWalletIdsRequest implements Serializable, Cloneable {
    
    private String userName;
    private Map<String, String> masterWalletIds = new HashMap<>();
    
    public UserAddMasterWalletIdsRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, String> getMasterWalletIds() {
        return masterWalletIds;
    }

    public void setMasterWalletIds(Map<String, String> masterWalletIds) {
        this.masterWalletIds = masterWalletIds;
    }
                
}
