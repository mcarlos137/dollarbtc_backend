/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardAddNumberRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardAddSubstractBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardCreateNewPinRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardMakePaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardAddNumber;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardAddSubstractBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardChangeStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardCreateNewPin;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardGetConfig;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardMakePayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard.DebitCardGetBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/debitCard")
@XmlRegistry
public class DebitCardServiceREST {

    @POST
    @Path("/changeStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeStatus(DebitCardChangeStatusRequest debitCardChangeStatusRequest) throws ServiceException {
        if (debitCardChangeStatusRequest == null) {
            throw new ServiceException("debitCardChangeStatusRequest is null");
        }
        if (debitCardChangeStatusRequest.getId() == null || debitCardChangeStatusRequest.getId().equals("")) {
            throw new ServiceException("debitCardChangeStatusRequest.getId() is null or empty");
        }
        if (debitCardChangeStatusRequest.getDebitCardStatus() == null) {
            throw new ServiceException("debitCardChangeStatusRequest.getDebitCardStatus() is null");
        }
        return Response
                .status(200)
                .entity(new DebitCardChangeStatus(debitCardChangeStatusRequest).getResponse())
                .build();
    }

    @POST
    @Path("/addSubstractBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addSubstractBalance(DebitCardAddSubstractBalanceRequest debitCardAddSubstractBalanceRequest) throws ServiceException {
        if (debitCardAddSubstractBalanceRequest == null) {
            throw new ServiceException("debitCardAddSubstractBalanceRequest is null");
        }
        if (debitCardAddSubstractBalanceRequest.getUserName() == null || debitCardAddSubstractBalanceRequest.getUserName().equals("")) {
            throw new ServiceException("debitCardAddSubstractBalanceRequest.getUserName() is null or empty");
        }
        if (debitCardAddSubstractBalanceRequest.getId() == null || debitCardAddSubstractBalanceRequest.getId().equals("")) {
            throw new ServiceException("debitCardAddSubstractBalanceRequest.getId() is null or empty");
        }
        if (debitCardAddSubstractBalanceRequest.getOperation() == null || debitCardAddSubstractBalanceRequest.getOperation().equals("")) {
            throw new ServiceException("debitCardAddSubstractBalanceRequest.getOperation() is null or empty");
        }
        if (debitCardAddSubstractBalanceRequest.getAmount() == null || debitCardAddSubstractBalanceRequest.getAmount() == 0) {
            throw new ServiceException("debitCardAddSubstractBalanceRequest.getId() is null or zero");
        }
        return Response
                .status(200)
                .entity(new DebitCardAddSubstractBalance(debitCardAddSubstractBalanceRequest).getResponse())
                .build();
    }

    @POST
    @Path("/makePayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response makePayment(DebitCardMakePaymentRequest debitCardMakePaymentRequest) throws ServiceException {
        if (debitCardMakePaymentRequest == null) {
            throw new ServiceException("debitCardMakePaymentRequest is null");
        }
        if (debitCardMakePaymentRequest.getUserName() == null || debitCardMakePaymentRequest.getUserName().equals("")) {
            throw new ServiceException("debitCardMakePaymentRequest.getUserName() is null or empty");
        }
        if (debitCardMakePaymentRequest.getId() == null || debitCardMakePaymentRequest.getId().equals("")) {
            throw new ServiceException("debitCardMakePaymentRequest.getId() is null or empty");
        }
        if (debitCardMakePaymentRequest.getTargetUserName() == null || debitCardMakePaymentRequest.getTargetUserName().equals("")) {
            throw new ServiceException("debitCardMakePaymentRequest.getTargetUserName() is null or empty");
        }
        if (debitCardMakePaymentRequest.getAmount() == null || debitCardMakePaymentRequest.getAmount() == 0) {
            throw new ServiceException("debitCardMakePaymentRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new DebitCardMakePayment(debitCardMakePaymentRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getConfig/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(
            @PathParam("id") String id
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new DebitCardGetConfig(id).getResponse())
                .build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(DebitCardListRequest debitCardListRequest) throws ServiceException {
        if (debitCardListRequest == null) {
            throw new ServiceException("debitCardListRequest is null");
        }
        return Response
                .status(200)
                .entity(new DebitCardList(debitCardListRequest).getResponse())
                .build();
    }

    @POST
    @Path("/createNewPin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createNewPin(DebitCardCreateNewPinRequest debitCardCreateNewPinRequest) throws ServiceException {
        if (debitCardCreateNewPinRequest.getId() == null || debitCardCreateNewPinRequest.getId().equals("")) {
            throw new ServiceException("id is null or empty");
        }
        if (debitCardCreateNewPinRequest.getSecretKey() == null || debitCardCreateNewPinRequest.getSecretKey().equals("")) {
            throw new ServiceException("secretKey is null or empty");
        }
        if (debitCardCreateNewPinRequest.getPin() == null || debitCardCreateNewPinRequest.getPin().equals("")) {
            throw new ServiceException("pin is null or empty");
        }
        return Response
                .status(200)
                .entity(new DebitCardCreateNewPin(debitCardCreateNewPinRequest).getResponse())
                .build();
    }

    @POST
    @Path("/addNumber")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addNumber(DebitCardAddNumberRequest debitCardAddNumberRequest) throws ServiceException {
        if (debitCardAddNumberRequest == null) {
            throw new ServiceException("debitCardAddNumberRequest is null");
        }
        if (debitCardAddNumberRequest.getId() == null || debitCardAddNumberRequest.getId().equals("")) {
            throw new ServiceException("debitCardAddNumberRequest.getId() is null or empty");
        }
        if (debitCardAddNumberRequest.getNumber() == null || debitCardAddNumberRequest.getNumber().equals("")) {
            throw new ServiceException("debitCardAddNumberRequest.getNumber() is null or empty");
        }
        return Response
                .status(200)
                .entity(new DebitCardAddNumber(debitCardAddNumberRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getBalanceMovements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalanceMovements(
            @QueryParam("id") String id,
            @QueryParam("initTimestamp") String initTimestamp,
            @QueryParam("endTimestamp") String endTimestamp
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new DebitCardGetBalanceMovements(id, initTimestamp, endTimestamp, null).getResponse())
                .build();
    }

}
