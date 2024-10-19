/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetVerificationMessages extends AbstractOperation<JsonNode> {

    private final UserVerificationType userVerificationType;

    public UserGetVerificationMessages(UserVerificationType userVerificationType) {
        super(JsonNode.class);
        this.userVerificationType = userVerificationType;
    }

    @Override
    protected void execute() {
        File userVerificationFieldsFile = BaseFilesLocator.getUserVerificationFieldsNewFile();
        try {
            JsonNode userVerificationFields = mapper.readTree(userVerificationFieldsFile);
            if (userVerificationFields.get("messages").has(userVerificationType.name())) {
                super.response = userVerificationFields.get("messages").get(userVerificationType.name());
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGetVerificationMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
