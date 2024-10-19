/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.review.ReviewCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.review.ReviewCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.review.ReviewGet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.review.ReviewGetLasts;
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
@Path("/review")
@XmlRegistry
public class ReviewServiceREST {
    
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(
            ReviewCreateRequest reviewCreateRequest
    ) throws ServiceException {
        if (reviewCreateRequest == null) {
            throw new ServiceException("reviewCreateRequest is null");
        }
        if (reviewCreateRequest.getUserName() == null || reviewCreateRequest.getUserName().equals("")) {
            throw new ServiceException("reviewCreateRequest.getUserName() is null or empty");
        }
        if (reviewCreateRequest.getOperationId() == null || reviewCreateRequest.getOperationId().equals("")) {
            throw new ServiceException("reviewCreateRequest.getOperationId() is null or empty");
        }
        if (reviewCreateRequest.getOperationType() == null || reviewCreateRequest.getOperationType().equals("")) {
            throw new ServiceException("reviewCreateRequest.getOperationType() is null or empty");
        }
        if (reviewCreateRequest.getComment() == null || reviewCreateRequest.getComment().equals("")) {
            throw new ServiceException("reviewCreateRequest.getComment() is null or empty");
        }
        if (reviewCreateRequest.getStarsQuantity() == null || reviewCreateRequest.getStarsQuantity() == 0) {
            throw new ServiceException("reviewCreateRequest.getStarsQuantity() is null or zero");
        }
        return Response
                .status(200)
                .entity(new ReviewCreate(reviewCreateRequest).getResponse())
                .build();
    }
        
    @GET
    @Path("/getLasts/{quantity}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLasts(
            @PathParam("quantity") Integer quantity
    ) throws ServiceException {
        if (quantity == null || quantity == 0) {
            throw new ServiceException("quantity is null or zero");
        }
        return Response
                .status(200)
                .entity(new ReviewGetLasts(null, quantity).getResponse())
                .build();
    }
    
    @GET
    @Path("/getLasts/{userName}/{quantity}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLasts(
            @PathParam("userName") String userName,
            @PathParam("quantity") Integer quantity
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (quantity == null || quantity == 0) {
            throw new ServiceException("quantity is null or zero");
        }
        return Response
                .status(200)
                .entity(new ReviewGetLasts(userName, quantity).getResponse())
                .build();
    }
    
    @GET
    @Path("/get/{operationId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @PathParam("operationId") String operationId
    ) throws ServiceException {
        if (operationId == null || operationId.equals("")) {
            throw new ServiceException("operationId is null or empty");
        }
        return Response
                .status(200)
                .entity(new ReviewGet(operationId).getResponse())
                .build();
    }
    
}
