/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCancelVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCRemovePayment;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersVerificationFolderLocator;
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
public class UserCancelVerification extends AbstractOperation<String> {

    private final UserCancelVerificationRequest userCancelVerificationRequest;

    public UserCancelVerification(UserCancelVerificationRequest userCancelVerificationRequest) {
        super(String.class);
        this.userCancelVerificationRequest = userCancelVerificationRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userCancelVerificationRequest.getUserName());
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (!userConfig.has("verification")) {
                super.response = "USER DOES NOT HAVE VERIFICATIONS";
                return;
            }
            if (!userConfig.get("verification").has(userCancelVerificationRequest.getUserVerificationType().name())) {
                super.response = "USER DOES NOT HAVE THIS VERIFICATION TYPE";
                return;
            }
            UserVerificationStatus userVerificationStatus = UserVerificationStatus.valueOf(
                    userConfig.get("verification").get(userCancelVerificationRequest.getUserVerificationType().name())
                            .get("userVerificationStatus").textValue());
            String timestamp = userConfig.get("verification")
                    .get(userCancelVerificationRequest.getUserVerificationType().name()).get("timestamp").textValue();
            switch (userCancelVerificationRequest.getUserVerificationType()) {
                case D:
                    String paymentId = null;
                    Iterator<JsonNode> fieldNamesIterator = userConfig.get("verification")
                            .get(userCancelVerificationRequest.getUserVerificationType().name()).get("fieldNames")
                            .elements();
                    while (fieldNamesIterator.hasNext()) {
                        String fieldName = fieldNamesIterator.next().textValue();
                        if (fieldName.contains("paymentId")) {
                            paymentId = fieldName.split("__")[1];
                            break;
                        }
                    }
                    if (paymentId != null) {
                        new OTCRemovePayment(userCancelVerificationRequest.getUserName(), null, paymentId).getResponse();
                    }
                    ((ObjectNode) userConfig.get("verification"))
                            .remove(userCancelVerificationRequest.getUserVerificationType().name());
                    FileUtil.editFile(userConfig, userConfigFile);
                    FileUtil.deleteFile(new File(UsersVerificationFolderLocator.getStatusFolder(userVerificationStatus),
                            DateUtil.getFileDate(timestamp) + ".json"));
                    break;
                default:
                    super.response = "CASE NOT SUPPORTED";
                    return;
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserCancelVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
