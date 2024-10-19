/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerAddBalanceToDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerAddDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerAddEscrowRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerEditDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerGetDollarBTCPaymentBalanceMovementsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerGetDollarBTCPaymentBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerGetOperationsNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerRemoveEscrowRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerSubstractBalanceToDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerTransferBetweenDollarBTCPaymentsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerAddBalanceToDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerAddCurrency;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerAddDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerAddEscrow;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerEditDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetDollarBTCPaymentBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetDollarBTCPaymentBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetDollarBTCPaymentBalances;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetOperationIndexesAndValues;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetOperationsNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerGetReferredUsers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerRemoveCurrency;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerRemoveEscrow;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerSubstractBalanceToDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.banker.BankerTransferBetweenDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/banker")
@XmlRegistry
public class BankerServiceREST {

    @GET
    @Path("/getCurrencies/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrencies(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerGetCurrencies(userName).getResponse())
                .build();
    }

    @POST
    @Path("/addDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addDollarBTCPayment(
            BankerAddDollarBTCPaymentRequest bankerAddDollarBTCPaymentRequest
    ) throws ServiceException {
        if (bankerAddDollarBTCPaymentRequest == null) {
            throw new ServiceException("bankerAddDollarBTCPaymentRequest is null");
        }
        if (bankerAddDollarBTCPaymentRequest.getUserName() == null || bankerAddDollarBTCPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("bankerAddDollarBTCPaymentRequest.getUserName() is null or empty");
        }
        if (bankerAddDollarBTCPaymentRequest.getCurrency() == null || bankerAddDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerAddDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (bankerAddDollarBTCPaymentRequest.getPayment() == null) {
            throw new ServiceException("bankerAddDollarBTCPaymentRequest.getPayment() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerAddDollarBTCPayment(bankerAddDollarBTCPaymentRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getDollarBTCPayment/{userName}/{currency}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPayment(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency,
            @PathParam("id") String id
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
        return Response
                .status(200)
                .entity(new BankerGetDollarBTCPayments(userName, null).getResponse())
                .build();
    }

    @GET
    @Path("/getDollarBTCPayments/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPayments(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerGetDollarBTCPayments(userName, null).getResponse())
                .build();
    }

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
                .entity(new BankerGetDollarBTCPayments(userName, currency).getResponse())
                .build();
    }
    
    @POST
    @Path("/getDollarBTCPaymentBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPaymentBalance(
            BankerGetDollarBTCPaymentBalanceRequest bankerGetDollarBTCPaymentBalanceRequest
    ) throws ServiceException {
        if (bankerGetDollarBTCPaymentBalanceRequest == null) {
            throw new ServiceException("bankerGetDollarBTCPaymentBalanceRequest is null");
        }
        if (bankerGetDollarBTCPaymentBalanceRequest.getCurrency() == null || bankerGetDollarBTCPaymentBalanceRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerGetDollarBTCPaymentBalanceRequest.getPaymentIds() is null or empty");
        }
        if (bankerGetDollarBTCPaymentBalanceRequest.getPaymentIds() == null || bankerGetDollarBTCPaymentBalanceRequest.getPaymentIds().length == 0) {
            throw new ServiceException("bankerGetDollarBTCPaymentBalanceRequest.getPaymentIds() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerGetDollarBTCPaymentBalance(bankerGetDollarBTCPaymentBalanceRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getDollarBTCPaymentBalances/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPaymentBalances(
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
                .entity(new BankerGetDollarBTCPaymentBalances(userName, currency, null, null).getResponse())
                .build();
    }

    @POST
    @Path("/getDollarBTCPaymentBalanceMovements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPaymentBalanceMovements(
            BankerGetDollarBTCPaymentBalanceMovementsRequest bankerGetDollarBTCPaymentBalanceMovementsRequest
    ) throws ServiceException {
        if (bankerGetDollarBTCPaymentBalanceMovementsRequest == null) {
            throw new ServiceException("bankerGetDollarBTCPaymentBalanceMovementsRequest is null");
        }
        if (bankerGetDollarBTCPaymentBalanceMovementsRequest.getCurrency() == null || bankerGetDollarBTCPaymentBalanceMovementsRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerGetDollarBTCPaymentBalanceMovementsRequest.getCurrency() is null or empty");
        }
        if (bankerGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds() == null || bankerGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds().length == 0) {
            throw new ServiceException("bankerGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerGetDollarBTCPaymentBalanceMovements(bankerGetDollarBTCPaymentBalanceMovementsRequest).getResponse())
                .build();
    }

    @POST
    @Path("/editDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editDollarBTCPayment(
            BankerEditDollarBTCPaymentRequest bankerEditDollarBTCPaymentRequest
    ) throws ServiceException {
        if (bankerEditDollarBTCPaymentRequest == null) {
            throw new ServiceException("bankerEditDollarBTCPaymentRequest is null");
        }
        if (bankerEditDollarBTCPaymentRequest.getId() == null || bankerEditDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("bankerEditDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (bankerEditDollarBTCPaymentRequest.getUserName() == null || bankerEditDollarBTCPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("bankerEditDollarBTCPaymentRequest.getUserName() is null or empty");
        }
        if (bankerEditDollarBTCPaymentRequest.getCurrency() == null || bankerEditDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerEditDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (bankerEditDollarBTCPaymentRequest.getPayment() == null) {
            throw new ServiceException("bankerEditDollarBTCPaymentRequest.getPayment() is null");
        }
        return Response
                .status(200)
                .entity(new BankerEditDollarBTCPayment(bankerEditDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/addBalanceToDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addBalanceToDollarBTCPayment(
            BankerAddBalanceToDollarBTCPaymentRequest bankerAddBalanceToDollarBTCPaymentRequest
    ) throws ServiceException {
        if (bankerAddBalanceToDollarBTCPaymentRequest == null) {
            throw new ServiceException("bankerAddBalanceToDollarBTCPaymentRequest is null");
        }
        if (bankerAddBalanceToDollarBTCPaymentRequest.getUserName() == null || bankerAddBalanceToDollarBTCPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("bankerAddBalanceToDollarBTCPaymentRequest.getUserName() is null or empty");
        }
        if (bankerAddBalanceToDollarBTCPaymentRequest.getCurrency() == null || bankerAddBalanceToDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerAddBalanceToDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (bankerAddBalanceToDollarBTCPaymentRequest.getId() == null || bankerAddBalanceToDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("bankerAddBalanceToDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (bankerAddBalanceToDollarBTCPaymentRequest.getAmount() == null || bankerAddBalanceToDollarBTCPaymentRequest.getAmount() == 0.0) {
            throw new ServiceException("bankerAddBalanceToDollarBTCPaymentRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new BankerAddBalanceToDollarBTCPayment(bankerAddBalanceToDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/substractBalanceToDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response substractBalanceToDollarBTCPayment(
            BankerSubstractBalanceToDollarBTCPaymentRequest bankerSubstractBalanceToDollarBTCPaymentRequest
    ) throws ServiceException {
        if (bankerSubstractBalanceToDollarBTCPaymentRequest == null) {
            throw new ServiceException("bankerSubstractBalanceToDollarBTCPaymentRequest is null");
        }
        if (bankerSubstractBalanceToDollarBTCPaymentRequest.getUserName() == null || bankerSubstractBalanceToDollarBTCPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("bankerSubstractBalanceToDollarBTCPaymentRequest.getUserName() is null or empty");
        }
        if (bankerSubstractBalanceToDollarBTCPaymentRequest.getCurrency() == null || bankerSubstractBalanceToDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerSubstractBalanceToDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (bankerSubstractBalanceToDollarBTCPaymentRequest.getId() == null || bankerSubstractBalanceToDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("bankerSubstractBalanceToDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (bankerSubstractBalanceToDollarBTCPaymentRequest.getAmount() == null || bankerSubstractBalanceToDollarBTCPaymentRequest.getAmount() == 0.0) {
            throw new ServiceException("bankerSubstractBalanceToDollarBTCPaymentRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new BankerSubstractBalanceToDollarBTCPayment(bankerSubstractBalanceToDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/getOperationsNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationsNew(
            BankerGetOperationsNewRequest bankerGetOperationsNewRequest
    ) throws ServiceException {
        if (bankerGetOperationsNewRequest == null) {
            throw new ServiceException("bankerGetOperationsNewRequest is null");
        }
        return Response
                .status(200)
                .entity(new BankerGetOperationsNew(bankerGetOperationsNewRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getOperationIndexesAndValues/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationIndexesAndValues(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BankerGetOperationIndexesAndValues(userName).getResponse())
                .build();
    }

    @POST
    @Path("/transferBetweenDollarBTCPayments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response transferBetweenDollarBTCPayments(
            BankerTransferBetweenDollarBTCPaymentsRequest bankerTransferBetweenDollarBTCPaymentsRequest
    ) throws ServiceException {
        if (bankerTransferBetweenDollarBTCPaymentsRequest == null) {
            throw new ServiceException("bankerTransferBetweenDollarBTCPaymentsRequest is null");
        }
        if (bankerTransferBetweenDollarBTCPaymentsRequest.getUserName() == null || bankerTransferBetweenDollarBTCPaymentsRequest.getUserName().equals("")) {
            throw new ServiceException("bankerTransferBetweenDollarBTCPaymentsRequest.getUserName() is null or empty");
        }
        if (bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency() == null || bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerTransferBetweenDollarBTCPaymentsRequest.getCurrency() is null or empty");
        }
        if (bankerTransferBetweenDollarBTCPaymentsRequest.getBaseId() == null || bankerTransferBetweenDollarBTCPaymentsRequest.getBaseId().equals("")) {
            throw new ServiceException("bankerTransferBetweenDollarBTCPaymentsRequest.getBaseId() is null or empty");
        }
        if (bankerTransferBetweenDollarBTCPaymentsRequest.getTargetId() == null || bankerTransferBetweenDollarBTCPaymentsRequest.getTargetId().equals("")) {
            throw new ServiceException("bankerTransferBetweenDollarBTCPaymentsRequest.getTargetId() is null or empty");
        }
        if (bankerTransferBetweenDollarBTCPaymentsRequest.getAmount() == null || bankerTransferBetweenDollarBTCPaymentsRequest.getAmount() == 0.0) {
            throw new ServiceException("bankerTransferBetweenDollarBTCPaymentsRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new BankerTransferBetweenDollarBTCPayments(bankerTransferBetweenDollarBTCPaymentsRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getConfig/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BankerList(userName).getResponse())
                .build();
    }

    @GET
    @Path("/list/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BankerList(userName).getResponse())
                .build();
    }

    @GET
    @Path("/addCurrency/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCurrency(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BankerAddCurrency(userName, currency).getResponse())
                .build();
    }

    @GET
    @Path("/removeCurrency/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCurrency(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BankerRemoveCurrency(userName, currency).getResponse())
                .build();
    }

    @GET
    @Path("/getReferredUsers/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReferredUsers(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerGetReferredUsers(userName).getResponse())
                .build();
    }

    @POST
    @Path("/changeOperationStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeOperationStatus(
            BankerChangeOperationStatusRequest bankerChangeOperationStatusRequest
    ) throws ServiceException {
        if (bankerChangeOperationStatusRequest == null) {
            throw new ServiceException("bankerChangeOperationStatusRequest is null");
        }
        if (bankerChangeOperationStatusRequest.getId() == null || bankerChangeOperationStatusRequest.getId().equals("")) {
            throw new ServiceException("bankerChangeOperationStatusRequest.getId() is null or empty");
        }
        if (bankerChangeOperationStatusRequest.getOtcOperationStatus() == null) {
            throw new ServiceException("bankerChangeOperationStatusRequest.getOtcOperationStatus() is null");
        }
        if (bankerChangeOperationStatusRequest.getCanceledReason() != null && !bankerChangeOperationStatusRequest.getCanceledReason().equals("") && !bankerChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)) {
            bankerChangeOperationStatusRequest.setCanceledReason(null);
        }
        if (bankerChangeOperationStatusRequest.getOtcOperationType() == null) {
            throw new ServiceException("bankerChangeOperationStatusRequest.getOtcOperationType() is null");
        }
        if (bankerChangeOperationStatusRequest.getOtcOperationType().equals(OTCOperationType.BUY)) {
            throw new ServiceException("bankerChangeOperationStatusRequest.getOtcOperationType() is not allowed");
        }
        if (bankerChangeOperationStatusRequest.getOtcOperationType().equals(OTCOperationType.SELL)) {
            throw new ServiceException("bankerChangeOperationStatusRequest.getOtcOperationType() is not allowed");
        }
        return Response
                .status(200)
                .entity(new BankerChangeOperationStatus(bankerChangeOperationStatusRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getOperation/{userName}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperation(
            @PathParam("userName") String userName,
            @PathParam("id") String id
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new BankerGetOperation(userName, id).getResponse())
                .build();
    }

    @PUT
    @Path("/addEscrow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addEscrow(
            BankerAddEscrowRequest bankerAddEscrowRequest
    ) throws ServiceException {
        if (bankerAddEscrowRequest == null) {
            throw new ServiceException("bankerAddEscrowRequest is null");
        }
        if (bankerAddEscrowRequest.getUserName() == null || bankerAddEscrowRequest.getUserName().equals("")) {
            throw new ServiceException("bankerAddEscrowRequest.getUserName() is null or empty");
        }
        if (bankerAddEscrowRequest.getCurrency() == null || bankerAddEscrowRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerAddEscrowRequest.getCurrency() is null or empty");
        }
        if (bankerAddEscrowRequest.getAmount() == null || bankerAddEscrowRequest.getAmount().equals(0.0)) {
            throw new ServiceException("bankerAddEscrowRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new BankerAddEscrow(bankerAddEscrowRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/removeEscrow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeEscrow(
            BankerRemoveEscrowRequest bankerRemoveEscrowRequest
    ) throws ServiceException {
        if (bankerRemoveEscrowRequest == null) {
            throw new ServiceException("bankerRemoveEscrowRequest is null");
        }
        if (bankerRemoveEscrowRequest.getUserName() == null || bankerRemoveEscrowRequest.getUserName().equals("")) {
            throw new ServiceException("bankerRemoveEscrowRequest.getUserName() is null or empty");
        }
        if (bankerRemoveEscrowRequest.getCurrency() == null || bankerRemoveEscrowRequest.getCurrency().equals("")) {
            throw new ServiceException("bankerRemoveEscrowRequest.getCurrency() is null or empty");
        }
        if (bankerRemoveEscrowRequest.getAmount() == null || bankerRemoveEscrowRequest.getAmount().equals(0.0)) {
            throw new ServiceException("bankerRemoveEscrowRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new BankerRemoveEscrow(bankerRemoveEscrowRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getEscrowBalance/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEscrowBalance(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(BaseOperation.getBalance(BankersFolderLocator.getEscrowFolder(userName)))
                .build();
    }
    
    @GET
    @Path("/getEscrowMovements/{userName}/{currency}/{initTimestamp}/{finalTimestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEscrowMovements(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency,
            @PathParam("initTimestamp") String initTimestamp,
            @PathParam("finalTimestamp") String finalTimestamp
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(BaseOperation.getBalanceMovements(
                        BankersFolderLocator.getEscrowFolder(userName), 
                        initTimestamp,
                        finalTimestamp,
                        null,
                        currency,
                        null
                        ))
                .build();
    }

}
