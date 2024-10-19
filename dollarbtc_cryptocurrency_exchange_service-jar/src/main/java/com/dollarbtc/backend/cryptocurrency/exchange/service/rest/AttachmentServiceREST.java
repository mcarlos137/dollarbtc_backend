/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment.AttachmentGetMoneyOrderFile;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment.AttachmentGetOTCOperationFile;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment.AttachmentGetUserOrRetailFile;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment.AttachmentGetUserOrRetailQRCode;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetConfig;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
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
@Path("/attachment")
@XmlRegistry
public class AttachmentServiceREST {

    @GET
    @Path("/getUserGAQRCode/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getUserGAQRCode(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AttachmentGetUserOrRetailQRCode(userName).getResponse())
                .build();
    }

    @GET
    @Path("/getRetailQRCode/{retailId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getRetailQRCode(
            @PathParam("retailId") String retailId
    ) throws ServiceException {
        if (retailId == null || retailId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        return Response
                .status(200)
                .entity(new AttachmentGetUserOrRetailQRCode(retailId).getResponse())
                .build();
    }

    @GET
    @Path("/getRetailFile/{retailId}/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getRetailFile(
            @PathParam("retailId") String retailId,
            @PathParam("fileName") String fileName
    ) throws ServiceException {
        if (retailId == null || retailId.equals("")) {
            throw new ServiceException("retailId is null or empty");
        }
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AttachmentGetUserOrRetailFile(retailId, fileName).getResponse())
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .build();
    }

    @GET
    @Path("/getUserFile/{userName}/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getUserFile(
            @PathParam("userName") String userName,
            @PathParam("fileName") String fileName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AttachmentGetUserOrRetailFile(userName, fileName).getResponse())
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .build();
    }

    @GET
    @Path("/getUserFile/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getUserFile(
            @PathParam("userName") String userName
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        JsonNode userConfig = new UserGetConfig(userName, "OK").getResponse();
        if(userConfig.has("profileImage")){
            return Response
                .status(200)
                .entity(new AttachmentGetUserOrRetailFile(userName, userConfig.get("profileImage").textValue()).getResponse())
                .header("Content-Disposition", "attachment; filename=" + userConfig.get("profileImage").textValue())
                .build();
        } 
        return Response
                .status(200)
                .entity(MoneyclickFolderLocator.getEmptyUserFile())
                .header("Content-Disposition", "attachment; filename=emptyUser")
                .build();
    }

    @GET
    @Path("/getOTCOperationFile/{otcOperationId}/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getOTCOperationFile(
            @PathParam("otcOperationId") String otcOperationId,
            @PathParam("fileName") String fileName
    ) throws ServiceException {
        if (otcOperationId == null || otcOperationId.equals("")) {
            throw new ServiceException("otcOperationId is null or empty");
        }
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AttachmentGetOTCOperationFile(otcOperationId, fileName).getResponse())
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .build();
    }

    @GET
    @Path("/getMoneyOrderImage/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMoneyOrderImage(
            @PathParam("fileName") String fileName
    ) throws ServiceException {
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        return Response
                .status(200)
                .entity(new AttachmentGetMoneyOrderFile(fileName).getResponse())
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .build();
    }

}
