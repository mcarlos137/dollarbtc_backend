/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserAddWalletRequest implements Serializable, Cloneable {
    
    private String userName, blockchain, address, privateKey;
    private boolean moneyClick;
    
    public UserAddWalletRequest() {
    }

    public UserAddWalletRequest(String userName, String blockchain, String address, String privateKey, boolean moneyClick) {
        this.userName = userName;
        this.blockchain = blockchain;
        this.address = address;
        this.privateKey = privateKey;
        this.moneyClick = moneyClick;
    }    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public boolean isMoneyClick() {
        return moneyClick;
    }

    public void setMoneyClick(boolean moneyClick) {
        this.moneyClick = moneyClick;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }
    
}
