/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PriceType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetBuyPercent;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetHistoricalTickers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTicker;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTickersAndUSDPrice;
import com.fasterxml.jackson.databind.JsonNode;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
@Path("/localBitcoins")
@XmlRegistry
public class LocalBitcoinsServiceREST {

    @GET
    @Path("/getTicker/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicker(@PathParam("symbol") String symbol) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        JsonNode ticker = new LocalBitcoinsGetTicker(symbol).getResponse();
        return Response
                .status(200)
                .entity(ticker)
                .build();
    }
    
    @GET
    @Path("/getHistoricalTickers/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocalbitcoinHistoricalTickers(@PathParam("symbol") String symbol) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        return Response
                .status(200)
                .entity(new LocalBitcoinsGetHistoricalTickers(symbol, null, null).getResponse())
                .build();
    }
    
    @GET
    @Path("/getHistoricalTickers/{symbol}/{offerType}/{priceType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoricalTickers(
            @PathParam("symbol") String symbol,
            @PathParam("offerType") OfferType offerType,
            @PathParam("priceType") PriceType priceType
    ) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        if (offerType == null) {
            throw new ServiceException("offerType is null");
        }
        if (priceType == null) {
            throw new ServiceException("priceType is null");
        }
        return Response
                .status(200)
                .entity(new LocalBitcoinsGetHistoricalTickers(symbol, offerType, priceType).getResponse())
                .build();
    }
    
    @GET
    @Path("/getTickersAndUSDPrice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTickersAndUSDPrice() throws ServiceException {
        return Response
                .status(200)
                .entity(new LocalBitcoinsGetTickersAndUSDPrice().getResponse())
                .build();
    }

    @GET
    @Path("/getBuyPercent/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuyPercent(@PathParam("symbol") String symbol) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        int btcvefBuyPercent = new LocalBitcoinsGetBuyPercent(symbol).getResponse();
        return Response
                .status(200)
                .entity(btcvefBuyPercent)
                .build();
    }

}
