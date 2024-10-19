/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.sms.SmsSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.sms.SmsSend;
import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import javax.ws.rs.core.Response;

/**
 *
 * @author conamerica90
 */
@Path("/sms")
@XmlRegistry
public class SmsServiceREST {

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response send(SmsSendRequest smsSendRequest) throws ServiceException {
        if (smsSendRequest == null) {
            throw new ServiceException("smsSendRequest is null");
        }
        if (smsSendRequest.getMessage() == null || smsSendRequest.getMessage().equals("")) {
            throw new ServiceException("smsSendRequest.getMessage() is null or empty");
        }
        if (smsSendRequest.getPhones() == null || smsSendRequest.getPhones().isEmpty()) {
            throw new ServiceException("smsSendRequest.getPhones() is null or empty");
        }
        return Response
                .status(200)
                .entity(new SmsSend(smsSendRequest).getResponse())
                .build();
    }

}
