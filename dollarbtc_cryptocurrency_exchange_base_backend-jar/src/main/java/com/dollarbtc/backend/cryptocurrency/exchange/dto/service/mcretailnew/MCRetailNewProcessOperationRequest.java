/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCRetailNewProcessOperationRequest implements Serializable, Cloneable {
    
    private String userName, operationId, canceledReason;
    private MCRetailOperationStatus mcRetailOperationStatus;
    private boolean cash;

    public MCRetailNewProcessOperationRequest() {
    }
    
    public MCRetailNewProcessOperationRequest(String userName, String operationId, MCRetailOperationStatus mcRetailOperationStatus, boolean cash) {
        this.userName = userName;
        this.operationId = operationId;
        this.mcRetailOperationStatus = mcRetailOperationStatus;
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

    public MCRetailOperationStatus getMcRetailOperationStatus() {
        return mcRetailOperationStatus;
    }

    public void setMcRetailOperationStatus(MCRetailOperationStatus mcRetailOperationStatus) {
        this.mcRetailOperationStatus = mcRetailOperationStatus;
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
