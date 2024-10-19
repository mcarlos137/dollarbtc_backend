/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccountnew;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MasterAccountNewGetProfitsAndChargesBalanceRequest implements Serializable, Cloneable {

    private String userName, masterAccountName, initTimestamp, finalTimestamp;
    
    public MasterAccountNewGetProfitsAndChargesBalanceRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getFinalTimestamp() {
        return finalTimestamp;
    }

    public void setFinalTimestamp(String finalTimestamp) {
        this.finalTimestamp = finalTimestamp;
    }
    
}
