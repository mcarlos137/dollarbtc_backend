/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.JsonNodeResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.marketmodulator.MarketModulatorModifyAutomaticRulesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.marketmodulator.MarketModulatorModifyManualRulesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.StringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.MarketModulatorOperation;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/marketModulator")
@XmlRegistry
public class MarketModulatorServiceREST {

    @POST
    @Path("/modifyAutomaticRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyAutomaticRules(MarketModulatorModifyAutomaticRulesRequest marketModifyAutomaticRulesRequest) throws ServiceException {
        if (marketModifyAutomaticRulesRequest == null) {
            throw new ServiceException("marketModifyAutomaticRulesRequest is null");
        }
        return Response
                .status(200)
                .entity(new StringResponse(MarketModulatorOperation.modifyAutomaticRules(marketModifyAutomaticRulesRequest)))
                .build();
    }
    
    @POST
    @Path("/modifyManualRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyManualRules(MarketModulatorModifyManualRulesRequest marketModifyManualRulesRequest) throws ServiceException {
        if (marketModifyManualRulesRequest == null) {
            throw new ServiceException("marketModifyManualRulesRequest is null");
        }
        return Response
                .status(200)
                .entity(new StringResponse(MarketModulatorOperation.modifyManualRules(marketModifyManualRulesRequest)))
                .build();
    }
    
    @GET
    @Path("/getAutomaticRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAutomaticRules() throws ServiceException {
        return Response
                .status(200)
                .entity(new JsonNodeResponse(MarketModulatorOperation.getAutomaticRules()))
                .build();
    }
    
    @GET
    @Path("/getManualRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManualRules() throws ServiceException {
        return Response
                .status(200)
                .entity(new JsonNodeResponse(MarketModulatorOperation.getManualRules()))
                .build();
    }
    
    @GET
    @Path("/getActiveSymbols")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveSymbols() throws ServiceException {
        return Response
                .status(200)
                .entity(new JsonNodeResponse(MarketModulatorOperation.getActiveSymbols()))
                .build();
    }
    
}
