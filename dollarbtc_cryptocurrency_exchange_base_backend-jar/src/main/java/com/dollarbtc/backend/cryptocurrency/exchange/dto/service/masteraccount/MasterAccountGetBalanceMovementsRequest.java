/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MasterAccountGetBalanceMovementsRequest implements Serializable, Cloneable {

    private String masterAccountName, initTimestamp, endTimestamp;
    private BalanceOperationType balanceOperationType;
    
    public MasterAccountGetBalanceMovementsRequest() {
    }

    public String getMasterAccountName() {
        return masterAccountName;
    }

    public void setMasterAccountName(String masterAccountName) {
        this.masterAccountName = masterAccountName;
    }
        
    public String getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(String initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public BalanceOperationType getBalanceOperationType() {
        return balanceOperationType;
    }

    public void setBalanceOperationType(BalanceOperationType balanceOperationType) {
        this.balanceOperationType = balanceOperationType;
    }
        
}
