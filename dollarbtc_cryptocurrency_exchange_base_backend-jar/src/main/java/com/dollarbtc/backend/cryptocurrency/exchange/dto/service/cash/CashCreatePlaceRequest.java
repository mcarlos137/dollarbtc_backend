/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class CashCreatePlaceRequest implements Serializable, Cloneable {

    private String title, description, userName, email, creationTimestamp;
    private Double latitude, longitude;
    private boolean onlyForMap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isOnlyForMap() {
        return onlyForMap;
    }

    public void setOnlyForMap(boolean onlyForMap) {
        this.onlyForMap = onlyForMap;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
    
    public JsonNode toJsonNode(JsonNode jsonNode) {
        ((ObjectNode) jsonNode).put("title", this.title);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("email", this.email);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode coordinate = mapper.createObjectNode();
        ((ObjectNode) coordinate).put("latitude", this.latitude);
        ((ObjectNode) coordinate).put("longitude", this.longitude);
        ((ObjectNode) jsonNode).set("coordinate", coordinate);
        ((ObjectNode) jsonNode).putArray("escrowLimits").addAll(mapper.createArrayNode());
        ((ObjectNode) jsonNode).putArray("currencies").addAll(mapper.createArrayNode());
        ((ObjectNode) jsonNode).putArray("operations").addAll(mapper.createArrayNode());
        ((ObjectNode) jsonNode).putArray("attachments").addAll(mapper.createArrayNode());
        ((ObjectNode) jsonNode).put("onlyForMap", this.onlyForMap);
        if(this.creationTimestamp != null){
            ((ObjectNode) jsonNode).put("creationTimestamp", this.creationTimestamp);
        }
        return jsonNode;
    }

}
