/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.getId;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailCreateStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailNewCreate extends AbstractOperation<String> {

    private final MCRetailNewCreateRequest mcRetailNewCreateRequest;

    public MCRetailNewCreate(MCRetailNewCreateRequest mcRetailNewCreateRequest) {
        super(String.class);
        this.mcRetailNewCreateRequest = mcRetailNewCreateRequest;
    }

    @Override
    public void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(mcRetailNewCreateRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File moneyclickRetailsFolder = MoneyclickFolderLocator.getRetailsFolder();
        String id = getId();
        File moneyclickRetailFolder = new File(moneyclickRetailsFolder, id);
        if (moneyclickRetailFolder.isDirectory()) {
            super.response = "RETAIL ID IS ALREADY REGISTERED";
            return;
        }
        FileUtil.createFolderIfNoExist(moneyclickRetailFolder);
        FileUtil.createFolderIfNoExist(new File(moneyclickRetailFolder, "Balance"));
        FileUtil.createFolderIfNoExist(new File(moneyclickRetailFolder, "EscrowBalance"));
        JsonNode create = mcRetailNewCreateRequest.toJsonNode(mapper.createObjectNode());
        ((ObjectNode) create).put("id", id);
        if (mcRetailNewCreateRequest.isOnlyForMap()) {
            ((ObjectNode) create).put("active", true);
            ((ObjectNode) create).put("mcRetailCreateStatus", MCRetailCreateStatus.ACTIVATED.name());
        } else {
            ((ObjectNode) create).put("active", false);
            ((ObjectNode) create).put("securityPin", ((int) (Math.random() * 9000) + 1000));
            ((ObjectNode) create).put("mcRetailCreateStatus", MCRetailCreateStatus.SENDED.name());
        }
        FileUtil.createFile(create, moneyclickRetailFolder, "config.json");
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (!userConfig.has("retailIds")) {
                ((ObjectNode) userConfig).putArray("retailIds");
            }
            ((ArrayNode) userConfig.get("retailIds")).add(id);
            FileUtil.editFile(userConfig, userConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (mcRetailNewCreateRequest.isOnlyForMap()) {
            super.response = "OK";
            return;
        }
        try {
            Set<String> recipients = new HashSet<>();
            recipients.add(mcRetailNewCreateRequest.getEmail());
            String subject = "Your MC Retail is in creation process";
            String message = "Your MC Retail " + mcRetailNewCreateRequest.getTitle() + " " + mcRetailNewCreateRequest.getDescription() + " is in creaction process. You will be contacted within the next 72 hours by our agents to analysis your requirement.";
            new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("support@moneyclick.com__MONEYCLICK", subject, message, recipients, null);
            super.response = "OK";
            return;
        } catch (MailException ex) {
            Logger.getLogger(MCRetailNewCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
