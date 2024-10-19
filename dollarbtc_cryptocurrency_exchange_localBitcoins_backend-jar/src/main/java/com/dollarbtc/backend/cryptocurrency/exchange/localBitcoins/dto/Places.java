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
public class Places {
    
    private final List<Place> places = new ArrayList<>();

    public Places(JsonNode jsonNode) {
        ArrayNode arrayNode = (ArrayNode) jsonNode.get("data").get("places");
        Iterator<JsonNode> arrayNodeIterator = arrayNode.elements();
        while(arrayNodeIterator.hasNext()){
            JsonNode arrayNodeIt = arrayNodeIterator.next();
            places.add(new Place(
                    arrayNodeIt.get("sell_local_url").textValue(), 
                    arrayNodeIt.get("location_string").textValue(), 
                    arrayNodeIt.get("url").textValue(), 
                    arrayNodeIt.get("buy_local_url").textValue(), 
                    arrayNodeIt.get("lon").intValue(), 
                    arrayNodeIt.get("lat").intValue())
            );
        }
    }

    public List<Place> getPlaces() {
        return places;
    }
    
    public static class Place {
        
        private final String sell_local_url, location_string, url, buy_local_url;
        private final Integer lon, lat;

        public Place(String sell_local_url, String location_string, String url, String buy_local_url, Integer lon, Integer lat) {
            this.sell_local_url = sell_local_url;
            this.location_string = location_string;
            this.url = url;
            this.buy_local_url = buy_local_url;
            this.lon = lon;
            this.lat = lat;
        }

        public String getSell_local_url() {
            return sell_local_url;
        }

        public String getLocation_string() {
            return location_string;
        }

        public String getUrl() {
            return url;
        }

        public String getBuy_local_url() {
            return buy_local_url;
        }

        public Integer getLon() {
            return lon;
        }

        public Integer getLat() {
            return lat;
        }        
        
    }
        
}
