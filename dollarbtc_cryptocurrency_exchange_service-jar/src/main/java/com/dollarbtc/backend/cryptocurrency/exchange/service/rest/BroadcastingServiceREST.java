/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingAddCommentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingAddEpisodeTrailerRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingChangeTagsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingReactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingUnreactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingAddComment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingAddEpisodeTrailer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingChangeStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingChangeTags;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingGet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingGetAttachment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingGetComments;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingOverviewData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingOverviewId;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingReact;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingUnreact;
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
@Path("/broadcasting")
@XmlRegistry
public class BroadcastingServiceREST {

    @PUT
    @Path("/addEpisodeTrailer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addEpisodeTrailer(BroadcastingAddEpisodeTrailerRequest broadcastingAddEpisodeTrailerRequest) throws ServiceException {
        if (broadcastingAddEpisodeTrailerRequest == null) {
            throw new ServiceException("broadcastingAddEpisodeTrailerRequest is null");
        }
        if (broadcastingAddEpisodeTrailerRequest.getBroadcastingId() == null || broadcastingAddEpisodeTrailerRequest.getBroadcastingId().equals("")) {
            throw new ServiceException("broadcastingAddEpisodeTrailerRequest.getBroadcastingId() is null or empty");
        }
        if (broadcastingAddEpisodeTrailerRequest.getTitle() == null || broadcastingAddEpisodeTrailerRequest.getTitle().equals("")) {
            throw new ServiceException("broadcastingAddEpisodeTrailerRequest.getTitle() is null or empty");
        }
        if (broadcastingAddEpisodeTrailerRequest.getDescription() == null || broadcastingAddEpisodeTrailerRequest.getDescription().equals("")) {
            throw new ServiceException("broadcastingAddEpisodeTrailerRequest.getDescription() is null or empty");
        }
        if (broadcastingAddEpisodeTrailerRequest.getType() == null || broadcastingAddEpisodeTrailerRequest.getType().equals("")) {
            throw new ServiceException("broadcastingAddEpisodeTrailerRequest.getType() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingAddEpisodeTrailer(broadcastingAddEpisodeTrailerRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/changeStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeStatus(BroadcastingChangeStatusRequest broadcastingChangeStatusRequest) throws ServiceException {
        if (broadcastingChangeStatusRequest == null) {
            throw new ServiceException("broadcastingChangeStatusRequest is null");
        }
        if (broadcastingChangeStatusRequest.getBroadcastingId() == null || broadcastingChangeStatusRequest.getBroadcastingId().equals("")) {
            throw new ServiceException("broadcastingChangeStatusRequest.getBroadcastingId() is null or empty");
        }
        if (broadcastingChangeStatusRequest.getStatus() == null || broadcastingChangeStatusRequest.getStatus().equals("")) {
            throw new ServiceException("broadcastingChangeStatusRequest.getStatus() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingChangeStatus(broadcastingChangeStatusRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/changeTags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeTags(BroadcastingChangeTagsRequest broadcastingChangeTagsRequest) throws ServiceException {
        if (broadcastingChangeTagsRequest == null) {
            throw new ServiceException("broadcastingChangeTagsRequest is null");
        }
        if (broadcastingChangeTagsRequest.getId() == null || broadcastingChangeTagsRequest.getId().equals("")) {
            throw new ServiceException("broadcastingChangeTagsRequest.getId() is null or empty");
        }
        if (broadcastingChangeTagsRequest.getTags() == null || broadcastingChangeTagsRequest.getTags().length == 0) {
            throw new ServiceException("broadcastingChangeTagsRequest.getTags() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingChangeTags(broadcastingChangeTagsRequest).getResponse())
                .build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(BroadcastingListRequest broadcastingListRequest) throws ServiceException {
        if (broadcastingListRequest == null) {
            throw new ServiceException("broadcastingListRequest is null");
        }
        return Response
                .status(200)
                .entity(new BroadcastingList(broadcastingListRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/get/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @PathParam("id") String id
    ) throws ServiceException {
        if (id == null || id.equals("")) {
            throw new ServiceException("id is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingGet(id).getResponse())
                .build();
    }

    @GET
    @Path("/getAttachment/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMessageAttachment(
            @PathParam("fileName") String fileName
    ) throws ServiceException {
        if (fileName == null || fileName.equals("")) {
            throw new ServiceException("fileName is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingGetAttachment(fileName).getResponse())
                .build();
    }

    @GET
    @Path("/overviewId/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response overviewId(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BroadcastingOverviewId(userName).getResponse())
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
                .entity(new BroadcastingOverviewData(userName).getResponse())
                .build();
    }

    @POST
    @Path("/addComment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addComment(BroadcastingAddCommentRequest broadcastingAddCommentRequest) throws ServiceException {
        if (broadcastingAddCommentRequest == null) {
            throw new ServiceException("broadcastingAddCommentRequest is null");
        }
        if (broadcastingAddCommentRequest.getId() == null || broadcastingAddCommentRequest.getId().equals("")) {
            throw new ServiceException("broadcastingAddCommentRequest.getId() is null or empty");
        }
        if (broadcastingAddCommentRequest.getEpisodeTrailerId() == null || broadcastingAddCommentRequest.getEpisodeTrailerId().equals("")) {
            throw new ServiceException("broadcastingAddCommentRequest.getEpisodeTrailerId() is null or empty");
        }
        if (broadcastingAddCommentRequest.getUserName() == null || broadcastingAddCommentRequest.getUserName().equals("")) {
            throw new ServiceException("broadcastingAddCommentRequest.getUserName() is null or empty");
        }
        if (broadcastingAddCommentRequest.getName() == null || broadcastingAddCommentRequest.getName().equals("")) {
            throw new ServiceException("broadcastingAddCommentRequest.getName() is null or empty");
        }
        if (broadcastingAddCommentRequest.getComment() == null || broadcastingAddCommentRequest.getComment().equals("")) {
            throw new ServiceException("broadcastingAddCommentRequest.getComment() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingAddComment(broadcastingAddCommentRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getComments/{broadcastingId}/{episodeTrailerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(
            @PathParam("broadcastingId") String broadcastingId,
            @PathParam("episodeTrailerId") String episodeTrailerId
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new BroadcastingGetComments(broadcastingId, episodeTrailerId).getResponse())
                .build();
    }
    
    @PUT
    @Path("/react")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response react(BroadcastingReactRequest broadcastingReactRequest) throws ServiceException {
        if (broadcastingReactRequest == null) {
            throw new ServiceException("broadcastingReactRequest is null");
        }
        if (broadcastingReactRequest.getId() == null || broadcastingReactRequest.getId().equals("")) {
            throw new ServiceException("broadcastingReactRequest.getId() is null or empty");
        }
        if (broadcastingReactRequest.getEpisodeTrailerId() == null || broadcastingReactRequest.getEpisodeTrailerId().equals("")) {
            throw new ServiceException("broadcastingReactRequest.getEpisodeTrailerId() is null or empty");
        }
        if (broadcastingReactRequest.getUserName() == null || broadcastingReactRequest.getUserName().equals("")) {
            throw new ServiceException("broadcastingReactRequest.getUserName() is null or empty");
        }
        if (broadcastingReactRequest.getName() == null || broadcastingReactRequest.getName().equals("")) {
            throw new ServiceException("broadcastingReactRequest.getName() is null or empty");
        }
        if (broadcastingReactRequest.getReaction() == null || broadcastingReactRequest.getReaction().equals("")) {
            throw new ServiceException("broadcastingReactRequest.getReaction() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingReact(broadcastingReactRequest).getResponse())
                .build();
    }
    
    @PUT
    @Path("/unreact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response unreact(BroadcastingUnreactRequest broadcastingUnreactRequest) throws ServiceException {
        if (broadcastingUnreactRequest == null) {
            throw new ServiceException("broadcastingUnreactRequest is null");
        }
        if (broadcastingUnreactRequest.getId() == null || broadcastingUnreactRequest.getId().equals("")) {
            throw new ServiceException("broadcastingUnreactRequest.getId() is null or empty");
        }
        if (broadcastingUnreactRequest.getEpisodeTrailerId() == null || broadcastingUnreactRequest.getEpisodeTrailerId().equals("")) {
            throw new ServiceException("broadcastingUnreactRequest.getEpisodeTrailerId() is null or empty");
        }
        if (broadcastingUnreactRequest.getUserName() == null || broadcastingUnreactRequest.getUserName().equals("")) {
            throw new ServiceException("broadcastingUnreactRequest.getUserName() is null or empty");
        }
        if (broadcastingUnreactRequest.getReaction() == null || broadcastingUnreactRequest.getReaction().equals("")) {
            throw new ServiceException("broadcastingUnreactRequest.getReaction() is null or empty");
        }
        return Response
                .status(200)
                .entity(new BroadcastingUnreact(broadcastingUnreactRequest).getResponse())
                .build();
    }
    
}
