/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author carlosmolina
 */
public class MoneyMarketGetOrderBookMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;
    private String ordersString;

    public MoneyMarketGetOrderBookMessage(Session session, JsonNode jsonNode) {
        this.session = session;
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        String pair = jsonNode.get("pair").textValue();
        String type = jsonNode.get("type").textValue();
        File moneyMarketPairTypeFolder = MoneyMarketFolderLocator.getPairTypeFolder(pair, OrderType.valueOf(type).name());
        if (moneyMarketPairTypeFolder == null
                || !moneyMarketPairTypeFolder.isDirectory() 
                //|| moneyMarketPairTypeFolder.listFiles().length == 1
                ) {
            try {
                session.getRemote().sendPing(null);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetOrderBookMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("type", type);
        String newOrdersString = "";
        for (File moneyMarketPairTypeFile : moneyMarketPairTypeFolder.listFiles()) {
            if (!moneyMarketPairTypeFile.isFile()) {
                continue;
            }
            try {
                JsonNode moneyMarketPairType = mapper.readTree(moneyMarketPairTypeFile);
                String orderId = moneyMarketPairType.get("id").textValue();
                System.out.println("orderId " + orderId);
                JsonNode moneyMarketOrder = mapper.readTree(MoneyMarketFolderLocator.getOrderFile(orderId));
                newOrdersString = newOrdersString + "__" + moneyMarketOrder;
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetOrderBookMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        boolean send = false;
        if (ordersString == null || !ordersString.equals(newOrdersString)) {
            ordersString = newOrdersString;
            send = true;
        }
        if (send) {
            ArrayNode orderBook = mapper.createArrayNode();
            for (File moneyMarketPairTypeFile : moneyMarketPairTypeFolder.listFiles()) {
                if (!moneyMarketPairTypeFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode moneyMarketPairType = mapper.readTree(moneyMarketPairTypeFile);
                    String orderId = moneyMarketPairType.get("id").textValue();
                    System.out.println("orderId " + orderId);
                    orderBook.add(mapper.readTree(MoneyMarketFolderLocator.getOrderFile(orderId)));
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetOrderBookMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("orderBook " + orderBook);
            try {
                session.getRemote().sendString(ServiceUtil.createWSResponseWithData(orderBook, "currentOrderBook", "params", params).toString());
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetOrderBookMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                session.getRemote().sendPing(null);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetOrderBookMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
