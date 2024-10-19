/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.tag.TagCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.tag.TagCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.tag.TagList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Path("/tag")
@XmlRegistry
public class TagServiceREST {

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws ServiceException {
        return Response
                .status(200)
                .entity(new TagList().getResponse())
                .build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(TagCreateRequest tagCreateRequest) throws ServiceException {
        if (tagCreateRequest == null) {
            throw new ServiceException("tagCreateRequest is null");
        }
        if (tagCreateRequest.getName() == null || tagCreateRequest.getName().equals("")) {
            throw new ServiceException("tagCreateRequest.getName() is null or empty");
        }
        String response = new TagCreate(tagCreateRequest).getResponse();
        Logger.getLogger(TagServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

}
