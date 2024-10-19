/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class CashGetOperationsRequest implements Serializable, Cloneable {

    private String userName, currency, placeId;
    private CashOperationType cashOperationType;
    private CashOperationStatus CashOperationStatus;

    public CashGetOperationsRequest() {
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public CashOperationType getCashOperationType() {
        return cashOperationType;
    }

    public void setCashOperationType(CashOperationType cashOperationType) {
        this.cashOperationType = cashOperationType;
    }

    public CashOperationStatus getCashOperationStatus() {
        return CashOperationStatus;
    }

    public void setCashOperationStatus(CashOperationStatus CashOperationStatus) {
        this.CashOperationStatus = CashOperationStatus;
    }
        
}
