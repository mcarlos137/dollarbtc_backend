/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasicUser;
import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import java.math.BigDecimal;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author conamerica02
 */
public class UserCreateNewOrderOperation extends AbstractRestClient<Order> {

    private static Client client;
    private static final String URL = "https://api.hitbtc.com/api/2/order";
    private final String loginAccount, symbol, clientOrderId, side, type, price, quantity, id;
    
    public UserCreateNewOrderOperation(String loginAccount, String symbol, String clientOrderId, String side, String type, String price, String quantity, String id) {
        super(Order.class);
        this.loginAccount = loginAccount;
        this.symbol = ExchangeUtil.getSymbol("HitBTC", symbol);
        this.clientOrderId = clientOrderId;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.id = id;
    }

    @Override
    public Client getClient() {
        if (client == null) {
            client = ClientBuilder.newClient();
        }
        return client;
    }

    @Override
    public String getMediaType() {
        return MediaType.APPLICATION_JSON;
    }
    
    public Order getResponse() {
        Order newOrder = new Order(clientOrderId, symbol, side, type, new BigDecimal(quantity), new BigDecimal(price), id);        
        String[] loginAccountParams = WebSocketBasicUser.getUserCredentialJsonFile(loginAccount);
        return super.post(newOrder, URL, RequestRestType.SYNC, SecurityType.BASIC, loginAccountParams, 30);
    }
    
}
