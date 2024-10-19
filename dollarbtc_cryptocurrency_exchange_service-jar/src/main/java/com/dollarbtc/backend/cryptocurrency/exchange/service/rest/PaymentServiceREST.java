/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.payment.PaymentCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentBank;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.payment.PaymentCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.payment.PaymentGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author CarlosDaniel
 */
@Path("/payment")
@XmlRegistry
public class PaymentServiceREST {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(PaymentCreateRequest paymentCreateRequest) throws ServiceException {
        if (paymentCreateRequest == null) {
            throw new ServiceException("paymentCreateRequest is null");
        }
        if (paymentCreateRequest.getUserName() == null || paymentCreateRequest.getUserName().equals("")) {
            throw new ServiceException("paymentCreateRequest.getUserName() is null or empty");
        }
        if (paymentCreateRequest.getCurrency() == null || paymentCreateRequest.getCurrency().equals("")) {
            throw new ServiceException("paymentCreateRequest.getCurrency() is null or empty");
        }
        if (paymentCreateRequest.getBankLogin() == null || paymentCreateRequest.getBankLogin().equals("")) {
            throw new ServiceException("paymentCreateRequest.getBankLogin() is null or empty");
        }
        if (paymentCreateRequest.getBankPassword() == null || paymentCreateRequest.getBankPassword().equals("")) {
            throw new ServiceException("paymentCreateRequest.getBankPassword() is null or empty");
        }
        if (paymentCreateRequest.getBankPassword() == null) {
            throw new ServiceException("paymentCreateRequest.getPaymentBank() is null");
        }
        if (paymentCreateRequest.getPaymentType() == null) {
            throw new ServiceException("paymentCreateRequest.getPaymentType() is null");
        }
        return Response
                .status(200)
                .entity(new PaymentCreate(paymentCreateRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getBalance/{userName}/{currency}/{id}/{paymentBank}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency,
            @PathParam("id") String id,
            @PathParam("paymentBank") PaymentBank paymentBank
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        if (paymentBank == null) {
            throw new ServiceException("paymentBank is null");
        }
        return Response
                .status(200)
                .entity(new PaymentGetBalance(userName, currency, id, paymentBank).getResponse())
                .build();
    }

}
