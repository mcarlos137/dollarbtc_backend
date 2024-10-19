/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankApplyProcessRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankChangeOperationsOfProcessStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankChangeProcessStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankCreateProcessRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankApplyProcess;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankChangeOperationsOfProcessStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankChangeProcessStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankCreateProcess;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankGetLastProcesses;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank.TransferToBankGetProcessFile;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
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
@Path("/transferToBank")
@XmlRegistry
public class TransferToBankServiceREST {

    @POST
    @Path("/getOperations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperations(TransferToBankGetOperationsRequest transferToBankGetOperationsRequest) throws ServiceException {
        if (transferToBankGetOperationsRequest == null) {
            throw new ServiceException("transferToBankGetOperationsRequest is null");
        }
        if (transferToBankGetOperationsRequest.getUserName() == null || transferToBankGetOperationsRequest.getUserName().equals("")) {
            throw new ServiceException("transferToBankGetOperationsRequest.getUserName() is null or empty");
        }
        if (transferToBankGetOperationsRequest.getCurrency() == null || transferToBankGetOperationsRequest.getCurrency().equals("")) {
            throw new ServiceException("transferToBankGetOperationsRequest.getCurrency() is null or empty");
        }
        if (transferToBankGetOperationsRequest.getUserPaymentType() == null || transferToBankGetOperationsRequest.getUserPaymentType().equals("")) {
            throw new ServiceException("transferToBankGetOperationsRequest.getUserPaymentType() is null or empty");
        }
        if (transferToBankGetOperationsRequest.getMinPerOperationAmount() == null) {
            throw new ServiceException("transferToBankGetOperationsRequest.getMinPerOperationAmount() is null");
        }
        if (transferToBankGetOperationsRequest.getMaxPerOperationAmount() == null || transferToBankGetOperationsRequest.getMaxPerOperationAmount() == 0.0) {
            throw new ServiceException("transferToBankGetOperationsRequest.getMaxPerOperationAmount() is null or zero");
        }
        if (transferToBankGetOperationsRequest.getTotalAmount() == null || transferToBankGetOperationsRequest.getTotalAmount() == 0.0) {
            throw new ServiceException("transferToBankGetOperationsRequest.getTotalAmount() is null or zero");
        }
        return Response
                .status(200)
                .entity(new TransferToBankGetOperations(transferToBankGetOperationsRequest).getResponse())
                .build();
    }

    @POST
    @Path("/createProcess")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createProcess(TransferToBankCreateProcessRequest transferToBankCreateProcessRequest) throws ServiceException {
        if (transferToBankCreateProcessRequest.getUserName() == null || transferToBankCreateProcessRequest.getUserName().equals("")) {
            throw new ServiceException("transferToBankCreateProcessRequest.getUserName() is null or empty");
        }
        if (transferToBankCreateProcessRequest.getCurrency() == null || transferToBankCreateProcessRequest.getCurrency().equals("")) {
            throw new ServiceException("transferToBankCreateProcessRequest.getCurrency() is null or empty");
        }
        if (transferToBankCreateProcessRequest.getIds() == null || transferToBankCreateProcessRequest.getIds().isEmpty()) {
            throw new ServiceException("transferToBankCreateProcessRequest.getIds() is null or empty");
        }
        return Response
                .status(200)
                .entity(new TransferToBankCreateProcess(transferToBankCreateProcessRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getLastProcesses/{size}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastProcesses(
            @PathParam("size") int size
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new TransferToBankGetLastProcesses(size).getResponse())
                .build();
    }

    @GET
    @Path("/getProcessFile/{userName}/{id}/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessFile(
            @PathParam("userName") String userName,
            @PathParam("id") String id,
            @PathParam("type") String type
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        if (type == null || type.equals("")) {
            throw new ServiceException("type is null or empty");
        }
        return Response
                .status(200)
                .entity(new TransferToBankGetProcessFile(userName, id, type).getResponse())
                .build();
    }

    @POST
    @Path("/changeProcessStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeProcessStatus(TransferToBankChangeProcessStatusRequest transferToBankChangeProcessStatusRequest) throws ServiceException {
        if (transferToBankChangeProcessStatusRequest.getUserName() == null || transferToBankChangeProcessStatusRequest.getUserName().equals("")) {
            throw new ServiceException("transferToBankChangeProcessStatusRequest.getUserName() is null or empty");
        }
        if (transferToBankChangeProcessStatusRequest.getId() == null || transferToBankChangeProcessStatusRequest.getId().equals("")) {
            throw new ServiceException("transferToBankChangeProcessStatusRequest.getId() is null or empty");
        }
        if (transferToBankChangeProcessStatusRequest.getStatus() == null || transferToBankChangeProcessStatusRequest.getStatus().equals("")) {
            throw new ServiceException("transferToBankChangeProcessStatusRequest.getStatus() is null or empty");
        }
        return Response
                .status(200)
                .entity(new TransferToBankChangeProcessStatus(transferToBankChangeProcessStatusRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/changeOperationsOfProcessStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeOperationsOfProcessStatus(TransferToBankChangeOperationsOfProcessStatusRequest transferToBankChangeOperationsOfProcessStatusRequest) throws ServiceException {
        if (transferToBankChangeOperationsOfProcessStatusRequest.getUserName() == null || transferToBankChangeOperationsOfProcessStatusRequest.getUserName().equals("")) {
            throw new ServiceException("transferToBankChangeOperationsOfProcessStatusRequest.getUserName() is null or empty");
        }
        if (transferToBankChangeOperationsOfProcessStatusRequest.getProcessId() == null || transferToBankChangeOperationsOfProcessStatusRequest.getProcessId().equals("")) {
            throw new ServiceException("transferToBankChangeOperationsOfProcessStatusRequest.getProcessId() is null or empty");
        }
        if (transferToBankChangeOperationsOfProcessStatusRequest.getId() == null || transferToBankChangeOperationsOfProcessStatusRequest.getId().equals("")) {
            throw new ServiceException("transferToBankChangeOperationsOfProcessStatusRequest.getId() is null or empty");
        }
        if (transferToBankChangeOperationsOfProcessStatusRequest.getStatus() == null || transferToBankChangeOperationsOfProcessStatusRequest.getStatus().equals("")) {
            throw new ServiceException("transferToBankChangeOperationsOfProcessStatusRequest.getStatus() is null or empty");
        }
        return Response
                .status(200)
                .entity(new TransferToBankChangeOperationsOfProcessStatus(transferToBankChangeOperationsOfProcessStatusRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/applyProcess")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response applyProcess(TransferToBankApplyProcessRequest transferToBankApplyProcessRequest) throws ServiceException {
        if (transferToBankApplyProcessRequest.getUserName() == null || transferToBankApplyProcessRequest.getUserName().equals("")) {
            throw new ServiceException("transferToBankApplyProcessRequest.getUserName() is null or empty");
        }
        if (transferToBankApplyProcessRequest.getId() == null || transferToBankApplyProcessRequest.getId().equals("")) {
            throw new ServiceException("transferToBankApplyProcessRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new TransferToBankApplyProcess(transferToBankApplyProcessRequest).getResponse())
                .build();
    }
    
}
