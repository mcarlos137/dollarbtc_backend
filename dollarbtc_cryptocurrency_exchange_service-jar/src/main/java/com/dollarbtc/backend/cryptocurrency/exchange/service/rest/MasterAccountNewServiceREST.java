/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccountnew.MasterAccountNewGetProfitsAndChargesBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountBalances;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountNames;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountProfitsAndChargesBalance;
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
@Path("/masterAccountNew")
@XmlRegistry
public class MasterAccountNewServiceREST {

    @GET
    @Path("/getOTCMasterAccountNames/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOTCMasterAccountNames(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new MasterAccountNewGetOTCMasterAccountNames(userName).getResponse())
                .build();
    }

    @GET
    @Path("/getOTCMasterAccountBalances/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOTCMasterAccountBalances(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new MasterAccountNewGetOTCMasterAccountBalances(userName).getResponse())
                .build();
    }

    @POST
    @Path("/getProfitsAndChargesBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOTCMasterAccountProfitsAndChargesBalance(
            MasterAccountNewGetProfitsAndChargesBalanceRequest masterAccountNewGetProfitsAndChargesBalanceRequest
    ) throws ServiceException {
        if (masterAccountNewGetProfitsAndChargesBalanceRequest == null) {
            throw new ServiceException("masterAccountNewGetProfitsAndChargesBalanceRequest is null");
        }
        if (masterAccountNewGetProfitsAndChargesBalanceRequest.getUserName() == null || masterAccountNewGetProfitsAndChargesBalanceRequest.getUserName().equals("")) {
            throw new ServiceException("masterAccountNewGetProfitsAndChargesBalanceRequest.getUserName() is null or empty");
        }
        if (masterAccountNewGetProfitsAndChargesBalanceRequest.getMasterAccountName() == null || masterAccountNewGetProfitsAndChargesBalanceRequest.getMasterAccountName().equals("")) {
            throw new ServiceException("masterAccountNewGetProfitsAndChargesBalanceRequest.getMasterAccountName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MasterAccountNewGetOTCMasterAccountProfitsAndChargesBalance(masterAccountNewGetProfitsAndChargesBalanceRequest).getResponse())
                .build();
    }

}
