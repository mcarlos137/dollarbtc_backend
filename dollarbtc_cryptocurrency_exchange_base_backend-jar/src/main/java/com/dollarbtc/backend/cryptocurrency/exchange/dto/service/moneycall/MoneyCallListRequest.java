/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MoneyCallListRequest implements Serializable, Cloneable {

    private String initTimestamp, finalTimestamp;
    private String[] senderUserNames, receiverUserNames, currencies, rates, times, stars, statuses;

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

    public String[] getSenderUserNames() {
        return senderUserNames;
    }

    public void setSenderUserNames(String[] senderUserNames) {
        this.senderUserNames = senderUserNames;
    }

    public String[] getReceiverUserNames() {
        return receiverUserNames;
    }

    public void setReceiverUserNames(String[] receiverUserNames) {
        this.receiverUserNames = receiverUserNames;
    }

    public String[] getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String[] currencies) {
        this.currencies = currencies;
    }

    public String[] getRates() {
        return rates;
    }

    public void setRates(String[] rates) {
        this.rates = rates;
    }

    public String[] getTimes() {
        return times;
    }

    public void setTimes(String[] times) {
        this.times = times;
    }

    public String[] getStars() {
        return stars;
    }

    public void setStars(String[] stars) {
        this.stars = stars;
    }

    public String[] getStatuses() {
        return statuses;
    }

    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }
    
}
