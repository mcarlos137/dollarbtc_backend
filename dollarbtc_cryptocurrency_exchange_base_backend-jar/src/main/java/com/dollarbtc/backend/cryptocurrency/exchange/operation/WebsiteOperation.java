/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.WebsiteEditBestBotsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class WebsiteOperation {

    public static JsonNode getBestBots() {
        File websiteBestBotsFolder = new File(new File(OPERATOR_PATH, "Website"), "BestBots");
        for (File websiteBestBotsFile : websiteBestBotsFolder.listFiles()) {
            if (!websiteBestBotsFile.isFile()) {
                continue;
            }
            JsonNode websiteBestBots = null;
            try {
                websiteBestBots = new ObjectMapper().readTree(websiteBestBotsFile);
            } catch (IOException ex) {
                Logger.getLogger(WebsiteOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (websiteBestBots == null) {
                websiteBestBots = new ObjectMapper().createObjectNode();
            }
            return websiteBestBots;
        }
        return new ObjectMapper().createObjectNode();
    }

    public static String editBestBots(WebsiteEditBestBotsRequest websiteEditBestBotsRequest) {
        File websiteBestBotsFolder = new File(new File(OPERATOR_PATH, "Website"), "BestBots");
        File websiteBestBotsOldFolder = new File(websiteBestBotsFolder, "Old");
        for (File websiteBestBotsFile : websiteBestBotsFolder.listFiles()) {
            FileUtil.moveFileToFolder(websiteBestBotsFile, websiteBestBotsOldFolder);
        }
        String timestamp = DateUtil.getCurrentDate();
        FileUtil.createFile(websiteEditBestBotsRequest.toJsonNode(), new File(websiteBestBotsFolder, DateUtil.getFileDate(timestamp) + ".json"));
        return "OK";
    }

}
