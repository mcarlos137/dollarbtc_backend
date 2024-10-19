/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketCloseOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketGetOrdersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketPostOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketTakeOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketCloseOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketGetOrders;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketGetPairs;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketGetTopTraders;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketPostOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketTakeOrder;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/moneyMarket")
@XmlRegistry
public class MoneyMarketServiceREST {
    
    @POST
    @Path("/postOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postOrder(MoneyMarketPostOrderRequest moneyMarketPostOrderRequest) throws ServiceException {
        if (moneyMarketPostOrderRequest == null) {
            throw new ServiceException("moneyMarketPostOrderRequest is null");
        }
        if (moneyMarketPostOrderRequest.getUserName() == null || moneyMarketPostOrderRequest.getUserName().equals("")) {
            throw new ServiceException("moneyMarketPostOrderRequest.getUserName() is null or empty");
        }
        if (moneyMarketPostOrderRequest.getPair() == null || moneyMarketPostOrderRequest.getPair().equals("")) {
            throw new ServiceException("moneyMarketPostOrderRequest.getPair() is null or empty");
        }
        if (moneyMarketPostOrderRequest.getAmount() == null || moneyMarketPostOrderRequest.getAmount().equals(0.0)) {
            throw new ServiceException("moneyMarketPostOrderRequest.getAmount() is null or zero");
        }
        if (moneyMarketPostOrderRequest.getPrice() == null || moneyMarketPostOrderRequest.getPrice().equals(0.0)) {
            throw new ServiceException("moneyMarketPostOrderRequest.getPrice() is null or zero");
        }
        if (moneyMarketPostOrderRequest.getTime() == 0) {
            throw new ServiceException("moneyMarketPostOrderRequest.getTime() is zero");
        }
        if (!(moneyMarketPostOrderRequest.getTimeUnit().equals("MINUTES") || !moneyMarketPostOrderRequest.getTimeUnit().equals("HOURS") || !moneyMarketPostOrderRequest.getTimeUnit().equals("DAYS"))) {
            throw new ServiceException("moneyMarketPostOrderRequest.getTimeUnit() is not MINUTES or HOURS or DAYS");
        }
        if (moneyMarketPostOrderRequest.getType() == null) {
            throw new ServiceException("moneyMarketPostOrderRequest.getType() is null");
        }
        if (moneyMarketPostOrderRequest.getNickName() == null || moneyMarketPostOrderRequest.getNickName().equals("")) {
            throw new ServiceException("moneyMarketPostOrderRequest.getNickName() is null or empty");
        }
        if (moneyMarketPostOrderRequest.getSource() == null || moneyMarketPostOrderRequest.getSource().equals("")) {
            throw new ServiceException("moneyMarketPostOrderRequest.getSource() is null or empty");
        }
        String response = new MoneyMarketPostOrder(moneyMarketPostOrderRequest).getResponse();
        Logger.getLogger(MoneyMarketServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/closeOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response closeOrder(MoneyMarketCloseOrderRequest moneyMarketCloseOrderRequest) throws ServiceException {
        if (moneyMarketCloseOrderRequest == null) {
            throw new ServiceException("moneyMarketCloseOrderRequest is null");
        }
        if (moneyMarketCloseOrderRequest.getUserName() == null || moneyMarketCloseOrderRequest.getUserName().equals("")) {
            throw new ServiceException("moneyMarketCloseOrderRequest.getUserName() is null or empty");
        }
        if (moneyMarketCloseOrderRequest.getId() == null || moneyMarketCloseOrderRequest.getId().equals("")) {
            throw new ServiceException("moneyMarketCloseOrderRequest.getId() is null or empty");
        }
        String response = new MoneyMarketCloseOrder(moneyMarketCloseOrderRequest).getResponse();
        Logger.getLogger(MoneyMarketServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/takeOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response takeOrder(MoneyMarketTakeOrderRequest moneyMarketTakeOrderRequest) throws ServiceException {
        if (moneyMarketTakeOrderRequest == null) {
            throw new ServiceException("moneyMarketTakeOrderRequest is null");
        }
        if (moneyMarketTakeOrderRequest.getUserName() == null || moneyMarketTakeOrderRequest.getUserName().equals("")) {
            throw new ServiceException("moneyMarketTakeOrderRequest.getUserName() is null or empty");
        }
        if (moneyMarketTakeOrderRequest.getAmount() == null || moneyMarketTakeOrderRequest.getAmount().equals(0.0)) {
            throw new ServiceException("moneyMarketTakeOrderRequest.getAmount() is null or zero");
        }
        if (moneyMarketTakeOrderRequest.getPrice() == null || moneyMarketTakeOrderRequest.getPrice().equals(0.0)) {
            throw new ServiceException("moneyMarketTakeOrderRequest.getPrice() is null or zero");
        }
        if (moneyMarketTakeOrderRequest.getId() == null || moneyMarketTakeOrderRequest.getId().equals("")) {
            throw new ServiceException("moneyMarketTakeOrderRequest.getId() is null or empty");
        }
        if (moneyMarketTakeOrderRequest.getNickName() == null || moneyMarketTakeOrderRequest.getNickName().equals("")) {
            throw new ServiceException("moneyMarketTakeOrderRequest.getNickName() is null or empty");
        }
        String response = new MoneyMarketTakeOrder(moneyMarketTakeOrderRequest).getResponse();
        Logger.getLogger(MoneyMarketServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/getOrders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(MoneyMarketGetOrdersRequest moneyMarketGetOrdersRequest) throws ServiceException {
        if (moneyMarketGetOrdersRequest == null) {
            throw new ServiceException("moneyMarketGetOrdersRequest is null");
        }
        JsonNode response = new MoneyMarketGetOrders(moneyMarketGetOrdersRequest).getResponse();
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @GET
    @Path("/getPairs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPairs() throws ServiceException {
        return Response
                .status(200)
                .entity(new MoneyMarketGetPairs().getResponse())
                .build();
    }

    @GET
    @Path("/getTopTraders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopTraders() throws ServiceException {
        return Response
                .status(200)
                .entity(new MoneyMarketGetTopTraders().getResponse())
                .build();
    }

}
