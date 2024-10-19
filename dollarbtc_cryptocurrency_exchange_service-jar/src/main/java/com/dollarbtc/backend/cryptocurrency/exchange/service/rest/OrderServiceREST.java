/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.OrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.OrderResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.OrderOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author conamerica90
 */
@Path("/order")
@XmlRegistry
public class OrderServiceREST {

    @POST
    @Path("/getCurrentOrders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentOrders(OrderRequest orderRequest) throws ServiceException {
        if (orderRequest == null) {
            throw new ServiceException("orderRequest is null");
        }
        if (orderRequest.getExchangeId() == null || orderRequest.getExchangeId().equals("")) {
            throw new ServiceException("orderRequest.getExchangeId() is null or empty");
        }
        if (orderRequest.getSymbol() == null || orderRequest.getSymbol().equals("")) {
            throw new ServiceException("orderRequest.getSymbol() is null or empty");
        }
        if (orderRequest.getModelName() == null || orderRequest.getModelName().equals("")) {
            throw new ServiceException("orderRequest.getModelName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new OrderResponse(
                                OrderOperation.getOrders(orderRequest.getExchangeId(), orderRequest.getSymbol(), orderRequest.getModelName(), orderRequest.getInitDate(), orderRequest.getEndDate(), orderRequest.getCollectionOrderByDate()),
                                null
                        ))
                .build();
    }

    @GET
    @Path("/getCurrentOrders/{exchangeId}/{symbol}/{modelName}/{initDate}/{endDate}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentOrders(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol,
            @PathParam("modelName") String modelName,
            @PathParam("initDate") String initDate,
            @PathParam("endDate") String endDate,
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate
    ) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new OrderResponse(
                                OrderOperation.getOrders(exchangeId, symbol, modelName, initDate, endDate, collectionOrderByDate),
                                null
                        ))
                .build();
    }

    @GET
    @Path("/getAllOrders/{exchangeId}/{symbol}/{userModelName}/{initDate}/{endDate}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol,
            @PathParam("userModelName") String userModelName,
            @PathParam("initDate") String initDate,
            @PathParam("endDate") String endDate,
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate
    ) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        if (userModelName == null || userModelName.equals("")) {
            throw new ServiceException("userModelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new OrderResponse(
                                OrderOperation.getOrders(exchangeId, symbol, userModelName, initDate, endDate, collectionOrderByDate),
                                OrderOperation.getOrderIntervals(exchangeId, symbol, userModelName, initDate, endDate, collectionOrderByDate)
                        ))
                .build();
    }
    
    @GET
    @Path("/getOrderAlgorithmTypes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderAlgorithmTypes(
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(OrderOperation.getOrderAlgorithmTypes())
                .build();
    }

}
