/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank;

import java.io.Serializable;


/**
 *
 * @author carlosmolina
 */
public class TransferToBankChangeProcessStatusRequest implements Serializable, Cloneable {
    
    private String userName, id;
    private String status; //PENDING, FAILED, OK
    private String dollarBTCPaymentId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDollarBTCPaymentId() {
        return dollarBTCPaymentId;
    }

    public void setDollarBTCPaymentId(String dollarBTCPaymentId) {
        this.dollarBTCPaymentId = dollarBTCPaymentId;
    }
    
}
