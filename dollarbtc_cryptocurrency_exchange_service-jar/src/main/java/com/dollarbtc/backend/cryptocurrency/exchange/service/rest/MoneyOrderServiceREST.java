/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneyorder.MoneyOrderCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneyorder.MoneyOrderProcessRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder.MoneyOrderCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder.MoneyOrderGetInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder.MoneyOrderList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder.MoneyOrderProcess;
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
@Path("/moneyOrder")
@XmlRegistry
public class MoneyOrderServiceREST {

    @GET
    @Path("/getInfo/{currency}/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(
            @PathParam("currency") String currency,
            @PathParam("language") String language
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        return Response
                .status(200)
                .entity(new MoneyOrderGetInfo(currency, language).getResponse())
                .build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(MoneyOrderCreateRequest moneyOrderCreateRequest) throws ServiceException {
        if (moneyOrderCreateRequest == null) {
            throw new ServiceException("moneyOrderCreateRequest is null");
        }
        if (moneyOrderCreateRequest.getUserName() == null || moneyOrderCreateRequest.getUserName().equals("")) {
            throw new ServiceException("moneyOrderCreateRequest.getUserName() is null or empty");
        }
        if (moneyOrderCreateRequest.getCurrency() == null || moneyOrderCreateRequest.getCurrency().equals("")) {
            throw new ServiceException("moneyOrderCreateRequest.getCurrency() is null or empty");
        }
        if (moneyOrderCreateRequest.getAmount() == null || moneyOrderCreateRequest.getAmount() == 0) {
            throw new ServiceException("moneyOrderCreateRequest.getAmount() is null or zero");
        }
        if (moneyOrderCreateRequest.getSenderName() == null || moneyOrderCreateRequest.getSenderName().equals("")) {
            throw new ServiceException("moneyOrderCreateRequest.getSenderName() is null or empty");
        }
        if (moneyOrderCreateRequest.getOrderId() == null || moneyOrderCreateRequest.getOrderId().equals("")) {
            throw new ServiceException("moneyOrderCreateRequest.getOrderId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MoneyOrderCreate(moneyOrderCreateRequest).getResponse())
                .build();
    }

    @GET
    @Path("/list/{status}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(
            @PathParam("status") String status
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new MoneyOrderList(status).getResponse())
                .build();
    }

    @POST
    @Path("/process")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response process(MoneyOrderProcessRequest moneyOrderProcessRequest) throws ServiceException {
        if (moneyOrderProcessRequest == null) {
            throw new ServiceException("moneyOrderProcessRequest is null");
        }
        if (moneyOrderProcessRequest.getUserName() == null || moneyOrderProcessRequest.getUserName().equals("")) {
            throw new ServiceException("moneyOrderProcessRequest.getUserName() is null or empty");
        }
        if (moneyOrderProcessRequest.getStatus() == null || moneyOrderProcessRequest.getStatus().equals("")) {
            throw new ServiceException("moneyOrderProcessRequest.getStatus() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MoneyOrderProcess(moneyOrderProcessRequest).getResponse())
                .build();
    }

}
