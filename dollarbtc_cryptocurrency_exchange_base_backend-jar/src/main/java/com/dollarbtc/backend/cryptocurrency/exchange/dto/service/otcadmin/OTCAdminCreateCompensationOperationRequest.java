/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCAdminCreateCompensationOperationRequest implements Serializable, Cloneable {

    private String currency, id, additionalInfo, masterAccountName;
    private Double amount, masterAccountAmount;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMasterAccountName() {
        return masterAccountName;
    }

    public void setMasterAccountName(String masterAccountName) {
        this.masterAccountName = masterAccountName;
    }

    public Double getMasterAccountAmount() {
        return masterAccountAmount;
    }

    public void setMasterAccountAmount(Double masterAccountAmount) {
        this.masterAccountAmount = masterAccountAmount;
    }
            
}
