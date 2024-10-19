/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsAddCommentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsChangeTagsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsDeleteRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsReactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsShareRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsUnreactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsViewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsAddComment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsChangeStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsChangeTags;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsDelete;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsGetAttachment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsGetComments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsList;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsOverviewData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsOverviewId;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsReact;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsShare;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsUnreact;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
@Path("/shorts")
@XmlRegistry
public class ShortsServiceREST {

    @GET
    @Path("/overviewId/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response overviewId(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new ShortsOverviewId(userName).getResponse())
                .build();
    }

    @GET
    @Path("/overviewData/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response overviewData(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new ShortsOverviewData(userName).getResponse())
                .build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(ShortsListRequest shortsListRequest) throws ServiceException {
        if (shortsListRequest == null) {
            throw new ServiceException("shortsListRequest is null");
        }
        return Response
                .status(200)
                .entity(new ShortsList(shortsListRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getAttachment/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAttachment(
            @PathParam("fileName") String fileName
    ) throws ServiceException, IOException {
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        File response = new ShortsGetAttachment(fileName).getResponse();
        byte[] bytes = Files.readAllBytes(response.toPath());
        return Response
                .status(200)
                .entity(new ShortsGetAttachment(fileName).getResponse())
                //.header("Content-Disposition", "inline; filename=" + fileName)
                .header("Content-Type", "video/mp4")
                .header("Accept-Ranges", "bytes")
                .header("Cache-Control", "public, max-age=3600")
                .header("Content-Length", bytes.length)
                .build();
    }
    
    @POST
    @Path("/addComment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addComment(ShortsAddCommentRequest shortsAddCommentRequest) throws ServiceException {
        if (shortsAddCommentRequest == null) {
            throw new ServiceException("shortsAddRequest is null");
        }
        if (shortsAddCommentRequest.getId() == null || shortsAddCommentRequest.getId().equals("")) {
            throw new ServiceException("shortsAddCommentRequest.getId() is null or empty");
        }
        if (shortsAddCommentRequest.getUserName() == null || shortsAddCommentRequest.getUserName().equals("")) {
            throw new ServiceException("shortsAddCommentRequest.getUserName() is null or empty");
        }
        if (shortsAddCommentRequest.getName() == null || shortsAddCommentRequest.getName().equals("")) {
            throw new ServiceException("shortsAddCommentRequest.getName() is null or empty");
        }
        if (shortsAddCommentRequest.getComment() == null || shortsAddCommentRequest.getComment().equals("")) {
            throw new ServiceException("shortsAddCommentRequest.getComment() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsAddComment(shortsAddCommentRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getComments/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(
            @PathParam("id") String id
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new ShortsGetComments(id).getResponse())
                .build();
    }
    
    @PUT
    @Path("/share")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response share(ShortsShareRequest shortsShareRequest) throws ServiceException {
        if (shortsShareRequest == null) {
            throw new ServiceException("shortsShareRequest is null");
        }
        if (shortsShareRequest.getId() == null || shortsShareRequest.getId().equals("")) {
            throw new ServiceException("shortsShareRequest.getId() is null or empty");
        }
        if (shortsShareRequest.getUserName() == null || shortsShareRequest.getUserName().equals("")) {
            throw new ServiceException("shortsShareRequest.getUserName() is null or empty");
        }
        if (shortsShareRequest.getName() == null || shortsShareRequest.getName().equals("")) {
            throw new ServiceException("shortsShareRequest.getName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsShare(shortsShareRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/view")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response view(ShortsViewRequest shortsViewRequest) throws ServiceException {
        if (shortsViewRequest == null) {
            throw new ServiceException("shortsViewRequest is null");
        }
        if (shortsViewRequest.getId() == null || shortsViewRequest.getId().equals("")) {
            throw new ServiceException("shortsViewRequest.getId() is null or empty");
        }
        if (shortsViewRequest.getUserName() == null || shortsViewRequest.getUserName().equals("")) {
            throw new ServiceException("shortsViewRequest.getUserName() is null or empty");
        }
        if (shortsViewRequest.getName() == null || shortsViewRequest.getName().equals("")) {
            throw new ServiceException("shortsViewRequest.getName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsView(shortsViewRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/react")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response react(ShortsReactRequest shortsReactRequest) throws ServiceException {
        if (shortsReactRequest == null) {
            throw new ServiceException("shortsReactRequest is null");
        }
        if (shortsReactRequest.getId() == null || shortsReactRequest.getId().equals("")) {
            throw new ServiceException("shortsReactRequest.getId() is null or empty");
        }
        if (shortsReactRequest.getUserName() == null || shortsReactRequest.getUserName().equals("")) {
            throw new ServiceException("shortsReactRequest.getUserName() is null or empty");
        }
        if (shortsReactRequest.getName() == null || shortsReactRequest.getName().equals("")) {
            throw new ServiceException("shortsReactRequest.getName() is null or empty");
        }
        if (shortsReactRequest.getReaction() == null || shortsReactRequest.getReaction().equals("")) {
            throw new ServiceException("shortsReactRequest.getReaction() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsReact(shortsReactRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/unreact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response unreact(ShortsUnreactRequest shortsUnreactRequest) throws ServiceException {
        if (shortsUnreactRequest == null) {
            throw new ServiceException("shortsUnreactRequest is null");
        }
        if (shortsUnreactRequest.getId() == null || shortsUnreactRequest.getId().equals("")) {
            throw new ServiceException("shortsUnreactRequest.getId() is null or empty");
        }
        if (shortsUnreactRequest.getUserName() == null || shortsUnreactRequest.getUserName().equals("")) {
            throw new ServiceException("shortsUnreactRequest.getUserName() is null or empty");
        }
        if (shortsUnreactRequest.getReaction() == null || shortsUnreactRequest.getReaction().equals("")) {
            throw new ServiceException("shortsUnreactRequest.getReaction() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsUnreact(shortsUnreactRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/changeStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeStatus(ShortsChangeStatusRequest shortsChangeStatusRequest) throws ServiceException {
        if (shortsChangeStatusRequest == null) {
            throw new ServiceException("shortsChangeStatusRequest is null");
        }
        if (shortsChangeStatusRequest.getId() == null || shortsChangeStatusRequest.getId().equals("")) {
            throw new ServiceException("shortsChangeStatusRequest.getId() is null or empty");
        }
        if (shortsChangeStatusRequest.getStatus() == null || shortsChangeStatusRequest.getStatus().equals("")) {
            throw new ServiceException("shortsChangeStatusRequest.getStatus() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsChangeStatus(shortsChangeStatusRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/changeTags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeTags(ShortsChangeTagsRequest shortsChangeTagsRequest) throws ServiceException {
        if (shortsChangeTagsRequest == null) {
            throw new ServiceException("shortsChangeTagsRequest is null");
        }
        if (shortsChangeTagsRequest.getId() == null || shortsChangeTagsRequest.getId().equals("")) {
            throw new ServiceException("shortsChangeStatusRequest.getId() is null or empty");
        }
        if (shortsChangeTagsRequest.getTags() == null || shortsChangeTagsRequest.getTags().length == 0) {
            throw new ServiceException("shortsChangeStatusRequest.getTags() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsChangeTags(shortsChangeTagsRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(ShortsDeleteRequest shortsDeleteRequest) throws ServiceException {
        if (shortsDeleteRequest == null) {
            throw new ServiceException("shortsDeleteRequest is null");
        }
        if (shortsDeleteRequest.getId() == null || shortsDeleteRequest.getId().equals("")) {
            throw new ServiceException("shortsDeleteRequest.getId() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ShortsDelete(shortsDeleteRequest).getResponse())
                .build();
    }

}
