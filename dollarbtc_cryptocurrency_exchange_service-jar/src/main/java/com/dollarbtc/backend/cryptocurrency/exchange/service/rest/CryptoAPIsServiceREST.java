/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cryptoapis.CryptoAPIsReceiveCallBackRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsReceiveCallBack;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.service.hmac.SHAHMACAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author conamerica90
 */
@Path("/cryptoapis")
@XmlRegistry
public class CryptoAPIsServiceREST {

    @GET
    @Path("/{fileName}")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getFile(
            @PathParam("fileName") String fileName
    ) throws ServiceException {
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        String response = "";
        try {
            response = Files.readString(new File(ExchangeUtil.OPERATOR_PATH + "/" + fileName).toPath());
        } catch (IOException ex) {
            Logger.getLogger(CryptoAPIsServiceREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response
                .status(200)
                .entity(response)
                .build();
    }

    @POST
    @Path("/receiveCallBack")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response receiveCallBack(
            @HeaderParam("x-signature") String xSignature,
//            @Context HttpServletRequest request,
            CryptoAPIsReceiveCallBackRequest cryptoAPIsReceiveCallBackRequest
    ) throws ServiceException {
        if (cryptoAPIsReceiveCallBackRequest == null) {
            throw new ServiceException("cryptoAPIsReceiveCallBackRequest is null");
        }
//        try {
//            String requestBody = IOUtils.toString(request.getReader());
//        } catch (IOException ex) {
//            Logger.getLogger(CryptoAPIsServiceREST.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            String encryptedMessage = new SHAHMACAlgorithm(256).encryptMessage("DollARBtc2021FoReveR", "AAA");
//        } catch (SignatureException ex) {
//            Logger.getLogger(CryptoAPIsServiceREST.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return Response
                .status(200)
                .entity(new CryptoAPIsReceiveCallBack(cryptoAPIsReceiveCallBackRequest).getResponse())
                .build();
    }

}
