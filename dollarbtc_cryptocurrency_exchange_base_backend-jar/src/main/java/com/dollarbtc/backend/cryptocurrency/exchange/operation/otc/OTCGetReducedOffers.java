/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class OTCGetReducedOffers extends AbstractOperation<ArrayNode> {

    public OTCGetReducedOffers() {
        super(ArrayNode.class);
    }

    @Override
    protected void execute() {
        ArrayNode reducedOffers = mapper.createArrayNode();
        File otcFolder = OTCFolderLocator.getFolder("MAIN");
        for (File otcCurrencyFolder : otcFolder.listFiles()) {
            if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                continue;
            }
            Set<OfferType> addedCurrentOperator = new HashSet<>();
            String currency = otcCurrencyFolder.getName();
            Iterator<JsonNode> offersIterator = new OTCGetOffers(currency, null, null, null, false).getResponse().iterator();
            while (offersIterator.hasNext()) {
                JsonNode offersIt = offersIterator.next();
                Iterator<JsonNode> offersItIterator = offersIt.iterator();
                JsonNode offer = mapper.createObjectNode();
                while (offersItIterator.hasNext()) {
                    JsonNode offersItIt = offersItIterator.next();
                    String paymentId = offersItIt.get("paymentId").textValue();
                    if (paymentId.equals("MONEYCLICK") || paymentId.equals("DOLLARBTC")) {
                        continue;
                    }
                    OfferType offerType = OfferType.valueOf(offersItIt.get("offerType").textValue());
                    Double price = offersItIt.get("price").doubleValue();
                    if (!offer.has(offerType.name().toLowerCase() + "Price")) {
                        ((ObjectNode) offer).put(offerType.name().toLowerCase() + "Price", price);
                    } else {
                        Double pricee = offer.get(offerType.name().toLowerCase() + "Price").doubleValue();
                        boolean sameOperator = false;
                        if (offersItIt.has("sameOperator") && offersItIt.get("sameOperator").booleanValue()) {
                            sameOperator = true;
                        }
                        if (sameOperator) {
                            ((ObjectNode) offer).put(offerType.name().toLowerCase() + "Price", price);
                            addedCurrentOperator.add(offerType);
                        }
                        if (addedCurrentOperator.contains(offerType) && !sameOperator) {
                            continue;
                        }
                        switch (offerType) {
                            case ASK:
                                if (price < pricee) {
                                    ((ObjectNode) offer).put(offerType.name().toLowerCase() + "Price", price);
                                }
                                break;
                            case BID:
                                if (price > pricee) {
                                    ((ObjectNode) offer).put(offerType.name().toLowerCase() + "Price", price);
                                }
                                break;
                        }
                    }
                }
                if (offer.size() == 0) {
                    continue;
                }
                ((ObjectNode) offer).put("currency", currency);
                reducedOffers.add(offer);
            }
        }
        super.response = reducedOffers;
    }

}
