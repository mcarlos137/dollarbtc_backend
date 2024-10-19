/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.bridge.operation.SecundaryOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.JsonNodeResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.StringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserActivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddFieldToVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddFlagRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddInfoRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddMasterWalletIdsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddWalletRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAllowAssignedPaymentsOnlyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAutomaticChangeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserBalanceOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCancelVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCheckSecurityQuestionsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCurrencyChangeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserGetConfigsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserInactivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserModifyInfoRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserProcessBalanceMovementRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserProcessVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserStartVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserChangeProfileRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserChangeToAdminRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserDeleteRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserMarkMessageAsReadedRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserPostMessageRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserReceiveAuthorizationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserRecoverDeletedRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserRemoveVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserSpecialOptionRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserStartVerificationEmailRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserTransferBTCRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.blockchain.BlockchainCheckInTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserAddCryptoAPIsAddress;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddFieldToVerification;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddFlag;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddMasterWalletIds;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddWallet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAllowAssignedPaymentsOnly;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAutomaticChange;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserCancelVerification;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserChangeProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserChangeReceiveAuthorizationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserChangeToAdmin;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserCheckSecurityQuestions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserDelete;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetConfig;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetConfigs;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetNameAndType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetProcessingBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetReceiveAuthorizationMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetReceiveAuthorizations;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetRelatedConfig;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetSegurityQuestions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerificationFields;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerificationFieldsNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerificationMessages;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetVerifications;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListAddressesWithReceivedTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListByFlagColor;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListByIndexAndValue;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListNames;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListNamesByIndexAndValue;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListSize;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListWithReceivedTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserMarkMessageAsReaded;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserModifyInfo;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserProcess;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserProcessBalanceMovement;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserProcessVerification;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserRecoverDeleted;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserRemoveVerification;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserSpecialOption;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserStartVerification;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserStartVerificationEmail;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserTransferBTC;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserVerifyEmail;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/user")
@XmlRegistry
public class UserServiceREST {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(UserCreateRequest userCreateRequest) throws ServiceException {
        if (userCreateRequest == null) {
            throw new ServiceException("userCreateRequest is null");
        }
        if (userCreateRequest.getUserName() == null || userCreateRequest.getUserName().equals("")) {
            throw new ServiceException("userAddEditRequest.getUserName() is null or empty");
        }
//        if (userCreateRequest.getEmail() == null || userCreateRequest.getEmail().equals("")) {
//            throw new ServiceException("userAddEditRequest.getEmail() is null or empty");
//        }
//        if (userCreateRequest.getMasterWalletIds() == null || userCreateRequest.getMasterWalletIds().isEmpty()) {
//            throw new ServiceException("userAddEditRequest.getMasterWalletIds() is null or empty");
//        }
        return Response
                .status(200)
                .entity(new StringResponse(new UserCreate(userCreateRequest).getResponse()))
                .build();
    }

    @POST
    @Path("/addMasterWalletIds")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMasterWalletIds(UserAddMasterWalletIdsRequest userAddMasterWalletIdsRequest) throws ServiceException {
        if (userAddMasterWalletIdsRequest == null) {
            throw new ServiceException("userAddMasterWalletIdsRequest is null");
        }
        if (userAddMasterWalletIdsRequest.getUserName() == null || userAddMasterWalletIdsRequest.getUserName().equals("")) {
            throw new ServiceException("userAddMasterWalletIdsRequest.getUserName() is null or empty");
        }
        if (userAddMasterWalletIdsRequest.getMasterWalletIds() == null || userAddMasterWalletIdsRequest.getMasterWalletIds().isEmpty()) {
            throw new ServiceException("userAddMasterWalletIdsRequest.getMasterWalletIds() is null or empty");
        }
        return Response
                .status(200)
                .entity(new StringResponse(new UserAddMasterWalletIds(userAddMasterWalletIdsRequest).getResponse()))
                .build();
    }

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws ServiceException {
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserList().getResponse()))
                .build();
    }

    @GET
    @Path("/getConfig/{userName}/{securityCode}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(
            @PathParam("userName") String userName,
            @PathParam("securityCode") String securityCode
    ) throws ServiceException {
        if (userName == null || userName.equals("") || userName.equals("null")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetConfig(userName, securityCode).getResponse()))
                .build();
    }

    @GET
    @Path("/getConfig/{userName}/{type}/{securityCode}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(
            @PathParam("userName") String userName,
            @PathParam("type") String type,
            @PathParam("securityCode") String securityCode
    ) throws ServiceException {
        if (userName == null || userName.equals("") || userName.equals("null")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetConfig(userName, type, securityCode).getResponse()))
                .build();
    }

    @GET
    @Path("/getRelatedConfig/{userName}/{relatedUserName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRelatedConfig(
            @PathParam("userName") String userName,
            @PathParam("relatedUserName") String relatedUserName
    ) throws ServiceException {
        if (userName == null || userName.equals("") || userName.equals("null")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetRelatedConfig(userName, relatedUserName).getResponse()))
                .build();
    }
    
    @POST
    @Path("/getConfigs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfigs(
            UserGetConfigsRequest userGetConfigsRequest
    ) throws ServiceException {
        if (userGetConfigsRequest == null) {
            throw new ServiceException("userGetConfigsRequest is null");
        }
        if (userGetConfigsRequest.getUserNames() == null || userGetConfigsRequest.getUserNames().length == 0) {
            throw new ServiceException("userGetConfigsRequest.getUserNames() is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetConfigs(userGetConfigsRequest).getResponse()))
                .build();
    }

    @GET
    @Path("/getBalance/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        new BlockchainCheckInTransactions(userName, new String[]{"wallets", "mcWallets"}).getResponse();
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetBalance(userName).getResponse()))
                .build();
    }

    @PUT
    @Path("/activate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activate(UserActivateRequest userActivateRequest) throws ServiceException {
        if (userActivateRequest == null) {
            throw new ServiceException("userActivateRequest is null or empty");
        }
        if (userActivateRequest.getUserName() == null || userActivateRequest.getUserName().equals("")) {
            throw new ServiceException("userActivateRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserProcess(userActivateRequest.getUserName(), UserProcess.ProcessType.ACTIVATE, userActivateRequest.getRegistrationCode()).getResponse())
                .build();
    }

    @PUT
    @Path("/inactivate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response inactivate(UserInactivateRequest userInactivateRequest) throws ServiceException {
        if (userInactivateRequest == null) {
            throw new ServiceException("userInactivateRequest is null or empty");
        }
        if (userInactivateRequest.getUserName() == null || userInactivateRequest.getUserName().equals("")) {
            throw new ServiceException("userInactivateRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserProcess(userInactivateRequest.getUserName(), UserProcess.ProcessType.INACTIVATE, null).getResponse())
                .build();
    }

    @POST
    @Path("/balanceOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response balanceOperation(
            UserBalanceOperationRequest userBalanceOperationRequest
    ) throws ServiceException {
        if (userBalanceOperationRequest == null) {
            throw new ServiceException("userBalanceOperationRequest is null");
        }
        if (userBalanceOperationRequest.getUserName() == null || userBalanceOperationRequest.getUserName().equals("")) {
            throw new ServiceException("userBalanceOperationRequest.getUserName() is null or empty");
        }
        if (userBalanceOperationRequest.getAmounts() == null || userBalanceOperationRequest.getAmounts().isEmpty() || userBalanceOperationRequest.getAmounts().keySet().size() > 1) {
            throw new ServiceException("userBalanceOperationRequest.getAmounts() is null or empty or bigger than max size");
        }
        if (userBalanceOperationRequest.getBalanceOperationType() == null) {
            throw new ServiceException("userBalanceOperationRequest.getBalanceOperationType() is null");
        }
        String currency = null;
        Double amount = null;
        for (String c : userBalanceOperationRequest.getAmounts().keySet()) {
            currency = c;
            amount = userBalanceOperationRequest.getAmounts().get(c);
            break;
        }
        switch (userBalanceOperationRequest.getBalanceOperationType()) {
            case DEBIT:
                return Response
                        .status(200)
                        .entity(new StringResponse(
                                BaseOperation.substractToBalance(
                                        UsersFolderLocator.getBalanceFolder(userBalanceOperationRequest.getUserName()),
                                        currency,
                                        amount,
                                        BalanceOperationType.DEBIT,
                                        BalanceOperationStatus.OK,
                                        userBalanceOperationRequest.getAdditionalInfo(),
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
                                        UsersFolderLocator.getBalanceFolder(userBalanceOperationRequest.getUserName()),
                                        currency,
                                        amount,
                                        BalanceOperationType.CREDIT,
                                        BalanceOperationStatus.OK,
                                        userBalanceOperationRequest.getAdditionalInfo(),
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
                if (userBalanceOperationRequest.getTargetAddress() == null || userBalanceOperationRequest.getTargetAddress().equals("")) {
                    throw new ServiceException("userBalanceOperationRequest.getTargetAddress() is null or empty");
                }
                String targetUserNameAndType = new UserGetNameAndType(userBalanceOperationRequest.getTargetAddress()).getResponse();
                ObjectNode additionals = new ObjectMapper().createObjectNode();
                additionals.put("senderUserName", userBalanceOperationRequest.getUserName());
                additionals.put("targetAddress", userBalanceOperationRequest.getTargetAddress());
                if (targetUserNameAndType != null) {
                    String targetUserName = targetUserNameAndType.split("____")[0];
                    String targetType = "DOLLARBTC";
                    if (targetUserNameAndType.split("____").length == 2) {
                        targetType = targetUserNameAndType.split("____")[1];
                    }
                    String inLimits = BaseOperation.inLimits(userBalanceOperationRequest.getUserName(), currency, amount, BalanceOperationType.SEND_IN);
                    if (!inLimits.equals("OK")) {
                        return Response
                                .status(200)
                                .entity(new StringResponse(inLimits))
                                .build();
                    }
                    String substractResponse = BaseOperation.substractToBalance(
                            UsersFolderLocator.getBalanceFolder(userBalanceOperationRequest.getUserName()),
                            currency,
                            amount,
                            BalanceOperationType.SEND_IN,
                            BalanceOperationStatus.OK,
                            userBalanceOperationRequest.getAdditionalInfo() + " TARGET ADDRESS " + userBalanceOperationRequest.getTargetAddress(),
                            null,
                            false,
                            BaseOperation.getCharges("BTC", null, BalanceOperationType.SEND_IN, null, "DOLLARBTC", null),
                            false,
                            additionals
                    );
                    if (substractResponse.equals("OK")) {
                        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(targetUserName);
                        if (targetType.equals("MONEYCLICK")) {
                            userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(targetUserName);
                        }
                        return Response
                                .status(200)
                                .entity(new StringResponse(
                                        BaseOperation.addToBalance(
                                                userBalanceFolder,
                                                currency,
                                                amount,
                                                BalanceOperationType.RECEIVE_IN,
                                                BalanceOperationStatus.OK,
                                                userBalanceOperationRequest.getAdditionalInfo(),
                                                null,
                                                null,
                                                false,
                                                additionals
                                        )))
                                .build();
                    } else {
                        return Response
                                .status(200)
                                .entity(new StringResponse(substractResponse))
                                .build();
                    }
                } else {
                    String inLimits = BaseOperation.inLimits(userBalanceOperationRequest.getUserName(), currency, amount, BalanceOperationType.SEND_OUT);
                    if (!inLimits.equals("OK")) {
                        return Response
                                .status(200)
                                .entity(new StringResponse(inLimits))
                                .build();
                    }
                    return Response
                            .status(200)
                            .entity(new StringResponse(
                                    BaseOperation.substractToBalance(
                                            UsersFolderLocator.getBalanceFolder(userBalanceOperationRequest.getUserName()),
                                            currency,
                                            amount,
                                            BalanceOperationType.SEND_OUT,
                                            BalanceOperationStatus.PROCESSING,
                                            userBalanceOperationRequest.getAdditionalInfo() + " TARGET ADDRESS " + userBalanceOperationRequest.getTargetAddress(),
                                            null,
                                            false,
                                            BaseOperation.getCharges("BTC", null, BalanceOperationType.SEND_OUT, null, "DOLLARBTC", null),
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

    @GET
    @Path("/getBalanceMovements/{userName}/{initTimestamp}/{endTimestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalanceMovements(
            @PathParam("userName") String userName,
            @PathParam("initTimestamp") String initTimestamp,
            @PathParam("endTimestamp") String endTimestamp
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (initTimestamp == null || initTimestamp.equals("")) {
            throw new ServiceException("initTimestamp is null or empty");
        }
        if (endTimestamp == null || endTimestamp.equals("")) {
            throw new ServiceException("endTimestamp is null or empty");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetBalanceMovements(userName, initTimestamp, endTimestamp, null, false).getResponse()))
                .build();
    }

    @GET
    @Path("/getProcessingBalanceMovements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessingBalanceMovements() throws ServiceException {
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new UserGetProcessingBalanceMovements().getResponse()))
                .build();
    }

    @GET
    @Path("/processBalanceMovement/{userName}/{balanceOperationProcessId}/{balanceOperationStatus}/{message}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processBalanceMovement(
            @PathParam("userName") String userName,
            @PathParam("balanceOperationProcessId") String balanceOperationProcessId,
            @PathParam("balanceOperationStatus") BalanceOperationStatus balanceOperationStatus,
            @PathParam("message") String message
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (balanceOperationProcessId == null || balanceOperationProcessId.equals("")) {
            throw new ServiceException("balanceOperationProcessId is null or empty");
        }
        if (balanceOperationStatus == null) {
            throw new ServiceException("balanceOperationStatus is null");
        }
        return Response
                .status(200)
                .entity(new StringResponse(new UserProcessBalanceMovement(userName, balanceOperationProcessId, balanceOperationStatus, message).getResponse()))
                .build();
    }

    @POST
    @Path("/processBalanceMovement")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processBalanceMovement(UserProcessBalanceMovementRequest userProcessBalanceMovementRequest) throws ServiceException {
        if (userProcessBalanceMovementRequest == null) {
            throw new ServiceException("userProcessBalanceMovementRequest is null");
        }
        if (userProcessBalanceMovementRequest.getUserName() == null || userProcessBalanceMovementRequest.getUserName().equals("")) {
            throw new ServiceException("userProcessBalanceMovementRequest.getUserName() is null or empty");
        }
        if (userProcessBalanceMovementRequest.getBalanceOperationProcessId() == null || userProcessBalanceMovementRequest.getBalanceOperationProcessId().equals("")) {
            throw new ServiceException("userProcessBalanceMovementRequest.getBalanceOperationProcessId() is null or empty");
        }
        if (userProcessBalanceMovementRequest.getBalanceOperationStatus() == null) {
            throw new ServiceException("userProcessBalanceMovementRequest.getBalanceOperationStatus() is null");
        }
        return Response
                .status(200)
                .entity(new StringResponse(new UserProcessBalanceMovement(userProcessBalanceMovementRequest).getResponse()))
                .build();
    }

    @POST
    @Path("/currencyChange")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response currencyChange(
            UserCurrencyChangeRequest userCurrencyChangeRequest
    ) throws ServiceException {
        if (userCurrencyChangeRequest == null) {
            throw new ServiceException("userCurrencyChangeRequest is null");
        }
        if (userCurrencyChangeRequest.getUserName() == null || userCurrencyChangeRequest.getUserName().equals("")) {
            throw new ServiceException("userCurrencyChangeRequest.getUserName() is null or empty");
        }
        if (userCurrencyChangeRequest.getBaseCurrency() == null || userCurrencyChangeRequest.getBaseCurrency().isEmpty()) {
            throw new ServiceException("userCurrencyChangeRequest.getBaseCurrency() is null or empty");
        }
        if (userCurrencyChangeRequest.getTargetCurrency() == null || userCurrencyChangeRequest.getTargetCurrency().isEmpty()) {
            throw new ServiceException("userCurrencyChangeRequest.getTargetCurrency() is null or empty");
        }
        if (userCurrencyChangeRequest.getRequestedAmount() == null) {
            throw new ServiceException("userCurrencyChangeRequest.getRequestedAmount() is null");
        }
        return Response
                .status(200)
                .entity(new StringResponse(SecundaryOperation.currencyChangeUser(userCurrencyChangeRequest.getUserName(), userCurrencyChangeRequest.getBaseCurrency(), userCurrencyChangeRequest.getTargetCurrency(), userCurrencyChangeRequest.getRequestedAmount())))
                .build();
    }

    @GET
    @Path("/getMarketPrice/{baseCurrency}/{targetCurrency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMarketPrice(
            @PathParam("baseCurrency") String baseCurrency,
            @PathParam("targetCurrency") String targetCurrency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(BigDecimal.ONE.divide(SecundaryOperation.getLocalMarketPrice(baseCurrency, targetCurrency), 8, RoundingMode.UP))
                .build();
    }

    @PUT
    @Path("/changeProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeProfile(UserChangeProfileRequest userChangeProfileRequest) throws ServiceException {
        if (userChangeProfileRequest.getUserName() == null || userChangeProfileRequest.getUserName().equals("")) {
            throw new ServiceException("userChangeProfileRequest.getUserName() is null or empty");
        }
        if (userChangeProfileRequest.getUserProfile() == null) {
            throw new ServiceException("userChangeProfileRequest.getUserProfile() is null");
        }
        return Response
                .status(200)
                .entity(new UserChangeProfile(userChangeProfileRequest.getUserName(), userChangeProfileRequest.getUserProfile()).getResponse())
                .build();
    }

    @POST
    @Path("/startVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response startVerification(
            UserStartVerificationRequest userStartVerificationRequest
    ) throws ServiceException {
        if (userStartVerificationRequest == null) {
            throw new ServiceException("userStartVerificationRequest is null");
        }
        if (userStartVerificationRequest.getUserName() == null || userStartVerificationRequest.getUserName().equals("")) {
            throw new ServiceException("userStartVerificationRequest.getUserName() is null or empty");
        }
        if (userStartVerificationRequest.getUserVerificationType() == null) {
            throw new ServiceException("userStartVerificationRequest.getUserVerificationType() is null");
        }
        if (!userStartVerificationRequest.getUserVerificationType().equals(UserVerificationType.F) && (userStartVerificationRequest.getFieldNames() == null || userStartVerificationRequest.getFieldNames().length == 0)
                && (userStartVerificationRequest.getInfo() == null || userStartVerificationRequest.getInfo().equals(""))) {
            throw new ServiceException("userStartVerificationRequest.getFieldNames() and userStartVerificationRequest.getInfo() are null or empty");
        }
        return Response
                .status(200)
                .entity(new UserStartVerification(userStartVerificationRequest).getResponse())
                .build();
    }

    @POST
    @Path("/startVerificationEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response startVerificationEmail(
            UserStartVerificationEmailRequest userStartVerificationEmailRequest
    ) throws ServiceException {
        if (userStartVerificationEmailRequest == null) {
            throw new ServiceException("userStartVerificationEmailRequest is null");
        }
        if (userStartVerificationEmailRequest.getUserName() == null || userStartVerificationEmailRequest.getUserName().equals("")) {
            throw new ServiceException("userStartVerificationEmailRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserStartVerificationEmail(userStartVerificationEmailRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getVerifications")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVerifications(
            @QueryParam("userName") String userName,
            @QueryParam("userVerificationStatus") UserVerificationStatus userVerificationStatus
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new UserGetVerifications(userName, userVerificationStatus).getResponse())
                .build();
    }

    @PUT
    @Path("/processVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response processVerification(
            UserProcessVerificationRequest userProcessVerificationRequest
    ) throws ServiceException {
        if (userProcessVerificationRequest == null) {
            throw new ServiceException("userProcessVerificationRequest is null");
        }
        if (userProcessVerificationRequest.getUserName() == null || userProcessVerificationRequest.getUserName().equals("")) {
            throw new ServiceException("userProcessVerificationRequest.getUserName() is null or empty");
        }
        if (userProcessVerificationRequest.getTimestamp() == null || userProcessVerificationRequest.getTimestamp().equals("")) {
            throw new ServiceException("userProcessVerificationRequest.getTimestamp() is null or empty");
        }
        if (userProcessVerificationRequest.getLastUserVerificationStatus() == null) {
            throw new ServiceException("userProcessVerificationRequest.getLastUserVerificationStatus() is null");
        }
        return Response
                .status(200)
                .entity(new UserProcessVerification(userProcessVerificationRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/addFieldToVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addFieldToVerification(
            UserAddFieldToVerificationRequest userAddFieldToVerificationRequest
    ) throws ServiceException {
        if (userAddFieldToVerificationRequest == null) {
            throw new ServiceException("userAddFieldToVerificationRequest is null");
        }
        if (userAddFieldToVerificationRequest.getUserName() == null || userAddFieldToVerificationRequest.getUserName().equals("")) {
            throw new ServiceException("userAddFieldToVerificationRequest.getUserName() is null or empty");
        }
        if (userAddFieldToVerificationRequest.getUserVerificationType() == null) {
            throw new ServiceException("userAddFieldToVerificationRequest.getUserVerificationType() is null");
        }
        if (userAddFieldToVerificationRequest.getFieldNames() == null || userAddFieldToVerificationRequest.getFieldNames().length == 0) {
            throw new ServiceException("userAddFieldToVerificationRequest.getFieldNames() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserAddFieldToVerification(userAddFieldToVerificationRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/cancelVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response cancelVerification(
            UserCancelVerificationRequest userCancelVerificationRequest
    ) throws ServiceException {
        if (userCancelVerificationRequest == null) {
            throw new ServiceException("userCancelVerificationRequest is null");
        }
        if (userCancelVerificationRequest.getUserName() == null || userCancelVerificationRequest.getUserName().equals("")) {
            throw new ServiceException("userCancelVerificationRequest.getUserName() is null or empty");
        }
        if (userCancelVerificationRequest.getVerificationOperationId() == null || userCancelVerificationRequest.getVerificationOperationId().equals("")) {
            throw new ServiceException("userCancelVerificationRequest.getVerificationOperationId() is null or empty");
        }
        if (userCancelVerificationRequest.getUserVerificationType() == null) {
            throw new ServiceException("userCancelVerificationRequest.getUserVerificationType() is null");
        }
        return Response
                .status(200)
                .entity(new UserCancelVerification(userCancelVerificationRequest).getResponse())
                .build();
    }

    @POST
    @Path("/postMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMessage(UserPostMessageRequest userPostMessageRequest) throws ServiceException {
        if (userPostMessageRequest == null) {
            throw new ServiceException("userPostMessageRequest is null");
        }
        if (userPostMessageRequest.getUserName() == null || userPostMessageRequest.getUserName().equals("")) {
            throw new ServiceException("userPostMessageRequest.getUserName() is null or empty");
        }
        if (userPostMessageRequest.getMessage() == null || userPostMessageRequest.getMessage().equals("")) {
            throw new ServiceException("userPostMessageRequest.getMessage() is null or empty");
        }
        if (userPostMessageRequest.getRedirectionPath() == null || userPostMessageRequest.getRedirectionPath().equals("")) {
            throw new ServiceException("userPostMessageRequest.getRedirectionPath() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserPostMessage(userPostMessageRequest.getUserName(), userPostMessageRequest.getMessage(), userPostMessageRequest.getRedirectionPath()).getResponse())
                .build();
    }

    @PUT
    @Path("/markMessageAsReaded")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response markMessageAsReaded(UserMarkMessageAsReadedRequest userMarkMessageAsReadedRequest) throws ServiceException {
        if (userMarkMessageAsReadedRequest == null) {
            throw new ServiceException("userMarkMessageAsReadedRequest is null");
        }
        if (userMarkMessageAsReadedRequest.getUserName() == null || userMarkMessageAsReadedRequest.getUserName().equals("")) {
            throw new ServiceException("userMarkMessageAsReadedRequest.getUserName() is null or empty");
        }
        if (userMarkMessageAsReadedRequest.getId() == null || userMarkMessageAsReadedRequest.getId().equals("")) {
            throw new ServiceException("userMarkMessageAsReadedRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserMarkMessageAsReaded(userMarkMessageAsReadedRequest.getUserName(), userMarkMessageAsReadedRequest.getId()).getResponse())
                .build();
    }

    @POST
    @Path("/addInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addInfo(
            UserAddInfoRequest userAddInfoRequest
    ) throws ServiceException {
        if (userAddInfoRequest == null) {
            throw new ServiceException("userAddInfoRequest is null");
        }
        if (userAddInfoRequest.getUserName() == null || userAddInfoRequest.getUserName().equals("")) {
            throw new ServiceException("userAddInfoRequest.getUserName() is null or empty");
        }
        if (userAddInfoRequest.getFieldName() == null || userAddInfoRequest.getFieldName().equals("")) {
            throw new ServiceException("userAddInfoRequest.getFieldName() is null or empty");
        }
        if (userAddInfoRequest.getFieldValue() == null && userAddInfoRequest.getFieldValueArray() == null) {
            throw new ServiceException("userAddInfoRequest.getFieldValue() and userAddInfoRequest.getFieldValueArray() is null");
        }
        return Response
                .status(200)
                .entity(new UserAddInfo(userAddInfoRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/modifyInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response modifyInfo(
            UserModifyInfoRequest userModifyInfoRequest
    ) throws ServiceException {
        if (userModifyInfoRequest == null) {
            throw new ServiceException("userModifyInfoRequest is null");
        }
        if (userModifyInfoRequest.getUserName() == null || userModifyInfoRequest.getUserName().equals("")) {
            throw new ServiceException("userModifyInfoRequest.getUserName() is null or empty");
        }
        if (userModifyInfoRequest.getFieldName() == null || userModifyInfoRequest.getFieldName().equals("")) {
            throw new ServiceException("userModifyInfoRequest.getFieldName() is null or empty");
        }
        if (userModifyInfoRequest.getFieldValue() == null && userModifyInfoRequest.getFieldValueArray() == null) {
            throw new ServiceException("userModifyInfoRequest.getFieldValue() and userModifyInfoRequest.getFieldValueArray() is null");
        }
        return Response
                .status(200)
                .entity(new UserModifyInfo(userModifyInfoRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/changeToAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeToAdmin(UserChangeToAdminRequest userChangeToAdminRequest) throws ServiceException {
        if (userChangeToAdminRequest == null) {
            throw new ServiceException("userChangeToAdminRequest is null");
        }
        if (userChangeToAdminRequest.getUserName() == null || userChangeToAdminRequest.getUserName().equals("")) {
            throw new ServiceException("userChangeToAdminRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserChangeToAdmin(userChangeToAdminRequest.getUserName()).getResponse())
                .build();
    }

    @POST
    @Path("/addWallet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addWallet(
            UserAddWalletRequest userAddWalletRequest
    ) throws ServiceException {
        if (userAddWalletRequest == null) {
            throw new ServiceException("userAddWalletRequest is null");
        }
        if (userAddWalletRequest.getUserName() == null || userAddWalletRequest.getUserName().equals("")) {
            throw new ServiceException("userAddWalletRequest.getUserName() is null or empty");
        }
        if (userAddWalletRequest.isMoneyClick()) {
            if (userAddWalletRequest.getBlockchain() == null || userAddWalletRequest.getBlockchain().equals("")) {
                userAddWalletRequest.setBlockchain("bitcoin");
            }
            if (!(userAddWalletRequest.getBlockchain().equals("bitcoin") || userAddWalletRequest.getBlockchain().equals("ethereum"))) {
                throw new ServiceException("userAddWalletRequest.getBlockchain() value is not allowed");
            }
            return Response
                    .status(200)
                    .entity(new MCUserAddCryptoAPIsAddress(userAddWalletRequest.getUserName(), userAddWalletRequest.getBlockchain(), true).getResponse())
                    .build();
        }
        return Response
                .status(200)
                .entity(new UserAddWallet(userAddWalletRequest.getUserName(), true, userAddWalletRequest.isMoneyClick()).getResponse())
                .build();
    }

    @POST
    @Path("/automaticChange")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response automaticChange(
            UserAutomaticChangeRequest userAutomaticChangeRequest
    ) throws ServiceException {
        if (userAutomaticChangeRequest == null) {
            throw new ServiceException("userAutomaticChangeRequest is null");
        }
        if (userAutomaticChangeRequest.getUserName() == null || userAutomaticChangeRequest.getUserName().equals("")) {
            throw new ServiceException("userAutomaticChangeRequest.getUserName() is null or empty");
        }
        if (userAutomaticChangeRequest.getCurrency() == null || userAutomaticChangeRequest.getCurrency().equals("")) {
            throw new ServiceException("userAutomaticChangeRequest.getCurrency() is null or empty");
        }
        if (userAutomaticChangeRequest.getMarkedPrice() == null || userAutomaticChangeRequest.getMarkedPrice() == 0.0) {
            throw new ServiceException("userAutomaticChangeRequest.getMarkedPrice() is null or zero");
        }
        if (userAutomaticChangeRequest.getAlertBand() == null || userAutomaticChangeRequest.getAlertBand() == 0.0) {
            throw new ServiceException("userAutomaticChangeRequest.getAlertBand() is null or zero");
        }
        return Response
                .status(200)
                .entity(new UserAutomaticChange(userAutomaticChangeRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getSegurityQuestions/{userName}/{quantity}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSegurityQuestions(
            @PathParam("userName") String userName,
            @PathParam("quantity") int quantity
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (quantity == 0) {
            throw new ServiceException("quantity must be greater than zero");
        }
        return Response
                .status(200)
                .entity(new UserGetSegurityQuestions(userName, quantity).getResponse())
                .build();
    }

    @POST
    @Path("/checkSecurityQuestions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkSecurityQuestions(
            UserCheckSecurityQuestionsRequest userCheckSecurityQuestionsRequest
    ) throws ServiceException {
        if (userCheckSecurityQuestionsRequest == null) {
            throw new ServiceException("userCheckSecurityQuestionsRequest is null");
        }
        if (userCheckSecurityQuestionsRequest.getUserName() == null || userCheckSecurityQuestionsRequest.getUserName().equals("")) {
            throw new ServiceException("userCheckSecurityQuestionsRequest.getUserName() is null or empty");
        }
        if (userCheckSecurityQuestionsRequest.getSecurityQuestionsAndAnswers() == null || userCheckSecurityQuestionsRequest.getSecurityQuestionsAndAnswers().size() == 0) {
            throw new ServiceException("userCheckSecurityQuestionsRequest.getSecurityQuestionsAndAnswers() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserCheckSecurityQuestions(userCheckSecurityQuestionsRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getSendOpetarionType/{targetAddress}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSendOpetarionType(
            @PathParam("targetAddress") String targetAddress
    ) throws ServiceException {
        if (targetAddress == null || targetAddress.equals("")) {
            throw new ServiceException("targetAddress is null or empty");
        }
        String targetUserName = new UserGetNameAndType(targetAddress).getResponse();
        if (targetUserName != null) {
            return Response
                    .status(200)
                    .entity(BalanceOperationType.SEND_IN)
                    .build();
        }
        return Response
                .status(200)
                .entity(BalanceOperationType.SEND_OUT)
                .build();
    }

    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(
            UserDeleteRequest userDeleteRequest
    ) throws ServiceException {
        if (userDeleteRequest == null) {
            throw new ServiceException("userDeleteRequest is null");
        }
        if (userDeleteRequest.getUserName() == null || userDeleteRequest.getUserName().equals("")) {
            throw new ServiceException("userDeleteRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserDelete(userDeleteRequest).getResponse())
                .build();
    }

    @POST
    @Path("/recoverDeleted")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response recoverDeleted(
            UserRecoverDeletedRequest userRecoverDeletedRequest
    ) throws ServiceException {
        if (userRecoverDeletedRequest == null) {
            throw new ServiceException("userRecoverDeletedRequest is null");
        }
        if (userRecoverDeletedRequest.getUserName() == null || userRecoverDeletedRequest.getUserName().equals("")) {
            throw new ServiceException("userRecoverDeletedRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserRecoverDeleted(userRecoverDeletedRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/removeUserVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeVerification(
            UserRemoveVerificationRequest userRemoveVerificationRequest
    ) throws ServiceException {
        if (userRemoveVerificationRequest == null) {
            throw new ServiceException("userRemoveVerificationRequest is null");
        }
        if (userRemoveVerificationRequest.getUserName() == null || userRemoveVerificationRequest.getUserName().equals("")) {
            throw new ServiceException("userRemoveVerificationRequest.getUserName() is null or empty");
        }
        if (userRemoveVerificationRequest.getUserVerificationType() == null) {
            throw new ServiceException("userRemoveVerificationRequest.getUserVerificationType() is null");
        }
        return Response
                .status(200)
                .entity(new UserRemoveVerification(userRemoveVerificationRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getUserVerificationFields/{userVerificationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVerificationFields(
            @PathParam("userVerificationType") UserVerificationType userVerificationType
    ) throws ServiceException {
        if (userVerificationType == null) {
            throw new ServiceException("userVerificationType is null");
        }
        return Response
                .status(200)
                .entity(new UserGetVerificationFields(userVerificationType).getResponse())
                .build();
    }

    @GET
    @Path("/getUserVerificationFieldsNew/{userVerificationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserVerificationFieldsNew(
            @PathParam("userVerificationType") UserVerificationType userVerificationType
    ) throws ServiceException {
        if (userVerificationType == null) {
            throw new ServiceException("userVerificationType is null");
        }
        return Response
                .status(200)
                .entity(new UserGetVerificationFieldsNew(userVerificationType).getResponse())
                .build();
    }

    @GET
    @Path("/getUserVerificationMessages/{userVerificationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserVerificationMessages(
            @PathParam("userVerificationType") UserVerificationType userVerificationType
    ) throws ServiceException {
        if (userVerificationType == null) {
            throw new ServiceException("userVerificationType is null");
        }
        return Response
                .status(200)
                .entity(new UserGetVerificationMessages(userVerificationType).getResponse())
                .build();
    }

    @GET
    @Path("/verifyEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyEmailId(
            @QueryParam("userName") String userName,
            @QueryParam("id") String id
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null");
        }
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null");
        }
        return Response
                .status(200)
                .entity(new UserVerifyEmail(userName, id).getResponse())
                .build();
    }

    @PUT
    @Path("/transferBTC")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response transferBTC(
            UserTransferBTCRequest userTransferBTCRequest
    ) throws ServiceException {
        if (userTransferBTCRequest == null) {
            throw new ServiceException("userTransferBTCRequest is null");
        }
        if (userTransferBTCRequest.getUserName() == null || userTransferBTCRequest.getUserName().equals("")) {
            throw new ServiceException("userTransferBTCRequest.getUserName() is null or empty");
        }
        if (userTransferBTCRequest.getAmount() == null || userTransferBTCRequest.getAmount().equals(0)) {
            throw new ServiceException("userTransferBTCRequest.getUserVerificationType() is null or zero");
        }
        if (userTransferBTCRequest.getBalanceOperationType() == null) {
            throw new ServiceException("userTransferBTCRequest.getBalanceOperationType() is null");
        }
        if (!(userTransferBTCRequest.getBalanceOperationType().equals(BalanceOperationType.TRANSFER_FROM_BALANCE_TO_MCBALANCE) || userTransferBTCRequest.getBalanceOperationType().equals(BalanceOperationType.TRANSFER_FROM_MCBALANCE_TO_BALANCE))) {
            throw new ServiceException("userTransferBTCRequest.getBalanceOperationType() value is not allowed");
        }
        return Response
                .status(200)
                .entity(new UserTransferBTC(userTransferBTCRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getReceiveAuthorizations/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReceiveAuthorizations(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserGetReceiveAuthorizations(userName).getResponse())
                .build();
    }

    @PUT
    @Path("/changeReceiveAuthorizationStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeReceiveAuthorizationStatus(
            UserReceiveAuthorizationStatusRequest userReceiveAuthorizationStatusRequest
    ) throws ServiceException {
        if (userReceiveAuthorizationStatusRequest == null) {
            throw new ServiceException("userReceiveAuthorizationStatusRequest is null");
        }
        if (userReceiveAuthorizationStatusRequest.getUserName() == null || userReceiveAuthorizationStatusRequest.getUserName().equals("")) {
            throw new ServiceException("userReceiveAuthorizationStatusRequest.getUserName() is null or empty");
        }
        if (userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId() == null || userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId().equals("")) {
            throw new ServiceException("userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId() is null or empty");
        }
        if (userReceiveAuthorizationStatusRequest.getReceiveAuthorizationStatus() == null) {
            throw new ServiceException("userReceiveAuthorizationStatusRequest.getReceiveAuthorizationStatus() is null");
        }
        return Response
                .status(200)
                .entity(new UserChangeReceiveAuthorizationStatus(userReceiveAuthorizationStatusRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getReceiveAuthorizationMessage/{receiveAuthorizationId}/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getReceiveAuthorizationMessage(
            @PathParam("receiveAuthorizationId") String receiveAuthorizationId,
            @PathParam("language") String language
    ) throws ServiceException {
        if (receiveAuthorizationId == null || receiveAuthorizationId.equals("")) {
            throw new ServiceException("receiveAuthorizationId is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserGetReceiveAuthorizationMessage(receiveAuthorizationId, language).getResponse())
                .build();
    }

    @GET
    @Path("/getUsersWithReceivedTransactions/currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWithReceivedTransactions(
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new UserListWithReceivedTransactions(currency).getResponse())
                .build();
    }

    @GET
    @Path("/getUsersAddressesWithReceivedTransactions/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAddressesWithReceivedTransactions(
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new UserListAddressesWithReceivedTransactions(currency).getResponse())
                .build();
    }

    @GET
    @Path("/listNames")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listNames() throws ServiceException {
        return Response
                .status(200)
                .entity(new UserListNames().getResponse())
                .build();
    }

    @PUT
    @Path("/addFlag")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addFlag(
            UserAddFlagRequest userAddFlagRequest
    ) throws ServiceException {
        if (userAddFlagRequest == null) {
            throw new ServiceException("userAddFlagRequest is null");
        }
        if (userAddFlagRequest.getOperatorUserName() == null || userAddFlagRequest.getOperatorUserName().equals("")) {
            throw new ServiceException("userAddFlagRequest.getOperatorUserName() is null or empty");
        }
        if (userAddFlagRequest.getUserName() == null || userAddFlagRequest.getUserName().equals("")) {
            throw new ServiceException("userAddFlagRequest.getUserName() is null or empty");
        }
        if (userAddFlagRequest.getFlagColor() == null || userAddFlagRequest.getFlagColor().equals("")) {
            throw new ServiceException("userAddFlagRequest.getFlagColor() is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserAddFlag(userAddFlagRequest).getResponse())
                .build();
    }

    @GET
    @Path("/listByFlagColor/{flagColor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listByFlagColor(
            @PathParam("flagColor") String flagColor
    ) throws ServiceException {
        if (flagColor == null || flagColor.equals("")) {
            throw new ServiceException("flagColor is null or empty");
        }
        return Response
                .status(200)
                .entity(new UserListByFlagColor(flagColor).getResponse())
                .build();
    }

    @GET
    @Path("/listSize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listSize() throws ServiceException {
        return Response
                .status(200)
                .entity(new UserListSize().getResponse())
                .build();
    }

    @PUT
    @Path("/specialOption")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response specialOption(
            UserSpecialOptionRequest userSpecialOptionRequest
    ) throws ServiceException {
        if (userSpecialOptionRequest == null) {
            throw new ServiceException("userSpecialOptionRequest is null");
        }
        if (userSpecialOptionRequest.getUserName() == null || userSpecialOptionRequest.getUserName().equals("")) {
            throw new ServiceException("userSpecialOptionRequest.getUserName() is null or empty");
        }
        if (userSpecialOptionRequest.getOption() == null || userSpecialOptionRequest.getOption().equals("")) {
            throw new ServiceException("userSpecialOptionRequest.getOption() is null or empty");
        }
        if (userSpecialOptionRequest.getEnable() == null) {
            throw new ServiceException("userSpecialOptionRequest.getEnable() is null");
        }
        return Response
                .status(200)
                .entity(new UserSpecialOption(userSpecialOptionRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/allowAssignedPaymentsOnly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response allowAssignedPaymentsOnly(
            UserAllowAssignedPaymentsOnlyRequest userAllowAssignedPaymentsOnlyRequest
    ) throws ServiceException {
        if (userAllowAssignedPaymentsOnlyRequest == null) {
            throw new ServiceException("userAllowAssignedPaymentsOnlyRequest is null");
        }
        if (userAllowAssignedPaymentsOnlyRequest.getUserName() == null || userAllowAssignedPaymentsOnlyRequest.getUserName().equals("")) {
            throw new ServiceException("userAllowAssignedPaymentsOnlyRequest.getUserName() is null or empty");
        }
        if (userAllowAssignedPaymentsOnlyRequest.getAllow() == null) {
            throw new ServiceException("userAllowAssignedPaymentsOnlyRequest.getAllow() is null");
        }
        return Response
                .status(200)
                .entity(new UserAllowAssignedPaymentsOnly(userAllowAssignedPaymentsOnlyRequest).getResponse())
                .build();
    }

    @GET
    @Path("/listNamesByIndexAndValue/{index}/{value}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listNamesByIndexAndValue(
            @PathParam("index") String index,
            @PathParam("value") String value
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new UserListNamesByIndexAndValue(index, value).getResponse())
                .build();
    }

    @GET
    @Path("/listByIndexAndValue/{index}/{value}/{page}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listByIndexAndValue(
            @PathParam("index") String index,
            @PathParam("value") String value,
            @PathParam("page") String page
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new UserListByIndexAndValue(index, value, page).getResponse())
                .build();
    }

}
