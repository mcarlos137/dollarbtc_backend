/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;

/**
 *
 * @author CarlosDaniel
 */
public class JsonNodeResponse implements Serializable, Cloneable {
    
    private final JsonNode result;

    public JsonNodeResponse(JsonNode result) {
        this.result = result;
    }

    public JsonNode getResult() {
        return result;
    }
        
}
