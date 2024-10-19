/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetMessageOffersByUserName extends AbstractOperation<ArrayNode> {

    private final String userName, pair;
    private final boolean old;
    private final OfferType offerType;

    public MCUserGetMessageOffersByUserName(String userName, boolean old, String pair, OfferType offerType) {
        super(ArrayNode.class);
        this.userName = userName;
        this.old = old;
        this.pair = pair;
        this.offerType = offerType;
    }

    @Override
    protected void execute() {
        ArrayNode messageOffers = mapper.createArrayNode();
        File messageOffersUserNameFile = MoneyclickFolderLocator.getMessageOffersUserNameFile(userName);
        if (messageOffersUserNameFile.isFile()) {
            try {
                JsonNode messageOffersUserName = mapper.readTree(messageOffersUserNameFile);
                if(messageOffersUserName == null){
                    super.response = messageOffers;
                    return;
                }
                Iterator<String> messageOffersUserNameFieldNames = messageOffersUserName.fieldNames();
                while (messageOffersUserNameFieldNames.hasNext()) {
                    String id = messageOffersUserNameFieldNames.next();
                    String pairr = messageOffersUserName.get(id).get("pair").textValue();
                    OfferType offerTypee = OfferType.valueOf(messageOffersUserName.get(id).get("type").textValue());
                    if(this.pair != null && !this.pair.equals(pairr)){
                        continue;
                    }
                    if(this.offerType != null && !this.offerType.equals(offerTypee)){
                        continue;
                    }
                    File moneyclickMessageOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdFile(pair, offerType.name(), id);
                    if(old){
                        moneyclickMessageOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdOldFile(pair, offerType.name(), id);
                    }
                    if (!moneyclickMessageOfferPairTypeIdFile.isFile()) {
                        continue;
                    }
                    try {
                        messageOffers.add(mapper.readTree(moneyclickMessageOfferPairTypeIdFile));
                    } catch (IOException ex) {
                        Logger.getLogger(MCUserGetMessageOffersByUserName.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MCUserGetMessageOffersByUserName.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = messageOffers;
    }

}
