/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationAddGroupsToTopicRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationAddTokenToUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationCreateTopicRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationSendMessageRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationAddGroupsToTopic;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationAddTokenToUser;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationCreateTopic;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationGetGroups;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationGetMessages;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationGetTopics;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationMarkMessageAsReaded;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessage;
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
@Path("/notification")
@XmlRegistry
public class NotificationServiceREST {

    @POST
    @Path("/createTopic")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createTopic(NotificationCreateTopicRequest notificationCreateTopicRequest) throws ServiceException {
        if (notificationCreateTopicRequest == null) {
            throw new ServiceException("notificationCreateTopicRequest is null");
        }
        if (notificationCreateTopicRequest.getName() == null || notificationCreateTopicRequest.getName().equals("")) {
            throw new ServiceException("notificationCreateTopicRequest.getName() is null or empty");
        }
        if (notificationCreateTopicRequest.getOperatorUserName() == null || notificationCreateTopicRequest.getOperatorUserName().equals("")) {
            throw new ServiceException("notificationCreateTopicRequest.getOperatorUserName() is null or empty");
        }
        return Response
                .status(200)
                .entity(new NotificationCreateTopic(notificationCreateTopicRequest).getResponse())
                .build();
    }

    @GET
    @Path("/getTopics")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopics() throws ServiceException {
        return Response
                .status(200)
                .entity(new NotificationGetTopics().getResponse())
                .build();
    }

    @GET
    @Path("/getGroups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups() throws ServiceException {
        return Response
                .status(200)
                .entity(new NotificationGetGroups().getResponse())
                .build();
    }

    @GET
    @Path("/getMessages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages() throws ServiceException {
        return Response
                .status(200)
                .entity(new NotificationGetMessages().getResponse())
                .build();
    }

    @POST
    @Path("/addGroupsToTopic")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addGroupsToTopic(NotificationAddGroupsToTopicRequest notificationAddGroupsToTopicRequest) throws ServiceException {
        if (notificationAddGroupsToTopicRequest == null) {
            throw new ServiceException("notificationAddGroupsToTopicRequest is null");
        }
        if (notificationAddGroupsToTopicRequest.getTopicId() == null || notificationAddGroupsToTopicRequest.getTopicId().equals("")) {
            throw new ServiceException("notificationAddGroupsToTopicRequest.getTopicId() is null or empty");
        }
        if (notificationAddGroupsToTopicRequest.getGroups() == null || notificationAddGroupsToTopicRequest.getGroups().isEmpty()) {
            throw new ServiceException("notificationAddGroupsToTopicRequest.getGroups() is null or empty");
        }
        return Response
                .status(200)
                .entity(new NotificationAddGroupsToTopic(notificationAddGroupsToTopicRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/sendMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMessage(NotificationSendMessageRequest notificationSendMessageRequest) throws ServiceException {
        if (notificationSendMessageRequest == null) {
            throw new ServiceException("notificationSendMessageRequest is null");
        }
        if (notificationSendMessageRequest.getContent() == null || notificationSendMessageRequest.getContent().equals("")) {
            throw new ServiceException("notificationSendMessageRequest.getContent() is null or empty");
        }
        if (notificationSendMessageRequest.getUserNames() != null && (notificationSendMessageRequest.getTitle() == null || notificationSendMessageRequest.getTitle().equals(""))) {
            throw new ServiceException("notificationSendMessageRequest.getTitle() is null or empty when using userNames");
        }
        return Response
                .status(200)
                .entity(new NotificationSendMessage(notificationSendMessageRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/getMessages/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages(
            @PathParam("userName") String userName
    ) throws ServiceException {
        return Response
                .status(200)
                .entity(new NotificationGetMessages(userName).getResponse())
                .build();
    }
    
   @GET
    @Path("/markMessageAsReaded/{userName}/{timestamp}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response markMessageAsReaded(
            @PathParam("userName") String userName,
            @PathParam("timestamp") String timestamp
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (timestamp == null || timestamp.equals("")) {
            throw new ServiceException("timestamp is null or empty");
        }
        return Response
                .status(200)
                .entity(new NotificationMarkMessageAsReaded(userName, timestamp).getResponse())
                .build();
    }
    
    @POST
    @Path("/addTokenToUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addTokenToUser(NotificationAddTokenToUserRequest notificationAddTokenToUserRequest) throws ServiceException {
        if (notificationAddTokenToUserRequest == null) {
            throw new ServiceException("notificationAddTokenToUserRequest is null");
        }
        if (notificationAddTokenToUserRequest.getUserName() == null || notificationAddTokenToUserRequest.getUserName().equals("")) {
            throw new ServiceException("notificationAddTokenToUserRequest.getUserName() is null or empty");
        }
        if (notificationAddTokenToUserRequest.getToken() == null || notificationAddTokenToUserRequest.getToken().equals("")) {
            throw new ServiceException("notificationAddTokenToUserRequest.getToken() is null or empty");
        }
        return Response
                .status(200)
                .entity(new NotificationAddTokenToUser(notificationAddTokenToUserRequest).getResponse())
                .build();
    }

}
