/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserBuyBitcoinsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserBuyCryptoRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserCashbackRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserCloseMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserDeleteMessageRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserDeleteMessagesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserFastChangeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserGetMessageOffersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserMarkMessageAsReadedRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserPostMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSellBitcoinsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSellCryptoRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSendToPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserTakeMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.blockchain.BlockchainCheckInTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.sendtopayment.SendToPaymentCreateOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.sendtopayment.SendToPaymentCreateOperationNew;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsCheckInCoinTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsCheckInEthereumTokenTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserAddCryptoAPIsAddress;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserAddTronGridAddress;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserBuyBitcoins;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserBuyCrypto;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserCashback;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserCloseMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserDeleteMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserDeleteMessages;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserExist;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserFastChange;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetAlerts;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetBitcoinPrice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetCryptoPrice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetFastChangeFactor;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetMessageAttachment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetMessageOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetPairs;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetReferralCodes;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetSpecialBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserMarkMessageAsReaded;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserPostMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserSellBitcoins;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserSellCrypto;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserSend;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserSendNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserTakeMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.trongrid.TronGridCheckInTokenTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserAddWallet;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
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
@Path("/mcUser")
@XmlRegistry
public class MCUserServiceREST {

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response send(MCUserSendRequest mcUserSendRequest) throws ServiceException {
        if (mcUserSendRequest == null) {
            throw new ServiceException("mcUserSendRequest is null");
        }
        if (mcUserSendRequest.getBaseUserName() == null || mcUserSendRequest.getBaseUserName().equals("")) {
            throw new ServiceException("mcUserSendRequest.getBaseUserName() is null or empty");
        }
        if (mcUserSendRequest.getTargetUserName() == null || mcUserSendRequest.getTargetUserName().equals("")) {
            throw new ServiceException("mcUserSendRequest.getTargetUserName() is null or empty");
        }
        if (mcUserSendRequest.getCurrency() == null || mcUserSendRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserSendRequest.getCurrency() is null or empty");
        }
        if (mcUserSendRequest.getAmount() == null || mcUserSendRequest.getAmount().equals(0.0)) {
            throw new ServiceException("mcUserSendRequest.getAmount() is null or empty");
        }
        String response = new MCUserSend(mcUserSendRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/sendToPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendToPayment(MCUserSendToPaymentRequest mcUserSendToPaymentRequest) throws ServiceException {
        if (mcUserSendToPaymentRequest == null) {
            throw new ServiceException("mcUserSendToPaymentRequest is null");
        }
        if (mcUserSendToPaymentRequest.getUserName() == null || mcUserSendToPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserSendToPaymentRequest.getUserName() is null or empty");
        }
        if (mcUserSendToPaymentRequest.getCurrency() == null || mcUserSendToPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserSendToPaymentRequest.getCurrency() is null or empty");
        }
        if (mcUserSendToPaymentRequest.getAmount() == null || mcUserSendToPaymentRequest.getAmount().equals(0.0)) {
            throw new ServiceException("mcUserSendToPaymentRequest.getAmount() is null or empty");
        }
        if (mcUserSendToPaymentRequest.getPayment() == null) {
            throw new ServiceException("mcUserSendToPaymentRequest.getPayment() is null");
        }
        return Response
                .status(200)
                .entity(new SendToPaymentCreateOperation(mcUserSendToPaymentRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/sendToPaymentNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendToPaymentNew(MCUserSendToPaymentRequest mcUserSendToPaymentRequest) throws ServiceException {
        if (mcUserSendToPaymentRequest == null) {
            throw new ServiceException("mcUserSendToPaymentRequest is null");
        }
        if (mcUserSendToPaymentRequest.getUserName() == null || mcUserSendToPaymentRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserSendToPaymentRequest.getUserName() is null or empty");
        }
        if (mcUserSendToPaymentRequest.getCurrency() == null || mcUserSendToPaymentRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserSendToPaymentRequest.getCurrency() is null or empty");
        }
        if (mcUserSendToPaymentRequest.getAmount() == null || mcUserSendToPaymentRequest.getAmount().equals(0.0)) {
            throw new ServiceException("mcUserSendToPaymentRequest.getAmount() is null or empty");
        }
        if (mcUserSendToPaymentRequest.getPayment() == null) {
            throw new ServiceException("mcUserSendToPaymentRequest.getPayment() is null");
        }
        return Response
                .status(200)
                .entity(new SendToPaymentCreateOperationNew(mcUserSendToPaymentRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getNewBalance/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNewBalance(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        //new MCUserAddAddress(userName, "bitcoin", false).getResponse();
        new UserAddWallet(userName, false, true).getResponse();
        new MCUserAddCryptoAPIsAddress(userName, "ethereum", false).getResponse();
        new MCUserAddCryptoAPIsAddress(userName, "tron", false).getResponse();
        //new MCUserAddTronGridAddress(userName, false).getResponse();
        //new CryptoAPIsSubscribeUserName(userName, "bitcoin", "mainnet", "ADDRESS_COINS_TRANSACTION_UNCONFIRMED", new String[]{"mcWallets"}).getResponse();
        //new CryptoAPIsSubscribeUserName(userName, "ethereum", "mainnet", "ADDRESS_COINS_TRANSACTION_UNCONFIRMED", new String[]{"mcWalletsEthereum"}).getResponse();
        //new CryptoAPIsSubscribeUserName(userName, "ethereum", "mainnet", "ADDRESS_TOKENS_TRANSACTION_UNCONFIRMED", new String[]{"mcWalletsEthereum"}).getResponse();
        //new BlockchainCheckInTransactions(userName, new String[]{"mcWallets"}).getResponse();
        blockchainCheckInCoinTransactionsThread(userName);
        cryptoAPIsCheckInCoinTransactionsThread(userName);    
        tronGridCheckInCoinTransactionsThread(userName);
        return Response
                .status(200)
                .entity(new MCUserGetBalance(userName, false, true).getResponse())
                .build();
    }
    
    private void blockchainCheckInCoinTransactionsThread(String userName) {
        Thread blockchainCheckInCoinTransactionsThread = new Thread(() -> {
            new BlockchainCheckInTransactions(userName, new String[]{"mcWallets"}).getResponse();
        });
        blockchainCheckInCoinTransactionsThread.start();
    }

    private void cryptoAPIsCheckInCoinTransactionsThread(String userName) {
        Thread cryptoAPIsCheckInCoinTransactionsThread = new Thread(() -> {
            new CryptoAPIsCheckInCoinTransactions(userName, "ETH").getResponse();
            new CryptoAPIsCheckInEthereumTokenTransactions(userName, "USDT").getResponse();
        });
        cryptoAPIsCheckInCoinTransactionsThread.start();
    }
    
    private void tronGridCheckInCoinTransactionsThread(String userName) {
        Thread tronGridCheckInCoinTransactionsThread = new Thread(() -> {
            new TronGridCheckInTokenTransactions(userName, "USDT").getResponse();
        });
        tronGridCheckInCoinTransactionsThread.start();
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
                .entity(new MCUserGetBalanceMovements(userName, initTimestamp, endTimestamp, null).getResponse())
                .build();
    }

    @GET
    @Path("/getAlerts/{currency}/{paymentType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlerts(
            @PathParam("currency") String currency,
            @PathParam("paymentType") PaymentType paymentType
    ) throws ServiceException {
        if (paymentType == null) {
            throw new ServiceException("paymentType is null");
        }
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserGetAlerts(currency, paymentType, null, null, null, null).getResponse())
                .build();
    }

    @GET
    @Path("/getAlerts/{currency}/{paymentType}/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlerts(
            @PathParam("currency") String currency,
            @PathParam("paymentType") PaymentType paymentType,
            @PathParam("language") String language
    ) throws ServiceException {
        if (paymentType == null) {
            throw new ServiceException("paymentType is null");
        }
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserGetAlerts(currency, paymentType, language, null, null, null).getResponse())
                .build();
    }

    @GET
    @Path("/getAlerts/{currency}/{balanceOperationType}/{paymentType}/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlerts(
            @PathParam("currency") String currency,
            @PathParam("balanceOperationType") BalanceOperationType balanceOperationType,
            @PathParam("paymentType") PaymentType paymentType,
            @PathParam("language") String language
    ) throws ServiceException {
        if (paymentType == null) {
            throw new ServiceException("paymentType is null");
        }
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserGetAlerts(currency, paymentType, language, null, null, balanceOperationType).getResponse())
                .build();
    }

    @GET
    @Path("/getAlerts/{currency}/{paymentType}/{language}/{userName}/{amount}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlerts(
            @PathParam("currency") String currency,
            @PathParam("paymentType") PaymentType paymentType,
            @PathParam("language") String language,
            @PathParam("userName") String userName,
            @PathParam("amount") Double amount
    ) throws ServiceException {
        if (paymentType == null) {
            throw new ServiceException("paymentType is null");
        }
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (amount == null || amount == 0.0) {
            throw new ServiceException("amount is null or zero");
        }
        return Response
                .status(200)
                .entity(new MCUserGetAlerts(currency, paymentType, language, userName, amount, null).getResponse())
                .build();
    }

    @GET
    @Path("/getFastChangeFactor/{baseCurrency}/{targetCurrency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFastChangeFactor(
            @PathParam("baseCurrency") String baseCurrency,
            @PathParam("targetCurrency") String targetCurrency
    ) throws ServiceException {
        if (baseCurrency == null || baseCurrency.equals("")) {
            throw new ServiceException("baseCurrency is null or empty");
        }
        if (targetCurrency == null || targetCurrency.equals("")) {
            throw new ServiceException("targetCurrency is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserGetFastChangeFactor(baseCurrency, targetCurrency).getResponse())
                .build();
    }

    @POST
    @Path("/fastChange")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response fastChange(MCUserFastChangeRequest mcUserFastChangeRequest) throws ServiceException {
        if (mcUserFastChangeRequest == null) {
            throw new ServiceException("mcUserFastChangeRequest is null");
        }
        if (mcUserFastChangeRequest.getUserName() == null || mcUserFastChangeRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserFastChangeRequest.getUserName() is null or empty");
        }
        if (mcUserFastChangeRequest.getBaseCurrency() == null || mcUserFastChangeRequest.getBaseCurrency().equals("")) {
            throw new ServiceException("mcUserFastChangeRequest.getBaseCurrency() is null or empty");
        }
        if (mcUserFastChangeRequest.getTargetCurrency() == null || mcUserFastChangeRequest.getTargetCurrency().equals("")) {
            throw new ServiceException("mcUserFastChangeRequest.getTargetCurrency() is null or empty");
        }
        if (mcUserFastChangeRequest.getAmount() == null || mcUserFastChangeRequest.getAmount() == 0.0) {
            throw new ServiceException("mcUserFastChangeRequest.getAmount() is null or empty");
        }
        if (mcUserFastChangeRequest.getFactor() == null || mcUserFastChangeRequest.getFactor() == 0.0) {
            throw new ServiceException("mcUserFastChangeRequest.getFactor() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserFastChange(mcUserFastChangeRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getReferralCodes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReferralCodes() throws ServiceException {
        return Response
                .status(200)
                .entity(new MCUserGetReferralCodes().getResponse())
                .build();
    }

    @POST
    @Path("/deleteMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteMessage(MCUserDeleteMessageRequest mcUserDeleteMessageRequest) throws ServiceException {
        if (mcUserDeleteMessageRequest == null) {
            throw new ServiceException("mcUserDeleteMessageRequest is null");
        }
        if (mcUserDeleteMessageRequest.getUserName() == null || mcUserDeleteMessageRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserDeleteMessageRequest.getUserName() is null or empty");
        }
        if (mcUserDeleteMessageRequest.getChatRoom() == null || mcUserDeleteMessageRequest.getChatRoom().equals("")) {
            throw new ServiceException("mcUserDeleteMessageRequest.getChatRoom() is null or empty");
        }
        if (mcUserDeleteMessageRequest.getId() == null || mcUserDeleteMessageRequest.getId().equals("")) {
            throw new ServiceException("mcUserDeleteMessageRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserDeleteMessage(mcUserDeleteMessageRequest).getResponse())
                .build();
    }

    @POST
    @Path("/deleteMessages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteMessages(MCUserDeleteMessagesRequest mcUserDeleteMessagesRequest) throws ServiceException {
        if (mcUserDeleteMessagesRequest == null) {
            throw new ServiceException("mcUserDeleteMessagesRequest is null");
        }
        if (mcUserDeleteMessagesRequest.getUserName() == null || mcUserDeleteMessagesRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserDeleteMessagesRequest.getUserName() is null or empty");
        }
        if (mcUserDeleteMessagesRequest.getChatRoom() == null || mcUserDeleteMessagesRequest.getChatRoom().equals("")) {
            throw new ServiceException("mcUserDeleteMessagesRequest.getChatRoom() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserDeleteMessages(mcUserDeleteMessagesRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getMessageAttachment/{userName}/{chatRoom}/{attachmentFileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMessageAttachment(
            @PathParam("userName") String userName,
            @PathParam("chatRoom") String chatRoom,
            @PathParam("attachmentFileName") String attachmentFileName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (chatRoom == null || chatRoom.equals("")) {
            throw new ServiceException("chatRoom is null or empty");
        }
        if (attachmentFileName == null || attachmentFileName.equals("")) {
            throw new ServiceException("attachmentFileName is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserGetMessageAttachment(userName, chatRoom, attachmentFileName).getResponse())
                .build();
    }

    @POST
    @Path("/markMessageAsReaded")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response markMessageAsReaded(MCUserMarkMessageAsReadedRequest mcUserMarkMessageAsReadedRequest) throws ServiceException {
        if (mcUserMarkMessageAsReadedRequest == null) {
            throw new ServiceException("mcUserMarkMessageAsReadedRequest is null");
        }
        if (mcUserMarkMessageAsReadedRequest.getUserName() == null || mcUserMarkMessageAsReadedRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserMarkMessageAsReadedRequest.getUserName() is null or empty");
        }
        if (mcUserMarkMessageAsReadedRequest.getChatRoom() == null || mcUserMarkMessageAsReadedRequest.getChatRoom().equals("")) {
            throw new ServiceException("mcUserMarkMessageAsReadedRequest.getChatRoom() is null or empty");
        }
        if (mcUserMarkMessageAsReadedRequest.getId() == null || mcUserMarkMessageAsReadedRequest.getId().equals("")) {
            throw new ServiceException("mcUserMarkMessageAsReadedRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserMarkMessageAsReaded(mcUserMarkMessageAsReadedRequest).getResponse())
                .build();
    }

    @POST
    @Path("/sendNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendNew(MCUserSendRequest mcUserSendRequest) throws ServiceException {
        if (mcUserSendRequest == null) {
            throw new ServiceException("mcUserSendRequest is null");
        }
        if (mcUserSendRequest.getBaseUserName() == null || mcUserSendRequest.getBaseUserName().equals("")) {
            throw new ServiceException("mcUserSendRequest.getBaseUserName() is null or empty");
        }
        if (mcUserSendRequest.getTargetUserName() == null || mcUserSendRequest.getTargetUserName().equals("")) {
            throw new ServiceException("mcUserSendRequest.getTargetUserName() is null or empty");
        }
        if (mcUserSendRequest.getCurrency() == null || mcUserSendRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserSendRequest.getCurrency() is null or empty");
        }
        if (mcUserSendRequest.getAmount() == null || mcUserSendRequest.getAmount().equals(0.0)) {
            throw new ServiceException("mcUserSendRequest.getAmount() is null or empty");
        }
        String response = new MCUserSendNew(mcUserSendRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/postMessageOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMessageOffer(MCUserPostMessageOfferRequest mcUserPostMessageOfferRequest) throws ServiceException {
        if (mcUserPostMessageOfferRequest == null) {
            throw new ServiceException("mcUserPostMessageOfferRequest is null");
        }
        if (mcUserPostMessageOfferRequest.getUserName() == null || mcUserPostMessageOfferRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getUserName() is null or empty");
        }
        if (mcUserPostMessageOfferRequest.getPair() == null || mcUserPostMessageOfferRequest.getPair().equals("")) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getPair() is null or empty");
        }
        if (mcUserPostMessageOfferRequest.getAmount() == null || mcUserPostMessageOfferRequest.getAmount().equals(0.0)) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getAmount() is null or zero");
        }
        if (mcUserPostMessageOfferRequest.getPrice() == null || mcUserPostMessageOfferRequest.getPrice().equals(0.0)) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getPrice() is null or zero");
        }
        if (mcUserPostMessageOfferRequest.getTime() == 0) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getTime() is zero");
        }
        if (!(mcUserPostMessageOfferRequest.getTimeUnit().equals("MINUTES") || !mcUserPostMessageOfferRequest.getTimeUnit().equals("HOURS") || !mcUserPostMessageOfferRequest.getTimeUnit().equals("DAYS"))) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getTimeUnit() is not MINUTES or HOURS or DAYS");
        }
        if (mcUserPostMessageOfferRequest.getType() == null) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getType() is null");
        }
        if (mcUserPostMessageOfferRequest.getNickName() == null || mcUserPostMessageOfferRequest.getNickName().equals("")) {
            throw new ServiceException("mcUserPostMessageOfferRequest.getNickName() is null or empty");
        }
        String response = new MCUserPostMessageOffer(mcUserPostMessageOfferRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/closeMessageOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response closeMessageOffer(MCUserCloseMessageOfferRequest mcUserCloseMessageOfferRequest) throws ServiceException {
        if (mcUserCloseMessageOfferRequest == null) {
            throw new ServiceException("mcUserCloseMessageOfferRequest is null");
        }
        if (mcUserCloseMessageOfferRequest.getUserName() == null || mcUserCloseMessageOfferRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserCloseMessageOfferRequest.getUserName() is null or empty");
        }
        if (mcUserCloseMessageOfferRequest.getPair() == null || mcUserCloseMessageOfferRequest.getPair().equals("")) {
            throw new ServiceException("mcUserCloseMessageOfferRequest.getPair() is null or empty");
        }
        if (mcUserCloseMessageOfferRequest.getId() == null || mcUserCloseMessageOfferRequest.getId().equals("")) {
            throw new ServiceException("mcUserCloseMessageOfferRequest.getId() is null or empty");
        }
        if (mcUserCloseMessageOfferRequest.getType() == null) {
            throw new ServiceException("mcUserCloseMessageOfferRequest.getType() is null");
        }
        String response = new MCUserCloseMessageOffer(mcUserCloseMessageOfferRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/takeMessageOffer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response takeMessageOffer(MCUserTakeMessageOfferRequest mcUserTakeMessageOfferRequest) throws ServiceException {
        if (mcUserTakeMessageOfferRequest == null) {
            throw new ServiceException("mcUserTakeMessageOfferRequest is null");
        }
        if (mcUserTakeMessageOfferRequest.getUserName() == null || mcUserTakeMessageOfferRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getUserName() is null or empty");
        }
        if (mcUserTakeMessageOfferRequest.getPair() == null || mcUserTakeMessageOfferRequest.getPair().equals("")) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getPair() is null or empty");
        }
        if (mcUserTakeMessageOfferRequest.getAmount() == null || mcUserTakeMessageOfferRequest.getAmount().equals(0.0)) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getAmount() is null or zero");
        }
        if (mcUserTakeMessageOfferRequest.getPrice() == null || mcUserTakeMessageOfferRequest.getPrice().equals(0.0)) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getPrice() is null or zero");
        }
        if (mcUserTakeMessageOfferRequest.getId() == null || mcUserTakeMessageOfferRequest.getId().equals("")) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getId() is null or empty");
        }
        if (mcUserTakeMessageOfferRequest.getType() == null) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getType() is null");
        }
        if (mcUserTakeMessageOfferRequest.getNickName() == null || mcUserTakeMessageOfferRequest.getNickName().equals("")) {
            throw new ServiceException("mcUserTakeMessageOfferRequest.getNickName() is null or empty");
        }
        String response = new MCUserTakeMessageOffer(mcUserTakeMessageOfferRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/getMessageOffers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageOffers(MCUserGetMessageOffersRequest mcUserGetMessageOffersRequest) throws ServiceException {
        if (mcUserGetMessageOffersRequest == null) {
            throw new ServiceException("mcUserGetMessageOffersRequest is null");
        }
        //if (mcUserGetMessageOffersRequest.getPair() == null || mcUserGetMessageOffersRequest.getPair().equals("")) {
        //    throw new ServiceException("mcUserGetMessageOffersRequest.getPair() is null or empty");
        //}
        JsonNode response = new MCUserGetMessageOffers(mcUserGetMessageOffersRequest).getResponse();
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @GET
    @Path("/getPairs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPairs() throws ServiceException {
        return Response
                .status(200)
                .entity(new MCUserGetPairs().getResponse())
                .build();
    }

    @PUT
    @Path("/buyBitcoins")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buyBitcoins(MCUserBuyBitcoinsRequest mcUserBuyBitcoinsRequest) throws ServiceException {
        if (mcUserBuyBitcoinsRequest == null) {
            throw new ServiceException("mcUserBuyBitcoinsRequest is null");
        }
        if (mcUserBuyBitcoinsRequest.getUserName() == null || mcUserBuyBitcoinsRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserBuyBitcoinsRequest.getUserName() is null or empty");
        }
        if (mcUserBuyBitcoinsRequest.getCurrency() == null || mcUserBuyBitcoinsRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserBuyBitcoinsRequest.getCurrency() is null or empty");
        }
        if (mcUserBuyBitcoinsRequest.getAmount() == null || mcUserBuyBitcoinsRequest.getAmount() == 0) {
            throw new ServiceException("mcUserBuyBitcoinsRequest.getAmount() is null or zero");
        }
        if (mcUserBuyBitcoinsRequest.getBtcAmount() == null || mcUserBuyBitcoinsRequest.getBtcAmount() == 0) {
            throw new ServiceException("mcUserBuyBitcoinsRequest.getBtcAmount() is null or zero");
        }
        String response = new MCUserBuyBitcoins(mcUserBuyBitcoinsRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/sellBitcoins")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellBitcoins(MCUserSellBitcoinsRequest mcUserSellBitcoinsRequest) throws ServiceException {
        if (mcUserSellBitcoinsRequest == null) {
            throw new ServiceException("mcUserSellBitcoinsRequest is null");
        }
        if (mcUserSellBitcoinsRequest.getUserName() == null || mcUserSellBitcoinsRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserSellBitcoinsRequest.getUserName() is null or empty");
        }
        if (mcUserSellBitcoinsRequest.getCurrency() == null || mcUserSellBitcoinsRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserSellBitcoinsRequest.getCurrency() is null or empty");
        }
        if (mcUserSellBitcoinsRequest.getAmount() == null || mcUserSellBitcoinsRequest.getAmount() == 0) {
            throw new ServiceException("mcUserSellBitcoinsRequest.getAmount() is null or zero");
        }
        if (mcUserSellBitcoinsRequest.getBtcAmount() == null || mcUserSellBitcoinsRequest.getBtcAmount() == 0) {
            throw new ServiceException("mcUserSellBitcoinsRequest.getBtcAmount() is null or zero");
        }
        String response = new MCUserSellBitcoins(mcUserSellBitcoinsRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @GET
    @Path("/getBitcoinPrice/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBitcoinPrice(
            @PathParam("currency") String currency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new MCUserGetBitcoinPrice(currency).getResponse())
                .build();
    }

    @PUT
    @Path("/buyCrypto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buyCrypto(MCUserBuyCryptoRequest mcUserBuyCryptoRequest) throws ServiceException {
        if (mcUserBuyCryptoRequest == null) {
            throw new ServiceException("mcUserBuyCryptoRequest is null");
        }
        if (mcUserBuyCryptoRequest.getUserName() == null || mcUserBuyCryptoRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserBuyCryptoRequest.getUserName() is null or empty");
        }
        if (mcUserBuyCryptoRequest.getCryptoCurrency() == null || mcUserBuyCryptoRequest.getCryptoCurrency().equals("")) {
            throw new ServiceException("mcUserBuyCryptoRequest.getCryptoCurrency() is null or empty");
        }
        if (mcUserBuyCryptoRequest.getCryptoAmount() == null || mcUserBuyCryptoRequest.getCryptoAmount() == 0) {
            throw new ServiceException("mcUserBuyCryptoRequest.getCryptoAmount() is null or zero");
        }
        if (mcUserBuyCryptoRequest.getFiatCurrency() == null || mcUserBuyCryptoRequest.getFiatCurrency().equals("")) {
            throw new ServiceException("mcUserBuyCryptoRequest.getFiatCurrency() is null or empty");
        }
        if (mcUserBuyCryptoRequest.getFiatAmount() == null || mcUserBuyCryptoRequest.getFiatAmount() == 0) {
            throw new ServiceException("mcUserBuyCryptoRequest.getFiatAmount() is null or zero");
        }
        String response = new MCUserBuyCrypto(mcUserBuyCryptoRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/sellCrypto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellCrypto(MCUserSellCryptoRequest mcUserSellCryptoRequest) throws ServiceException {
        if (mcUserSellCryptoRequest == null) {
            throw new ServiceException("mcUserSellCryptoRequest is null");
        }
        if (mcUserSellCryptoRequest.getUserName() == null || mcUserSellCryptoRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserSellCryptoRequest.getUserName() is null or empty");
        }
        if (mcUserSellCryptoRequest.getFiatCurrency() == null || mcUserSellCryptoRequest.getFiatCurrency().equals("")) {
            throw new ServiceException("mcUserSellCryptoRequest.getFiatCurrency() is null or empty");
        }
        if (mcUserSellCryptoRequest.getFiatAmount() == null || mcUserSellCryptoRequest.getFiatAmount() == 0) {
            throw new ServiceException("mcUserSellCryptoRequest.getFiatAmount() is null or zero");
        }
        if (mcUserSellCryptoRequest.getCryptoCurrency() == null || mcUserSellCryptoRequest.getCryptoCurrency().equals("")) {
            throw new ServiceException("mcUserSellCryptoRequest.getCryptoCurrency() is null or empty");
        }
        if (mcUserSellCryptoRequest.getCryptoAmount() == null || mcUserSellCryptoRequest.getCryptoAmount() == 0) {
            throw new ServiceException("mcUserSellCryptoRequest.getCryptoAmount() is null or zero");
        }
        String response = new MCUserSellCrypto(mcUserSellCryptoRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @GET
    @Path("/getCryptoPrice/{cryptoCurrency}/{fiatCurrency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCryptoPrice(
            @PathParam("cryptoCurrency") String cryptoCurrency,
            @PathParam("fiatCurrency") String fiatCurrency
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new MCUserGetCryptoPrice(cryptoCurrency, fiatCurrency).getResponse())
                .build();
    }

    @GET
    @Path("/getCryptoBuyAvailableTimestamp/{cryptoCurrency}/{operationTimestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCryptoBuyAvailableTimestamp(
            @PathParam("cryptoCurrency") String cryptoCurrency,
            @PathParam("operationTimestamp") String operationTimestamp
    ) throws ServiceException {
        String availableTimestamp = operationTimestamp;
        switch (cryptoCurrency) {
            case "BTC":
            case "ETH":
            case "USDT":
                availableTimestamp = DateUtil.getDateHoursAfter(operationTimestamp, 6);
                break;
        }
        return Response
                .status(200)
                .entity(availableTimestamp)
                .build();
    }

    @PUT
    @Path("/cashback")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cashback(MCUserCashbackRequest mcUserCashbackRequest) throws ServiceException {
        if (mcUserCashbackRequest == null) {
            throw new ServiceException("mcUserCashbackRequest is null");
        }
        if (mcUserCashbackRequest.getBaseUserName() == null || mcUserCashbackRequest.getBaseUserName().equals("")) {
            throw new ServiceException("mcUserCashbackRequest.getBaseUserName() is null or empty");
        }
        if (mcUserCashbackRequest.getTargetUserName() == null || mcUserCashbackRequest.getTargetUserName().equals("")) {
            throw new ServiceException("mcUserCashbackRequest.getTargetUserName() is null or empty");
        }
        if (mcUserCashbackRequest.getCurrency() == null || mcUserCashbackRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserCashbackRequest.getCurrency() is null or empty");
        }
        if (mcUserCashbackRequest.getAmount() == null || mcUserCashbackRequest.getAmount() == 0) {
            throw new ServiceException("mcUserCashbackRequest.getAmount() is null or zero");
        }
        String response = new MCUserCashback(mcUserCashbackRequest).getResponse();
        Logger.getLogger(MCUserServiceREST.class.getName()).log(Level.INFO, "SEND RESPONSE: {0}", response);
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @GET
    @Path("/getSpecialBalanceMovements/{currency}/{type}/{minAmount}/{initTimestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecialBalanceMovements(
            @PathParam("currency") String currency,
            @PathParam("type") String type,
            @PathParam("minAmount") double minAmount,
            @PathParam("initTimestamp") String initTimestamp
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (type == null || type.equals("")) {
            throw new ServiceException("type is null or empty");
        }
        if (minAmount == 0.0) {
            throw new ServiceException("minAmount is zero");
        }
        if (initTimestamp == null || initTimestamp.equals("")) {
            throw new ServiceException("initTimestamp is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserGetSpecialBalanceMovements(currency, type, minAmount, initTimestamp).getResponse())
                .build();
    }

    @GET
    @Path("/exist/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response exist(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCUserExist(userName).getResponse())
                .build();
    }

}
