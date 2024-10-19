/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
 @XmlRootElement
public class MCRetailCreateRequest implements Serializable, Cloneable {

    private String id, title, description;
    private Double latitude, longitude;
    private ArrayNode currencies;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public ArrayNode getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayNode currencies) {
        this.currencies = currencies;
    }

    public JsonNode toJsonNode(JsonNode jsonNode) {
        ((ObjectNode) jsonNode).put("id", this.id);
        ((ObjectNode) jsonNode).put("title", this.title);
        ((ObjectNode) jsonNode).put("description", this.description);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode coordinate = mapper.createObjectNode();
        ((ObjectNode) coordinate).put("latitude", this.latitude);
        ((ObjectNode) coordinate).put("longitude", this.longitude);
        ((ObjectNode) jsonNode).put("coordinate", coordinate);
        ArrayNode operations = mapper.createArrayNode();
        Iterator<JsonNode> currenciesIterator = currencies.iterator();
        while (currenciesIterator.hasNext()) {
            JsonNode currenciesIr = currenciesIterator.next();
            for (MCRetailOperationType mcRetailOperationType : MCRetailOperationType.values()) {
                ObjectNode operation = mapper.createObjectNode();
                operation.put("currency", currenciesIr.textValue());
                operation.put("type", mcRetailOperationType.name());
                operations.add(operation);
            }
        }
        ((ObjectNode) jsonNode).putArray("currencies").addAll(currencies);
        ((ObjectNode) jsonNode).putArray("operations").addAll(operations);
        return jsonNode;
    }

}
