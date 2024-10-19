/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UpdateBroadcastingOverviewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        File broadcastingsOverviewFile = BroadcastingFolderLocator.getOverviewFile();
        ArrayNode overview = mapper.createArrayNode();
        
    }

}
