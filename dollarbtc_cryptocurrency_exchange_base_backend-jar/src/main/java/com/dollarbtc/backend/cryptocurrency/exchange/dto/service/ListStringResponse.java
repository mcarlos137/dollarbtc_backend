/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class ListStringResponse implements Serializable {
    
    private final List<String> result;   

    public ListStringResponse(List<String> result) {
        this.result = result;
    }

    public List<String> getResult() {
        return result;
    }
        
}
