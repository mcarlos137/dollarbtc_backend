/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.StringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcusernew.MCUserNewBalanceOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcusernew.MCUserNewBuyBalanceRetailRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcusernew.MCUserNewSellBalanceRetailRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcusernew.MCUserNewBuyBalanceRetail;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcusernew.MCUserNewSellBalanceRetail;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetNameAndType;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/mcUserNew")
@XmlRegistry
public class MCUserNewServiceREST {

    //LISTO
    @POST
    @Path("/buyBalanceRetail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response buyBalanceRetail(MCUserNewBuyBalanceRetailRequest mcUserNewBuyBalanceRetailRequest) throws ServiceException {
        if (mcUserNewBuyBalanceRetailRequest == null) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest is null");
        }
        if (mcUserNewBuyBalanceRetailRequest.getUserName() == null || mcUserNewBuyBalanceRetailRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest.getUserName() is null or empty");
        }
        if (mcUserNewBuyBalanceRetailRequest.getCurrency() == null || mcUserNewBuyBalanceRetailRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest.getCurrency() is null or empty");
        }
        if (mcUserNewBuyBalanceRetailRequest.getRetailId() == null || mcUserNewBuyBalanceRetailRequest.getRetailId().equals("")) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest.getRetailId() is null or empty");
        }
        if (mcUserNewBuyBalanceRetailRequest.getAmount() == null || mcUserNewBuyBalanceRetailRequest.getAmount() == 0.0) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserNewBuyBalanceRetail(mcUserNewBuyBalanceRetailRequest).getResponse())
                .build();
    }

    //LISTO
    @POST
    @Path("/sellBalanceRetail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sellBalanceRetail(MCUserNewSellBalanceRetailRequest mcUserNewSellBalanceRetailRequest) throws ServiceException {
        if (mcUserNewSellBalanceRetailRequest == null) {
            throw new ServiceException("mcUserNewSellBalanceRetailRequest is null");
        }
        if (mcUserNewSellBalanceRetailRequest.getUserName() == null || mcUserNewSellBalanceRetailRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserNewSellBalanceRetailRequest.getUserName() is null or empty");
        }
        if (mcUserNewSellBalanceRetailRequest.getCurrency() == null || mcUserNewSellBalanceRetailRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserNewSellBalanceRetailRequest.getCurrency() is null or empty");
        }
        if (mcUserNewSellBalanceRetailRequest.getRetailId() == null || mcUserNewSellBalanceRetailRequest.getRetailId().equals("")) {
            throw new ServiceException("mcUserNewSellBalanceRetailRequest.getRetailId() is null or empty");
        }
        if (mcUserNewSellBalanceRetailRequest.getAmount() == null || mcUserNewSellBalanceRetailRequest.getAmount() == 0.0) {
            throw new ServiceException("mcUserNewSellBalanceRetailRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserNewSellBalanceRetail(mcUserNewSellBalanceRetailRequest).getResponse())
                .build();
    }

    @POST
    @Path("/balanceOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response balanceOperation(
            MCUserNewBalanceOperationRequest mcUserNewBalanceOperationRequest
    ) throws ServiceException {
        if (mcUserNewBalanceOperationRequest.getUserName() == null || mcUserNewBalanceOperationRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserNewBalanceOperationRequest.getUserName() is null or empty");
        }
        if (mcUserNewBalanceOperationRequest.getAmounts() == null || mcUserNewBalanceOperationRequest.getAmounts().isEmpty() || mcUserNewBalanceOperationRequest.getAmounts().keySet().size() > 1) {
            throw new ServiceException("mcUserNewBalanceOperationRequest.getAmounts() is null or empty or bigger than max size");
        }
        if (mcUserNewBalanceOperationRequest.getBalanceOperationType() == null) {
            throw new ServiceException("mcUserNewBalanceOperationRequest.getBalanceOperationType() is null");
        }
        String currency = null;
        Double amount = null;
        for (String c : mcUserNewBalanceOperationRequest.getAmounts().keySet()) {
            currency = c;
            amount = mcUserNewBalanceOperationRequest.getAmounts().get(c);
            break;
        }
        switch (mcUserNewBalanceOperationRequest.getBalanceOperationType()) {
            case DEBIT:
                return Response
                        .status(200)
                        .entity(new StringResponse(
                                BaseOperation.substractToBalance(
                                        UsersFolderLocator.getMCBalanceFolder(mcUserNewBalanceOperationRequest.getUserName()),
                                        currency,
                                        amount,
                                        BalanceOperationType.DEBIT,
                                        BalanceOperationStatus.OK,
                                        mcUserNewBalanceOperationRequest.getAdditionalInfo(),
                                        null,
                                        false,
                                        null,
                                        false,
                                        null
                                )))
                        .build();
            case CREDIT:
                return Response
                        .status(200)
                        .entity(new StringResponse(
                                BaseOperation.addToBalance(
                                        UsersFolderLocator.getMCBalanceFolder(mcUserNewBalanceOperationRequest.getUserName()),
                                        currency,
                                        amount,
                                        BalanceOperationType.CREDIT,
                                        BalanceOperationStatus.OK,
                                        mcUserNewBalanceOperationRequest.getAdditionalInfo(),
                                        null,
                                        null,
                                        false,
                                        null
                                )))
                        .build();
            case RECEIVE:
                return Response
                        .status(200)
                        .entity(new StringResponse("OK"))
                        .build();
            case SEND:
                if (mcUserNewBalanceOperationRequest.getTargetAddress() == null || mcUserNewBalanceOperationRequest.getTargetAddress().equals("")) {
                    throw new ServiceException("mcUserNewBalanceOperationRequest.getTargetAddress() is null or empty");
                }
                String targetUserNameAndType = new UserGetNameAndType(mcUserNewBalanceOperationRequest.getTargetAddress()).getResponse();
                if (targetUserNameAndType != null) {
                    String targetUserName = targetUserNameAndType.split("____")[0];
                    String targetType = "DOLLARBTC";
                    if (targetUserNameAndType.split("____").length == 2) {
                        targetType = targetUserNameAndType.split("____")[1];
                    }
                    String inLimits = BaseOperation.inLimits(mcUserNewBalanceOperationRequest.getUserName(), currency, amount, BalanceOperationType.SEND_IN);
                    if (!inLimits.equals("OK")) {
                        return Response
                                .status(200)
                                .entity(new StringResponse(inLimits))
                                .build();
                    }
                    ObjectNode additionals = new ObjectMapper().createObjectNode();
                    additionals.put("targetAddress", mcUserNewBalanceOperationRequest.getTargetAddress());
                    additionals.put("senderUserName", mcUserNewBalanceOperationRequest.getUserName());
                    String substractResponse = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(mcUserNewBalanceOperationRequest.getUserName()),
                            currency,
                            amount,
                            BalanceOperationType.SEND_IN,
                            BalanceOperationStatus.OK,
                            mcUserNewBalanceOperationRequest.getAdditionalInfo(),
                            null,
                            false,
                            BaseOperation.getChargesNew(currency, amount, BalanceOperationType.SEND_IN, mcUserNewBalanceOperationRequest.getPaymentType(), "MONEYCLICK", null, null),
                            false,
                            additionals
                    );
                    if (substractResponse.equals("OK")) {
                        File userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(targetUserName);
                        if (targetType.equals("DOLLARBTC")) {
                            userBalanceFolder = UsersFolderLocator.getBalanceFolder(targetUserName);
                        }
                        BaseOperation.addToBalance(
                                userBalanceFolder,
                                currency,
                                amount,
                                BalanceOperationType.RECEIVE_IN,
                                BalanceOperationStatus.OK,
                                mcUserNewBalanceOperationRequest.getAdditionalInfo(),
                                null,
                                null,
                                false,
                                additionals
                        );
                        if (targetType.equals("MONEYCLICK")) {
//                            new MCUserAutomaticChange(targetUserName, amount, mcUserNewBalanceOperationRequest.getAdditionalInfo()).getResponse();
                        }
                        String notificationMessage = "" + mcUserNewBalanceOperationRequest.getUserName() + " has sent you " + String.format("%(,.8f", amount) + " " + currency + ". To see your balance, please go to MC app.";
                        new NotificationSendMessageByUserName(targetUserName, "Transaction information", notificationMessage).getResponse();
                        return Response
                                .status(200)
                                .entity(new StringResponse("OK"))
                                .build();
                    } else {
                        return Response
                                .status(200)
                                .entity(new StringResponse(substractResponse))
                                .build();
                    }
                } else {
                    String inLimits = BaseOperation.inLimits(mcUserNewBalanceOperationRequest.getUserName(), currency, amount, BalanceOperationType.SEND_OUT);
                    if (!inLimits.equals("OK")) {
                        return Response
                                .status(200)
                                .entity(new StringResponse(inLimits))
                                .build();
                    }
                    ObjectNode additionals = new ObjectMapper().createObjectNode();
                    additionals.put("targetAddress", mcUserNewBalanceOperationRequest.getTargetAddress());
                    additionals.put("senderUserName", mcUserNewBalanceOperationRequest.getUserName());
                    return Response
                            .status(200)
                            .entity(new StringResponse(
                                    BaseOperation.substractToBalance(
                                            UsersFolderLocator.getMCBalanceFolder(mcUserNewBalanceOperationRequest.getUserName()),
                                            currency,
                                            amount,
                                            BalanceOperationType.SEND_OUT,
                                            BalanceOperationStatus.PROCESSING,
                                            mcUserNewBalanceOperationRequest.getAdditionalInfo() + "TARGET ADDRESS " + mcUserNewBalanceOperationRequest.getTargetAddress(),
                                            null,
                                            false,
                                            BaseOperation.getChargesNew(currency, amount, BalanceOperationType.SEND_OUT, mcUserNewBalanceOperationRequest.getPaymentType(), "MONEYCLICK", null, null),
                                            false,
                                            additionals
                                    )))
                            .build();
                }
            default:
                return Response
                        .status(200)
                        .entity(new StringResponse("BALANCE OPERATION TYPE NOT SUPPORTED"))
                        .build();
        }
    }

}
