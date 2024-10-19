/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.donation.DonationSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.donation.DonationSend;
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
@Path("/donation")
@XmlRegistry
public class DonationServiceREST {

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response send(DonationSendRequest donationSendRequest) throws ServiceException {
        if (donationSendRequest == null) {
            throw new ServiceException("donationSendRequest is null");
        }
        if (donationSendRequest.getBaseUserName() == null || donationSendRequest.getBaseUserName().equals("")) {
            throw new ServiceException("donationSendRequest.getBaseUserName() is null or empty");
        }
        if (donationSendRequest.getTargetUserName() == null || donationSendRequest.getTargetUserName().equals("")) {
            throw new ServiceException("donationSendRequest.getTargetUserName() is null or empty");
        }
        if (donationSendRequest.getContentType() == null || donationSendRequest.getContentType().equals("")) {
            throw new ServiceException("donationSendRequest.getContentType() is null or empty");
        }
        if (donationSendRequest.getComment() == null || donationSendRequest.getComment().equals("")) {
            throw new ServiceException("donationSendRequest.getComment() is null or empty");
        }
        if (donationSendRequest.getAmount() == null || donationSendRequest.getAmount() == 0.0) {
            throw new ServiceException("donationSendRequest.getAmount() is null or empty");
        }
        return Response
                .status(200)
                .entity(new DonationSend(donationSendRequest).getResponse())
                .build();
    }
        
}
