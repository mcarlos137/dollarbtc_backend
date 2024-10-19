/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest;

import com.dollarbtc.backend.cryptocurrency.exchange.service.exception.ServiceException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRegistry;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author conamerica90
 */
@Path("/mail")
@XmlRegistry
public class MailServiceREST {

    @POST
    @Path("/sendContact")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response send(@Context HttpServletRequest request) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        try {
            String body = IOUtils.toString(request.getInputStream());
            for (String param : body.split("&")) {
                String[] ps = param.split("=");
                if (ps.length != 2) {
                    continue;
                }
                String key = ps[0];
                String value = ps[1]
                        .replace("%40", "@")
                        .replace("+", " ")
                        .replace("%2B", "+")
                        .replace("%20", " ")
                        .replace("%3F", "?")
                        .replace("%C3%A1", "á")
                        .replace("%C3%A9", "é")
                        .replace("%C3%AD", "í")
                        .replace("%C3%B3", "ó")
                        .replace("%C3%BA", "ú");
                params.put(key, value);
            }
        } catch (IOException ex) {
            Logger.getLogger(MailServiceREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!params.containsKey("anti-honeypot") || !params.get("anti-honeypot").equals("RealMadrid180380...")) {
            throw new ServiceException("spam");
        }
        if (!params.containsKey("name")) {
            throw new ServiceException("name is not present");
        }
        if (!params.containsKey("email")) {
            throw new ServiceException("email is not present");
        }
        boolean moneyClickContact = false;
        if (params.containsKey("destination") && params.get("destination").equals("MC")) {
            moneyClickContact = true;
        }
        String name = params.get("name");
        String email = params.get("email");
        String phone = params.get("phone");
        String message = params.get("message");
        String company = params.get("company");
        if (company == null) {
            company = "";
        }
        String language = "EN";
        if (params.containsKey("language")) {
            language = params.get("language");
        }
        Set<String> recipients = new HashSet<>();
        if (!moneyClickContact) {
            recipients.add("admin@dollarbtc.com");
        } else {
            recipients.add("support@moneyclick.com");
        }
        recipients.add(email);
        StringBuilder subjectBuilder = new StringBuilder();
        subjectBuilder.append(params.get("subject"));
        subjectBuilder.append(" ");
        subjectBuilder.append(name);
        subjectBuilder.append(" ");
        if (company != null && !company.equals("")) {
            subjectBuilder.append(company);
            subjectBuilder.append(" ");
        }
        subjectBuilder.append(email);
        subjectBuilder.append(" ");
        subjectBuilder.append(phone);
        try {
            String from = "admin@dollarbtc.com__DOLLARBTC";
            if (moneyClickContact) {
                from = "support@moneyclick.com__MONEYCLICK";
            }
            new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").sendHTML(from, subjectBuilder.toString(), getMessage(language, moneyClickContact, name, company, email, phone, message), recipients, null);
        } catch (MailException ex) {
            Logger.getLogger(MailServiceREST.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServiceException(ex.getMessage());
        }
        return Response
                .status(200)
                .entity("OK")
                .build();
    }

    private String getMessage(String language, boolean moneyClickContact, String name, String company, String email, String phone, String message) {
        String logoUrl = "https://dollarbtc.com/static/media/logoDollarBtc.2f588dc4.png";
        if (moneyClickContact) {
            logoUrl = "https://moneyclick.com/static/media/logo.b8dc8796.png";
        }
        StringBuilder messageBuilder = new StringBuilder();
        String message1;
        String message2;
        String message3 = null;
        String message4;
        String message5;
        String message6;
        String message7;
        switch (language) {
            case "ES":
                message1 = "Gracias por tu contacto.";
                message2 = "Nombre: " + name;
                if (!company.equals("")) {
                    message3 = "Compañía: " + company;
                }
                message4 = "Email: " + email;
                message5 = "Teléfono: " + phone;
                message6 = "Mensaje: " + message;
                message7 = "Bienvenido a nuestra plataforma.";
                break;
            case "EN":
                message1 = "Thank you for your contact.";
                message2 = "Name: " + name;
                if (!company.equals("")) {
                    message3 = "Company: " + company;
                }
                message4 = "Email: " + email;
                message5 = "Phone: " + phone;
                message6 = "Message: " + message;
                message7 = "Welcome to our platform.";
                break;
            default:
                return "LANGUAGE NOT SUPPORTED";
        }
        messageBuilder.append("<div>");
        messageBuilder.append("<div dir=\"ltr\" role=\"textbox\" aria-multiline=\"true\" aria-label=\"Cuerpo del mensaje\" spellcheck=\"false\" contenteditable=\"true\">");
        messageBuilder.append("<div style=\"font-family: Calibri, Arial, Helvetica, sans-serif; font-size: 12pt; color: rgb(0, 0, 0);\">");
        messageBuilder.append("<img originalsrc=\"cid:0c501623-f1b0-4dc1-a871-c2026250fe71\" size=\"18410\" contenttype=\"image/jpeg\" style=\"user-select: none; width: 145.25px;\" crossorigin=\"use-credentials\" src=\"" + logoUrl + "\" unselectable=\"on\" tabindex=\"-1\" data-imgsize=\"small\">");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message1);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        if (name != null && !name.equals("")) {
            messageBuilder.append(message2);
            messageBuilder.append("<br>");
        }
        if (company != null && !company.equals("")) {
            messageBuilder.append(message3);
            messageBuilder.append("<br>");
        }
        messageBuilder.append(message4);
        messageBuilder.append("<br>");
        messageBuilder.append(message5);
        messageBuilder.append("<br>");
        messageBuilder.append(message6);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message7);
        messageBuilder.append("</div>");
        messageBuilder.append("</div>");
        return messageBuilder.toString();
    }

}
