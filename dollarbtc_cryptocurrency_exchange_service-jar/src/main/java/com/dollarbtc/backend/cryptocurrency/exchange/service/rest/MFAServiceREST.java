/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFACreateGASecretKeyRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFASendCodeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFAVerifyCodeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFAVerifyGACodeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa.MFACreateGASecretKey;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa.MFAGetGAQRCodeUrl;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa.MFASendCode;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa.MFAVerifyCode;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa.MFAVerifyGACode;
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
@Path("/mfa")
@XmlRegistry
public class MFAServiceREST {

    @POST
    @Path("/sendCode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendCode(MFASendCodeRequest mfaSendCodeRequest) throws ServiceException {
        if (mfaSendCodeRequest == null) {
            throw new ServiceException("mfaSendCodeRequest is null");
        }
        if (mfaSendCodeRequest.getUserName() == null || mfaSendCodeRequest.getUserName().equals("")) {
            throw new ServiceException("mfaSendCodeRequest.getUserName() is null or empty");
        }
        if (mfaSendCodeRequest.getLanguage() == null || mfaSendCodeRequest.getLanguage().equals("")) {
            throw new ServiceException("mfaSendCodeRequest.getLanguage() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MFASendCode(mfaSendCodeRequest).getResponse())
                .build();
    }

    @POST
    @Path("/verifyCode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyCode(MFAVerifyCodeRequest mfaVerifyCodeRequest) throws ServiceException {
        if (mfaVerifyCodeRequest == null) {
            throw new ServiceException("mfaVerifyCodeRequest is null");
        }
        if (mfaVerifyCodeRequest.getUserName() == null || mfaVerifyCodeRequest.getUserName().equals("")) {
            throw new ServiceException("mfaVerifyCodeRequest.getUserName() is null or empty");
        }
        if (mfaVerifyCodeRequest.getCode() == null || mfaVerifyCodeRequest.getCode().equals("")) {
            throw new ServiceException("mfaVerifyCodeRequest.getCode() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MFAVerifyCode(mfaVerifyCodeRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/createGASecretKey")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createGASecretKey(MFACreateGASecretKeyRequest mfaCreateGASecretKeyRequest) throws ServiceException {
        if (mfaCreateGASecretKeyRequest == null) {
            throw new ServiceException("mfaCreateGASecretKeyRequest is null");
        }
        if (mfaCreateGASecretKeyRequest.getUserName() == null || mfaCreateGASecretKeyRequest.getUserName().equals("")) {
            throw new ServiceException("mfaCreateGASecretKeyRequest.getUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MFACreateGASecretKey(mfaCreateGASecretKeyRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getGAQRCodeUrl/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getGAQRCodeUrl(@PathParam("userName") String userName) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new MFAGetGAQRCodeUrl(userName).getResponse())
                .build();
    }
    
    @POST
    @Path("/verifyGACode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyGACode(MFAVerifyGACodeRequest mfaVerifyGACodeRequest) throws ServiceException {
        if (mfaVerifyGACodeRequest == null) {
            throw new ServiceException("mfaVerifyGACodeRequest is null");
        }
        if (mfaVerifyGACodeRequest.getUserName() == null || mfaVerifyGACodeRequest.getUserName().equals("")) {
            throw new ServiceException("mfaVerifyGACodeRequest.getUserName() is null or empty");
        }
        if (mfaVerifyGACodeRequest.getCode() == null || mfaVerifyGACodeRequest.getCode().equals("")) {
            throw new ServiceException("mfaVerifyGACodeRequest.getCode() is null or empty");
        }
        return Response
                .status(200)
                .entity(new MFAVerifyGACode(mfaVerifyGACodeRequest).getResponse())
                .build();
    }
    
}
