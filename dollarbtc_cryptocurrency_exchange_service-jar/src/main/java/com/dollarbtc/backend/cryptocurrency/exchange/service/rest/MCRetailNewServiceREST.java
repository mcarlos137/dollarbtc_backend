/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewAddAttachmentToCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewAddCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewAddEscrowFromMCUserBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewChangeCreateStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewGetChargesBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewLinkDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewProcessOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewRemoveCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewSubstractEscrowToMCUserBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailBalanceType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewAddAttachmentToCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewAddCurrencyOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewAddEscrowFromMCUserBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewChangeCreateStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewGetBalanceMovements;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewGetChargesBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewGetRetail;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewGetRetails;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewLinkDevice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewProcessOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewRemoveCurrencyOperationType;
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
@Path("/mcRetailNew")
@XmlRegistry
public class MCRetailNewServiceREST {

    //LISTO
    @POST
    @Path("/processOperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response processOperation(MCRetailNewProcessOperationRequest mcRetailNewProcessOperationRequest) throws ServiceException {
        if (mcRetailNewProcessOperationRequest == null) {
            throw new ServiceException("mcRetailProcessOperationRequest is null");
        }
        if (mcRetailNewProcessOperationRequest.getUserName() == null || mcRetailNewProcessOperationRequest.getUserName().equals("")) {
            throw new ServiceException("mcRetailNewProcessOperationRequest.getUserName() is null or empty");
        }
        if (mcRetailNewProcessOperationRequest.getOperationId() == null || mcRetailNewProcessOperationRequest.getOperationId().equals("")) {
            throw new ServiceException("mcRetailNewProcessOperationRequest.getOperationId() is null or empty");
        }
        if (mcRetailNewProcessOperationRequest.getMcRetailOperationStatus() == null || mcRetailNewProcessOperationRequest.getMcRetailOperationStatus().equals(MCRetailOperationStatus.PROCESSING)) {
            throw new ServiceException("mcRetailNewProcessOperationRequest.getOperationId() is null or is not allowed");
        }
        if(mcRetailNewProcessOperationRequest.getCanceledReason() != null && !mcRetailNewProcessOperationRequest.getCanceledReason().equals("") && !mcRetailNewProcessOperationRequest.getMcRetailOperationStatus().equals(MCRetailOperationStatus.CANCELED)){
            mcRetailNewProcessOperationRequest.setCanceledReason(null);
        }
        return Response
                .status(200)
                .entity(new MCRetailNewProcessOperation(mcRetailNewProcessOperationRequest).getResponse())
                .build();
    }

    //LISTO
    @GET
    @Path("/getRetail/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRetail(
            @PathParam("id") String id
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewGetRetail(id).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/getRetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRetails() throws ServiceException {
        return Response
                .status(200)
                .entity(new MCRetailNewGetRetails(null, null).getResponse())
                .build();
    }

    //LISTO
    @GET
    @Path("/getRetails/{currency}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRetails(
            @PathParam("currency") String currency
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewGetRetails(currency, null).getResponse())
                .build();
    }

    //LISTO
    @GET
    @Path("/getRetails/{currency}/{mcRetailOperationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRetails(
            @PathParam("currency") String currency,
            @PathParam("mcRetailOperationType") MCRetailOperationType mcRetailOperationType
    ) throws ServiceException {
        if (currency == null || currency.equals("")) {
            throw new ServiceException("currency is null or empty");
        }
        if (mcRetailOperationType == null) {
            throw new ServiceException("mcRetailOperationType is null");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewGetRetails(currency, mcRetailOperationType).getResponse())
                .build();
    }

    //LISTO
    @GET
    @Path("/getBalance/{retailId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("retailId") String retailId
    ) throws ServiceException {
        if (retailId == null || retailId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewGetBalance(retailId, false).getResponse())
                .build();
    }

    //LISTO
    @GET
    @Path("/getBalanceMovements/{retailId}/{initTimestamp}/{endTimestamp}/{mcRetailBalanceType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalanceMovements(
            @PathParam("retailId") String retailId,
            @PathParam("initTimestamp") String initTimestamp,
            @PathParam("endTimestamp") String endTimestamp,
            @PathParam("mcRetailBalanceType") MCRetailBalanceType mcRetailBalanceType
    ) throws ServiceException {
        if (retailId == null || retailId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        if (initTimestamp == null || initTimestamp.equals("")) {
            throw new ServiceException("initTimestamp is null or empty");
        }
        if (endTimestamp == null || endTimestamp.equals("")) {
            throw new ServiceException("endTimestamp is null or empty");
        }
        if (mcRetailBalanceType == null) {
            throw new ServiceException("mcRetailBalanceType is null");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewGetBalanceMovements(retailId, initTimestamp, endTimestamp, null, mcRetailBalanceType).getResponse())
                .build();
    }

    //LISTO
    @POST
    @Path("/addEscrowFromMCUserBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addEscrowFromMCUserBalance(MCRetailNewAddEscrowFromMCUserBalanceRequest mcRetailNewAddEscrowFromMCUserBalanceRequest) throws ServiceException {
        if (mcRetailNewAddEscrowFromMCUserBalanceRequest == null) {
            throw new ServiceException("mcRetailNewAddEscrowFromMCUserBalanceRequest is null");
        }
        if (mcRetailNewAddEscrowFromMCUserBalanceRequest.getUserName() == null || mcRetailNewAddEscrowFromMCUserBalanceRequest.getUserName().equals("")) {
            throw new ServiceException("mcRetailNewAddEscrowFromMCUserBalanceRequest.getUserName() is null or empty");
        }
        if (mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId() == null || mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId().equals("")) {
            throw new ServiceException("mcRetailNewAddEscrowFromMCUserBalanceRequest.getRetailId() is null or empty");
        }
        if (mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount() == null || mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount() == 0) {
            throw new ServiceException("mcRetailNewAddEscrowFromMCUserBalanceRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewAddEscrowFromMCUserBalance(mcRetailNewAddEscrowFromMCUserBalanceRequest).getResponse())
                .build();
    }

    //LISTO
    @POST
    @Path("/substractEscrowToMCUserBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response substractEscrowToMCUserBalance(MCRetailNewSubstractEscrowToMCUserBalanceRequest mcRetailNewSubstractEscrowToMCUserBalanceRequest) throws ServiceException {
        if (mcRetailNewSubstractEscrowToMCUserBalanceRequest == null) {
            throw new ServiceException("mcRetailNewSubstractEscrowToMCUserBalanceRequest is null");
        }
        if (mcRetailNewSubstractEscrowToMCUserBalanceRequest.getUserName() == null || mcRetailNewSubstractEscrowToMCUserBalanceRequest.getUserName().equals("")) {
            throw new ServiceException("mcRetailNewSubstractEscrowToMCUserBalanceRequest.getUserName() is null or empty");
        }
        if (mcRetailNewSubstractEscrowToMCUserBalanceRequest.getRetailId() == null || mcRetailNewSubstractEscrowToMCUserBalanceRequest.getRetailId().equals("")) {
            throw new ServiceException("mcRetailNewSubstractEscrowToMCUserBalanceRequest.getRetailId() is null or empty");
        }
        if (mcRetailNewSubstractEscrowToMCUserBalanceRequest.getAmount() == null || mcRetailNewSubstractEscrowToMCUserBalanceRequest.getAmount() == 0) {
            throw new ServiceException("mcRetailNewSubstractEscrowToMCUserBalanceRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity("OK")
                .build();
    }

    //LISTO
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(MCRetailNewCreateRequest mcRetailNewCreateRequest) throws ServiceException {
        if (mcRetailNewCreateRequest == null) {
            throw new ServiceException("mcRetailNewCreateRequest is null");
        }
        if (mcRetailNewCreateRequest.getUserName() == null || mcRetailNewCreateRequest.getUserName().equals("")) {
            throw new ServiceException("mcRetailNewCreateRequest.getUserName() is null or empty");
        }
        if (mcRetailNewCreateRequest.getTitle() == null || mcRetailNewCreateRequest.getTitle().equals("")) {
            throw new ServiceException("mcRetailNewCreateRequest.getTitle() is null or empty");
        }
        if (mcRetailNewCreateRequest.getDescription() == null || mcRetailNewCreateRequest.getDescription().equals("")) {
            throw new ServiceException("mcRetailNewCreateRequest.getDescription() is null or empty");
        }
        if (mcRetailNewCreateRequest.getEmail() == null || mcRetailNewCreateRequest.getEmail().equals("")) {
            throw new ServiceException("mcRetailNewCreateRequest.getEmail() is null or empty");
        }
        if (mcRetailNewCreateRequest.getLatitude() == null || mcRetailNewCreateRequest.getLatitude() == 0) {
            throw new ServiceException("mcRetailNewCreateRequest.getLatitude() is null or empty");
        }
        if (mcRetailNewCreateRequest.getLongitude() == null || mcRetailNewCreateRequest.getLongitude() == 0) {
            throw new ServiceException("mcRetailNewCreateRequest.getLongitude() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewCreate(mcRetailNewCreateRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/addCurrencyOperationType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addCurrencyOperationType(MCRetailNewAddCurrencyOperationTypeRequest mcRetailNewAddCurrencyOperationTypeRequest) throws ServiceException {
        if (mcRetailNewAddCurrencyOperationTypeRequest == null) {
            throw new ServiceException("mcRetailNewAddCurrencyOperationTypeRequest is null");
        }
        if (mcRetailNewAddCurrencyOperationTypeRequest.getId() == null || mcRetailNewAddCurrencyOperationTypeRequest.getId().equals("")) {
            throw new ServiceException("mcRetailNewAddCurrencyOperationTypeRequest.getId() is null or empty");
        }
        if (mcRetailNewAddCurrencyOperationTypeRequest.getCurrency() == null || mcRetailNewAddCurrencyOperationTypeRequest.getCurrency().equals("")) {
            throw new ServiceException("mcRetailNewAddCurrencyOperationTypeRequest.getCurrency() is null or empty");
        }
        if (mcRetailNewAddCurrencyOperationTypeRequest.getMcRetailOperationType() == null) {
            throw new ServiceException("mcRetailNewAddCurrencyOperationTypeRequest.getMcRetailOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewAddCurrencyOperationType(mcRetailNewAddCurrencyOperationTypeRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/removeCurrencyOperationType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeCurrencyOperationType(MCRetailNewRemoveCurrencyOperationTypeRequest mcRetailNewRemoveCurrencyOperationTypeRequest) throws ServiceException {
        if (mcRetailNewRemoveCurrencyOperationTypeRequest == null) {
            throw new ServiceException("mcRetailNewRemoveCurrencyOperationTypeRequest is null");
        }
        if (mcRetailNewRemoveCurrencyOperationTypeRequest.getId() == null || mcRetailNewRemoveCurrencyOperationTypeRequest.getId().equals("")) {
            throw new ServiceException("mcRetailNewRemoveCurrencyOperationTypeRequest.getId() is null or empty");
        }
        if (mcRetailNewRemoveCurrencyOperationTypeRequest.getCurrency() == null || mcRetailNewRemoveCurrencyOperationTypeRequest.getCurrency().equals("")) {
            throw new ServiceException("mcRetailNewRemoveCurrencyOperationTypeRequest.getCurrency() is null or empty");
        }
        if (mcRetailNewRemoveCurrencyOperationTypeRequest.getMcRetailOperationType() == null) {
            throw new ServiceException("mcRetailNewRemoveCurrencyOperationTypeRequest.getMcRetailOperationType() is null");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewRemoveCurrencyOperationType(mcRetailNewRemoveCurrencyOperationTypeRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/changeCreateStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeCreateStatus(MCRetailNewChangeCreateStatusRequest mcRetailNewChangeCreateStatusRequest) throws ServiceException {
        if (mcRetailNewChangeCreateStatusRequest == null) {
            throw new ServiceException("mcRetailNewChangeCreateStatusRequest is null");
        }
        if (mcRetailNewChangeCreateStatusRequest.getId() == null || mcRetailNewChangeCreateStatusRequest.getId().equals("")) {
            throw new ServiceException("mcRetailNewChangeCreateStatusRequest.getId() is null or empty");
        }
        if (mcRetailNewChangeCreateStatusRequest.getMcRetailCreateStatus() == null) {
            throw new ServiceException("mcRetailNewRemoveCurrencyOperationTypeRequest.getMcRetailCreateStatus() is null");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewChangeCreateStatus(mcRetailNewChangeCreateStatusRequest).getResponse())
                .build();
    }

    //LISTO
    @PUT
    @Path("/addAttachmentToCreate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addAttachmentToCreate(MCRetailNewAddAttachmentToCreateRequest mcRetailNewAddAttachmentToCreateRequest) throws ServiceException {
        if (mcRetailNewAddAttachmentToCreateRequest == null) {
            throw new ServiceException("mcRetailNewAddAttachmentToCreateRequest is null");
        }
        if (mcRetailNewAddAttachmentToCreateRequest.getId() == null || mcRetailNewAddAttachmentToCreateRequest.getId().equals("")) {
            throw new ServiceException("mcRetailNewAddAttachmentToCreateRequest.getId() is null or empty");
        }
        if (mcRetailNewAddAttachmentToCreateRequest.getAttachmentUrl() == null || mcRetailNewAddAttachmentToCreateRequest.getAttachmentUrl().equals("")) {
            throw new ServiceException("mcRetailNewAddAttachmentToCreateRequest.getAttachmentUrl() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewAddAttachmentToCreate(mcRetailNewAddAttachmentToCreateRequest).getResponse())
                .build();
    }
    
    //LISTO
    @PUT
    @Path("/linkDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response linkDevice(MCRetailNewLinkDeviceRequest mcRetailNewLinkDeviceRequest) throws ServiceException {
        if (mcRetailNewLinkDeviceRequest == null) {
            throw new ServiceException("mcRetailNewLinkDeviceRequest is null");
        }
        if (mcRetailNewLinkDeviceRequest.getDeviceId() == null || mcRetailNewLinkDeviceRequest.getDeviceId().equals("")) {
            throw new ServiceException("mcRetailNewLinkDeviceRequest.getDeviceId() is null or empty");
        }
        if (mcRetailNewLinkDeviceRequest.getRetailId() == null || mcRetailNewLinkDeviceRequest.getRetailId().equals("")) {
            throw new ServiceException("mcRetailNewLinkDeviceRequest.getRetailId() is null or empty");
        }
        if (mcRetailNewLinkDeviceRequest.getType() == null || mcRetailNewLinkDeviceRequest.getType().equals("")) {
            throw new ServiceException("mcRetailNewLinkDeviceRequest.getType() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewLinkDevice(mcRetailNewLinkDeviceRequest).getResponse())
                .build();
    }
    
    //REVISAR
    @POST
    @Path("/getChargesBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChargesBalance(MCRetailNewGetChargesBalanceRequest mcRetailNewGetChargesBalanceRequest)  throws ServiceException {
        if (mcRetailNewGetChargesBalanceRequest == null) {
            throw new ServiceException("mcRetailNewGetChargesBalanceRequest is null");
        }
        if (mcRetailNewGetChargesBalanceRequest.getRetailId() == null || mcRetailNewGetChargesBalanceRequest.getRetailId().equals("")) {
            throw new ServiceException("mcRetailNewGetChargesBalanceRequest.getRetailId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailNewGetChargesBalance(mcRetailNewGetChargesBalanceRequest).getResponse())
                .build();
    }

}
