/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserGetMessageOffersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetMessageOffers extends AbstractOperation<JsonNode> {

    private final MCUserGetMessageOffersRequest mcUserGetMessageOffersRequest;

    public MCUserGetMessageOffers(MCUserGetMessageOffersRequest mcUserGetMessageOffersRequest) {
        super(JsonNode.class);
        this.mcUserGetMessageOffersRequest = mcUserGetMessageOffersRequest;
    }

    @Override
    protected void execute() {
        JsonNode messageOffers = mapper.createObjectNode();
        if (mcUserGetMessageOffersRequest.getUserName() != null) {
            File moneyclickMessageOfferUserNameFile = MoneyclickFolderLocator.getMessageOffersUserNameFile(mcUserGetMessageOffersRequest.getUserName());
            try {
                JsonNode moneyclickMessageOfferUserName = mapper.readTree(moneyclickMessageOfferUserNameFile);
                Iterator<String> moneyclickMessageOfferUserNameIterator = moneyclickMessageOfferUserName.fieldNames();
                while (moneyclickMessageOfferUserNameIterator.hasNext()) {
                    String id = moneyclickMessageOfferUserNameIterator.next();
                    String timestamp = moneyclickMessageOfferUserName.get(id).get("timestamp").textValue();
                    String pair = moneyclickMessageOfferUserName.get(id).get("pair").textValue();
                    OfferType offerType = OfferType.valueOf(moneyclickMessageOfferUserName.get(id).get("type").textValue());
                    if (mcUserGetMessageOffersRequest.getId() != null && !mcUserGetMessageOffersRequest.getId().equals(id)) {
                        continue;
                    }
                    if (mcUserGetMessageOffersRequest.getPair() != null && !mcUserGetMessageOffersRequest.getPair().equals(pair)) {
                        continue;
                    }
                    if (mcUserGetMessageOffersRequest.getType() != null && !mcUserGetMessageOffersRequest.getType().equals(offerType)) {
                        continue;
                    }
                    File moneyclickMessageOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdFile(pair, offerType.name(), id);
                    if(mcUserGetMessageOffersRequest.isOld()){
                        moneyclickMessageOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdOldFile(pair, offerType.name(), id);
                    }
                    if (!moneyclickMessageOfferPairTypeIdFile.isFile()) {
                        continue;
                    } 
                    if (!messageOffers.has(offerType.name())) {
                        ((ObjectNode) messageOffers).set(offerType.name(), mapper.createArrayNode());
                    }
                    ((ArrayNode) messageOffers.get(offerType.name())).add(mapper.readTree(moneyclickMessageOfferPairTypeIdFile));
                }
            } catch (IOException ex) {
                Logger.getLogger(MCUserGetMessageOffers.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (mcUserGetMessageOffersRequest.getUserName() == null && mcUserGetMessageOffersRequest.getPair() != null && mcUserGetMessageOffersRequest.getType() != null && mcUserGetMessageOffersRequest.getId() != null) {
            File moneyclickMessageOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdFile(mcUserGetMessageOffersRequest.getPair(), mcUserGetMessageOffersRequest.getType().name(), mcUserGetMessageOffersRequest.getId());
            try {
                ((ObjectNode) messageOffers).set(mcUserGetMessageOffersRequest.getType().name(), mapper.createObjectNode());
                ((ObjectNode) messageOffers.get(mcUserGetMessageOffersRequest.getType().name())).set(mcUserGetMessageOffersRequest.getId(), mapper.readTree(moneyclickMessageOfferPairTypeIdFile));
            } catch (IOException ex) {
                Logger.getLogger(MCUserGetMessageOffers.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            for (OfferType offerType : OfferType.values()) {
                if (mcUserGetMessageOffersRequest.getType() != null && !mcUserGetMessageOffersRequest.getType().equals(offerType)) {
                    continue;
                }
                File moneyclickMessageOfferPairTypeFolder = MoneyclickFolderLocator.getMessageOfferPairTypeFolder(mcUserGetMessageOffersRequest.getPair(), offerType.name());
                Map<Double, ArrayNode> messageOffersMap = new TreeMap<>();
                if (offerType.equals(OfferType.BID)) {
                    messageOffersMap = new TreeMap<>(Collections.reverseOrder());
                }
                for (File moneyclickMessageOfferPairTypeIdFile : moneyclickMessageOfferPairTypeFolder.listFiles()) {
                    if (!moneyclickMessageOfferPairTypeIdFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode messageOffer = mapper.readTree(moneyclickMessageOfferPairTypeIdFile);
                        if (messageOffer == null) {
                            continue;
                        }
                        Double price = messageOffer.get("price").doubleValue();
                        if (!messageOffersMap.containsKey(price)) {
                            messageOffersMap.put(price, mapper.createArrayNode());
                        }
                        if (!mcUserGetMessageOffersRequest.isBotInfo()) {
                            ((ObjectNode) messageOffer).remove("bot");
                        }
                        messageOffersMap.get(price).add(messageOffer);
                    } catch (IOException ex) {
                        Logger.getLogger(MCUserGetMessageOffers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for (Double key : messageOffersMap.keySet()) {
                    if (!messageOffers.has(offerType.name())) {
                        ((ObjectNode) messageOffers).set(offerType.name(), mapper.createObjectNode());
                    }
                    ((ObjectNode) messageOffers.get(offerType.name())).set(Double.toString(key), messageOffersMap.get(key));
                }
            }
        }
        super.response = messageOffers;
    }

}
