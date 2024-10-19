/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBaseInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.AccountOverviewResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model.ModelDataResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.StringResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.WebsiteEditBestBotsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AccountOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.OrderOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.TradeOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.WebsiteOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetBuyPercent;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTickersAndUSDPrice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetReducedOffers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
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
 * @author conamerica90
 */
@Path("/website")
@XmlRegistry
public class WebsiteServiceREST {

//    @GET
//    @Path("/getLocalbitcoinTicker/{symbol}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getLocalbitcoinTicker(@PathParam("symbol") String symbol) throws ServiceException {
//        if (symbol == null || symbol.equals("")) {
//            throw new ServiceException("symbol is null or empty");
//        }
//        JsonNode ticker = WebsiteOperation.getLocalbitcoinTicker(symbol);
//        return Response
//                .status(200)
//                .entity(ticker)
//                .build();
//    }
//    
//    @GET
//    @Path("/getLocalbitcoinHistoricalTickers/{symbol}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getLocalbitcoinHistoricalTickers(@PathParam("symbol") String symbol) throws ServiceException {
//        if (symbol == null || symbol.equals("")) {
//            throw new ServiceException("symbol is null or empty");
//        }
//        return Response
//                .status(200)
//                .entity(WebsiteOperation.getLocalbitcoinHistoricalTickers(symbol))
//                .build();
//    }
//    
//    @GET
//    @Path("/getLocalbitcoinHistoricalTickers/{symbol}/{offerType}/{priceType}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getLocalbitcoinHistoricalTickers(
//            @PathParam("symbol") String symbol,
//            @PathParam("offerType") OfferType offerType,
//            @PathParam("priceType") PriceType priceType
//    ) throws ServiceException {
//        if (symbol == null || symbol.equals("")) {
//            throw new ServiceException("symbol is null or empty");
//        }
//        if (offerType == null) {
//            throw new ServiceException("offerType is null");
//        }
//        if (priceType == null) {
//            throw new ServiceException("priceType is null");
//        }
//        return Response
//                .status(200)
//                .entity(WebsiteOperation.getLocalbitcoinHistoricalTickers(symbol, offerType, priceType))
//                .build();
//    }
    
    @GET
    @Path("/getLocalbitcoinReducedTickers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocalbitcoinReducedTickers() throws ServiceException {
        return Response
                .status(200)
                .entity(new LocalBitcoinsGetTickersAndUSDPrice().getResponse())
                .build();
    }

    @GET
    @Path("/getLocalbitcoinBuyPercent/{symbol}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocalbitcoinBuyPercent(@PathParam("symbol") String symbol) throws ServiceException {
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        return Response
                .status(200)
                .entity(new LocalBitcoinsGetBuyPercent(symbol).getResponse())
                .build();
    }

    @GET
    @Path("/getModelData/{exchangeId}/{symbol}/{userModelName}/{startTimestamp}/{endTimestamp}/{withTrades}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBotData(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol,
            @PathParam("userModelName") String userModelName,
            @PathParam("startTimestamp") String startTimestamp,
            @PathParam("endTimestamp") String endTimestamp,
            @PathParam("withTrades") boolean withTrades
    ) throws ServiceException {
        ModelDataResponse botDataResponse = new ModelDataResponse(
                OrderOperation.getOrders(exchangeId, symbol, userModelName, startTimestamp, endTimestamp, CollectionOrderByDate.ASC),
                OrderOperation.getOrderIntervals(exchangeId, symbol, userModelName, startTimestamp, endTimestamp, CollectionOrderByDate.ASC)
        );
        if (withTrades) {
            botDataResponse.getTrades().addAll(TradeOperation.getReducedTrades(exchangeId, symbol, startTimestamp, endTimestamp, CollectionOrderByDate.ASC));
        }
        List<AccountBaseInterval> accountIntervals = AccountOperation.getAccountIntervals(exchangeId, symbol, userModelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ALL, CollectionOrderByDate.ASC);
        for (AccountBaseInterval accountInterval : accountIntervals) {
            if (accountInterval.getAccounts().isEmpty()) {
                continue;
            }
            AccountBase finalAccount = accountInterval.getAccounts().get(accountInterval.getAccounts().size() - 1);
            double balanceToAdd = finalAccount.getCurrentBaseBalance().doubleValue() - finalAccount.getInitialBaseBalance().doubleValue() + finalAccount.getReservedBaseBalance().doubleValue() + finalAccount.getCurrentAssetBalance().doubleValue() * finalAccount.getLastAskPrice().doubleValue();
            AccountOverviewResponse.ExchangeIdSymbol accountOverviewResponseExchangeIdSymbol = new AccountOverviewResponse.ExchangeIdSymbol(exchangeId, symbol);
            if (!botDataResponse.getExchangeIdSymbols().contains(accountOverviewResponseExchangeIdSymbol)) {
                botDataResponse.getExchangeIdSymbols().add(accountOverviewResponseExchangeIdSymbol);
            }
            botDataResponse.getExchangeIdSymbols().get(botDataResponse.getExchangeIdSymbols().indexOf(accountOverviewResponseExchangeIdSymbol)).addToBalance(balanceToAdd);
        }
        return Response
                .status(200)
                .entity(botDataResponse)
                .build();
    }
    
    @GET
    @Path("/getReducedOffers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReducedOffers() throws ServiceException {
        return Response
                .status(200)
                .entity(new OTCGetReducedOffers().getResponse())
                .build();
    }

    @GET
    @Path("/getBestBots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBestBots() throws ServiceException {
        JsonNode bestBots = WebsiteOperation.getBestBots();
        return Response
                .status(200)
                .entity(bestBots)
                .build();
    }

    @POST
    @Path("/editBestBots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editBestBots(WebsiteEditBestBotsRequest websiteEditBestBotsRequest) throws ServiceException {
        if (websiteEditBestBotsRequest == null) {
            throw new ServiceException("websiteEditBestBotsRequest is null");
        }
        if (websiteEditBestBotsRequest.getBots().isEmpty()) {
            throw new ServiceException("websiteEditBestBotsRequest.getBots() is empty");
        }
        WebsiteOperation.editBestBots(websiteEditBestBotsRequest);
        return Response
                .status(200)
                .entity(new StringResponse("OK"))
                .build();
    }
    
    @GET
    @Path("/getOverallAchievements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverallAchievements() throws ServiceException {
        ObjectNode overallAchievements = new ObjectMapper().createObjectNode();
        overallAchievements.put("users", "+100.000");
        overallAchievements.put("transactions", "+1.000.000");
        overallAchievements.put("bitcoins", "+1.000");
        return Response
                .status(200)
                .entity(overallAchievements)
                .build();
    }

}
