/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import java.io.Serializable;

/**
 *
 * @author CarlosDaniel
 */
public class StringResponse implements Serializable {
    
    private final String result;   

    public StringResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
        
}
