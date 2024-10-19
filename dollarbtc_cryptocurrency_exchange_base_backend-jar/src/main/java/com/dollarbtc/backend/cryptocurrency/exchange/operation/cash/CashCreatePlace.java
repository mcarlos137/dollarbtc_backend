/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashCreatePlaceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashCreatePlaceStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.getId;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashCreatePlace extends AbstractOperation<String> {

    private final CashCreatePlaceRequest cashCreatePlaceRequest;

    public CashCreatePlace(CashCreatePlaceRequest cashCreatePlaceRequest) {
        super(String.class);
        this.cashCreatePlaceRequest = cashCreatePlaceRequest;
    }

    @Override
    public void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(cashCreatePlaceRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File cashPlacesFolder = CashFolderLocator.getPlacesFolder();
        String id = getId();
        File cashPlaceFolder = new File(cashPlacesFolder, id);
        if (cashPlaceFolder.isDirectory()) {
            super.response = "PLACE ID IS ALREADY REGISTERED";
            return;
        }
        FileUtil.createFolderIfNoExist(cashPlaceFolder);
        FileUtil.createFolderIfNoExist(new File(cashPlaceFolder, "Balance"));
        FileUtil.createFolderIfNoExist(new File(cashPlaceFolder, "EscrowBalance"));
        JsonNode create = cashCreatePlaceRequest.toJsonNode(mapper.createObjectNode());
        ((ObjectNode) create).put("id", id);
        if (cashCreatePlaceRequest.isOnlyForMap()) {
            ((ObjectNode) create).put("active", true);
            ((ObjectNode) create).put("status", CashCreatePlaceStatus.ACTIVATED.name());
        } else {
            ((ObjectNode) create).put("active", false);
            ((ObjectNode) create).put("securityPin", ((int) (Math.random() * 9000) + 1000));
            ((ObjectNode) create).put("status", CashCreatePlaceStatus.SENDED.name());
        }
        FileUtil.createFile(create, cashPlaceFolder, "config.json");
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (!userConfig.has("cashPlaceIds")) {
                ((ObjectNode) userConfig).putArray("cashPlaceIds");
            }
            ((ArrayNode) userConfig.get("cashPlaceIds")).add(id);
            FileUtil.editFile(userConfig, userConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(CashCreatePlace.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (cashCreatePlaceRequest.isOnlyForMap()) {
            super.response = "OK";
            return;
        }
        try {
            Set<String> recipients = new HashSet<>();
            recipients.add(cashCreatePlaceRequest.getEmail());
            String subject = "Your Cash Place is in creation process";
            String message = "Your Cash Place " + cashCreatePlaceRequest.getTitle() + " " + cashCreatePlaceRequest.getDescription() + " is in creaction process. You will be contacted within the next 72 hours by our agents to analysis your requirement.";
            new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("support@moneyclick.com__MONEYCLICK", subject, message, recipients, null);
            super.response = "OK";
            return;
        } catch (MailException ex) {
            Logger.getLogger(CashCreatePlace.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
