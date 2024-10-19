/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashCreatePlaceStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class CashChangeCreatePlaceStatusRequest implements Serializable, Cloneable {

    private String placeId;
    private CashCreatePlaceStatus cashCreatePlaceStatus;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public CashCreatePlaceStatus getCashCreatePlaceStatus() {
        return cashCreatePlaceStatus;
    }

    public void setCashCreatePlaceStatus(CashCreatePlaceStatus cashCreatePlaceStatus) {
        this.cashCreatePlaceStatus = cashCreatePlaceStatus;
    }
    
}
