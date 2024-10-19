/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.forex.ForexGetHistoricalRates;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.forex.ForexGetRate;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
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
@Path("/forex")
@XmlRegistry
public class ForexServiceREST {

    @GET
    @Path("/getRate/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRate(@PathParam("symbol") String symbol) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        return Response
                .status(200)
                .entity(new ForexGetRate(symbol).getResponse())
                .build();
    }
    
    @GET
    @Path("/getHistoricalRates/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoricalRates(@PathParam("symbol") String symbol) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        return Response
                .status(200)
                .entity(new ForexGetHistoricalRates(symbol).getResponse())
                .build();
    }

}
