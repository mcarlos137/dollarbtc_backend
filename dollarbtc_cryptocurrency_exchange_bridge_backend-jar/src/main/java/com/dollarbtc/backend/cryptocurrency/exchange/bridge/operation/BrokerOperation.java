/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class BrokerOperation extends BaseOperation {

    public static ArrayNode getAll(OfferType offerType) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode brokerOffersArray = mapper.createArrayNode();
        File brokersFolder = BrokersFolderLocator.getFolder();
        for (File brokerFolder : brokersFolder.listFiles()) {
            if (!brokerFolder.isDirectory()) {
                continue;
            }
            File brokerConfigFile = new File(brokerFolder, "config.json");
            File brokerOffersFile = new File(brokerFolder, "offers.json");
            File brokerStatsFile = new File(brokerFolder, "stats.json");
            if (!brokerConfigFile.isFile() || !brokerOffersFile.isFile() || !brokerStatsFile.isFile()) {
                continue;
            }
            try {
                JsonNode brokerConfig = mapper.readTree(brokerConfigFile);
                JsonNode brokerOffers = mapper.readTree(brokerOffersFile);
                JsonNode brokerStats = mapper.readTree(brokerStatsFile);
                Iterator<String> brokerOffersIterator = brokerOffers.fieldNames();
                while (brokerOffersIterator.hasNext()) {
                    String brokerOffersIt = brokerOffersIterator.next();
                    ObjectNode brokerOffer = mapper.createObjectNode();
                    OfferType ot = OfferType.valueOf(brokerOffersIt.split("__")[1]);
                    if (!ot.equals(offerType)) {
                        continue;
                    }
                    brokerOffer.put("currency", brokerOffersIt.split("__")[0]);
                    brokerOffer.put("name", brokerConfig.get("name").textValue());
                    brokerOffer.put("lastSeenInSeconds", brokerConfig.get("lastSeenInSeconds").intValue());
                    brokerOffer.put("provider", brokerConfig.get("provider").textValue());
                    brokerOffer.put("trust", brokerStats.get("trust").intValue());
                    brokerOffer.put("trades", brokerStats.get("trades").intValue());
                    brokerOffer.put("uniquePartners", brokerStats.get("uniquePartners").intValue());
                    brokerOffer.put("offerType", ot.name());
                    brokerOffer.put("active", brokerOffers.get(brokerOffersIt).get("active").booleanValue());
                    brokerOffer.put("price", brokerOffers.get(brokerOffersIt).get("price").doubleValue());
                    brokerOffer.put("minPerOperationAmount", brokerOffers.get(brokerOffersIt).get("minPerOperationAmount").doubleValue());
                    brokerOffer.put("maxPerOperationAmount", brokerOffers.get(brokerOffersIt).get("maxPerOperationAmount").doubleValue());
                    brokerOffer.put("redirectionURL", brokerOffers.get(brokerOffersIt).get("redirectionURL").textValue());
                    brokerOffersArray.add(brokerOffer);
                }
            } catch (IOException ex) {
                Logger.getLogger(BrokerOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return brokerOffersArray;
    }

}
