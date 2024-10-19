/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserProcessBalanceMovementRequest implements Serializable, Cloneable {
        
    private String userName, balanceOperationProcessId, adminMessage, baseAddress, txId;
    private BalanceOperationStatus balanceOperationStatus;
    
    public UserProcessBalanceMovementRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBalanceOperationProcessId() {
        return balanceOperationProcessId;
    }

    public void setBalanceOperationProcessId(String balanceOperationProcessId) {
        this.balanceOperationProcessId = balanceOperationProcessId;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }
    
    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public BalanceOperationStatus getBalanceOperationStatus() {
        return balanceOperationStatus;
    }

    public void setBalanceOperationStatus(BalanceOperationStatus balanceOperationStatus) {
        this.balanceOperationStatus = balanceOperationStatus;
    }

}
