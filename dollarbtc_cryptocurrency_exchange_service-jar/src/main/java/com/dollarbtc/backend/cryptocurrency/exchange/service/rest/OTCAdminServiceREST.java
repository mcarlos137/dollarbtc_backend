/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminBuyFromDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditAdminUserCommissionsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditChangeFactorsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditCurrenciesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditDollarBTCPaymentCommissionsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditOperationBalanceParamsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminGetOperationsNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminSellFromDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminTransferBetweenDollarBTCPaymentsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminBuyFromDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminEditAdminUserCommissions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminEditChangeFactors;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminEditCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminEditDollarBTCPaymentCommissions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminEditOperationBalanceParams;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetChangeFactors;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetClientsBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOperationBalanceParams;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOperationIndexesAndValues;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetOperationsNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetSpecialPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminResetOperationBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminSellFromDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminSendDollarBTCPaymentCommissionsToMoneyClick;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminTransferBetweenDollarBTCPayments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminUpdateFieldValues;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminVerification;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
@Path("/otcAdmin")
@XmlRegistry
public class OTCAdminServiceREST {

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
                .entity(new OTCAdminGetCurrencies(userName).getResponse())
                .build();
    }

    @POST
    @Path("/editCurrencies")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editCurrencies(
            OTCAdminEditCurrenciesRequest otcAdminEditCurrenciesRequest
    ) throws ServiceException {
        if (otcAdminEditCurrenciesRequest == null) {
            throw new ServiceException("otcAdminEditCurrenciesRequest is null");
        }
        if (otcAdminEditCurrenciesRequest.getUserName() == null || otcAdminEditCurrenciesRequest.getUserName().equals("")) {
            throw new ServiceException("otcAdminEditCurrenciesRequest.getUserName() is null or empty");
        }
        if (otcAdminEditCurrenciesRequest.getCurrencies() == null || otcAdminEditCurrenciesRequest.getCurrencies().size() == 0) {
            throw new ServiceException("otcAdminEditCurrenciesRequest.getCurrencies() is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCAdminEditCurrencies(otcAdminEditCurrenciesRequest).getResponse())
                .build();
    }

    @POST
    @Path("/verification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response verification(
            OTCAdminVerificationRequest otcAdminVerificationRequest
    ) throws ServiceException {
        if (otcAdminVerificationRequest == null) {
            throw new ServiceException("otcAdminVerificationRequest is null");
        }
        if (otcAdminVerificationRequest.getUserName() == null || otcAdminVerificationRequest.getUserName().equals("")) {
            throw new ServiceException("otcAdminVerificationRequest.getUserName() is null or empty");
        }
        if (otcAdminVerificationRequest.getType() == null || otcAdminVerificationRequest.getType().equals("")) {
            throw new ServiceException("otcAdminVerificationRequest.getType() is null or empty");
        }
        if (!otcAdminVerificationRequest.getType().equals("C") && !otcAdminVerificationRequest.getType().equals("D")) {
            throw new ServiceException("otcAdminVerificationRequest.getType() must be C or D");
        }
        return Response
                .status(200)
                .entity(new OTCAdminVerification(otcAdminVerificationRequest).getResponse())
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
                .entity(new OTCAdminGetDollarBTCPayments(userName, null).getResponse())
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
                .entity(new OTCAdminGetDollarBTCPayments(userName, currency).getResponse())
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
                .entity(new OTCAdminGetOffers(userName, null, null, null, null, false).getResponse())
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
                .entity(new OTCAdminGetOffers(userName, currency, null, null, null, false).getResponse())
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
                .entity(new OTCAdminGetOffers(userName, currency, paymentId, offerType, paymentType, false).getResponse())
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
                .entity(new OTCAdminGetOffers(userName, null, null, null, null, true).getResponse())
                .build();
    }

    @POST
    @Path("/getOperations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperations(
            OTCAdminGetOperationsRequest otcAdminGetOperationsRequest
    ) throws ServiceException {
        if (otcAdminGetOperationsRequest == null) {
            throw new ServiceException("otcAdminGetOperationsRequest is null");
        }
        return Response
                .status(200)
                .entity(new OTCAdminGetOperations(otcAdminGetOperationsRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/getOperationsNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationsNew(
            OTCAdminGetOperationsNewRequest otcAdminGetOperationsNewRequest
    ) throws ServiceException {
        if (otcAdminGetOperationsNewRequest == null) {
            throw new ServiceException("otcAdminGetOperationsNewRequest is null");
        }
        return Response
                .status(200)
                .entity(new OTCAdminGetOperationsNew(otcAdminGetOperationsNewRequest).getResponse())
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
                .entity(new OTCAdminGetOperationIndexesAndValues(userName).getResponse())
                .build();
    }

    @GET
    @Path("/updateFieldValues/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateFieldValues(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCAdminUpdateFieldValues(currency).getResponse())
                .build();
    }

    @GET
    @Path("/resetOperationBalance/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response resetOperationBalance(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCAdminResetOperationBalance(currency, false).getResponse())
                .build();
    }

    @GET
    @Path("/getOperationBalanceParams/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperationBalanceParams(
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCAdminGetOperationBalanceParams(currency, false).getResponse())
                .build();
    }

    @POST
    @Path("/editOperationBalanceParams")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editOperationBalanceParams(
            OTCAdminEditOperationBalanceParamsRequest otcAdminEditOperationBalanceParamsRequest
    ) throws ServiceException {
        if (otcAdminEditOperationBalanceParamsRequest == null) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest is null");
        }
        if (otcAdminEditOperationBalanceParamsRequest.getCurrency() == null || otcAdminEditOperationBalanceParamsRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest.getCurrency() is null or empty");
        }
        if (otcAdminEditOperationBalanceParamsRequest.getMaxSpreadPercent() == 0) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest.getMaxSpreadPercent() is zero");
        }
        if (otcAdminEditOperationBalanceParamsRequest.getChangePercent() == 0) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest.getChangePercent() is zero");
        }
        return Response
                .status(200)
                .entity(new OTCAdminEditOperationBalanceParams(otcAdminEditOperationBalanceParamsRequest, false).getResponse())
                .build();
    }

    @GET
    @Path("/getClientsBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientsBalance() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCAdminGetClientsBalance().getResponse())
                .build();
    }

    @POST
    @Path("/buyFromDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response buyFromDollarBTCPayment(
            OTCAdminBuyFromDollarBTCPaymentRequest otcAdminBuyFromDollarBTCPaymentRequest
    ) throws ServiceException {
        if (otcAdminBuyFromDollarBTCPaymentRequest == null) {
            throw new ServiceException("otcAdminBuyFromDollarBTCPaymentRequest is null");
        }
        if (otcAdminBuyFromDollarBTCPaymentRequest.getCurrency() == null || otcAdminBuyFromDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminBuyFromDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (otcAdminBuyFromDollarBTCPaymentRequest.getId() == null || otcAdminBuyFromDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("otcAdminBuyFromDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (otcAdminBuyFromDollarBTCPaymentRequest.getMasterAccountName() == null || otcAdminBuyFromDollarBTCPaymentRequest.getMasterAccountName().equals("")) {
            throw new ServiceException("otcAdminBuyFromDollarBTCPaymentRequest.getMasterAccountName() is null or empty");
        }
        if (otcAdminBuyFromDollarBTCPaymentRequest.getAmount() == null || otcAdminBuyFromDollarBTCPaymentRequest.getAmount() == 0.0) {
            throw new ServiceException("otcAdminBuyFromDollarBTCPaymentRequest.getAmount() is null or zero");
        }
        if (otcAdminBuyFromDollarBTCPaymentRequest.getMasterAccountAmount() == null || otcAdminBuyFromDollarBTCPaymentRequest.getMasterAccountAmount() == 0.0) {
            throw new ServiceException("otcAdminBuyFromDollarBTCPaymentRequest.getMasterAccountAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCAdminBuyFromDollarBTCPayment(otcAdminBuyFromDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/sellFromDollarBTCPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sellFromDollarBTCPayment(
            OTCAdminSellFromDollarBTCPaymentRequest otcSellAdminFromDollarBTCPaymentRequest
    ) throws ServiceException {
        if (otcSellAdminFromDollarBTCPaymentRequest == null) {
            throw new ServiceException("otcSellAdminFromDollarBTCPaymentRequest is null");
        }
        if (otcSellAdminFromDollarBTCPaymentRequest.getCurrency() == null || otcSellAdminFromDollarBTCPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("otcSellAdminFromDollarBTCPaymentRequest.getCurrency() is null or empty");
        }
        if (otcSellAdminFromDollarBTCPaymentRequest.getId() == null || otcSellAdminFromDollarBTCPaymentRequest.getId().equals("")) {
            throw new ServiceException("otcSellAdminFromDollarBTCPaymentRequest.getId() is null or empty");
        }
        if (otcSellAdminFromDollarBTCPaymentRequest.getMasterAccountName() == null || otcSellAdminFromDollarBTCPaymentRequest.getMasterAccountName().equals("")) {
            throw new ServiceException("otcSellAdminFromDollarBTCPaymentRequest.getMasterAccountName() is null or empty");
        }
        if (otcSellAdminFromDollarBTCPaymentRequest.getAmount() == null || otcSellAdminFromDollarBTCPaymentRequest.getAmount() == 0.0) {
            throw new ServiceException("otcSellAdminFromDollarBTCPaymentRequest.getAmount() is null or zero");
        }
        if (otcSellAdminFromDollarBTCPaymentRequest.getMasterAccountAmount() == null || otcSellAdminFromDollarBTCPaymentRequest.getMasterAccountAmount() == 0.0) {
            throw new ServiceException("otcSellAdminFromDollarBTCPaymentRequest.getMasterAccountAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCAdminSellFromDollarBTCPayment(otcSellAdminFromDollarBTCPaymentRequest).getResponse())
                .build();
    }

    @POST
    @Path("/transferBetweenDollarBTCPayments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response transferBetweenDollarBTCPayments(
            OTCAdminTransferBetweenDollarBTCPaymentsRequest otcAdminTransferBetweenDollarBTCPaymentsRequest
    ) throws ServiceException {
        if (otcAdminTransferBetweenDollarBTCPaymentsRequest == null) {
            throw new ServiceException("otcAdminTransferBetweenDollarBTCPaymentsRequest is null");
        }
        if (otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency() == null || otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminTransferBetweenDollarBTCPaymentsRequest.getCurrency() is null or empty");
        }
        if (otcAdminTransferBetweenDollarBTCPaymentsRequest.getBaseId() == null || otcAdminTransferBetweenDollarBTCPaymentsRequest.getBaseId().equals("")) {
            throw new ServiceException("otcAdminTransferBetweenDollarBTCPaymentsRequest.getBaseId() is null or empty");
        }
        if (otcAdminTransferBetweenDollarBTCPaymentsRequest.getTargetId() == null || otcAdminTransferBetweenDollarBTCPaymentsRequest.getTargetId().equals("")) {
            throw new ServiceException("otcAdminTransferBetweenDollarBTCPaymentsRequest.getTargetId() is null or empty");
        }
        if (otcAdminTransferBetweenDollarBTCPaymentsRequest.getAmount() == null || otcAdminTransferBetweenDollarBTCPaymentsRequest.getAmount() == 0.0) {
            throw new ServiceException("otcAdminTransferBetweenDollarBTCPaymentsRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCAdminTransferBetweenDollarBTCPayments(otcAdminTransferBetweenDollarBTCPaymentsRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getSpecialPayments/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecialPayments(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCAdminGetSpecialPayments(userName).getResponse())
                .build();
    }

    @GET
    @Path("/resetMoneyclickOperationBalance/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response resetMoneyclickOperationBalance(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new OTCAdminResetOperationBalance(currency, true).getResponse())
                .build();
    }

    @GET
    @Path("/getMoneyclickOperationBalanceParams/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMoneyclickOperationBalanceParams(
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCAdminGetOperationBalanceParams(currency, true).getResponse())
                .build();
    }

    @POST
    @Path("/editMoneyclickOperationBalanceParams")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editMoneyclickOperationBalanceParams(
            OTCAdminEditOperationBalanceParamsRequest otcAdminEditOperationBalanceParamsRequest
    ) throws ServiceException {
        if (otcAdminEditOperationBalanceParamsRequest == null) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest is null");
        }
        if (otcAdminEditOperationBalanceParamsRequest.getCurrency() == null || otcAdminEditOperationBalanceParamsRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest.getCurrency() is null or empty");
        }
        if (otcAdminEditOperationBalanceParamsRequest.getMaxSpreadPercent() == 0) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest.getMaxSpreadPercent() is zero");
        }
        if (otcAdminEditOperationBalanceParamsRequest.getChangePercent() == 0) {
            throw new ServiceException("otcAdminEditOperationBalanceParamsRequest.getChangePercent() is zero");
        }
        return Response
                .status(200)
                .entity(new OTCAdminEditOperationBalanceParams(otcAdminEditOperationBalanceParamsRequest, true).getResponse())
                .build();
    }

    @GET
    @Path("/getChangeFactors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChangeFactors() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCAdminGetChangeFactors().getResponse())
                .build();
    }
    
    @PUT
    @Path("/editChangeFactors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editChangeFactors(
            OTCAdminEditChangeFactorsRequest otcAdminEditChangeFactorsRequest
    ) throws ServiceException {
        if (otcAdminEditChangeFactorsRequest == null) {
            throw new ServiceException("otcAdminEditChangeFactorsRequest is null");
        }
        if (otcAdminEditChangeFactorsRequest.getUserName() == null || otcAdminEditChangeFactorsRequest.getUserName().equals("")) {
            throw new ServiceException("otcAdminEditChangeFactorsRequest.getUserName() is null or empty");
        }
        if (otcAdminEditChangeFactorsRequest.getChangeFactors() == null) {
            throw new ServiceException("otcAdminEditCurrenciesRequest.getChangeFactors() is null");
        }
        return Response
                .status(200)
                .entity(new OTCAdminEditChangeFactors(otcAdminEditChangeFactorsRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/editAdminUserCommissions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editUserCommissions(OTCAdminEditAdminUserCommissionsRequest otcAdminEditAdminUserCommissionsRequest) throws ServiceException {
        if (otcAdminEditAdminUserCommissionsRequest == null) {
            throw new ServiceException("otcAdminEditAdminUserCommissionsRequest is null");
        }
        if (otcAdminEditAdminUserCommissionsRequest.getUserName() == null || otcAdminEditAdminUserCommissionsRequest.getUserName().equals("")) {
            throw new ServiceException("otcAdminEditAdminUserCommissionsRequest.getUserName() is null or empty");
        }
        if (otcAdminEditAdminUserCommissionsRequest.getCurrency() == null || otcAdminEditAdminUserCommissionsRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminEditAdminUserCommissionsRequest.getCurrency() is null or empty");
        }
        if (otcAdminEditAdminUserCommissionsRequest.getMcBuyBalancePercent() == null) {
            throw new ServiceException("otcAdminEditAdminUserCommissionsRequest.getMcBuyBalancePercent() is null");
        }
        if (otcAdminEditAdminUserCommissionsRequest.getSendToPaymentPercent() == null) {
            throw new ServiceException("otcAdminEditAdminUserCommissionsRequest.getSendToPaymentPercent() is null");
        }
        return Response
                .status(200)
                .entity(new OTCAdminEditAdminUserCommissions(otcAdminEditAdminUserCommissionsRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/editDollarBTCPaymentCommissions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editDollarBTCPaymentCommissions(OTCAdminEditDollarBTCPaymentCommissionsRequest otcAdminEditDollarBTCPaymentCommissionsRequest) throws ServiceException {
        if (otcAdminEditDollarBTCPaymentCommissionsRequest == null) {
            throw new ServiceException("otcAdminEditDollarBTCPaymentCommissionsRequest is null");
        }
        if (otcAdminEditDollarBTCPaymentCommissionsRequest.getCurrency() == null || otcAdminEditDollarBTCPaymentCommissionsRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminEditDollarBTCPaymentCommissionsRequest.getCurrency() is null or empty");
        }
        if (otcAdminEditDollarBTCPaymentCommissionsRequest.getId() == null || otcAdminEditDollarBTCPaymentCommissionsRequest.getId().equals("")) {
            throw new ServiceException("otcAdminEditDollarBTCPaymentCommissionsRequest.getId() is null or empty");
        }
        if (otcAdminEditDollarBTCPaymentCommissionsRequest.getUserName() == null || otcAdminEditDollarBTCPaymentCommissionsRequest.getUserName().equals("")) {
            throw new ServiceException("otcAdminEditDollarBTCPaymentCommissionsRequest.getUserName() is null or empty");
        }
        if (otcAdminEditDollarBTCPaymentCommissionsRequest.getMcBuyBalancePercent() == null) {
            throw new ServiceException("otcAdminEditDollarBTCPaymentCommissionsRequest.getMcBuyBalancePercent() is null");
        }
        if (otcAdminEditDollarBTCPaymentCommissionsRequest.getSendToPaymentPercent() == null) {
            throw new ServiceException("otcAdminEditDollarBTCPaymentCommissionsRequest.getSendToPaymentPercent() is null");
        }
        return Response
                .status(200)
                .entity(new OTCAdminEditDollarBTCPaymentCommissions(otcAdminEditDollarBTCPaymentCommissionsRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getDollarBTCPaymentCommissionsBalance/{currency}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaymentCommissionsBalance(
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
                .entity(BaseOperation.getBalance(OTCFolderLocator.getCurrencyPaymentCommissionsBalanceFolder(OPERATOR_NAME, currency, id)))
                .build();
    }
    
    @GET
    @Path("/getDollarBTCPaymentCommissionsBalanceMovements/{currency}/{id}/{initTimestamp}/{finalTimestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaymentCommissionsBalanceMovements(
            @PathParam("currency") String currency,
            @PathParam("id") String id,
            @PathParam("initTimestamp") String initTimestamp,
            @PathParam("finalTimestamp") String finalTimestamp
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(BaseOperation.getBalanceMovements(
                        OTCFolderLocator.getCurrencyPaymentCommissionsBalanceFolder(OPERATOR_NAME, currency, id), 
                        initTimestamp, 
                        finalTimestamp, 
                        null, 
                        currency, 
                        null
                ))
                .build();
    }
    
    @PUT
    @Path("/sendDollarBTCPaymentCommissionsToMoneyClick")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendDollarBTCPaymentCommissionsToMoneyClick(OTCAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest) throws ServiceException {
        if (otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest == null) {
            throw new ServiceException("otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest is null");
        }
        if (otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getCurrency() == null || otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getCurrency().equals("")) {
            throw new ServiceException("otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getCurrency() is null or empty");
        }
        if (otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getId() == null || otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getId().equals("")) {
            throw new ServiceException("otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getId() is null or empty");
        }
        if (otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getTargetUserName() == null || otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getTargetUserName().equals("")) {
            throw new ServiceException("otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getTargetUserName() is null or empty");
        }
        if (otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getAmount() == null || otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getAmount() == 0.0) {
            throw new ServiceException("otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest.getAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new OTCAdminSendDollarBTCPaymentCommissionsToMoneyClick(otcAdminSendDollarBTCPaymentCommissionsToMoneyClickRequest).getResponse())
                .build();
    }

}
