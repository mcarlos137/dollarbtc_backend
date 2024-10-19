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
public class OTCAdminEditDollarBTCPaymentCommissionsRequest implements Serializable, Cloneable {

    private String userName, currency, id;
    private Float mcBuyBalancePercent, sendToPaymentPercent;

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

    public Float getMcBuyBalancePercent() {
        return mcBuyBalancePercent;
    }

    public void setMcBuyBalancePercent(Float mcBuyBalancePercent) {
        this.mcBuyBalancePercent = mcBuyBalancePercent;
    }

    public Float getSendToPaymentPercent() {
        return sendToPaymentPercent;
    }

    public void setSendToPaymentPercent(Float sendToPaymentPercent) {
        this.sendToPaymentPercent = sendToPaymentPercent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
            
}
