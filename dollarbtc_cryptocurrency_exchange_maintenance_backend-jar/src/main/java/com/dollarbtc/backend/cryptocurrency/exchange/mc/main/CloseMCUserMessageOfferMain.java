/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserCloseMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserCloseMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class CloseMCUserMessageOfferMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Starting CloseMCUserMessageOfferMain");
        String currentTimestamp = DateUtil.getCurrentDate();
        File moneyclickMessageOffersFolder = MoneyclickFolderLocator.getMessageOffersFolder();
        for (File moneyclickMessageOfferPairFolder : moneyclickMessageOffersFolder.listFiles()) {
            if (!moneyclickMessageOfferPairFolder.isDirectory()) {
                continue;
            }
            System.out.println(moneyclickMessageOfferPairFolder.getName());
            for (File moneyclickMessageOfferPairTypeFolder : moneyclickMessageOfferPairFolder.listFiles()) {
                if (!moneyclickMessageOfferPairTypeFolder.isDirectory()) {
                    continue;
                }
                System.out.println(moneyclickMessageOfferPairTypeFolder.getName());
                for (File messageOfferFile : moneyclickMessageOfferPairTypeFolder.listFiles()) {
                    if (!messageOfferFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode messageOffer = mapper.readTree(messageOfferFile);
                        if(messageOffer == null){
                            continue;
                        }
                        String id = messageOffer.get("id").textValue();
                        String userName = messageOffer.get("postUserName").textValue();
                        String timestamp = messageOffer.get("timestamp").textValue();
                        String pair = messageOffer.get("pair").textValue();
                        OfferType offerType = OfferType.valueOf(messageOffer.get("type").textValue());
                        Integer time = messageOffer.get("time").intValue();
                        String timeUnit = messageOffer.get("timeUnit").textValue();
                        String beforeTimestamp = null;
                        switch(timeUnit){
                            case "MINUTES":
                                beforeTimestamp = DateUtil.getDateMinutesBefore(currentTimestamp, time);
                                break;
                            case "HOURS":
                                beforeTimestamp = DateUtil.getDateHoursBefore(currentTimestamp, time);
                                break;
                            case "DAYS":
                                beforeTimestamp = DateUtil.getDateDaysBefore(currentTimestamp, time);
                                break;
                        }
                        System.out.println("beforeTimestamp " + beforeTimestamp);
                        System.out.println("timestamp " + timestamp);
                        if(beforeTimestamp != null && beforeTimestamp.compareTo(timestamp) > 0){
                            System.out.println("CLOSING MESSAGE beforeTimestamp " + id);
                            new MCUserCloseMessageOffer(new MCUserCloseMessageOfferRequest(userName, pair, id, offerType, true)).getResponse();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(CloseMCUserMessageOfferMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.out.println("Finishing CloseMCUserMessageOfferMain");
    }

}
