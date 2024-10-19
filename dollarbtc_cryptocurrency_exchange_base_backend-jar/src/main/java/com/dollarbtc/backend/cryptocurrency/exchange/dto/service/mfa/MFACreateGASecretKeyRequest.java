/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MFACreateGASecretKeyRequest implements Serializable, Cloneable {
    
    private String userName;
    
    public MFACreateGASecretKeyRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
                    
}
