/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/test")
@XmlRegistry
public class TestServiceREST {

    @GET
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response get() throws ServiceException {
        return Response
                .status(200)
                .entity("OK")
                .build();
    }
    
    @GET
    @Path("/getArticles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticles() throws ServiceException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode articles = mapper.createArrayNode();
        ObjectNode article = mapper.createObjectNode();
        article.put("id", "1");
        article.put("title", "Gorra");
        articles.add(article);
        article = mapper.createObjectNode();
        article.put("id", "2");
        article.put("title", "Franela");
        articles.add(article);
        article = mapper.createObjectNode();
        article.put("id", "3");
        article.put("title", "Bate");
        articles.add(article);
        article = mapper.createObjectNode();
        article.put("id", "4");
        article.put("title", "Pelota");
        articles.add(article);
        article = mapper.createObjectNode();
        article.put("id", "5");
        article.put("title", "Guante");
        articles.add(article);
        return Response
                .status(200)
                .entity(articles)
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/getWithParams")
    public Response getWithParams(
            @QueryParam("param1") String param1,
            @QueryParam("param2") String param2
    ) {
        return Response
                .status(200)
                .entity("OK")
                .build();
    }

    @POST
    @Path("/post")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(TestPostRequest testPostRequest) throws ServiceException {
        return Response
                .status(200)
                .entity("OK")
                .build();
    }

    public static class TestPostRequest {

        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

    }

}
