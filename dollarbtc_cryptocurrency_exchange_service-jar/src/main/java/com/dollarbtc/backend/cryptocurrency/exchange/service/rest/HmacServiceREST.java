/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.hmac.HmacSetSecretKeyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.hmac.HmacSetSecretKey;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/hmac")
@XmlRegistry
public class HmacServiceREST {

    @POST
    @Path("/setSecretKey")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response setSecretKey(
            @Context HttpServletRequest requestContext,
            HmacSetSecretKeyRequest hmacSetSecretKeyRequest
    ) throws ServiceException {
        if (hmacSetSecretKeyRequest == null) {
            throw new ServiceException("hmacSetSecretKeyRequest is null");
        }
        if (hmacSetSecretKeyRequest.getUserName() == null || hmacSetSecretKeyRequest.getUserName().equals("")) {
            throw new ServiceException("hmacSetSecretKeyRequest.getUserName() is null or empty");
        }
        if (hmacSetSecretKeyRequest.getSecretKey() == null || hmacSetSecretKeyRequest.getSecretKey().equals("")) {
            throw new ServiceException("hmacSetSecretKeyRequest.getSecretKey() is null or empty");
        }
        String ip = requestContext.getHeader("x-forwarded-for");      
        Logger.getLogger(HmacServiceREST.class.getName()).log(Level.INFO, "ip {0}", ip);
        String remoteAddress = requestContext.getRemoteAddr();
        Logger.getLogger(HmacServiceREST.class.getName()).log(Level.INFO, "remoteAddress {0}", remoteAddress);
        if(!remoteAddress.equals("")){
        
        }
        return Response
                .status(200)
                .entity(new HmacSetSecretKey(hmacSetSecretKeyRequest).getResponse())
                .build();
    }

}
