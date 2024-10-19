/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.shorts.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class CreateShortsOverviewMain {

    private final static String[] REACTIONS = new String[]{"LIKES_MAIN", "LIKES", "LOVES", "WOWS", "HAHAS", "SADS", "ANGRIES"};

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(">>>>>>>>>>>>>>>>>> STARTING CreateShortsOverviewMain >>>>>>>>>>>>>>>>>>");
        String overviewId = BaseOperation.getId();
        List<JsonNode> overviewData = new ArrayList<>();
        for (File shortsFile : ShortsFolderLocator.getFolder().listFiles()) {
            if (!shortsFile.isFile() || shortsFile.getName().contains("overview")) {
                continue;
            }
            try {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                JsonNode shorts = mapper.readTree(shortsFile);
                boolean subscribed = false;
                if (new Random().nextInt(2) == 1) {
                    subscribed = true;
                }
                ((ObjectNode) shorts).put("subscribed", subscribed);
                boolean notification = false;
                if (subscribed && new Random().nextInt(2) == 1) {
                    notification = true;
                }
                ((ObjectNode) shorts).put("notification", notification);
                boolean liked = false;
                if (new Random().nextInt(2) == 1) {
                    liked = true;
                }
                ((ObjectNode) shorts).put("liked", liked);
                ((ObjectNode) shorts).remove("shares");
                ((ObjectNode) shorts).put("sharesCount", new Random().nextInt(120) + 45);
                ((ObjectNode) shorts).put("viewsCount", ((ArrayNode) shorts.get("views")).size());
                ((ObjectNode) shorts).remove("views");
                ObjectNode reactionsCount = mapper.createObjectNode();
                //Implements
                for(String reaction : REACTIONS){
                    if(reaction.equals("LIKES_MAIN")){
                        reactionsCount.put(reaction, new Random().nextInt(200) + 50);
                    }
                }
                ((ObjectNode) shorts).set("reactionsCount", reactionsCount);
                //Implements
                ArrayNode comments = mapper.createArrayNode();
                ((ObjectNode) shorts).putArray("comments").addAll(comments);
                ((ObjectNode) shorts).put("allowMoneyCalls", true);
                ((ObjectNode) shorts).put("moneyCallRate", Double.valueOf((new Random().nextInt(2000) + 200)) / 100);
                ((ObjectNode) shorts).put("userSubscribers", new Random().nextInt(3000) + 120);
                System.out.println(">>>>>>>>>>>>>>>>>> " + shorts);
                overviewData.add(shorts);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            } catch (IOException ex) {
                Logger.getLogger(CreateShortsOverviewMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collections.shuffle(overviewData);
        System.out.println(">>>>>>>>>>>>>>>>>> ID " + overviewId);
        System.out.println(">>>>>>>>>>>>>>>>>> DATA " + overviewData);
        ObjectNode overview = mapper.createObjectNode();
        overview.put("id", overviewId);
        overview.putArray("data").addAll(overviewData);
        FileUtil.editFile(overview, ShortsFolderLocator.getOverviewFile());
        System.out.println(">>>>>>>>>>>>>>>>>> FINISHING CreateShortsOverviewMain >>>>>>>>>>>>>>>>>>");
    }

}
