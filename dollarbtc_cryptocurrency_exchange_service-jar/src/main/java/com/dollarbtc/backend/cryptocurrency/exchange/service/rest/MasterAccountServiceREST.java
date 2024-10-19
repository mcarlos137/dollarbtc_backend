/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.JsonNodeResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountAddWalletRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountBalanceOperationSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountGetBalanceMovementsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountEditAutomaticRulesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountTransferBetweenMastersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountAddWallet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountEditAutomaticRules;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountExist;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetAutomaticRules;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetBalances;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetConfig;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetDetails;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetNames;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetOperatorName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetNameAndType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;
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
@Path("/masterAccount")
@XmlRegistry
public class MasterAccountServiceREST {

    @GET
    @Path("/getNames")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNames() throws ServiceException {
        return Response
                .status(200)
                .entity(new MasterAccountGetNames().getResponse())
                .build();
    }

    @GET
    @Path("/getDetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetails() throws ServiceException {
        return Response
                .status(200)
                .entity(new MasterAccountGetDetails().getResponse())
                .build();
    }

    @GET
    @Path("/getBalances")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalances() throws ServiceException {
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new MasterAccountGetBalances().getResponse()))
                .build();
    }

    @GET
    @Path("/getBalance/{masterAccountName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("masterAccountName") String masterAccountName
    ) throws ServiceException {
        if (masterAccountName == null || masterAccountName.equals("")) {
            throw new ServiceException("masterAccountName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new MasterAccountGetBalance(masterAccountName).getResponse()))
                .build();
    }

    @POST
    @Path("/transferBetweenMasters")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response transferBetweenMasters(
            MasterAccountTransferBetweenMastersRequest masterAccountTransferBetweenMastersRequest
    ) throws ServiceException {
        if (masterAccountTransferBetweenMastersRequest == null) {
            throw new ServiceException("masterAccountTransferBetweenMastersRequest is null");
        }
        if (masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName() == null || masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName().equals("")) {
            throw new ServiceException("masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName() is null or empty");
        }
        if (masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName() == null || masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName().equals("")) {
            throw new ServiceException("masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName() is null or empty");
        }

        if (masterAccountTransferBetweenMastersRequest.getCurrency() == null || masterAccountTransferBetweenMastersRequest.getCurrency().equals("")) {
            throw new ServiceException("masterAccountTransferBetweenMastersRequest.getCurrency() is null or empty");
        }
        if (masterAccountTransferBetweenMastersRequest.getAmount() == 0) {
            throw new ServiceException("masterAccountTransferBetweenMastersRequest.getAmount() is zero");
        }
        if (masterAccountTransferBetweenMastersRequest.isCompensateMoneyclick()) {
            if (masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationCurrency() == null || masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationCurrency().equals("")) {
                throw new ServiceException("masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationCurrency() is null or empty");
            }
            if (masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationAmount() == null || masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationAmount() == 0) {
                throw new ServiceException("masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationAmount() is null or zero");
            }
        }
        if (masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName().equals(masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName())) {
            return Response
                    .status(200)
                    .entity("MASTER ACCOUNT BASE AND TARGET ARE THE SAME")
                    .build();
        }
        if (!new MasterAccountExist(masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName()).getResponse()) {
            return Response
                    .status(200)
                    .entity("MASTER ACCOUNT BASE DOES NOT EXIST")
                    .build();
        }
        if (!new MasterAccountExist(masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName()).getResponse()) {
            return Response
                    .status(200)
                    .entity("MASTER ACCOUNT TARGET DOES NOT EXIST")
                    .build();
        }
        String substractResponse = BaseOperation.substractToBalance(
                MasterAccountFolderLocator.getBalanceFolder(masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName()),
                masterAccountTransferBetweenMastersRequest.getCurrency(),
                masterAccountTransferBetweenMastersRequest.getAmount(),
                BalanceOperationType.TRANSFER_BETWEEN_MASTERS,
                BalanceOperationStatus.OK,
                "TRANSFER TO " + masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName(),
                null,
                false,
                null,
                false,
                null
        );
        if (substractResponse.equals("OK")) {
            BaseOperation.addToBalance(
                    MasterAccountFolderLocator.getBalanceFolder(masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName()),
                    masterAccountTransferBetweenMastersRequest.getCurrency(),
                    masterAccountTransferBetweenMastersRequest.getAmount(),
                    BalanceOperationType.TRANSFER_BETWEEN_MASTERS,
                    BalanceOperationStatus.OK,
                    "Transfer from " + masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName(),
                    null,
                    null,
                    false,
                    null
            );
            String operatorName = new MasterAccountGetOperatorName(masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName()).getResponse();
            if (!masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName().startsWith("OTC_")) {
                BaseOperation.addToBalance(
                        MoneyclickFolderLocator.getBalanceFolder(operatorName),
                        masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationCurrency(),
                        masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationAmount(),
                        BalanceOperationType.TRANSFER_BETWEEN_MASTERS,
                        BalanceOperationStatus.OK,
                        "Transfer from " + masterAccountTransferBetweenMastersRequest.getMasterAccountBaseName(),
                        null,
                        null,
                        false,
                        null
                );
            } else {
                BaseOperation.substractToBalance(
                        MoneyclickFolderLocator.getBalanceFolder(operatorName),
                        masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationCurrency(),
                        masterAccountTransferBetweenMastersRequest.getMoneyclickCompensationAmount(),
                        BalanceOperationType.TRANSFER_BETWEEN_MASTERS,
                        BalanceOperationStatus.OK,
                        "TRANSFER TO " + masterAccountTransferBetweenMastersRequest.getMasterAccountTargetName(),
                        null,
                        true,
                        null,
                        false,
                        null
                );
            }
            return Response
                    .status(200)
                    .entity("OK")
                    .build();
        } else {
            return Response
                    .status(200)
                    .entity(substractResponse)
                    .build();
        }
    }

    @GET
    @Path("/getAutomaticRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAutomaticRules() throws ServiceException {
        return Response
                .status(200)
                .entity(new MasterAccountGetAutomaticRules().getResponse())
                .build();
    }

    @POST
    @Path("/editAutomaticRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editAutomaticRules(
            MasterAccountEditAutomaticRulesRequest masterAccountEditAutomaticRulesRequest
    ) throws ServiceException {
        if (masterAccountEditAutomaticRulesRequest == null) {
            throw new ServiceException("masterAccountEditAutomaticRulesRequest is null");
        }
        if (masterAccountEditAutomaticRulesRequest.getAutomaticRules() == null || masterAccountEditAutomaticRulesRequest.getAutomaticRules().length == 0) {
            throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules() is null or empty");
        }
        for (MasterAccountEditAutomaticRulesRequest.AutomaticRule automaticRule : masterAccountEditAutomaticRulesRequest.getAutomaticRules()) {
            if (automaticRule == null) {
                throw new ServiceException("automaticRule is null");
            }
            if (automaticRule.getBaseAccount() == null || automaticRule.getBaseAccount().equals("")) {
                throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules().getBaseAccount() is null or empty");
            }
            if (automaticRule.getExecutionPeriodInHours() <= 1) {
                throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules().getExecutionPeriodInHours() is not allowed");
            }
            if (automaticRule.getBalanceOperationType() == null) {
                throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules().getBalanceOperationType() is null");
            }
            if (automaticRule.getTargetAccountsOrClientModelNameAndPercents() == null || automaticRule.getTargetAccountsOrClientModelNameAndPercents().length == 0) {
                throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules().getTargetAccountsOrClientModelNameAndPercents() is null or empty");
            }
            for (MasterAccountEditAutomaticRulesRequest.TargetAccountOrClientModelNameAndPercent targetAccountOrClientModelNameAndPercent : automaticRule.getTargetAccountsOrClientModelNameAndPercents()) {
                if (targetAccountOrClientModelNameAndPercent == null) {
                    throw new ServiceException("targetAccountOrClientModelNameAndPercent is null");
                }
                if (targetAccountOrClientModelNameAndPercent.getName() == null || targetAccountOrClientModelNameAndPercent.getName().equals("")) {
                    throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules().getTargetAccountsOrClientModelNameAndPercents().getName() is null or empty");
                }
                if (targetAccountOrClientModelNameAndPercent.getPercent() == 0) {
                    throw new ServiceException("masterAccountEditAutomaticRulesRequest.getAutomaticRules().getTargetAccountsOrClientModelNameAndPercents().getPercent() is zero");
                }
            }
        }
        return Response
                .status(200)
                .entity(new MasterAccountEditAutomaticRules(masterAccountEditAutomaticRulesRequest).getResponse())
                .build();
    }

    @POST
    @Path("/getBalanceMovements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalanceMovements(
            MasterAccountGetBalanceMovementsRequest masterAccountGetBalanceMovementsRequest
    ) throws ServiceException {
        if (masterAccountGetBalanceMovementsRequest == null) {
            throw new ServiceException("masterAccountGetBalanceMovementsRequest is null");
        }
        if (masterAccountGetBalanceMovementsRequest.getMasterAccountName() == null || masterAccountGetBalanceMovementsRequest.getMasterAccountName().equals("")) {
            throw new ServiceException("masterAccountGetBalanceMovementsRequest.getMasterAccountName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MasterAccountGetBalanceMovements(masterAccountGetBalanceMovementsRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getConfig/{masterAccountName}/{securityCode}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(
            @PathParam("masterAccountName") String masterAccountName,
            @PathParam("securityCode") String securityCode
    ) throws ServiceException {
        if (masterAccountName == null || masterAccountName.equals("") || masterAccountName.equals("null")) {
            throw new ServiceException("masterAccountName is null or empty");
        }
        return Response
                .status(200)
                .entity(new MasterAccountGetConfig(masterAccountName, securityCode).getResponse())
                .build();
    }

    @POST
    @Path("/addWallet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addWallet(
            MasterAccountAddWalletRequest masterAccountAddWalletRequest
    ) throws ServiceException {
        if (masterAccountAddWalletRequest == null) {
            throw new ServiceException("masterAccountAddWalletRequest is null");
        }
        if (masterAccountAddWalletRequest.getMasterAccountName() == null || masterAccountAddWalletRequest.getMasterAccountName().equals("")) {
            throw new ServiceException("masterAccountAddWalletRequest.getMasterAccountName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MasterAccountAddWallet(masterAccountAddWalletRequest.getMasterAccountName(), true).getResponse())
                .build();
    }

    @POST
    @Path("/balanceOperationSend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response balanceOperationSend(
            MasterAccountBalanceOperationSendRequest masterAccountBalanceOperationSendRequest
    ) throws ServiceException {
        if (masterAccountBalanceOperationSendRequest == null) {
            throw new ServiceException("masterAccountBalanceOperationSendRequest is null");
        }
        if (masterAccountBalanceOperationSendRequest.getMasterAccountName() == null || masterAccountBalanceOperationSendRequest.getMasterAccountName().equals("")) {
            throw new ServiceException("masterAccountBalanceOperationSendRequest.getMasterAccountName() is null or empty");
        }
        if (masterAccountBalanceOperationSendRequest.getAmounts() == null || masterAccountBalanceOperationSendRequest.getAmounts().isEmpty() || masterAccountBalanceOperationSendRequest.getAmounts().keySet().size() > 1) {
            throw new ServiceException("masterAccountBalanceOperationSendRequest.getAmounts() is null or empty or bigger than max size");
        }
        String currency = null;
        Double amount = null;
        for (String c : masterAccountBalanceOperationSendRequest.getAmounts().keySet()) {
            currency = c;
            amount = masterAccountBalanceOperationSendRequest.getAmounts().get(c);
            break;
        }
        if (masterAccountBalanceOperationSendRequest.getTargetAddress() == null || masterAccountBalanceOperationSendRequest.getTargetAddress().equals("")) {
            throw new ServiceException("masterAccountBalanceOperationSendRequest.getTargetAddress() is null or empty");
        }
        String targetUserNameAndType = new UserGetNameAndType(masterAccountBalanceOperationSendRequest.getTargetAddress()).getResponse();
        if (targetUserNameAndType != null) {
            String targetUserName = targetUserNameAndType.split("____")[0];
            String targetType = "DOLLARBTC";
            if (targetUserNameAndType.split("____").length == 2) {
                targetType = targetUserNameAndType.split("____")[1];
            }
            String substractResponse = BaseOperation.substractToBalance(
                    MasterAccountFolderLocator.getBalanceFolder(masterAccountBalanceOperationSendRequest.getMasterAccountName()),
                    currency,
                    amount,
                    BalanceOperationType.SEND_IN,
                    BalanceOperationStatus.OK,
                    masterAccountBalanceOperationSendRequest.getAdditionalInfo() + " TARGET ADDRESS " + masterAccountBalanceOperationSendRequest.getTargetAddress(),
                    null,
                    false,
                    BaseOperation.getCharges("BTC", null, BalanceOperationType.SEND_IN, null, "DOLLARBTC", null),
                    false,
                    null
            );
            if (substractResponse.equals("OK")) {
                File userBalanceFolder = UsersFolderLocator.getBalanceFolder(targetUserName);
                if (targetType.equals("MONEYCLICK")) {
                    userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(targetUserName);
                }
                return Response
                        .status(200)
                        .entity(BaseOperation.addToBalance(
                                userBalanceFolder,
                                currency,
                                amount,
                                BalanceOperationType.RECEIVE_IN,
                                BalanceOperationStatus.OK,
                                masterAccountBalanceOperationSendRequest.getAdditionalInfo(),
                                null,
                                null,
                                false,
                                null
                        ))
                        .build();
            } else {
                return Response
                        .status(200)
                        .entity(substractResponse)
                        .build();
            }
        } else {
            return Response
                    .status(200)
                    .entity(BaseOperation.substractToBalance(
                            MasterAccountFolderLocator.getBalanceFolder(masterAccountBalanceOperationSendRequest.getMasterAccountName()),
                            currency,
                            amount,
                            BalanceOperationType.SEND_OUT,
                            BalanceOperationStatus.PROCESSING,
                            masterAccountBalanceOperationSendRequest.getAdditionalInfo() + " TARGET ADDRESS " + masterAccountBalanceOperationSendRequest.getTargetAddress(),
                            null,
                            false,
                            BaseOperation.getCharges("BTC", null, BalanceOperationType.SEND_OUT, null, "DOLLARBTC", null),
                            false,
                            null
                    ))
                    .build();
        }
    }

}
