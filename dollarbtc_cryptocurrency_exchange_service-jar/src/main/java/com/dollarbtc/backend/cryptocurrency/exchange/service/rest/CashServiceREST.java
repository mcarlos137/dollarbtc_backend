/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashAddEscrowFromMCUserBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashAddPlaceAttachmentToCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashSellRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashBuyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashCreatePlaceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashSubstractEscrowToMCUserBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashAddPlaceCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashChangeCreatePlaceStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashCheckDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashLinkPlaceDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashProcessOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashRemovePlaceCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashUnlinkPlaceDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashBalanceType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashActivatePlace;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashAddEscrowFromMCUserBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashAddPlaceAttachmentToCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashAddPlaceCurrencyOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashSell;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashBuy;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashChangeCreatePlaceStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashCheckDevice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashCreatePlace;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashGetPlace;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashGetPlaceBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashGetPlaceBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashGetPlaces;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashInactivatePlace;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashLinkPlaceDevice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashProcessOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashRemovePlaceCurrencyOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashUnlinkPlaceDevice;
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
@Path("/cash")
@XmlRegistry
public class CashServiceREST {

    //LISTO
    @POST
    @Path("/buy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response buy(CashBuyRequest cashBuyRequest) throws ServiceException {
        if (cashBuyRequest == null) {
            throw new ServiceException("cashBuyRequest is null");
        }
        if (cashBuyRequest.getUserName() == null || cashBuyRequest.getUserName().equals("")) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest.getUserName() is null or empty");
        }
        if (cashBuyRequest.getCurrency() == null || cashBuyRequest.getCurrency().equals("")) {
            throw new ServiceException("mcUserNewBuyBalanceRetailRequest.getCurrency() is null or empty");
        }
        if (cashBuyRequest.getPlaceId() == null || cashBuyRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashBuyRequest.getPlaceId() is null or empty");
        }
        if (cashBuyRequest.getAmount() == null || cashBuyRequest.getAmount() == 0.0) {
            throw new ServiceException("cashBuyRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashBuy(cashBuyRequest).getResponse())
                .build();
    }

    //LISTO
    @POST
    @Path("/sell")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sell(CashSellRequest cashSellRequest) throws ServiceException {
        if (cashSellRequest == null) {
            throw new ServiceException("cashSellRequest is null");
        }
        if (cashSellRequest.getUserName() == null || cashSellRequest.getUserName().equals("")) {
            throw new ServiceException("cashSellRequest.getUserName() is null or empty");
        }
        if (cashSellRequest.getCurrency() == null || cashSellRequest.getCurrency().equals("")) {
            throw new ServiceException("cashSellRequest.getCurrency() is null or empty");
        }
        if (cashSellRequest.getPlaceId()== null || cashSellRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashSellRequest.getPlaceId() is null or empty");
        }
        if (cashSellRequest.getAmount() == null || cashSellRequest.getAmount() == 0.0) {
            throw new ServiceException("cashSellRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashSell(cashSellRequest).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/addEscrowFromMCUserBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addEscrowFromMCUserBalance(CashAddEscrowFromMCUserBalanceRequest cashAddEscrowFromMCUserBalanceRequest) throws ServiceException {
        if (cashAddEscrowFromMCUserBalanceRequest == null) {
            throw new ServiceException("cashAddEscrowFromMCUserBalanceRequest is null");
        }
        if (cashAddEscrowFromMCUserBalanceRequest.getUserName() == null || cashAddEscrowFromMCUserBalanceRequest.getUserName().equals("")) {
            throw new ServiceException("cashAddEscrowFromMCUserBalanceRequest.getUserName() is null or empty");
        }
        if (cashAddEscrowFromMCUserBalanceRequest.getPlaceId() == null || cashAddEscrowFromMCUserBalanceRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashAddEscrowFromMCUserBalanceRequest.getPlaceId() is null or empty");
        }
        if (cashAddEscrowFromMCUserBalanceRequest.getAmount() == null || cashAddEscrowFromMCUserBalanceRequest.getAmount() == 0) {
            throw new ServiceException("cashAddEscrowFromMCUserBalanceRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashAddEscrowFromMCUserBalance(cashAddEscrowFromMCUserBalanceRequest).getResponse())
                .build();
    }

    //LISTO
    @POST
    @Path("/substractEscrowToMCUserBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response substractEscrowToMCUserBalance(CashSubstractEscrowToMCUserBalanceRequest cashSubstractEscrowToMCUserBalanceRequest) throws ServiceException {
        if (cashSubstractEscrowToMCUserBalanceRequest == null) {
            throw new ServiceException("cashSubstractEscrowToMCUserBalanceRequest is null");
        }
        if (cashSubstractEscrowToMCUserBalanceRequest.getUserName() == null || cashSubstractEscrowToMCUserBalanceRequest.getUserName().equals("")) {
            throw new ServiceException("cashSubstractEscrowToMCUserBalanceRequest.getUserName() is null or empty");
        }
        if (cashSubstractEscrowToMCUserBalanceRequest.getPlaceId() == null || cashSubstractEscrowToMCUserBalanceRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashSubstractEscrowToMCUserBalanceRequest.getPlaceId() is null or empty");
        }
        if (cashSubstractEscrowToMCUserBalanceRequest.getAmount() == null || cashSubstractEscrowToMCUserBalanceRequest.getAmount() == 0) {
            throw new ServiceException("cashSubstractEscrowToMCUserBalanceRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity("OK")
                .build();
    }
    
    //LISTO
    @GET
    @Path("/getPlace/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlace(
            @PathParam("id") String id
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashGetPlace(id).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/getPlaces")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaces() throws ServiceException {
        return Response
                .status(200)
                .entity(new CashGetPlaces(null, null).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/getPlaces/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaces(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashGetPlaces(currency, null).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/getPlaces/{currency}/{cashOperationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaces(
            @PathParam("currency") String currency,
            @PathParam("cashOperationType") CashOperationType cashOperationType
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (cashOperationType == null) {
            throw new ServiceException("cashOperationType is null");
        }
        return Response
                .status(200)
                .entity(new CashGetPlaces(currency, cashOperationType).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/getPlaceBalance/{placeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaceBalance(
            @PathParam("placeId") String placeId
    ) throws ServiceException {
        if (placeId == null || placeId.equals("")) {
            throw new ServiceException("placeId is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashGetPlaceBalance(placeId, false).getResponse())
                .build();
    }

    //LISTO
    @GET
    @Path("/getPlaceBalanceMovements/{placeId}/{initTimestamp}/{endTimestamp}/{cashBalanceType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaceBalanceMovements(
            @PathParam("placeId") String placeId,
            @PathParam("initTimestamp") String initTimestamp,
            @PathParam("endTimestamp") String endTimestamp,
            @PathParam("cashBalanceType") CashBalanceType cashBalanceType
    ) throws ServiceException {
        if (placeId == null || placeId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        if (initTimestamp == null || initTimestamp.equals("")) {
            throw new ServiceException("initTimestamp is null or empty");
        }
        if (endTimestamp == null || endTimestamp.equals("")) {
            throw new ServiceException("endTimestamp is null or empty");
        }
        if (cashBalanceType == null) {
            throw new ServiceException("cashBalanceType is null");
        }
        return Response
                .status(200)
                .entity(new CashGetPlaceBalanceMovements(placeId, initTimestamp, endTimestamp, null, cashBalanceType).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/createPlace")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createPlace(CashCreatePlaceRequest cashCreatePlaceRequest) throws ServiceException {
        if (cashCreatePlaceRequest == null) {
            throw new ServiceException("cashCreatePlaceRequest is null");
        }
        if (cashCreatePlaceRequest.getUserName() == null || cashCreatePlaceRequest.getUserName().equals("")) {
            throw new ServiceException("cashCreatePlaceRequest.getUserName() is null or empty");
        }
        if (cashCreatePlaceRequest.getTitle() == null || cashCreatePlaceRequest.getTitle().equals("")) {
            throw new ServiceException("cashCreatePlaceRequest.getTitle() is null or empty");
        }
        if (cashCreatePlaceRequest.getDescription() == null || cashCreatePlaceRequest.getDescription().equals("")) {
            throw new ServiceException("cashCreatePlaceRequest.getDescription() is null or empty");
        }
        if (cashCreatePlaceRequest.getEmail() == null || cashCreatePlaceRequest.getEmail().equals("")) {
            throw new ServiceException("cashCreatePlaceRequest.getEmail() is null or empty");
        }
        if (cashCreatePlaceRequest.getLatitude() == null || cashCreatePlaceRequest.getLatitude() == 0) {
            throw new ServiceException("cashCreatePlaceRequest.getLatitude() is null or empty");
        }
        if (cashCreatePlaceRequest.getLongitude() == null || cashCreatePlaceRequest.getLongitude() == 0) {
            throw new ServiceException("cashCreatePlaceRequest.getLongitude() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashCreatePlace(cashCreatePlaceRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/addPlaceCurrencyOperationType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addPlaceCurrencyOperationType(CashAddPlaceCurrencyOperationTypeRequest cashAddPlaceCurrencyOperationTypeRequest) throws ServiceException {
        if (cashAddPlaceCurrencyOperationTypeRequest == null) {
            throw new ServiceException("cashAddPlaceCurrencyOperationTypeRequest is null");
        }
        if (cashAddPlaceCurrencyOperationTypeRequest.getPlaceId() == null || cashAddPlaceCurrencyOperationTypeRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashAddPlaceCurrencyOperationTypeRequest.getPlaceId() is null or empty");
        }
        if (cashAddPlaceCurrencyOperationTypeRequest.getCurrency() == null || cashAddPlaceCurrencyOperationTypeRequest.getCurrency().equals("")) {
            throw new ServiceException("cashAddPlaceCurrencyOperationTypeRequest.getCurrency() is null or empty");
        }
        if (cashAddPlaceCurrencyOperationTypeRequest.getCashOperationType() == null) {
            throw new ServiceException("cashAddPlaceCurrencyOperationTypeRequest.getCashOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new CashAddPlaceCurrencyOperationType(cashAddPlaceCurrencyOperationTypeRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/removePlaceCurrencyOperationType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removePlaceCurrencyOperationType(CashRemovePlaceCurrencyOperationTypeRequest cashRemovePlaceCurrencyOperationTypeRequest) throws ServiceException {
        if (cashRemovePlaceCurrencyOperationTypeRequest == null) {
            throw new ServiceException("cashRemovePlaceCurrencyOperationTypeRequest is null");
        }
        if (cashRemovePlaceCurrencyOperationTypeRequest.getPlaceId() == null || cashRemovePlaceCurrencyOperationTypeRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashRemovePlaceCurrencyOperationTypeRequest.getPlaceId() is null or empty");
        }
        if (cashRemovePlaceCurrencyOperationTypeRequest.getCurrency() == null || cashRemovePlaceCurrencyOperationTypeRequest.getCurrency().equals("")) {
            throw new ServiceException("cashRemovePlaceCurrencyOperationTypeRequest.getCurrency() is null or empty");
        }
        if (cashRemovePlaceCurrencyOperationTypeRequest.getCashOperationType() == null) {
            throw new ServiceException("cashRemovePlaceCurrencyOperationTypeRequest.getMcRetailOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new CashRemovePlaceCurrencyOperationType(cashRemovePlaceCurrencyOperationTypeRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/changeCreatePlaceStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeCreatePlaceStatus(CashChangeCreatePlaceStatusRequest cashChangeCreatePlaceStatusRequest) throws ServiceException {
        if (cashChangeCreatePlaceStatusRequest == null) {
            throw new ServiceException("cashChangeCreatePlaceStatusRequest is null");
        }
        if (cashChangeCreatePlaceStatusRequest.getPlaceId() == null || cashChangeCreatePlaceStatusRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashChangeCreatePlaceStatusRequest.getPlaceId() is null or empty");
        }
        if (cashChangeCreatePlaceStatusRequest.getCashCreatePlaceStatus() == null) {
            throw new ServiceException("cashChangeCreatePlaceStatusRequest.getMcRetailCreateStatus() is null");
        }
        return Response
                .status(200)
                .entity(new CashChangeCreatePlaceStatus(cashChangeCreatePlaceStatusRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/addPlaceAttachmentToCreate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addPlaceAttachmentToCreate(CashAddPlaceAttachmentToCreateRequest cashAddPlaceAttachmentToCreateRequest) throws ServiceException {
        if (cashAddPlaceAttachmentToCreateRequest == null) {
            throw new ServiceException("cashAddPlaceAttachmentToCreateRequest is null");
        }
        if (cashAddPlaceAttachmentToCreateRequest.getPlaceId() == null || cashAddPlaceAttachmentToCreateRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashAddPlaceAttachmentToCreateRequest.getPlaceId() is null or empty");
        }
        if (cashAddPlaceAttachmentToCreateRequest.getAttachmentUrl() == null || cashAddPlaceAttachmentToCreateRequest.getAttachmentUrl().equals("")) {
            throw new ServiceException("cashAddPlaceAttachmentToCreateRequest.getAttachmentUrl() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashAddPlaceAttachmentToCreate(cashAddPlaceAttachmentToCreateRequest).getResponse())
                .build();
    }
    
    //LISTO
    @PUT
    @Path("/linkPlaceDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response linkPlaceDevice(CashLinkPlaceDeviceRequest cashLinkPlaceDeviceRequest) throws ServiceException {
        if (cashLinkPlaceDeviceRequest == null) {
            throw new ServiceException("cashLinkPlaceDeviceRequest is null");
        }
        if (cashLinkPlaceDeviceRequest.getDeviceId() == null || cashLinkPlaceDeviceRequest.getDeviceId().equals("")) {
            throw new ServiceException("cashLinkPlaceDeviceRequest.getDeviceId() is null or empty");
        }
        if (cashLinkPlaceDeviceRequest.getPlaceId() == null || cashLinkPlaceDeviceRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashLinkPlaceDeviceRequest.getPlaceId() is null or empty");
        }
        if (cashLinkPlaceDeviceRequest.getType() == null || cashLinkPlaceDeviceRequest.getType().equals("")) {
            throw new ServiceException("cashLinkPlaceDeviceRequest.getType() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashLinkPlaceDevice(cashLinkPlaceDeviceRequest).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/checkDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkDevice(CashCheckDeviceRequest cashCheckDeviceRequest) throws ServiceException {
        if (cashCheckDeviceRequest == null) {
            throw new ServiceException("cashCheckDeviceRequest is null");
        }
        if (cashCheckDeviceRequest.getDeviceId() == null || cashCheckDeviceRequest.getDeviceId().equals("")) {
            throw new ServiceException("cashCheckDeviceRequest.getDeviceId() is null or empty");
        }
        if (cashCheckDeviceRequest.getPlaceId() == null || cashCheckDeviceRequest.getPlaceId().equals("")) {
            throw new ServiceException("cashCheckDeviceRequest.getPlaceId() is null or empty");
        }
        if (cashCheckDeviceRequest.getUserName() == null || cashCheckDeviceRequest.getUserName().equals("")) {
            throw new ServiceException("cashCheckDeviceRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashCheckDevice(cashCheckDeviceRequest).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/unlinkPlaceDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response unlinkPlaceDevice(CashUnlinkPlaceDeviceRequest cashUnlinkPlaceDeviceRequest) throws ServiceException {
        if (cashUnlinkPlaceDeviceRequest == null) {
            throw new ServiceException("cashUnlinkPlaceDeviceRequest is null");
        }
        if (cashUnlinkPlaceDeviceRequest.getDeviceId() == null || cashUnlinkPlaceDeviceRequest.getDeviceId().equals("")) {
            throw new ServiceException("cashUnlinkPlaceDeviceRequest.getDeviceId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashUnlinkPlaceDevice(cashUnlinkPlaceDeviceRequest).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/activatePlace/{placeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response activatePlace(
            @PathParam("placeId") String placeId
    ) throws ServiceException {
        if (placeId == null || placeId.equals("")) {
            throw new ServiceException("placeId is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashActivatePlace(placeId).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/inactivatePlace/{placeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response inactivatePlace(
            @PathParam("placeId") String placeId
    ) throws ServiceException {
        if (placeId == null || placeId.equals("")) {
            throw new ServiceException("placeId is null or empty");
        }
        return Response
                .status(200)
                .entity(new CashInactivatePlace(placeId).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/processOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response processOperation(CashProcessOperationRequest cashProcessOperationRequest) throws ServiceException {
        if (cashProcessOperationRequest == null) {
            throw new ServiceException("cashProcessOperationRequest is null");
        }
        if (cashProcessOperationRequest.getUserName() == null || cashProcessOperationRequest.getUserName().equals("")) {
            throw new ServiceException("cashProcessOperationRequest.getUserName() is null or empty");
        }
        if (cashProcessOperationRequest.getOperationId() == null || cashProcessOperationRequest.getOperationId().equals("")) {
            throw new ServiceException("cashProcessOperationRequest.getOperationId() is null or empty");
        }
        if (cashProcessOperationRequest.getCashOperationStatus() == null || cashProcessOperationRequest.getCashOperationStatus().equals(CashOperationStatus.PROCESSING)) {
            throw new ServiceException("cashProcessOperationRequest.getCashOperationStatus() is null or is not allowed");
        }
        if(cashProcessOperationRequest.getCanceledReason() != null && !cashProcessOperationRequest.getCanceledReason().equals("") && !cashProcessOperationRequest.getCashOperationStatus().equals(CashOperationStatus.CANCELED)){
            cashProcessOperationRequest.setCanceledReason(null);
        }
        return Response
                .status(200)
                .entity(new CashProcessOperation(cashProcessOperationRequest).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/getOperations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperations(CashGetOperationsRequest cashGetOperationsRequest) throws ServiceException {
        if (cashGetOperationsRequest == null) {
            throw new ServiceException("cashGetOperationsRequest is null");
        }
        return Response
                .status(200)
                .entity(new CashGetOperations(cashGetOperationsRequest).getResponse())
                .build();
    }

}
