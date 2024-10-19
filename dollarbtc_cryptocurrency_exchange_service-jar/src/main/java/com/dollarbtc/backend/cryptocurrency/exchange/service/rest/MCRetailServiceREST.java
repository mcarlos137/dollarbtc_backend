/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail.MCRetailCheckDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail.MCRetailGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail.MCRetailUnlinkDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail.MCRetailActivate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail.MCRetailCheckDevice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail.MCRetailGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail.MCRetailInactivate;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail.MCRetailUnlinkDevice;
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
@Path("/mcRetail")
@XmlRegistry
public class MCRetailServiceREST {
    
    //LISTO
    @POST
    @Path("/getOperations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperations(
            MCRetailGetOperationsRequest mcRetailGetOperationsRequest
    ) throws ServiceException {
        if (mcRetailGetOperationsRequest == null) {
            throw new ServiceException("otcGetOperationsRequest is null");
        }
        return Response
                .status(200)
                .entity(new MCRetailGetOperations(mcRetailGetOperationsRequest).getResponse())
                .build();
    }

    //LISTO
    @POST
    @Path("/checkDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkDevice(MCRetailCheckDeviceRequest mcRetailCheckDeviceRequest) throws ServiceException {
        if (mcRetailCheckDeviceRequest == null) {
            throw new ServiceException("mcRetailCheckDeviceRequest is null");
        }
        if (mcRetailCheckDeviceRequest.getDeviceId() == null || mcRetailCheckDeviceRequest.getDeviceId().equals("")) {
            throw new ServiceException("mcRetailCheckDeviceRequest.getDeviceId() is null or empty");
        }
        if (mcRetailCheckDeviceRequest.getRetailId() == null || mcRetailCheckDeviceRequest.getRetailId().equals("")) {
            throw new ServiceException("mcRetailCheckDeviceRequest.getRetailId() is null or empty");
        }
        if (mcRetailCheckDeviceRequest.getUserName() == null || mcRetailCheckDeviceRequest.getUserName().equals("")) {
            throw new ServiceException("mcRetailCheckDeviceRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailCheckDevice(mcRetailCheckDeviceRequest).getResponse())
                .build();
    }
    
    //LISTO
    @POST
    @Path("/unlinkDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response unlinkDevice(MCRetailUnlinkDeviceRequest mcRetailUnlinkDeviceRequest) throws ServiceException {
        if (mcRetailUnlinkDeviceRequest == null) {
            throw new ServiceException("mcRetailUnlinkDeviceRequest is null");
        }
        if (mcRetailUnlinkDeviceRequest.getDeviceId() == null || mcRetailUnlinkDeviceRequest.getDeviceId().equals("")) {
            throw new ServiceException("mcRetailUnlinkDeviceRequest.getDeviceId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailUnlinkDevice(mcRetailUnlinkDeviceRequest).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/activate/{retailId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response activate(
            @PathParam("retailId") String retailId
    ) throws ServiceException {
        if (retailId == null || retailId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailActivate(retailId).getResponse())
                .build();
    }
    
    //LISTO
    @GET
    @Path("/inactivate/{retailId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response inactivate(
            @PathParam("retailId") String retailId
    ) throws ServiceException {
        if (retailId == null || retailId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        return Response
                .status(200)
                .entity(new MCRetailInactivate(retailId).getResponse())
                .build();
    }
    
}
