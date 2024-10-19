/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.buyBalance.BuyBalanceChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.buyBalance.BuyBalanceCreateOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.buybalance.BuyBalanceChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.buybalance.BuyBalanceCreateOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.buybalance.BuyBalanceGetDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
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
@Path("/buyBalance")
@XmlRegistry
public class BuyBalanceServiceREST {
    
    @GET
    @Path("/getDollarBTCPayments/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPayments(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new BuyBalanceGetDollarBTCPayments(userName, currency).getResponse())
                .build();
    }
    
    @POST
    @Path("/createOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createOperation(BuyBalanceCreateOperationRequest buyBalanceCreateOperationRequest) throws ServiceException {
        if (buyBalanceCreateOperationRequest == null) {
            throw new ServiceException("buyBalanceCreateOperationRequest is null");
        }
        if (buyBalanceCreateOperationRequest.getUserName() == null || buyBalanceCreateOperationRequest.getUserName().equals("")) {
            throw new ServiceException("buyBalanceCreateOperationRequest.getUserName() is null or empty");
        }
        if (buyBalanceCreateOperationRequest.getCurrency() == null || buyBalanceCreateOperationRequest.getCurrency().equals("")) {
            throw new ServiceException("buyBalanceCreateOperationRequest.getCurrency() is null or empty");
        }
        if (buyBalanceCreateOperationRequest.getAmount() == null || buyBalanceCreateOperationRequest.getAmount().equals(0.0)) {
            throw new ServiceException("buyBalanceCreateOperationRequest.getAmount() is null or empty");
        }
        if (buyBalanceCreateOperationRequest.getDollarBTCPayment() == null) {
            throw new ServiceException("buyBalanceCreateOperationRequest.getDollarBTCPayment() is null");
        }
        return Response
                .status(200)
                .entity(new BuyBalanceCreateOperation(buyBalanceCreateOperationRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/changeOperationStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeOperationStatus(
            BuyBalanceChangeOperationStatusRequest buyBalanceChangeOperationStatusRequest
    ) throws ServiceException {
        System.out.println("0 - getId " + buyBalanceChangeOperationStatusRequest.getId());
        System.out.println("0 - getOtcOperationStatus " + buyBalanceChangeOperationStatusRequest.getOtcOperationStatus());
        System.out.println("0 - getCanceledReason " + buyBalanceChangeOperationStatusRequest.getCanceledReason());
        if (buyBalanceChangeOperationStatusRequest == null) {
            throw new ServiceException("buyBalanceChangeOperationStatusRequest is null");
        }
        System.out.println("1");
        if (buyBalanceChangeOperationStatusRequest.getId() == null || buyBalanceChangeOperationStatusRequest.getId().equals("")) {
            throw new ServiceException("buyBalanceChangeOperationStatusRequest.getId() is null or empty");
        }
        System.out.println("2");
        if (buyBalanceChangeOperationStatusRequest.getOtcOperationStatus() == null) {
            throw new ServiceException("buyBalanceChangeOperationStatusRequest.getOtcOperationStatus() is null");
        }
        System.out.println("3");
        if(buyBalanceChangeOperationStatusRequest.getCanceledReason() != null && !buyBalanceChangeOperationStatusRequest.getCanceledReason().equals("") && !buyBalanceChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)){
            buyBalanceChangeOperationStatusRequest.setCanceledReason(null);
        }
        System.out.println("4");
        return Response
                .status(200)
                .entity(new BuyBalanceChangeOperationStatus(buyBalanceChangeOperationStatusRequest).getResponse())
                .build();
    }
    
}
