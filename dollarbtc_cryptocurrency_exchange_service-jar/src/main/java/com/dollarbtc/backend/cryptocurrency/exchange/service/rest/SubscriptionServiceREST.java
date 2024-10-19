/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionJoinRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionUnjoinRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionGet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionJoin;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionPremiumOverviewData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionPremiumOverviewId;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionUnjoin;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/subscription")
@XmlRegistry
public class SubscriptionServiceREST {

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(SubscriptionListRequest subscriptionListRequest) throws ServiceException {
        return Response
                .status(200)
                .entity(new SubscriptionList(subscriptionListRequest).getResponse())
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
                .entity(new SubscriptionGet(id).getResponse())
                .build();
    }

    @PUT
    @Path("/join")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response join(SubscriptionJoinRequest subscriptionJoinRequest) throws ServiceException {
        if (subscriptionJoinRequest == null) {
            throw new ServiceException("subscriptionJoinRequest is null");
        }
        if (subscriptionJoinRequest.getBaseUserName() == null || subscriptionJoinRequest.getBaseUserName().equals("")) {
            throw new ServiceException("subscriptionJoinRequest.getBaseUserName() is null or empty");
        }
        if (subscriptionJoinRequest.getTargetUserName() == null || subscriptionJoinRequest.getTargetUserName().equals("")) {
            throw new ServiceException("subscriptionJoinRequest.getTargetUserName() is null or empty");
        }
        if (subscriptionJoinRequest.getSubscriptionType() == null) {
            throw new ServiceException("subscriptionJoinRequest.getSubscriptionType() is null");
        }
        String response = new SubscriptionJoin(subscriptionJoinRequest).getResponse();
        Logger.getLogger(SubscriptionServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/unjoin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response unjoin(SubscriptionUnjoinRequest subscriptionUnjoinRequest) throws ServiceException {
        if (subscriptionUnjoinRequest == null) {
            throw new ServiceException("subscriptionUnjoinRequest is null");
        }
        if (subscriptionUnjoinRequest.getBaseUserName() == null || subscriptionUnjoinRequest.getBaseUserName().equals("")) {
            throw new ServiceException("subscriptionUnjoinRequest.getBaseUserName() is null or empty");
        }
        if (subscriptionUnjoinRequest.getTargetUserName() == null || subscriptionUnjoinRequest.getTargetUserName().equals("")) {
            throw new ServiceException("subscriptionUnjoinRequest.getTargetUserName() is null or empty");
        }
        if (subscriptionUnjoinRequest.getSubscriptionType() == null) {
            throw new ServiceException("subscriptionUnjoinRequest.getSubscriptionType() is null");
        }
        String response = new SubscriptionUnjoin(subscriptionUnjoinRequest).getResponse();
        Logger.getLogger(SubscriptionServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }
    
    @GET
    @Path("/premiumOverviewId/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response overviewId(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new SubscriptionPremiumOverviewId(userName).getResponse())
                .build();
    }

    @GET
    @Path("/premiumOverviewData/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response overviewData(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new SubscriptionPremiumOverviewData(userName).getResponse())
                .build();
    }

}
