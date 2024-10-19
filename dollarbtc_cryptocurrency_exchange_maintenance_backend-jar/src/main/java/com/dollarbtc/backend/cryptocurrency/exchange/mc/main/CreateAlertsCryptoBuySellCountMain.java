/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddFlagRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddFlag;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class CreateAlertsCryptoBuySellCountMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.getLogger(CreateAlertsCryptoBuySellCountMain.class.getName()).log(Level.INFO, "Starting CreateAlertsCryptoBuySellCountMain");
        String timestamp = DateUtil.getCurrentDate();
        for (File moneyclickCryptoBuysSellsCountFile : MoneyclickFolderLocator.getCryptoBuysSellsCountFolder().listFiles()) {
            if (!moneyclickCryptoBuysSellsCountFile.isFile()) {
                continue;
            }
            try {
                String userName = moneyclickCryptoBuysSellsCountFile.getName().replace(".file", "");
                Logger.getLogger(CreateAlertsCryptoBuySellCountMain.class.getName()).log(Level.INFO, "userName {0}", userName);
                String moneyclicCryptoBuysSellsCount = Files.readString(moneyclickCryptoBuysSellsCountFile.toPath());
                String flagColor = null;
                if (moneyclicCryptoBuysSellsCount.split("__").length >= 5) {
                    flagColor = "YELLOW";
                }
                if (moneyclicCryptoBuysSellsCount.split("__").length >= 12) {
                    flagColor = "ORANGE";
                }
                if (moneyclicCryptoBuysSellsCount.split("__").length >= 20) {
                    flagColor = "RED";
                }
                if (flagColor == null || flagColor.equals("YELLOW")) {
                    Set<String> lastHourTimestamps = new HashSet<>();
                    for (String moneyclickCryptoBuysSellsCountTimestamp : moneyclicCryptoBuysSellsCount.split("__")) {
                        if (moneyclickCryptoBuysSellsCountTimestamp.equals("")) {
                            continue;
                        }
                        if (DateUtil.parseDate(DateUtil.getDateMinutesBefore(timestamp, 1440)).before(DateUtil.parseDate(moneyclickCryptoBuysSellsCountTimestamp))) {
                            lastHourTimestamps.add(moneyclickCryptoBuysSellsCountTimestamp);
                        }
                    }
                    if (lastHourTimestamps.size() >= 5) {
                        flagColor = "ORANGE";
                    }
                    if (lastHourTimestamps.size() >= 10) {
                        flagColor = "RED";
                    }
                }
                if (flagColor != null) {
                    new UserAddFlag(new UserAddFlagRequest("AUTOMATIC", userName, flagColor)).getResponse();
                    Logger.getLogger(CreateAlertsCryptoBuySellCountMain.class.getName()).log(Level.INFO, "create alert color {0}", flagColor);
                }
            } catch (IOException ex) {
                Logger.getLogger(CreateAlertsCryptoBuySellCountMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(CreateAlertsCryptoBuySellCountMain.class.getName()).log(Level.INFO, "Finishing CreateAlertsCryptoBuySellCountMain");
    }

}
