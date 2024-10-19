/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardActivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardCreateBatchRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardDeleteRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardRedeemNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardRedeemRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardResendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardSubmitRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardActivate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardCreateBatch;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardDelete;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardRedeem;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardResend;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardSend;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardListAll;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardRedeemNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard.GiftCardSubmit;
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
@Path("/giftCard")
@XmlRegistry
public class GiftCardServiceREST {

    @POST
    @Path("/activate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response activate(GiftCardActivateRequest giftCardActivateRequest) throws ServiceException {
        if (giftCardActivateRequest == null) {
            throw new ServiceException("giftCardActivateRequest is null");
        }
        if (giftCardActivateRequest.getUserName() == null || giftCardActivateRequest.getUserName().equals("")) {
            throw new ServiceException("giftCardActivateRequest.getUserName() is null or empty");
        }
        if (giftCardActivateRequest.getId() == null || giftCardActivateRequest.getId().equals("")) {
            throw new ServiceException("giftCardActivateRequest.getId() is null or empty");
        }
        if (giftCardActivateRequest.getCurrency() == null || giftCardActivateRequest.getCurrency().equals("")) {
            throw new ServiceException("giftCardActivateRequest.getCurrency() is null or empty");
        }
        if (giftCardActivateRequest.getAmount() == null || giftCardActivateRequest.getAmount() == 0.0) {
            throw new ServiceException("giftCardActivateRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new GiftCardActivate(giftCardActivateRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/redeem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response redeem(GiftCardRedeemRequest giftCardRedeemRequest) throws ServiceException {
        if (giftCardRedeemRequest == null) {
            throw new ServiceException("giftCardRedeemRequest is null");
        }
        if (giftCardRedeemRequest.getUserName() == null || giftCardRedeemRequest.getUserName().equals("")) {
            throw new ServiceException("giftCardRedeemRequest.getUserName() is null or empty");
        }
        if (giftCardRedeemRequest.getId() == null || giftCardRedeemRequest.getId().equals("")) {
            throw new ServiceException("giftCardRedeemRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardRedeem(giftCardRedeemRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/redeemNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response redeemNew(GiftCardRedeemNewRequest giftCardRedeemNewRequest) throws ServiceException {
        if (giftCardRedeemNewRequest == null) {
            throw new ServiceException("giftCardRedeemRequest is null");
        }
        if (giftCardRedeemNewRequest.getUserName() == null || giftCardRedeemNewRequest.getUserName().equals("")) {
            throw new ServiceException("giftCardRedeemRequest.getUserName() is null or empty");
        }
        if (giftCardRedeemNewRequest.getId() == null || giftCardRedeemNewRequest.getId().equals("")) {
            throw new ServiceException("giftCardRedeemRequest.getId() is null or empty");
        }
        if (giftCardRedeemNewRequest.getSource() == null || giftCardRedeemNewRequest.getSource().equals("")) {
            throw new ServiceException("giftCardRedeemRequest.getSource() is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardRedeemNew(giftCardRedeemNewRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response submit(GiftCardSubmitRequest giftCardSubmitRequest) throws ServiceException {
        if (giftCardSubmitRequest == null) {
            throw new ServiceException("giftCardSubmitRequest is null");
        }
        if (giftCardSubmitRequest.getId() == null || giftCardSubmitRequest.getId().equals("")) {
            throw new ServiceException("giftCardSubmitRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardSubmit(giftCardSubmitRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(GiftCardDeleteRequest giftCardDeleteRequest) throws ServiceException {
        if (giftCardDeleteRequest == null) {
            throw new ServiceException("giftCardDeleteRequest is null");
        }
        if (giftCardDeleteRequest.getId() == null || giftCardDeleteRequest.getId().equals("")) {
            throw new ServiceException("giftCardDeleteRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardDelete(giftCardDeleteRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response send(GiftCardSendRequest giftCardSendRequest) throws ServiceException {
        if (giftCardSendRequest == null) {
            throw new ServiceException("giftCardSendRequest is null");
        }
        if (giftCardSendRequest.getUserName() == null || giftCardSendRequest.getUserName().equals("")) {
            throw new ServiceException("giftCardSendRequest.getUserName() is null or empty");
        }
        if (giftCardSendRequest.getEmail() == null || giftCardSendRequest.getEmail().equals("")) {
            throw new ServiceException("giftCardSendRequest.getEmail() is null or empty");
        }
        if (giftCardSendRequest.getCurrency() == null || giftCardSendRequest.getCurrency().equals("")) {
            throw new ServiceException("giftCardSendRequest.getCurrency() is null or empty");
        }
        if (giftCardSendRequest.getAmount() == null || giftCardSendRequest.getAmount() == 0.0) {
            throw new ServiceException("giftCardSendRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new GiftCardSend(giftCardSendRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/resend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response resend(GiftCardResendRequest giftCardResendRequest) throws ServiceException {
        if (giftCardResendRequest == null) {
            throw new ServiceException("giftCardResendRequest is null");
        }
        if (giftCardResendRequest.getId() == null || giftCardResendRequest.getId().equals("")) {
            throw new ServiceException("giftCardResendRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardResend(giftCardResendRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/list/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardList(userName).getResponse())
                .build();
    }
    
    @GET
    @Path("/listAll/{status}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll(
            @PathParam("status") String status
    ) throws ServiceException {
        if (status == null || status.equals("")) {
            throw new ServiceException("status is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardListAll(status).getResponse())
                .build();
    }
    
    @POST
    @Path("/createBatch")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createBatch(GiftCardCreateBatchRequest giftCardCreateBatchRequest) throws ServiceException {
        if (giftCardCreateBatchRequest == null) {
            throw new ServiceException("giftCardCreateBatchRequest is null");
        }
        if (giftCardCreateBatchRequest.getCurrency() == null || giftCardCreateBatchRequest.getCurrency().equals("")) {
            throw new ServiceException("giftCardCreateBatchRequest.getCurrency() is null or empty");
        }
        if (giftCardCreateBatchRequest.getBatchName() == null || giftCardCreateBatchRequest.getBatchName().equals("")) {
            throw new ServiceException("giftCardCreateBatchRequest.getBatchName() is null or empty");
        }
        if (giftCardCreateBatchRequest.getSource() == null || giftCardCreateBatchRequest.getSource().equals("")) {
            throw new ServiceException("giftCardCreateBatchRequest.getSource() is null or empty");
        }
        if (giftCardCreateBatchRequest.getMaxAmount() == null || giftCardCreateBatchRequest.getMaxAmount() == 0) {
            throw new ServiceException("giftCardCreateBatchRequest.getMaxAmount() is null or zero");
        }
        if (giftCardCreateBatchRequest.getValuesAndQuantities() == null || giftCardCreateBatchRequest.getValuesAndQuantities().isEmpty()) {
            throw new ServiceException("giftCardCreateBatchRequest.getValuesAndQuantities() is null or empty");
        }
        return Response
                .status(200)
                .entity(new GiftCardCreateBatch(giftCardCreateBatchRequest).getResponse())
                .build();
    }

}
