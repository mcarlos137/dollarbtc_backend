/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCChangeOperationStatusRequest implements Serializable, Cloneable {

    private String id, paymentId;
    private OTCOperationStatus otcOperationStatus;
    private Double price;
    private boolean userChange;
    private String canceledReason;

    public OTCChangeOperationStatusRequest() {
    }

    public OTCChangeOperationStatusRequest(String id, OTCOperationStatus otcOperationStatus, boolean userChange) {
        this.id = id;
        this.otcOperationStatus = otcOperationStatus;
        this.userChange = userChange;
    }    
    
    public OTCChangeOperationStatusRequest(String id, OTCOperationStatus otcOperationStatus, String paymentId) {
        this.id = id;
        this.otcOperationStatus = otcOperationStatus;
        this.paymentId = paymentId;
    } 
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public OTCOperationStatus getOtcOperationStatus() {
        return otcOperationStatus;
    }

    public void setOtcOperationStatus(OTCOperationStatus otcOperationStatus) {
        this.otcOperationStatus = otcOperationStatus;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isUserChange() {
        return userChange;
    }

    public void setUserChange(boolean userChange) {
        this.userChange = userChange;
    }

    public String getCanceledReason() {
        return canceledReason;
    }

    public void setCanceledReason(String canceledReason) {
        this.canceledReason = canceledReason;
    }

}
