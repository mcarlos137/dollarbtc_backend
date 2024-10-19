/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin;

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
public class OTCAdminGetOperationsNewRequest implements Serializable, Cloneable {

    private String userName, initTimestamp, finalTimestamp;
    private String[] operationUserNames, currencies;
    private OTCOperationType[] otcOperationTypes;
    private OTCOperationStatus[] otcOperationStatuses;
    private Map<String, String[]> specialIndexes;

    public OTCAdminGetOperationsNewRequest() {
    }

    public OTCAdminGetOperationsNewRequest(String userName, String initTimestamp, String finalTimestamp, String[] operationUserNames, String[] currencies, OTCOperationType[] otcOperationTypes, OTCOperationStatus[] otcOperationStatuses, Map<String, String[]> specialIndexes) {
        this.userName = userName;
        this.initTimestamp = initTimestamp;
        this.finalTimestamp = finalTimestamp;
        this.operationUserNames = operationUserNames;
        this.currencies = currencies;
        this.otcOperationTypes = otcOperationTypes;
        this.otcOperationStatuses = otcOperationStatuses;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String[] getOperationUserNames() {
        return operationUserNames;
    }

    public void setOperationUserNames(String[] operationUserNames) {
        this.operationUserNames = operationUserNames;
    }

    public String[] getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String[] currencies) {
        this.currencies = currencies;
    }

    public OTCOperationType[] getOtcOperationTypes() {
        return otcOperationTypes;
    }

    public void setOtcOperationTypes(OTCOperationType[] otcOperationTypes) {
        this.otcOperationTypes = otcOperationTypes;
    }

    public OTCOperationStatus[] getOtcOperationStatuses() {
        return otcOperationStatuses;
    }

    public void setOtcOperationStatuses(OTCOperationStatus[] otcOperationStatuses) {
        this.otcOperationStatuses = otcOperationStatuses;
    }

    public Map<String, String[]> getSpecialIndexes() {
        return specialIndexes;
    }

    public void setSpecialIndexes(Map<String, String[]> specialIndexes) {
        this.specialIndexes = specialIndexes;
    }
            
}
