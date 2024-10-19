/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.chat.ChatListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.chat.ChatPostMessageRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.chat.ChatList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.chat.ChatMarkAdminMessagesAsReaded;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.chat.ChatPostMessage;
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
@Path("/chat")
@XmlRegistry
public class ChatServiceREST {

    @POST
    @Path("/postMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postMessage(ChatPostMessageRequest chatPostMessageRequest) throws ServiceException {
        if (chatPostMessageRequest == null) {
            throw new ServiceException("chatPostMessageRequest is null");
        }
        if (chatPostMessageRequest.getUserName() == null || chatPostMessageRequest.getUserName().equals("")) {
            throw new ServiceException("chatPostMessageRequest.getUserName() is null or empty");
        }
        if (chatPostMessageRequest.getMessage() == null || chatPostMessageRequest.getMessage().equals("")) {
            throw new ServiceException("chatPostMessageRequest.getMessage() is null or empty");
        }
        if (chatPostMessageRequest.getSubject() == null || chatPostMessageRequest.getSubject().equals("")) {
            throw new ServiceException("chatPostMessageRequest.getSubject() is null or empty");
        }
        if (chatPostMessageRequest.getLanguage() == null || chatPostMessageRequest.getLanguage().equals("")) {
            throw new ServiceException("chatPostMessageRequest.getLanguage() is null or empty");
        }
        return Response
                .status(200)
                .entity(new ChatPostMessage(chatPostMessageRequest).getResponse())
                .build();
    }
    
    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(ChatListRequest chatListRequest) throws ServiceException {
        if (chatListRequest == null) {
            throw new ServiceException("chatListRequest is null");
        }
        return Response
                .status(200)
                .entity(new ChatList(chatListRequest).getResponse())
                .build();
    }
    
    @GET
    @Path("/markAdminMessagesAsReaded/{userName}/{subject}/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response markAdminMessagesAsReaded(
            @PathParam("userName") String userName, 
            @PathParam("subject") String subject,
            @PathParam("language") String language
    ) throws ServiceException {
        if (userName == null || userName.equals("")) {
            throw new ServiceException("userName is null or empty");
        }
        if (subject == null || subject.equals("")) {
            throw new ServiceException("subject is null or empty");
        }
        if (language == null || language.equals("")) {
            throw new ServiceException("language is null or empty");
        }
        return Response
                .status(200)
                .entity(new ChatMarkAdminMessagesAsReaded(userName, subject, language).getResponse())
                .build();
    }

}
