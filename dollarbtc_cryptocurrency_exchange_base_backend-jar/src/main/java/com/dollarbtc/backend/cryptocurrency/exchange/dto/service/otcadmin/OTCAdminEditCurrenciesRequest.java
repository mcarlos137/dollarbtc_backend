/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCAdminEditCurrenciesRequest implements Serializable, Cloneable {

    private String userName;
    private ArrayNode currencies;

    public OTCAdminEditCurrenciesRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayNode getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayNode currencies) {
        this.currencies = currencies;
    }    
    
}
