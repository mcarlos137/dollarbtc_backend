/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.AccountRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.AccountResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBaseInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.AccountOverviewResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AccountOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import java.util.List;
import java.util.Arrays;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/account")
@XmlRegistry
public class AccountServiceREST {

    @POST
    @Path("/getCurrentAccounts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentAccounts(AccountRequest accountRequest) throws ServiceException {
        if (accountRequest == null) {
            throw new ServiceException("accountRequest is null");
        }
        if (accountRequest.getExchangeId() == null || accountRequest.getExchangeId().equals("")) {
            throw new ServiceException("accountRequest.getExchangeId() is null or empty");
        }
        if (accountRequest.getSymbol() == null || accountRequest.getSymbol().equals("")) {
            throw new ServiceException("accountRequest.getSymbol() is null or empty");
        }
        if (accountRequest.getModelName() == null || accountRequest.getModelName().equals("")) {
            throw new ServiceException("accountRequest.getModelName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new AccountResponse(
                                AccountOperation.getAccounts(accountRequest.getExchangeId(), accountRequest.getSymbol(), accountRequest.getModelName(), accountRequest.getInitDate(), accountRequest.getEndDate(), AccountBase.Retrieve.ALL, accountRequest.getCollectionOrderByDate()),
                                null
                        ))
                .build();
    }

    @GET
    @Path("/getCurrentAccounts/{exchangeId}/{symbol}/{modelName}/{initDate}/{endDate}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentAccounts(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol,
            @PathParam("modelName") String modelName,
            @PathParam("initDate") String initDate,
            @PathParam("endDate") String endDate,
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate
    ) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AccountResponse(
                                AccountOperation.getAccounts(exchangeId, symbol, modelName, initDate, endDate, AccountBase.Retrieve.ALL, collectionOrderByDate),
                                null
                        ))
                .build();
    }

    @GET
    @Path("/getAllAccounts/{exchangeId}/{symbol}/{modelName}/{startTimestamp}/{endTimestamp}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol,
            @PathParam("modelName") String modelName,
            @PathParam("startTimestamp") String startTimestamp,
            @PathParam("endTimestamp") String endTimestamp,
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate
    ) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AccountResponse(
                                AccountOperation.getAccounts(exchangeId, symbol, modelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ALL, collectionOrderByDate),
                                AccountOperation.getAccountIntervals(exchangeId, symbol, modelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ALL, collectionOrderByDate)
                        ))
                .build();
    }
    
    @GET
    @Path("/getAllAccountsFirstAccount/{exchangeId}/{symbol}/{modelName}/{startTimestamp}/{endTimestamp}/{collectionOrderByDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccountsFirstAccount(
            @PathParam("exchangeId") String exchangeId,
            @PathParam("symbol") String symbol,
            @PathParam("modelName") String modelName,
            @PathParam("startTimestamp") String startTimestamp,
            @PathParam("endTimestamp") String endTimestamp,
            @PathParam("collectionOrderByDate") CollectionOrderByDate collectionOrderByDate
    ) throws ServiceException {
        if (exchangeId == null || exchangeId.equals("")) {
            throw new ServiceException("exchangeId is null or empty");
        }
        if (symbol == null || symbol.equals("")) {
            throw new ServiceException("symbol is null or empty");
        }
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AccountResponse(
                                AccountOperation.getAccounts(exchangeId, symbol, modelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ONLY_FIRST, collectionOrderByDate),
                                AccountOperation.getAccountIntervals(exchangeId, symbol, modelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ONLY_FIRST, collectionOrderByDate)
                        ))
                .build();
    }

    @GET
    @Path("/overview/{modelName}/{startTimestamp}/{endTimestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverview(
            @PathParam("modelName") String modelName,
            @PathParam("startTimestamp") String startTimestamp,
            @PathParam("endTimestamp") String endTimestamp
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        if (startTimestamp == null || startTimestamp.equals("")) {
            throw new ServiceException("startTimestamp is null or empty");
        }
        if (endTimestamp == null || endTimestamp.equals("")) {
            throw new ServiceException("endTimestamp is null or empty");
        }
        AccountOverviewResponse accountOverviewResponse = new AccountOverviewResponse(modelName, startTimestamp, endTimestamp);
        List<String> exchangeIdSymbols = ExchangeUtil.getExchangeIdSymbols(null, null);
        exchangeIdSymbols.stream().forEach((exchangeIdSymbol) -> {
            String exchangeId = exchangeIdSymbol.split("__")[0];
            String symbol = exchangeIdSymbol.split("__")[1];
            List<AccountBaseInterval> accountIntervals = AccountOperation.getAccountIntervals(exchangeId, symbol, modelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ALL, CollectionOrderByDate.ASC);
            accountIntervals.stream().filter((accountInterval) -> !(accountInterval.getAccounts().isEmpty())).map((accountInterval) -> {
                AccountBase finalAccount = accountInterval.getAccounts().get(accountInterval.getAccounts().size() - 1);
                double balanceToAdd = finalAccount.getCurrentBaseBalance().doubleValue() - finalAccount.getInitialBaseBalance().doubleValue() + finalAccount.getReservedBaseBalance().doubleValue() + finalAccount.getCurrentAssetBalance().doubleValue() * finalAccount.getLastAskPrice().doubleValue();
                AccountOverviewResponse.ExchangeIdSymbol accountOverviewResponseExchangeIdSymbol = new AccountOverviewResponse.ExchangeIdSymbol(exchangeId, symbol);
                if (!accountOverviewResponse.getExchangeIdSymbols().contains(accountOverviewResponseExchangeIdSymbol)) {
                    accountOverviewResponse.getExchangeIdSymbols().add(accountOverviewResponseExchangeIdSymbol);
                }
                accountOverviewResponse.getExchangeIdSymbols().get(accountOverviewResponse.getExchangeIdSymbols().indexOf(accountOverviewResponseExchangeIdSymbol)).addToBalance(balanceToAdd);
                AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalName accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName = new AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalName(exchangeId, symbol, accountInterval.getIntervalAlgorithmName());
                if (!accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().contains(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName)) {
                    accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().add(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName);
                }
                accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().get(accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().indexOf(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName)).addToBalance(balanceToAdd);
                AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalNameDetailed accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed = new AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalNameDetailed(exchangeId, symbol, accountInterval.getIntervalAlgorithmName(), accountInterval.getStartTimestamp(), accountInterval.getEndTimestamp());
                accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setInitLowestPrice(accountInterval.getAccounts().get(0).getLastAskPrice().doubleValue());
                accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setEndLowestPrice(accountInterval.getAccounts().get(accountInterval.getAccounts().size() - 1).getLastAskPrice().doubleValue());
                accountInterval.getAccounts().stream().map((account) -> {
                    if (accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getBottomLowestPrice() == 0.0 || accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getBottomLowestPrice() > account.getLastAskPrice().doubleValue()) {
                        accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setBottomLowestPrice(account.getLastAskPrice().doubleValue());
                    }
                    return account;
                }).filter((account) -> (accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getTopLowestPrice() == 0.0 || accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getTopLowestPrice() < account.getLastAskPrice().doubleValue())).forEach((account) -> {
                    accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setTopLowestPrice(account.getLastAskPrice().doubleValue());
                });
                accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.addToBalance(balanceToAdd);
                return accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed;
            }).forEach((accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed) -> {
                accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNameDetaileds().add(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed);
            });
        });
        return Response
                .status(200)
                .entity(accountOverviewResponse)
                .build();
    }

    @GET
    @Path("/overview/{modelName}/{startTimestamp}/{endTimestamp}/{excludedSymbols}/{excludedAlgorithmNames}/{excludeZeroBalanceIntervals}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverview(
            @PathParam("modelName") String modelName,
            @PathParam("startTimestamp") String startTimestamp,
            @PathParam("endTimestamp") String endTimestamp,
            @PathParam("excludedSymbols") String excludedSymbols,
            @PathParam("excludedAlgorithmNames") String excludedAlgorithmNames,
            @PathParam("excludeZeroBalanceIntervals") boolean excludeZeroBalanceIntervals
    ) throws ServiceException {
        if (modelName == null || modelName.equals("")) {
            throw new ServiceException("modelName is null or empty");
        }
        if (startTimestamp == null || startTimestamp.equals("")) {
            throw new ServiceException("startTimestamp is null or empty");
        }
        if (endTimestamp == null || endTimestamp.equals("")) {
            throw new ServiceException("endTimestamp is null or empty");
        }
        AccountOverviewResponse accountOverviewResponse = new AccountOverviewResponse(modelName, startTimestamp, endTimestamp);
        List<String> exchangeIdSymbols = ExchangeUtil.getExchangeIdSymbols(null, null);
        exchangeIdSymbols.removeAll(Arrays.asList(excludedSymbols.split("____")));
        exchangeIdSymbols.stream().forEach((exchangeIdSymbol) -> {
            String exchangeId = exchangeIdSymbol.split("__")[0];
            String symbol = exchangeIdSymbol.split("__")[1];
            List<AccountBaseInterval> accountIntervals = AccountOperation.getAccountIntervals(exchangeId, symbol, modelName, startTimestamp, endTimestamp, AccountBase.Retrieve.ALL, CollectionOrderByDate.ASC);
            accountIntervals.stream().filter((accountInterval) -> !(accountInterval.getAccounts().isEmpty())).filter((accountInterval) -> !(Arrays.asList(excludedAlgorithmNames.split("____")).contains(accountInterval.getIntervalAlgorithmName()))).forEach((accountInterval) -> {
                AccountBase finalAccount = accountInterval.getAccounts().get(accountInterval.getAccounts().size() - 1);
                double balanceToAdd = finalAccount.getCurrentBaseBalance().doubleValue() - finalAccount.getInitialBaseBalance().doubleValue() + finalAccount.getReservedBaseBalance().doubleValue() + finalAccount.getCurrentAssetBalance().doubleValue() * finalAccount.getLastAskPrice().doubleValue();
                if (!(balanceToAdd == 0.0 && excludeZeroBalanceIntervals)) {
                    AccountOverviewResponse.ExchangeIdSymbol accountOverviewResponseExchangeIdSymbol = new AccountOverviewResponse.ExchangeIdSymbol(exchangeId, symbol);
                    if (!accountOverviewResponse.getExchangeIdSymbols().contains(accountOverviewResponseExchangeIdSymbol)) {
                        accountOverviewResponse.getExchangeIdSymbols().add(accountOverviewResponseExchangeIdSymbol);
                    }
                    accountOverviewResponse.getExchangeIdSymbols().get(accountOverviewResponse.getExchangeIdSymbols().indexOf(accountOverviewResponseExchangeIdSymbol)).addToBalance(balanceToAdd);
                    AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalName accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName = new AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalName(exchangeId, symbol, accountInterval.getIntervalAlgorithmName());
                    if (!accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().contains(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName)) {
                        accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().add(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName);
                    }
                    accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().get(accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNames().indexOf(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalName)).addToBalance(balanceToAdd);
                    AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalNameDetailed accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed = new AccountOverviewResponse.ExchangeIdSymbolAlgorithmIntervalNameDetailed(exchangeId, symbol, accountInterval.getIntervalAlgorithmName(), accountInterval.getStartTimestamp(), accountInterval.getEndTimestamp());
                    accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setInitLowestPrice(accountInterval.getAccounts().get(0).getLastAskPrice().doubleValue());
                    accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setEndLowestPrice(accountInterval.getAccounts().get(accountInterval.getAccounts().size() - 1).getLastAskPrice().doubleValue());
                    accountInterval.getAccounts().stream().map((account) -> {
                        if (accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getBottomLowestPrice() == 0.0 || accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getBottomLowestPrice() > account.getLastAskPrice().doubleValue()) {
                            accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setBottomLowestPrice(account.getLastAskPrice().doubleValue());
                        }
                        return account;
                    }).filter((account) -> (accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getTopLowestPrice() == 0.0 || accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.getTopLowestPrice() < account.getLastAskPrice().doubleValue())).forEach((account) -> {
                        accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.setTopLowestPrice(account.getLastAskPrice().doubleValue());
                    });
                    accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed.addToBalance(balanceToAdd);
                    accountOverviewResponse.getExchangeIdSymbolAlgorithmIntervalNameDetaileds().add(accountOverviewResponseExchangeIdSymbolAlgorithmIntervalNameDetailed);
                }
            });
        });
        return Response
                .status(200)
                .entity(accountOverviewResponse)
                .build();
    }

}
