/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MasterAccountBalanceOperationSendRequest implements Serializable, Cloneable {
    
    private String masterAccountName, targetAddress, additionalInfo;
    private Map<String, Double> amounts = new HashMap<>();

    public String getMasterAccountName() {
        return masterAccountName;
    }

    public void setMasterAccountName(String masterAccountName) {
        this.masterAccountName = masterAccountName;
    }
        
    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
        
    public Map<String, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(Map<String, Double> amounts) {
        this.amounts = amounts;
    }    
                
}
