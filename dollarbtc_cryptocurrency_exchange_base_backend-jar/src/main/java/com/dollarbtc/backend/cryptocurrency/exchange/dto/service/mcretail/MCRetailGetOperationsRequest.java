/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCRetailGetOperationsRequest implements Serializable, Cloneable {

    private String userName, currency, retailId;
    private MCRetailOperationType mcRetailOperationType;
    private MCRetailOperationStatus mcRetailOperationStatus;

    public MCRetailGetOperationsRequest() {
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
    
    public MCRetailOperationType getMcRetailOperationType() {
        return mcRetailOperationType;
    }

    public void setMcRetailOperationType(MCRetailOperationType mcRetailOperationType) {
        this.mcRetailOperationType = mcRetailOperationType;
    }

    public MCRetailOperationStatus getMcRetailOperationStatus() {
        return mcRetailOperationStatus;
    }

    public void setMcRetailOperationStatus(MCRetailOperationStatus mcRetailOperationStatus) {
        this.mcRetailOperationStatus = mcRetailOperationStatus;
    }
        
}
