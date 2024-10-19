/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddBalanceToDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCCreateOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCEditDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetDollarBTCPaymentBalanceMovementsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetDollarBTCPaymentBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCModifyOperationCheckListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCRemoveOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCConfirmThirdPartySendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCEditDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCEditOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCFastChangeFromBTCRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCFastChangeToBTCRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetChargesNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetChargesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCSubstractBalanceToDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCCreateOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OperationMessageSide;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCAddBalanceToDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCAddDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCAddDynamicOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCAddOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCAddPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCConfirmThirdPartySend;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCEditDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCEditDynamicOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCEditOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCFastChangeFromToBTC;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetAllowedAddPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetAutomaticChatMessages;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetCharges;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetChargesNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetClientPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetClientPaymentTypes;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetCurrenciesOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPaymentBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPaymentBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPaymentBalances;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetLimits;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOfficesInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperationCheckList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperationIndexesAndValues;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperationsOverall;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetThirdPartySendData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCModifyOperationCheckList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCRemoveOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCRemovePayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCSubstractBalanceToDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
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
@Path("/otc")
@XmlRegistry
public class OTCServiceREST {

    @GET
    @Path("/getCurrencies")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrencies() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetCurrencies(true, true).getResponse())
                .build();
    }
    
    @GET
    @Path("/getCurrenciesWithUSDT")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrenciesWithUSDT() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetCurrencies(true, false).getResponse())
                .build();
    }

    @GET
    @Path("/getCurrenciesWithCrypto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrenciesWithCrypto() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetCurrencies(true, false).getResponse())
                .build();
    }
    
    @GET
    @Path("/getClientPaymentTypes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientPaymentTypes() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetClientPaymentTypes(null).getResponse())
                .build();
    }

    @GET
    @Path("/getClientPaymentTypes/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientPaymentTypes(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetClientPaymentTypes(currency).getResponse())
                .build();
    }

    @GET
    @Path("/getClientPayment/{userName}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientPayment(
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
                .entity(new OTCGetClientPayment(userName, id).getResponse())
                .build();
    }

    @GET
    @Path("/getPayments/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayments(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetPayments(userName, null).getResponse())
                .build();
    }

    @GET
    @Path("/getPayments/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayments(
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
                .entity(new OTCGetPayments(userName, currency).getResponse())
                .build();
    }

    @POST
    @Path("/addPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addPayment(
            OTCAddPaymentRequest otcAddPaymentRequest
    ) throws ServiceException {
        if (otcAddPaymentRequest == null) {
            throw new ServiceException("otcAddPaymentRequest is null");
        }
        if (otcAddPaymentRequest.getUserName() == null || otcAddPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("otcAddPaymentRequest.getUserName() is null or empty");
        }
        if (otcAddPaymentRequest.getCurrency() == null || otcAddPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAddPaymentRequest.getCurrency() is null or empty");
        }
        if (otcAddPaymentRequest.getPayment() == null) {
            throw new ServiceException("otcAddPaymentRequest.getPayment() is null");
        }
        return Response
                .status(200)
                .entity(new OTCAddPayment(otcAddPaymentRequest).getResponse())
                .build();
    }

    @GET
    @Path("/removePayment/{userName}/{currency}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removePayment(
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
                .entity(new OTCRemovePayment(userName, currency, id).getResponse())
                .build();
    }

    @POST
    @Path("/addDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addDollarBTCPayment(
            OTCAddDollarBTCPaymentRequest otcAddDollarBTCPaymentRequest
    ) throws ServiceException {
        if (otcAddDollarBTCPaymentRequest == null) {
            throw new ServiceException("otcAddDollarBTCPaymentRequest is null");
        }
        if (otcAddDollarBTCPaymentRequest.getCurrency() == null || otcAddDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAddDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (otcAddDollarBTCPaymentRequest.getPayment() == null) {
            throw new ServiceException("otcAddDollarBTCPaymentRequest.getPayment() is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCAddDollarBTCPayment(otcAddDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getDollarBTCPayment/{currency}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPayment(
            @PathParam("currency") String currency,
            @PathParam("id") String id
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetDollarBTCPayment(currency, id).getResponse())
                .build();
    }

    @POST
    @Path("/getDollarBTCPaymentBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPaymentBalance(
            OTCGetDollarBTCPaymentBalanceRequest otcGetDollarBTCPaymentBalanceRequest
    ) throws ServiceException {
        if (otcGetDollarBTCPaymentBalanceRequest == null) {
            throw new ServiceException("otcGetDollarBTCPaymentBalanceRequest is null");
        }
        if (otcGetDollarBTCPaymentBalanceRequest.getCurrency() == null || otcGetDollarBTCPaymentBalanceRequest.getCurrency().equals("")) {
            throw new ServiceException("otcGetDollarBTCPaymentBalanceRequest.getPaymentIds() is null or empty");
        }
        if (otcGetDollarBTCPaymentBalanceRequest.getPaymentIds() == null || otcGetDollarBTCPaymentBalanceRequest.getPaymentIds().length == 0) {
            throw new ServiceException("otcGetDollarBTCPaymentBalanceRequest.getPaymentIds() is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetDollarBTCPaymentBalance(otcGetDollarBTCPaymentBalanceRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getDollarBTCPaymentBalances/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPaymentBalances(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetDollarBTCPaymentBalances(currency, null, null).getResponse())
                .build();
    }

    @POST
    @Path("/getDollarBTCPaymentBalanceMovements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDollarBTCPaymentBalanceMovements(
            OTCGetDollarBTCPaymentBalanceMovementsRequest otcGetDollarBTCPaymentBalanceMovementsRequest
    ) throws ServiceException {
        if (otcGetDollarBTCPaymentBalanceMovementsRequest == null) {
            throw new ServiceException("otcGetDollarBTCPaymentBalanceMovementsRequest is null");
        }
        if (otcGetDollarBTCPaymentBalanceMovementsRequest.getCurrency() == null || otcGetDollarBTCPaymentBalanceMovementsRequest.getCurrency().equals("")) {
            throw new ServiceException("otcGetDollarBTCPaymentBalanceMovementsRequest.getCurrency() is null or empty");
        }
        if (otcGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds() == null || otcGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds().length == 0) {
            throw new ServiceException("otcGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds() is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetDollarBTCPaymentBalanceMovements(otcGetDollarBTCPaymentBalanceMovementsRequest).getResponse())
                .build();
    }

    @POST
    @Path("/editDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editDollarBTCPayment(
            OTCEditDollarBTCPaymentRequest otcEditDollarBTCPaymentRequest
    ) throws ServiceException {
        if (otcEditDollarBTCPaymentRequest == null) {
            throw new ServiceException("otcEditDollarBTCPaymentRequest is null");
        }
        if (otcEditDollarBTCPaymentRequest.getId() == null || otcEditDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("otcEditDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (otcEditDollarBTCPaymentRequest.getCurrency() == null || otcEditDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcEditDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (otcEditDollarBTCPaymentRequest.getPayment() == null) {
            throw new ServiceException("otcEditDollarBTCPaymentRequest.getPayment() is null");
        }
        return Response
                .status(200)
                .entity(new OTCEditDollarBTCPayment(otcEditDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/addBalanceToDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addBalanceToDollarBTCPayment(
            OTCAddBalanceToDollarBTCPaymentRequest otcAddBalanceToDollarBTCPaymentRequest
    ) throws ServiceException {
        if (otcAddBalanceToDollarBTCPaymentRequest == null) {
            throw new ServiceException("otcAddBalanceToDollarBTCPaymentRequest is null");
        }
        if (otcAddBalanceToDollarBTCPaymentRequest.getCurrency() == null || otcAddBalanceToDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAddBalanceToDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (otcAddBalanceToDollarBTCPaymentRequest.getId() == null || otcAddBalanceToDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("otcAddBalanceToDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (otcAddBalanceToDollarBTCPaymentRequest.getAmount() == null || otcAddBalanceToDollarBTCPaymentRequest.getAmount() == 0.0) {
            throw new ServiceException("otcAddBalanceToDollarBTCPaymentRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCAddBalanceToDollarBTCPayment(otcAddBalanceToDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/substractBalanceToDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response substractBalanceToDollarBTCPayment(
            OTCSubstractBalanceToDollarBTCPaymentRequest otcSubstractBalanceToDollarBTCPaymentRequest
    ) throws ServiceException {
        if (otcSubstractBalanceToDollarBTCPaymentRequest == null) {
            throw new ServiceException("otcSubstractBalanceToDollarBTCPaymentRequest is null");
        }
        if (otcSubstractBalanceToDollarBTCPaymentRequest.getCurrency() == null || otcSubstractBalanceToDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcSubstractBalanceToDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (otcSubstractBalanceToDollarBTCPaymentRequest.getId() == null || otcSubstractBalanceToDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("otcSubstractBalanceToDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (otcSubstractBalanceToDollarBTCPaymentRequest.getAmount() == null || otcSubstractBalanceToDollarBTCPaymentRequest.getAmount() == 0.0) {
            throw new ServiceException("otcSubstractBalanceToDollarBTCPaymentRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCSubstractBalanceToDollarBTCPayment(otcSubstractBalanceToDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/addOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addOffer(
            OTCAddOfferRequest otcAddOfferRequest
    ) throws ServiceException {
        if (otcAddOfferRequest == null) {
            throw new ServiceException("otcAddOfferRequest is null");
        }
        if (otcAddOfferRequest.getCurrency() == null || otcAddOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAddOfferRequest.getCurrency() is null or empty");
        }
        if (otcAddOfferRequest.getOfferType() == null) {
            throw new ServiceException("otcAddOfferRequest.getOfferType() is null");
        }
        if (otcAddOfferRequest.getPaymentId() == null || otcAddOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("otcAddOfferRequest.getPaymentId() is null or empty");
        }
        if (otcAddOfferRequest.getPaymentType() == null) {
            throw new ServiceException("otcAddOfferRequest.getPaymentType() is null");
        }
        if (otcAddOfferRequest.getPrice() == null) {
            throw new ServiceException("otcAddOfferRequest.getPrice() is null");
        }
        if (otcAddOfferRequest.getMinPerOperationAmount() == null) {
            throw new ServiceException("otcAddOfferRequest.getMinPerOperationAmount() is null");
        }
        if (otcAddOfferRequest.getMaxPerOperationAmount() == null) {
            throw new ServiceException("otcAddOfferRequest.getMaxPerOperationAmount() is null");
        }
        if (otcAddOfferRequest.getTotalAmount() == null) {
            throw new ServiceException("otcAddOfferRequest.getTotalAmount() is null");
        }
        return Response
                .status(200)
                .entity(new OTCAddOffer(otcAddOfferRequest).getResponse())
                .build();
    }

    @POST
    @Path("/addDynamicOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addDynamicOffer(
            OTCAddDynamicOfferRequest otcAddDynamicOfferRequest
    ) throws ServiceException {
        if (otcAddDynamicOfferRequest == null) {
            throw new ServiceException("otcAddDynamicOfferRequest is null");
        }
        if (otcAddDynamicOfferRequest.getCurrency() == null || otcAddDynamicOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAddDynamicOfferRequest.getCurrency() is null or empty");
        }
        if (otcAddDynamicOfferRequest.getOfferType() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getOfferType() is null");
        }
        if (otcAddDynamicOfferRequest.getPaymentId() == null || otcAddDynamicOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("otcAddDynamicOfferRequest.getPaymentId() is null or empty");
        }
        if (otcAddDynamicOfferRequest.getPaymentType() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getPaymentType() is null");
        }
        if (otcAddDynamicOfferRequest.getSource() == null && !otcAddDynamicOfferRequest.getSource().equals("")) {
            throw new ServiceException("otcAddDynamicOfferRequest.getPrice() is null or empty");
        }
        if (otcAddDynamicOfferRequest.getLimitPrice() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getLimitPrice() is null");
        }
        if (otcAddDynamicOfferRequest.getMarginPercent() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getMarginPercent() is null");
        }
        if (otcAddDynamicOfferRequest.getSpreadPercent() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getSpreadPercent() is null");
        }
        if (otcAddDynamicOfferRequest.getMinPerOperationAmount() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getMinPerOperationAmount() is null");
        }
        if (otcAddDynamicOfferRequest.getMaxPerOperationAmount() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getMaxPerOperationAmount() is null");
        }
        if (otcAddDynamicOfferRequest.getTotalAmount() == null) {
            throw new ServiceException("otcAddDynamicOfferRequest.getTotalAmount() is null");
        }
        return Response
                .status(200)
                .entity(new OTCAddDynamicOffer(otcAddDynamicOfferRequest).getResponse())
                .build();
    }

    @POST
    @Path("/editOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editOffer(
            OTCEditOfferRequest otcEditOfferRequest
    ) throws ServiceException {
        if (otcEditOfferRequest == null) {
            throw new ServiceException("otcEditOfferRequest is null");
        }
        if (otcEditOfferRequest.getCurrency() == null || otcEditOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("otcEditOfferRequest.getCurrency() is null or empty");
        }
        if (otcEditOfferRequest.getPaymentId() == null || otcEditOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("otcEditOfferRequest.getPaymentId() is null or empty");
        }
        if (otcEditOfferRequest.getOfferType() == null) {
            throw new ServiceException("otcEditOfferRequest.getOfferType() is null");
        }
        if (otcEditOfferRequest.getPaymentType() == null) {
            throw new ServiceException("otcEditOfferRequest.getPaymentType() is null");
        }
        if (otcEditOfferRequest.getUseChangePriceByOperationBalance() == null) {
            throw new ServiceException("otcEditOfferRequest.getUseChangePriceByOperationBalance() is null");
        }
        return Response
                .status(200)
                .entity(new OTCEditOffer(otcEditOfferRequest).getResponse())
                .build();
    }

    @POST
    @Path("/editDynamicOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editDynamicOffer(
            OTCEditDynamicOfferRequest otcEditDynamicOfferRequest
    ) throws ServiceException {
        if (otcEditDynamicOfferRequest == null) {
            throw new ServiceException("otcEditDynamicOfferRequest is null");
        }
        if (otcEditDynamicOfferRequest.getCurrency() == null || otcEditDynamicOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("otcEditDynamicOfferRequest.getCurrency() is null or empty");
        }
        if (otcEditDynamicOfferRequest.getPaymentId() == null || otcEditDynamicOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("otcEditDynamicOfferRequest.getPaymentId() is null or empty");
        }
        if (otcEditDynamicOfferRequest.getOfferType() == null) {
            throw new ServiceException("otcEditDynamicOfferRequest.getOfferType() is null");
        }
        if (otcEditDynamicOfferRequest.getPaymentType() == null) {
            throw new ServiceException("otcEditDynamicOfferRequest.getPaymentType() is null");
        }
        if (otcEditDynamicOfferRequest.getUseChangePriceByOperationBalance() == null) {
            throw new ServiceException("otcEditDynamicOfferRequest.getUseChangePriceByOperationBalance() is null");
        }
        return Response
                .status(200)
                .entity(new OTCEditDynamicOffer(otcEditDynamicOfferRequest).getResponse())
                .build();
    }

    @POST
    @Path("/removeOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeOffer(
            OTCRemoveOfferRequest otcRemoveOfferRequest
    ) throws ServiceException {
        if (otcRemoveOfferRequest == null) {
            throw new ServiceException("otcRemoveOfferRequest is null");
        }
        if (otcRemoveOfferRequest.getCurrency() == null || otcRemoveOfferRequest.getCurrency().equals("")) {
            throw new ServiceException("otcRemoveOfferRequest.getCurrency() is null or empty");
        }
        if (otcRemoveOfferRequest.getPaymentId() == null || otcRemoveOfferRequest.getPaymentId().equals("")) {
            throw new ServiceException("otcRemoveOfferRequest.getPaymentId() is null or empty");
        }
        if (otcRemoveOfferRequest.getOfferType() == null) {
            throw new ServiceException("otcRemoveOfferRequest.getOfferType() is null");
        }
        if (otcRemoveOfferRequest.getPaymentType() == null) {
            throw new ServiceException("otcRemoveOfferRequest.getPaymentType() is null");
        }
        return Response
                .status(200)
                .entity(new OTCRemoveOffer(otcRemoveOfferRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getOffer/{encryptedOfferKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffer(
            @PathParam("encryptedOfferKey") String encryptedOfferKey
    ) throws ServiceException {
        if (encryptedOfferKey == null || encryptedOfferKey.equals("")) {
            throw new ServiceException("encryptedOfferKey is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetOffer(encryptedOfferKey).getResponse())
                .build();
    }

    @GET
    @Path("/getOffers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOffers(null, null, null, null, false).getResponse())
                .build();
    }

    @GET
    @Path("/getOffers/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers(
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOffers(currency, null, null, null, false).getResponse())
                .build();
    }
    
    @GET
    @Path("/getOffers/{currency}/{paymentId}/{paymentType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers(
            @PathParam("currency") String currency,
            @PathParam("paymentId") String paymentId,
            @PathParam("paymentType") PaymentType paymentType
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOffers(currency, paymentId, null, paymentType, false).getResponse())
                .build();
    }

    @GET
    @Path("/getOffers/{currency}/{paymentId}/{offerType}/{paymentType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffers(
            @PathParam("currency") String currency,
            @PathParam("paymentId") String paymentId,
            @PathParam("offerType") OfferType offerType,
            @PathParam("paymentType") PaymentType paymentType
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOffers(currency, paymentId, offerType, paymentType, false).getResponse())
                .build();
    }
    
    @GET
    @Path("/getOldOffers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOldOffers() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOffers(null, null, null, null, true).getResponse())
                .build();
    }

    @POST
    @Path("/createOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createOperation(
            OTCCreateOperationRequest otcCreateOperationRequest
    ) throws ServiceException {
        if (otcCreateOperationRequest == null) {
            throw new ServiceException("otcCreateOperationRequest is null");
        }
        if (otcCreateOperationRequest.getUserName() == null || otcCreateOperationRequest.getUserName().equals("")) {
            throw new ServiceException("otcCreateOperationRequest.getUserName() is null or empty");
        }
        if (otcCreateOperationRequest.getCurrency() == null || otcCreateOperationRequest.getCurrency().equals("")) {
            throw new ServiceException("otcCreateOperationRequest.getCurrency() is null or empty");
        }
        if (otcCreateOperationRequest.getPrice() == null) {
            throw new ServiceException("otcCreateOperationRequest.getPrice() is null");
        }
        if (otcCreateOperationRequest.getAmount() == null) {
            throw new ServiceException("otcCreateOperationRequest.getAmount() is null");
        }
        if (otcCreateOperationRequest.getOtcOperationType() == null) {
            throw new ServiceException("otcCreateOperationRequest.getOtcOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new OTCCreateOperation(otcCreateOperationRequest).getResponse())
                .build();
    }

    @POST
    @Path("/changeOperationStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeOperationStatus(
            OTCChangeOperationStatusRequest otcChangeOperationStatusRequest
    ) throws ServiceException {
        if (otcChangeOperationStatusRequest == null) {
            throw new ServiceException("otcChangeOperationStatusRequest is null");
        }
        if (otcChangeOperationStatusRequest.getId() == null || otcChangeOperationStatusRequest.getId().equals("")) {
            throw new ServiceException("otcChangeOperationStatusRequest.getId() is null or empty");
        }
        if (otcChangeOperationStatusRequest.getOtcOperationStatus() == null) {
            throw new ServiceException("otcChangeOperationStatusRequest.getOtcOperationStatus() is null");
        }
        if(otcChangeOperationStatusRequest.getCanceledReason() != null && !otcChangeOperationStatusRequest.getCanceledReason().equals("") && !otcChangeOperationStatusRequest.getOtcOperationStatus().equals(OTCOperationStatus.CANCELED)){
            otcChangeOperationStatusRequest.setCanceledReason(null);
        }
        return Response
                .status(200)
                .entity(new OTCChangeOperationStatus(otcChangeOperationStatusRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getOperation/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperation(
            @PathParam("id") String id
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetOperation(id).getResponse())
                .build();
    }

    @GET
    @Path("/getOperationIndexesAndValues")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationIndexesAndValues() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOperationIndexesAndValues().getResponse())
                .build();
    }

    @POST
    @Path("/getOperations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperations(
            OTCGetOperationsRequest otcGetOperationsRequest
    ) throws ServiceException {
        if (otcGetOperationsRequest == null) {
            throw new ServiceException("otcGetOperationsRequest is null");
        }
        return Response
                .status(200)
                .entity(new OTCGetOperations(otcGetOperationsRequest).getResponse())
                .build();
    }

    @GET
    @Path("/postOperationMessage/{id}/{userName}/{message}/{operationMessageSide}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postOperationMessage(
            @PathParam("id") String id,
            @PathParam("userName") String userName,
            @PathParam("message") String message,
            @PathParam("operationMessageSide") OperationMessageSide operationMessageSide
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (message == null || message.equals("")) {
            throw new ServiceException("message is null or empty");
        }
        if (operationMessageSide == null) {
            throw new ServiceException("operationMessageSide is null");
        }
        return Response
                .status(200)
                .entity(BaseOperation.postOperationMessage(id, userName, message, operationMessageSide, null))
                .build();
    }

    @GET
    @Path("/getAutomaticChatMessages/{currency}/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAutomaticChatMessages(
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
                .entity(new OTCGetAutomaticChatMessages(currency, language, null, null).getResponse())
                .build();
    }

    @GET
    @Path("/getOperationCheckList/{currency}/{otcOperationType}/{otcOperationStatus}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationCheckList(
            @PathParam("currency") String currency,
            @PathParam("otcOperationType") OTCOperationType otcOperationType,
            @PathParam("otcOperationStatus") OTCOperationStatus otcOperationStatus
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (otcOperationType == null) {
            throw new ServiceException("otcOperationType is null");
        }
        if (otcOperationStatus == null) {
            throw new ServiceException("otcOperationStatus is null");
        }
        return Response
                .status(200)
                .entity(new OTCGetOperationCheckList(currency, otcOperationType, otcOperationStatus).getResponse())
                .build();
    }

    @POST
    @Path("/modifyOperationCheckList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getModifyOperationCheckList(
            OTCModifyOperationCheckListRequest otcModifyOperationCheckListRequest
    ) throws ServiceException {
        if (otcModifyOperationCheckListRequest == null) {
            throw new ServiceException("otcModifyOperationCheckListRequest is null");
        }
        if (otcModifyOperationCheckListRequest.getId() == null || otcModifyOperationCheckListRequest.getId().equals("")) {
            throw new ServiceException("otcModifyOperationCheckListRequest.getId() is null or empty");
        }
        if (otcModifyOperationCheckListRequest.getCheckList() == null) {
            throw new ServiceException("otcModifyOperationCheckListRequest.getCheckList() is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCModifyOperationCheckList(otcModifyOperationCheckListRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getUSDPrice/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUSDPrice(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(0.0)
                .build();
    }

    @GET
    @Path("/getLimits")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLimits() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetLimits().getResponse())
                .build();
    }

    @GET
    @Path("/getCharges")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCharges() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetCharges(null).getResponse())
                .build();
    }

    @POST
    @Path("/getCharges")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCharges(OTCGetChargesRequest otcGetChargesRequest) throws ServiceException {
        if (otcGetChargesRequest == null) {
            throw new ServiceException("otcGetChargesRequest is null");
        }
        if (otcGetChargesRequest.getCurrency() == null || otcGetChargesRequest.getCurrency().equals("")) {
            throw new ServiceException("otcGetChargesRequest.getCurrency() is null or empty");
        }
        if (otcGetChargesRequest.getAmount() == null || otcGetChargesRequest.getAmount() == 0) {
            throw new ServiceException("otcGetChargesRequest.getAmount() is null or zero");
        }
        if (otcGetChargesRequest.getOperationType() == null) {
            throw new ServiceException("otcGetChargesRequest.getOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new OTCGetCharges(otcGetChargesRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getOperationsOverall")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationsOverall() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetOperationsOverall().getResponse())
                .build();
    }

    @GET
    @Path("/getOfficesInfo/{currency}/{officesInfoID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfficesInfo(
            @PathParam("currency") String currency,
            @PathParam("officesInfoID") String officesInfoID
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (officesInfoID == null || officesInfoID.equals("")) {
            throw new ServiceException("officesInfoID is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetOfficesInfo(currency, officesInfoID).getResponse())
                .build();
    }
    
    @PUT
    @Path("/fastChangeFromBTC")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response fastChangeFromBTC(
            OTCFastChangeFromBTCRequest otcFastChangeFromBTCRequest
    ) throws ServiceException {
        if (otcFastChangeFromBTCRequest == null) {
            throw new ServiceException("otcFastChangeFromBTCRequest is null");
        }
        if (otcFastChangeFromBTCRequest.getUserName() == null || otcFastChangeFromBTCRequest.getUserName().equals("")) {
            throw new ServiceException("otcFastChangeFromBTCRequest.getUserName() is null or empty");
        }
        if (otcFastChangeFromBTCRequest.getCurrency() == null || otcFastChangeFromBTCRequest.getCurrency().equals("")) {
            throw new ServiceException("otcFastChangeFromBTCRequest.getCurrency() is null or empty");
        }
        if (otcFastChangeFromBTCRequest.getAmount() == null || otcFastChangeFromBTCRequest.getAmount().equals(0)) {
            throw new ServiceException("otcFastChangeFromBTCRequest.getUserVerificationType() is null or zero");
        }
        if (otcFastChangeFromBTCRequest.getBtcPrice() == null || otcFastChangeFromBTCRequest.getBtcPrice().equals(0)) {
            throw new ServiceException("otcFastChangeFromBTCRequest.getBtcPrice() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCFastChangeFromToBTC(otcFastChangeFromBTCRequest.getUserName(), otcFastChangeFromBTCRequest.getCurrency(), otcFastChangeFromBTCRequest.getAmount(), otcFastChangeFromBTCRequest.getBtcPrice(), "FROM").getResponse())
                .build();
    }
    
    @PUT
    @Path("/fastChangeToBTC")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response fastChangeToBTC(
            OTCFastChangeToBTCRequest otcFastChangeToBTCRequest
    ) throws ServiceException {
        if (otcFastChangeToBTCRequest == null) {
            throw new ServiceException("otcFastChangeToBTCRequest is null");
        }
        if (otcFastChangeToBTCRequest.getUserName() == null || otcFastChangeToBTCRequest.getUserName().equals("")) {
            throw new ServiceException("otcFastChangeToBTCRequest.getUserName() is null or empty");
        }
        if (otcFastChangeToBTCRequest.getCurrency() == null || otcFastChangeToBTCRequest.getCurrency().equals("")) {
            throw new ServiceException("otcFastChangeToBTCRequest.getCurrency() is null or empty");
        }
        if (otcFastChangeToBTCRequest.getAmount() == null || otcFastChangeToBTCRequest.getAmount().equals(0)) {
            throw new ServiceException("otcFastChangeToBTCRequest.getAmount() is null or zero");
        }
        if (otcFastChangeToBTCRequest.getBtcPrice() == null || otcFastChangeToBTCRequest.getBtcPrice().equals(0)) {
            throw new ServiceException("otcFastChangeToBTCRequest.getBtcPrice() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCFastChangeFromToBTC(otcFastChangeToBTCRequest.getUserName(), otcFastChangeToBTCRequest.getCurrency(), otcFastChangeToBTCRequest.getAmount(), otcFastChangeToBTCRequest.getBtcPrice(), "TO").getResponse())
                .build();
    }
    
    @GET
    @Path("/getAllowedAddPayments/{userName}/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddPaymentsAllowed(
            @PathParam("userName") String userName,
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetAllowedAddPayments(userName, currency).getResponse())
                .build();
    }
    
    @GET
    @Path("/getCurrenciesOrder/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrenciesOrder(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetCurrenciesOrder(userName).getResponse())
                .build();
    }
    
    @GET
    @Path("/getAutomaticChatMessages/{currency}/{language}/{otcOperationType}/{otcOperationStatus}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAutomaticChatMessages(
            @PathParam("currency") String currency,
            @PathParam("language") String language,
            @PathParam("otcOperationType") OTCOperationType otcOperationType,
            @PathParam("otcOperationStatus") OTCOperationStatus otcOperationStatus
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        if (otcOperationType == null) {
            throw new ServiceException("otcOperationType is null");
        }
        if (otcOperationStatus == null) {
            throw new ServiceException("otcOperationStatus is null");
        }
        return Response
                .status(200)
                .entity(new OTCGetAutomaticChatMessages(currency, language, otcOperationType, otcOperationStatus).getResponse())
                .build();
    }
    
    @GET
    @Path("/getThirdPartySendData/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThirdPartySendData(
            @PathParam("id") String id
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCGetThirdPartySendData(id).getResponse())
                .build();
    }
    
    @POST
    @Path("/confirmThirdPartySend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmThirdPartySend(OTCConfirmThirdPartySendRequest otcConfirmThirdPartySendRequest) throws ServiceException {
        if (otcConfirmThirdPartySendRequest == null) {
            throw new ServiceException("otcConfirmThirdPartySendRequest is null");
        }
        if (otcConfirmThirdPartySendRequest.getId() == null || otcConfirmThirdPartySendRequest.getId().equals("")) {
            throw new ServiceException("otcConfirmThirdPartySendRequest.getId() is null or empty");
        }
        if (otcConfirmThirdPartySendRequest.getConfirm() == null) {
            throw new ServiceException("otcConfirmThirdPartySendRequest.getConfirm() is null");
        }
        return Response
                .status(200)
                .entity(new OTCConfirmThirdPartySend(otcConfirmThirdPartySendRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getChargesNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChargesNew() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetChargesNew(null).getResponse())
                .build();
    }

    @POST
    @Path("/getChargesNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChargesNew(OTCGetChargesNewRequest otcGetChargesNewRequest) throws ServiceException {
        if (otcGetChargesNewRequest == null) {
            throw new ServiceException("otcGetChargesNewRequest is null");
        }
        if (otcGetChargesNewRequest.getCurrency() == null || otcGetChargesNewRequest.getCurrency().equals("")) {
            throw new ServiceException("otcGetChargesNewRequest.getCurrency() is null or empty");
        }
//        if (otcGetChargesNewRequest.getAmount() == null || otcGetChargesNewRequest.getAmount() == 0) {
//            throw new ServiceException("otcGetChargesNewRequest.getAmount() is null or zero");
//        }
        if (otcGetChargesNewRequest.getOperationType() == null) {
            throw new ServiceException("otcGetChargesNewRequest.getOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new OTCGetChargesNew(otcGetChargesNewRequest).getResponse())
                .build();
    }
    
}
