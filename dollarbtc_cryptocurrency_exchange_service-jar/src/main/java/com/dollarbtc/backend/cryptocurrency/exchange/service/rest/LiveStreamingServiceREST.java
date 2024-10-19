/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingAddCommentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingChangeTagsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingAddComment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingChangeStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingChangeTags;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingGet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingGetAttachment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingGetComments;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingOverviewData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingOverviewId;
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
@Path("/liveStreaming")
@XmlRegistry
public class LiveStreamingServiceREST {

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(LiveStreamingListRequest liveStreamingListRequest) throws ServiceException {
        if (liveStreamingListRequest == null) {
            throw new ServiceException("liveStreamingListRequest is null");
        }
        return Response
                .status(200)
                .entity(new LiveStreamingList(liveStreamingListRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/changeStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeStatus(LiveStreamingChangeStatusRequest liveStreamingChangeStatusRequest) throws ServiceException {
        if (liveStreamingChangeStatusRequest == null) {
            throw new ServiceException("liveStreamingChangeStatusRequest is null");
        }
        if (liveStreamingChangeStatusRequest.getId() == null || liveStreamingChangeStatusRequest.getId().equals("")) {
            throw new ServiceException("liveStreamingChangeStatusRequest.getId() is null or empty");
        }
        if (liveStreamingChangeStatusRequest.getStatus() == null || liveStreamingChangeStatusRequest.getStatus().equals("")) {
            throw new ServiceException("liveStreamingChangeStatusRequest.getStatus() is null or empty");
        }
        return Response
                .status(200)
                .entity(new LiveStreamingChangeStatus(liveStreamingChangeStatusRequest).getResponse())
                .build();
    }

    @PUT
    @Path("/changeTags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeTags(LiveStreamingChangeTagsRequest liveStreamingChangeTagsRequest) throws ServiceException {
        if (liveStreamingChangeTagsRequest == null) {
            throw new ServiceException("liveStreamingChangeTagsRequest is null");
        }
        if (liveStreamingChangeTagsRequest.getId() == null || liveStreamingChangeTagsRequest.getId().equals("")) {
            throw new ServiceException("liveStreamingChangeTagsRequest.getId() is null or empty");
        }
        if (liveStreamingChangeTagsRequest.getTags() == null || liveStreamingChangeTagsRequest.getTags().length == 0) {
            throw new ServiceException("liveStreamingChangeTagsRequest.getTags() is null or empty");
        }
        return Response
                .status(200)
                .entity(new LiveStreamingChangeTags(liveStreamingChangeTagsRequest).getResponse())
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
                .entity(new LiveStreamingGet(id).getResponse())
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
                .entity(new LiveStreamingGetAttachment(fileName).getResponse())
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
                .entity(new LiveStreamingOverviewId(userName).getResponse())
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
                .entity(new LiveStreamingOverviewData(userName).getResponse())
                .build();
    }

    @POST
    @Path("/addComment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addComment(LiveStreamingAddCommentRequest liveStreamingAddCommentRequest) throws ServiceException {
        if (liveStreamingAddCommentRequest == null) {
            throw new ServiceException("liveStreamingAddCommentRequest is null");
        }
        if (liveStreamingAddCommentRequest.getId() == null || liveStreamingAddCommentRequest.getId().equals("")) {
            throw new ServiceException("liveStreamingAddCommentRequest.getId() is null or empty");
        }
        if (liveStreamingAddCommentRequest.getPublicationId() == null || liveStreamingAddCommentRequest.getPublicationId().equals("")) {
            throw new ServiceException("liveStreamingAddCommentRequest.getPublicationId() is null or empty");
        }
        if (liveStreamingAddCommentRequest.getUserName() == null || liveStreamingAddCommentRequest.getUserName().equals("")) {
            throw new ServiceException("liveStreamingAddCommentRequest.getUserName() is null or empty");
        }
        if (liveStreamingAddCommentRequest.getName() == null || liveStreamingAddCommentRequest.getName().equals("")) {
            throw new ServiceException("liveStreamingAddCommentRequest.getName() is null or empty");
        }
        if (liveStreamingAddCommentRequest.getComment() == null || liveStreamingAddCommentRequest.getComment().equals("")) {
            throw new ServiceException("liveStreamingAddCommentRequest.getComment() is null or empty");
        }
        return Response
                .status(200)
                .entity(new LiveStreamingAddComment(liveStreamingAddCommentRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getComments/{id}/{publicationId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(
            @PathParam("id") String id,
            @PathParam("publicationId") String publicationId
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new LiveStreamingGetComments(id, publicationId).getResponse())
                .build();
    }

}
