/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallBlockUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallPayRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallUnblockUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallBlockUser;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallChangeStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallGetBlockedUsers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallOverviewData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallOverviewId;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallPay;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall.MoneyCallUnblockUser;
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
@Path("/moneyCall")
@XmlRegistry
public class MoneyCallServiceREST {

    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response pay(MoneyCallPayRequest moneyCallPayRequest) throws ServiceException {
        if (moneyCallPayRequest == null) {
            throw new ServiceException("moneyCallPayRequest is null");
        }
        if (moneyCallPayRequest.getId() == null || moneyCallPayRequest.getId().equals("")) {
            throw new ServiceException("moneyCallPayRequest.getId() is null or empty");
        }
        if (moneyCallPayRequest.getTime() == null || moneyCallPayRequest.getTime() == 0) {
            throw new ServiceException("moneyCallPayRequest.getTime() is null or zero");
        }
        return Response
                .status(200)
                .entity(new MoneyCallPay(moneyCallPayRequest).getResponse())
                .build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(
            MoneyCallListRequest moneyCallListRequest
    ) throws ServiceException {
        if (moneyCallListRequest == null) {
            throw new ServiceException("moneyCallListRequest is null");
        }
        return Response
                .status(200)
                .entity(new MoneyCallList(moneyCallListRequest).getResponse())
                .build();
    }

    @GET
    @Path("/overviewId/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response overviewId(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new MoneyCallOverviewId(userName).getResponse())
                .build();
    }

    @GET
    @Path("/overviewData/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response overviewData(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new MoneyCallOverviewData(userName).getResponse())
                .build();
    }

    @POST
    @Path("/changeStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeStatus(MoneyCallChangeStatusRequest moneyCallChangeStatusRequest) throws ServiceException {
        if (moneyCallChangeStatusRequest == null) {
            throw new ServiceException("moneyCallChangeStatusRequest is null");
        }
        if (moneyCallChangeStatusRequest.getId() == null || moneyCallChangeStatusRequest.getId().equals("")) {
            throw new ServiceException("moneyCallChangeStatusRequest.getId() is null or empty");
        }
        if (moneyCallChangeStatusRequest.getStatus() == null || moneyCallChangeStatusRequest.getStatus().equals("")) {
            throw new ServiceException("moneyCallChangeStatusRequest.getStatus() is null or empty");
        }
        if (moneyCallChangeStatusRequest.getUserName() == null || moneyCallChangeStatusRequest.getUserName().equals("")) {
            throw new ServiceException("moneyCallChangeStatusRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MoneyCallChangeStatus(moneyCallChangeStatusRequest).getResponse())
                .build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(MoneyCallCreateRequest moneyCallCreateRequest) throws ServiceException {
        if (moneyCallCreateRequest == null) {
            throw new ServiceException("moneyCallCreateRequest is null");
        }
        if (moneyCallCreateRequest.getType() == null || moneyCallCreateRequest.getType().equals("")) {
            throw new ServiceException("moneyCallCreateRequest.getType() is null or empty");
        }
        if (moneyCallCreateRequest.getCreateUserName() == null || moneyCallCreateRequest.getCreateUserName().equals("")) {
            throw new ServiceException("moneyCallCreateRequest.getCreateUserName() is null or empty");
        }
        if (moneyCallCreateRequest.getSenderUserName() == null || moneyCallCreateRequest.getSenderUserName().equals("")) {
            throw new ServiceException("moneyCallCreateRequest.getSenderUserName() is null or empty");
        }
        if (moneyCallCreateRequest.getReceiverUserName() == null || moneyCallCreateRequest.getReceiverUserName().equals("")) {
            throw new ServiceException("moneyCallCreateRequest.getReceiverUserName() is null or empty");
        }
        if (moneyCallCreateRequest.getCurrency() == null || moneyCallCreateRequest.getCurrency().equals("")) {
            throw new ServiceException("moneyCallCreateRequest.getCurrency() is null or empty");
        }
        if (moneyCallCreateRequest.getRate() == null || moneyCallCreateRequest.getRate() == 0.0) {
            throw new ServiceException("moneyCallCreateRequest.getRate() is null or zero");
        }
        return Response
                .status(200)
                .entity(new MoneyCallCreate(moneyCallCreateRequest).getResponse())
                .build();
    }

    @POST
    @Path("/blockUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response blockUser(MoneyCallBlockUserRequest moneyCallBlockUserRequest) throws ServiceException {
        if (moneyCallBlockUserRequest == null) {
            throw new ServiceException("moneyCallBlockUserRequest is null");
        }
        if (moneyCallBlockUserRequest.getUserName() == null || moneyCallBlockUserRequest.getUserName().equals("")) {
            throw new ServiceException("moneyCallBlockUserRequest.getUserName() is null or empty");
        }
        if (moneyCallBlockUserRequest.getOtherUserName() == null || moneyCallBlockUserRequest.getOtherUserName().equals("")) {
            throw new ServiceException("moneyCallBlockUserRequest.getOtherUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MoneyCallBlockUser(moneyCallBlockUserRequest).getResponse())
                .build();
    }

    @POST
    @Path("/unblockUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response unblockUser(MoneyCallUnblockUserRequest moneyCallUnblockUserRequest) throws ServiceException {
        if (moneyCallUnblockUserRequest == null) {
            throw new ServiceException("moneyCallUnblockUserRequest is null");
        }
        if (moneyCallUnblockUserRequest.getUserName() == null || moneyCallUnblockUserRequest.getUserName().equals("")) {
            throw new ServiceException("moneyCallUnblockUserRequest.getUserName() is null or empty");
        }
        if (moneyCallUnblockUserRequest.getOtherUserName() == null || moneyCallUnblockUserRequest.getOtherUserName().equals("")) {
            throw new ServiceException("moneyCallUnblockUserRequest.getOtherUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MoneyCallUnblockUser(moneyCallUnblockUserRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getBlockedUsers/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlockedUsers(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new MoneyCallGetBlockedUsers(userName).getResponse())
                .build();
    }

}
