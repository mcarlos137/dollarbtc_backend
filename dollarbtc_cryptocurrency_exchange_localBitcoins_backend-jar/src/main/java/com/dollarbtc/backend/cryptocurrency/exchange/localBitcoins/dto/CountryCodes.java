/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
        
/**
 *
 * @author CarlosDaniel
 */
public class CountryCodes {
    
    private final List<String> codes = new ArrayList<>();

    public CountryCodes(JsonNode jsonNode) {
        ArrayNode arrayNode = (ArrayNode) jsonNode.get("data").get("cc_list");
        Iterator<JsonNode> arrayNodeIterator = arrayNode.elements();
        while(arrayNodeIterator.hasNext()){
            JsonNode arrayNodeIt = arrayNodeIterator.next();
            codes.add(arrayNodeIt.textValue());
        }
    }

    public List<String> getCodes() {
        return codes;
    }
        
}
