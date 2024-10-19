/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.JsonNodeResponse;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.admin.AdminGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserOperationAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
@Path("/admin")
@XmlRegistry
public class AdminServiceREST {

    @GET
    @Path("/getBalance/{userType}/{userEnvironment}/{userOperationAccount}/{excludeUsersDetails}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("userType") UserType userType,
            @PathParam("userEnvironment") UserEnvironment userEnvironment,
            @PathParam("userOperationAccount") UserOperationAccount userOperationAccount,
            @PathParam("excludeUsersDetails") boolean excludeUsersDetails
    ) throws ServiceException {
        if(userType == null){
            throw new ServiceException("userType is null");
        }
        if(userEnvironment == null){
            throw new ServiceException("userEnvironment is null");
        }
        if(userOperationAccount == null){
            throw new ServiceException("userOperationAccount is null");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new AdminGetBalance(userType, userEnvironment, userOperationAccount, excludeUsersDetails, null).getResponse()))
                .build();
    }
    
    @GET
    @Path("/getBalance/{userProfile}/{excludeUsersDetails}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(
            @PathParam("userProfile") UserProfile userProfile,
            @PathParam("excludeUsersDetails") boolean excludeUsersDetails
    ) throws ServiceException {
        if(userProfile == null){
            throw new ServiceException("userProfile is null");
        }
        return Response
                .status(200)
                .entity(new JsonNodeResponse(new AdminGetBalance(null, null, null, excludeUsersDetails, userProfile).getResponse()))
                .build();
    }

}
