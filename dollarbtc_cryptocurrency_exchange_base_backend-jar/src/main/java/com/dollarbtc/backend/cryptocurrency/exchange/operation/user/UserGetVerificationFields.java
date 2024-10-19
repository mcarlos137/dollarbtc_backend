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
public class UserGetVerificationFields extends AbstractOperation<JsonNode> {

    private final UserVerificationType userVerificationType;

    public UserGetVerificationFields(UserVerificationType userVerificationType) {
        super(JsonNode.class);
        this.userVerificationType = userVerificationType;
    }

    @Override
    protected void execute() {
        File userVerificationFieldsFile = BaseFilesLocator.getUserVerificationFieldsFile();
        JsonNode userVerificationFields = mapper.createObjectNode();
        try {
            JsonNode userVerificationFs = mapper.readTree(userVerificationFieldsFile);
            if (userVerificationFs.get("verifications").has(userVerificationType.name())) {
                Iterator<JsonNode> userVerificationFsIterator = userVerificationFs.get("verifications").get(userVerificationType.name()).iterator();
                while (userVerificationFsIterator.hasNext()) {
                    JsonNode userVerificationFsIt = userVerificationFsIterator.next();
                    String fieldName = userVerificationFsIt.textValue();
                    if (userVerificationFs.get("fields").has(fieldName)) {
                        ((ObjectNode) userVerificationFields).set(fieldName, userVerificationFs.get("fields").get(fieldName));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGetVerificationFields.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = userVerificationFields;
    }

}
