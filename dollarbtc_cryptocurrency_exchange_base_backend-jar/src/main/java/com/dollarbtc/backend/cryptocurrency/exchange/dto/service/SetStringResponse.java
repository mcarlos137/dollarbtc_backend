/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author CarlosDaniel
 */
public class SetStringResponse implements Serializable {
    
    private final Set<String> result;   

    public SetStringResponse(Set<String> result) {
        this.result = result;
    }

    public Set<String> getResult() {
        return result;
    }
        
}
