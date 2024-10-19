/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
public class ServiceUtil {

    public static JsonNode createWSResponse(JsonNode jsonNode, String method, String resultTag) {
        JsonNode response = new ObjectMapper().createObjectNode();
        ((ObjectNode) response).put("jsonrpc", "2.0");
        if(method != null){
            ((ObjectNode) response).put("method", method);
        }
        if(resultTag == null || resultTag.equals("")){
            resultTag = "params";
        }
        ((ObjectNode) response).put(resultTag, jsonNode);
        return response;
    }

    public static JsonNode createWSResponseWithData(ArrayNode arrayNode, String method, String resultTag, Map<String, String> otherResultParams) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.createObjectNode();
        JsonNode result = mapper.createObjectNode();
        ((ObjectNode) response).put("jsonrpc", "2.0");
        if(method != null){
            ((ObjectNode) response).put("method", method);
        }
        if(resultTag == null || resultTag.equals("")){
            resultTag = "result";
        }
        ((ObjectNode) result).putArray("data").addAll(arrayNode);
        for(String key : otherResultParams.keySet()){
            ((ObjectNode) result).put(key, otherResultParams.get(key));
        }
        ((ObjectNode) response).put(resultTag, result);
        return response;
    }
    
    public static JsonNode createWSResponseWithData(JsonNode jsonNode, String method, String resultTag, Map<String, String> otherResultParams) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.createObjectNode();
        JsonNode result = mapper.createObjectNode();
        ((ObjectNode) response).put("jsonrpc", "2.0");
        if(method != null){
            ((ObjectNode) response).put("method", method);
        }
        if(resultTag == null || resultTag.equals("")){
            resultTag = "result";
        }
        ((ObjectNode) result).put("data", jsonNode);
        for(String key : otherResultParams.keySet()){
            ((ObjectNode) result).put(key, otherResultParams.get(key));
        }
        ((ObjectNode) response).put(resultTag, result);
        return response;
    }
    
}
