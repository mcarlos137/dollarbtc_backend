/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author mcarlos
 */
public class MoneyMarketUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static String getCurrency(String pair, Boolean base) throws IOException {
        JsonNode pairs = MAPPER.readTree(MoneyMarketFolderLocator.getPairsFile());
        if (pairs.has(pair)) {
            if (base) {
                return pairs.get(pair).get("base").textValue();
            } else {
                return pairs.get(pair).get("quote").textValue();
            }
        }
        throw new IOException("PAIR DOES NOT EXIST");
    }

    static void createOrder(JsonNode order, File orderFile) {
        FileUtil.createFile(order, orderFile);
    }

    static void addTakeToOrder(JsonNode order, JsonNode takeOrder, File orderFile) {
        if (!order.has("take")) {
            ((ObjectNode) order).set("take", MAPPER.createArrayNode());
        }
        ((ArrayNode) order.get("take")).add(takeOrder);
        FileUtil.editFile(order, orderFile);
    }

    static void createIndex(String id, String timestamp, File indexFolder) {
        JsonNode index = MAPPER.createObjectNode();
        ((ObjectNode) index).put("id", id);
        ((ObjectNode) index).put("timestamp", timestamp);
        FileUtil.createFile(index, new File(indexFolder, id + ".json"));
    }

}
