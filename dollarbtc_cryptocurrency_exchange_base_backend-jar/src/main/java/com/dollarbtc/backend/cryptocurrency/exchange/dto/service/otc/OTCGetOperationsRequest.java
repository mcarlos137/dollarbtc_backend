/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCGetOperationsRequest implements Serializable, Cloneable {

    private String userName, currency, brokerUserName, initTimestamp, finalTimestamp;
    private OTCOperationType otcOperationType;
    private OTCOperationStatus otcOperationStatus;
    private Map<String, String> specialIndexes;

    public OTCGetOperationsRequest() {
    }

    public OTCGetOperationsRequest(String userName, String currency, String brokerUserName, OTCOperationType otcOperationType, OTCOperationStatus otcOperationStatus, Map<String, String> specialIndexes) {
        this.userName = userName;
        this.currency = currency;
        this.brokerUserName = brokerUserName;
        this.otcOperationType = otcOperationType;
        this.otcOperationStatus = otcOperationStatus;
        this.specialIndexes = specialIndexes;
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

    public String getBrokerUserName() {
        return brokerUserName;
    }

    public void setBrokerUserName(String brokerUserName) {
        this.brokerUserName = brokerUserName;
    }
    
    public OTCOperationType getOtcOperationType() {
        return otcOperationType;
    }

    public void setOtcOperationType(OTCOperationType otcOperationType) {
        this.otcOperationType = otcOperationType;
    }

    public OTCOperationStatus getOtcOperationStatus() {
        return otcOperationStatus;
    }

    public void setOtcOperationStatus(OTCOperationStatus otcOperationStatus) {
        this.otcOperationStatus = otcOperationStatus;
    }

    public Map<String, String> getSpecialIndexes() {
        return specialIndexes;
    }

    public void setSpecialIndexes(Map<String, String> specialIndexes) {
        this.specialIndexes = specialIndexes;
    }

    public String getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(String initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public String getFinalTimestamp() {
        return finalTimestamp;
    }

    public void setFinalTimestamp(String finalTimestamp) {
        this.finalTimestamp = finalTimestamp;
    }
            
}
