/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.analysis.AnalysisGetFullPriceInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author conamerica90
 */
@Path("/analysis")
@XmlRegistry
public class AnalysisServiceREST {
    
    @GET
    @Path("/getFullPriceInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFullPriceInfo() throws ServiceException {
        return Response
                .status(200)
                .entity(new AnalysisGetFullPriceInfo().getResponse())
                .build();
    }
  
}
