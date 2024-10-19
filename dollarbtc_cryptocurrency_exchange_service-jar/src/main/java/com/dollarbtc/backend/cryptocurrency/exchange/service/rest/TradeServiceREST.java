/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.TradeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.TradeResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.TradeOperation;
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
@Path("/trade")
@XmlRegistry
public class TradeServiceREST {
    
    @POST
    @Path("/getTrades")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrades(TradeRequest tradeRequest) throws ServiceException {
        if (tradeRequest == null) {
            throw new ServiceException("tradeRequestDTO is null");
        }
        if (tradeRequest.getExchangeId() == null || tradeRequest.getExchangeId().equals("")) {
            throw new ServiceException("tradeRequest.getExchangeId() is null or empty");
        }
        if (tradeRequest.getSymbol() == null || tradeRequest.getSymbol().equals("")) {
            throw new ServiceException("tradeRequest.getSymbol() is null or empty");
        }
        return Response
                .status(200)
                .entity(new TradeResponse(TradeOperation.getAllTrades(tradeRequest.getExchangeId(), tradeRequest.getSymbol(), tradeRequest.getInitDate(), tradeRequest.getEndDate(), tradeRequest.getCollectionOrderByDate())))
                .build();
    }
    
    @GET
    @Path("/getTrades/{exchangeId}/{symbol}/{initDate}/{endDate}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrades(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol, 
            @PathParam("initDate") String initDate, 
            @PathParam("endDate") String endDate, 
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        return Response
                .status(200)
                .entity(new TradeResponse(TradeOperation.getAllTrades(exchangeId, symbol, initDate, endDate, collectionOrderByDate)))
                .build();
    }
    
    @GET
    @Path("/getReducedTrades/{exchangeId}/{symbol}/{initDate}/{endDate}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReducedTrades(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol, 
            @PathParam("initDate") String initDate, 
            @PathParam("endDate") String endDate, 
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        return Response
                .status(200)
                .entity(new TradeResponse(TradeOperation.getReducedTrades(exchangeId, symbol, initDate, endDate, collectionOrderByDate)))
                .build();
    }
            
}
