/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.address;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class AddressCreateRequest implements Serializable, Cloneable {
    
    private String currency, address, privateKey, otcMasterAccount, wif;
    
    public AddressCreateRequest() {
    }

    public AddressCreateRequest(String currency, String address, String privateKey, String otcMasterAccount, String wif) {
        this.currency = currency;
        this.address = address;
        this.privateKey = privateKey;
        this.otcMasterAccount = otcMasterAccount;
        this.wif = wif;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getOtcMasterAccount() {
        return otcMasterAccount;
    }

    public void setOtcMasterAccount(String otcMasterAccount) {
        this.otcMasterAccount = otcMasterAccount;
    }    

    public String getWif() {
        return wif;
    }

    public void setWif(String wif) {
        this.wif = wif;
    }    
                            
}
