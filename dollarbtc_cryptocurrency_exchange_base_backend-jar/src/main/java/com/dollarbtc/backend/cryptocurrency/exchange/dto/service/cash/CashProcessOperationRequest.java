/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class CashProcessOperationRequest implements Serializable, Cloneable {
    
    private String userName, operationId, canceledReason;
    private CashOperationStatus cashOperationStatus;
    private boolean cash;

    public CashProcessOperationRequest() {
    }
    
    public CashProcessOperationRequest(String userName, String operationId, CashOperationStatus cashOperationStatus, boolean cash) {
        this.userName = userName;
        this.operationId = operationId;
        this.cashOperationStatus = cashOperationStatus;
        this.cash = cash;
    }
        
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public CashOperationStatus getCashOperationStatus() {
        return cashOperationStatus;
    }

    public void setCashOperationStatus(CashOperationStatus cashOperationStatus) {
        this.cashOperationStatus = cashOperationStatus;
    }
    
    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
    }

    public String getCanceledReason() {
        return canceledReason;
    }

    public void setCanceledReason(String canceledReason) {
        this.canceledReason = canceledReason;
    }
                                    
}
