/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent.SubscriptionEventCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent.SubscriptionEventListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent.SubscriptionEventCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent.SubscriptionEventGet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent.SubscriptionEventList;
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
@Path("/subscriptionEvent")
@XmlRegistry
public class SubscriptionEventServiceREST {

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(SubscriptionEventListRequest subscriptionEventListRequest) throws ServiceException {
        return Response
                .status(200)
                .entity(new SubscriptionEventList(subscriptionEventListRequest).getResponse())
                .build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(SubscriptionEventCreateRequest subscriptionEventCreateRequest) throws ServiceException {
        return Response
                .status(200)
                .entity(new SubscriptionEventCreate(subscriptionEventCreateRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/get/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @PathParam("id") String id
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new SubscriptionEventGet(id).getResponse())
                .build();
    }

}
