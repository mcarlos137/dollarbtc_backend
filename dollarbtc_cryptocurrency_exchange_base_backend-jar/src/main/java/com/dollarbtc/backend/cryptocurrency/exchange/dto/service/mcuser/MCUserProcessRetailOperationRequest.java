/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCUserProcessRetailOperationRequest implements Serializable, Cloneable {
    
    private String userName, currency, operationId;
    private MCRetailOperationStatus mcRetailOperationStatus;
    
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
                                
}
