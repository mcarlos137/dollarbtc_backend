/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerAddDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerAddOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerEditDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerEditOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerRemoveOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerSendToPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerAddDynamicOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerAddOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerEditDynamicOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerEditOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerGetOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerGetOfferParams;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerRemoveOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broker.BrokerSendToPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/broker")
@XmlRegistry
public class BrokerServiceREST {
    
    @GET
    @Path("/getBalance/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if(userName == null || userName.equals("")){
            throw new ServiceException("userName is null or empty");
        }
        return Response
            .status(200)
            .entity(new BrokerGetBalance(userName).getResponse())
            .build();
    }
    
    @GET
    @Path("/getOfferParams/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfferParams(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if(currency == null || currency.equals("")){
            throw new ServiceException("currency is null or empty");
        }
        return Response
            .status(200)
            .entity(new BrokerGetOfferParams(currency).getResponse())
            .build();
    }
    
    @POST
    @Path("/addOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addOffer(
            BrokerAddOfferRequest brokerAddOfferRequest
    ) throws ServiceException {
        if (brokerAddOfferRequest == null) {
            throw new ServiceException("brokerAddOfferRequest is null");
        }
        if (brokerAddOfferRequest.getUserName() == null || brokerAddOfferRequest.getUserName().equals("")) {
            throw new ServiceException("brokerAddOfferRequest.getUserName() is null or empty");
        }
        if (brokerAddOfferRequest.getCurrency() == null || brokerAddOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("brokerAddOfferRequest.getCurrency() is null or empty");
        }
        if (brokerAddOfferRequest.getOfferType() == null) {
            throw new ServiceException("brokerAddOfferRequest.getOfferType() is null");
        }
        if (brokerAddOfferRequest.getPaymentId() == null || brokerAddOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("brokerAddOfferRequest.getPaymentId() is null or empty");
        }
        if (brokerAddOfferRequest.getPaymentType() == null) {
            throw new ServiceException("brokerAddOfferRequest.getPaymentType() is null");
        }
        if (brokerAddOfferRequest.getPrice() == null) {
            throw new ServiceException("brokerAddOfferRequest.getPrice() is null");
        }
        if (brokerAddOfferRequest.getMinPerOperationAmount() == null) {
            throw new ServiceException("brokerAddOfferRequest.getMinPerOperationAmount() is null");
        }
        if (brokerAddOfferRequest.getMaxPerOperationAmount() == null) {
            throw new ServiceException("brokerAddOfferRequest.getMaxPerOperationAmount() is null");
        }
        if (brokerAddOfferRequest.getTotalAmount() == null) {
            throw new ServiceException("brokerAddOfferRequest.getTotalAmount() is null");
        }
        return Response
                .status(200)
                .entity(new BrokerAddOffer(brokerAddOfferRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/addDynamicOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addDynamicOffer(
            BrokerAddDynamicOfferRequest brokerAddDynamicOfferRequest
    ) throws ServiceException {
        if (brokerAddDynamicOfferRequest == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest is null");
        }
        if (brokerAddDynamicOfferRequest.getUserName() == null || brokerAddDynamicOfferRequest.getUserName().equals("")) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getUserName() is null or empty");
        }
        if (brokerAddDynamicOfferRequest.getCurrency() == null || brokerAddDynamicOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getCurrency() is null or empty");
        }
        if (brokerAddDynamicOfferRequest.getOfferType() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getOfferType() is null");
        }
        if (brokerAddDynamicOfferRequest.getPaymentId() == null || brokerAddDynamicOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getPaymentId() is null or empty");
        }
        if (brokerAddDynamicOfferRequest.getPaymentType() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getPaymentType() is null");
        }
        if (brokerAddDynamicOfferRequest.getSource() == null && !brokerAddDynamicOfferRequest.getSource().equals("")) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getPrice() is null or empty");
        }
        if (brokerAddDynamicOfferRequest.getLimitPrice() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getLimitPrice() is null");
        }
        if (brokerAddDynamicOfferRequest.getMarginPercent() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getMarginPercent() is null");
        }
        if (brokerAddDynamicOfferRequest.getSpreadPercent()== null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getSpreadPercent() is null");
        }
        if (brokerAddDynamicOfferRequest.getMinPerOperationAmount() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getMinPerOperationAmount() is null");
        }
        if (brokerAddDynamicOfferRequest.getMaxPerOperationAmount() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getMaxPerOperationAmount() is null");
        }
        if (brokerAddDynamicOfferRequest.getTotalAmount() == null) {
            throw new ServiceException("brokerAddDynamicOfferRequest.getTotalAmount() is null");
        }
        return Response
                .status(200)
                .entity(new BrokerAddDynamicOffer(brokerAddDynamicOfferRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/editOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editOffer(
            BrokerEditOfferRequest brokerEditOfferRequest
    ) throws ServiceException {
        if (brokerEditOfferRequest == null) {
            throw new ServiceException("brokerEditOfferRequest is null");
        }
        if (brokerEditOfferRequest.getUserName() == null || brokerEditOfferRequest.getUserName().equals("")) {
            throw new ServiceException("brokerEditOfferRequest.getUserName() is null or empty");
        }
        if (brokerEditOfferRequest.getCurrency() == null || brokerEditOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("brokerEditOfferRequest.getCurrency() is null or empty");
        }
        if (brokerEditOfferRequest.getPaymentId() == null || brokerEditOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("brokerEditOfferRequest.getPaymentId() is null or empty");
        }
        if (brokerEditOfferRequest.getOfferType() == null) {
            throw new ServiceException("brokerEditOfferRequest.getOfferType() is null");
        }
        if (brokerEditOfferRequest.getPaymentType() == null) {
            throw new ServiceException("brokerEditOfferRequest.getPaymentType() is null");
        }
        return Response
                .status(200)
                .entity(new BrokerEditOffer(brokerEditOfferRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/editDynamicOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editDynamicOffer(
            BrokerEditDynamicOfferRequest brokerEditDynamicOfferRequest
    ) throws ServiceException {
        if (brokerEditDynamicOfferRequest == null) {
            throw new ServiceException("brokerEditDynamicOfferRequest is null");
        }
        if (brokerEditDynamicOfferRequest.getUserName() == null || brokerEditDynamicOfferRequest.getUserName().equals("")) {
            throw new ServiceException("brokerEditDynamicOfferRequest.getUserName() is null or empty");
        }
        if (brokerEditDynamicOfferRequest.getCurrency() == null || brokerEditDynamicOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("brokerEditDynamicOfferRequest.getCurrency() is null or empty");
        }
        if (brokerEditDynamicOfferRequest.getPaymentId() == null || brokerEditDynamicOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("brokerEditDynamicOfferRequest.getPaymentId() is null or empty");
        }
        if (brokerEditDynamicOfferRequest.getOfferType() == null) {
            throw new ServiceException("brokerEditDynamicOfferRequest.getOfferType() is null");
        }
        if (brokerEditDynamicOfferRequest.getPaymentType() == null) {
            throw new ServiceException("brokerEditDynamicOfferRequest.getPaymentType() is null");
        }
        return Response
                .status(200)
                .entity(new BrokerEditDynamicOffer(brokerEditDynamicOfferRequest).getResponse())
                .build();
    }

    @POST
    @Path("/removeOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeOffer(
            BrokerRemoveOfferRequest brokerRemoveOfferRequest
    ) throws ServiceException {
        if (brokerRemoveOfferRequest == null) {
            throw new ServiceException("brokerRemoveOfferRequest is null");
        }
        if (brokerRemoveOfferRequest.getUserName() == null || brokerRemoveOfferRequest.getUserName().equals("")) {
            throw new ServiceException("brokerRemoveOfferRequest.getUserName() is null or empty");
        }
        if (brokerRemoveOfferRequest.getCurrency() == null || brokerRemoveOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("brokerRemoveOfferRequest.getCurrency() is null or empty");
        }
        if (brokerRemoveOfferRequest.getPaymentId() == null || brokerRemoveOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("brokerRemoveOfferRequest.getPaymentId() is null or empty");
        }
        if (brokerRemoveOfferRequest.getOfferType() == null) {
            throw new ServiceException("brokerRemoveOfferRequest.getOfferType() is null");
        }
        if (brokerRemoveOfferRequest.getPaymentType() == null) {
            throw new ServiceException("brokerRemoveOfferRequest.getPaymentType() is null");
        }
        return Response
                .status(200)
                .entity(new BrokerRemoveOffer(brokerRemoveOfferRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getOffer/{encryptedOfferKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffer(
            @PathParam("encryptedOfferKey") String encryptedOfferKey
    ) throws ServiceException {
        if(encryptedOfferKey == null || encryptedOfferKey.equals("")){
            throw new ServiceException("encryptedOfferKey is null or empty");
        }
        return Response
            .status(200)
            .entity(new BrokerGetOffer(encryptedOfferKey).getResponse())
            .build();
    }

    @GET
    @Path("/getOffers/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BrokerGetOffers(userName, null, null, null, null, false).getResponse())
                .build();
    }

    @GET
    @Path("/getOffers/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BrokerGetOffers(userName, currency, null, null, null, false).getResponse())
                .build();
    }

    @GET
    @Path("/getOffers/{userName}/{currency}/{paymentId}/{offerType}/{paymentType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency,
            @PathParam("paymentId") String paymentId,
            @PathParam("offerType") OfferType offerType,
            @PathParam("paymentType") PaymentType paymentType
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BrokerGetOffers(userName, currency, paymentId, offerType, paymentType, false).getResponse())
                .build();
    }

    @GET
    @Path("/getOldOffers/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOldOffers(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BrokerGetOffers(userName, null, null, null, null, true).getResponse())
                .build();
    }
    
    @POST
    @Path("/sendToPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendToPayment(BrokerSendToPaymentRequest brokerSendToPaymentRequest) throws ServiceException {
        if (brokerSendToPaymentRequest == null) {
            throw new ServiceException("brokerSendToPaymentRequest is null");
        }
        if (brokerSendToPaymentRequest.getUserName() == null || brokerSendToPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("brokerSendToPaymentRequest.getUserName() is null or empty");
        }
        if (brokerSendToPaymentRequest.getCurrency() == null || brokerSendToPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("brokerSendToPaymentRequest.getCurrency() is null or empty");
        }
        if (brokerSendToPaymentRequest.getAmount() == null || brokerSendToPaymentRequest.getAmount().equals(0.0)) {
            throw new ServiceException("brokerSendToPaymentRequest.getAmount() is null or empty");
        }
        if (brokerSendToPaymentRequest.getPayment() == null) {
            throw new ServiceException("brokerSendToPaymentRequest.getPayment() is null");
        }
        return Response
                .status(200)
                .entity(new BrokerSendToPayment(brokerSendToPaymentRequest).getResponse())
                .build();
    }
    
}
