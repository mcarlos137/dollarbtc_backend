/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.address.AddressCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.address.AddressCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.address.AddressList;
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
@Path("/address")
@XmlRegistry
public class AddressServiceREST {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(
            AddressCreateRequest addressCreateRequest
    ) throws ServiceException {
        if (addressCreateRequest == null) {
            throw new ServiceException("addressCreateRequest is null");
        }
        if (addressCreateRequest.getCurrency() == null || addressCreateRequest.getCurrency().equals("")) {
            throw new ServiceException("addressCreateRequest.getCurrency() is null or empty");
        }
        if (addressCreateRequest.getAddress() == null || addressCreateRequest.getAddress().equals("")) {
            throw new ServiceException("addressCreateRequest.getAddress() is null or empty");
        }
        if (addressCreateRequest.getOtcMasterAccount() == null || addressCreateRequest.getOtcMasterAccount().equals("")) {
            throw new ServiceException("addressCreateRequest.getOtcMasterAccount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new AddressCreate(addressCreateRequest).getResponse())
                .build();
    }

    @GET
    @Path("/list/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new AddressList(currency).getResponse())
                .build();
    }
    
}
