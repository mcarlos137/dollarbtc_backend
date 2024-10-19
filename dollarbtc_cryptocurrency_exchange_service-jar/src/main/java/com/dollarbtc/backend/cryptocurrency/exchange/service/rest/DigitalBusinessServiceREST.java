/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.digitalbusiness.DigitalBusinessGetDonationsDetails;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.digitalbusiness.DigitalBusinessGetFinancialOverview;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.digitalbusiness.DigitalBusinessGetMoneyCallsDetails;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.digitalbusiness.DigitalBusinessGetSubscriptionsDetails;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/digitalBusiness")
@XmlRegistry
public class DigitalBusinessServiceREST {

    @GET
    @Path("/getFinancialOverview/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFinancialOverview (
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null) {
            throw new ServiceException("userName is null");
        }
        return Response
                .status(200)
                .entity(new DigitalBusinessGetFinancialOverview(userName).getResponse())
                .build();
    }
    
    @GET
    @Path("/getSubscriptionsDetails/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriptionsDetails(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null) {
            throw new ServiceException("userName is null");
        }
        return Response
                .status(200)
                .entity(new DigitalBusinessGetSubscriptionsDetails(userName).getResponse())
                .build();
    }
        
    @GET
    @Path("/getMoneyCallsDetails/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMoneyCallsDetails(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null) {
            throw new ServiceException("userName is null");
        }
        return Response
                .status(200)
                .entity(new DigitalBusinessGetMoneyCallsDetails(userName).getResponse())
                .build();
    }
    
    @GET
    @Path("/getDonationsDetails/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDonationsDetails(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null) {
            throw new ServiceException("userName is null");
        }
        return Response
                .status(200)
                .entity(new DigitalBusinessGetDonationsDetails(userName).getResponse())
                .build();
    }
        
}
