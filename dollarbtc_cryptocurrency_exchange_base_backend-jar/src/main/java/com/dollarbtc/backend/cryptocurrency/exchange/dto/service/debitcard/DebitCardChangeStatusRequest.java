/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.DebitCardStatus;

/**
 *
 * @author carlosmolina
 */
public class DebitCardChangeStatusRequest {
    
    private String id;
    private DebitCardStatus debitCardStatus;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DebitCardStatus getDebitCardStatus() {
        return debitCardStatus;
    }

    public void setDebitCardStatus(DebitCardStatus debitCardStatus) {
        this.debitCardStatus = debitCardStatus;
    }
                    
}
