/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.sms;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.sms.SmsSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SmsSend extends AbstractOperation<String> {

    private final SmsSendRequest smsSendRequest;

    public SmsSend(SmsSendRequest smsSendRequest) {
        super(String.class);
        this.smsSendRequest = smsSendRequest;
    }

    @Override
    protected void execute() {
        /*List<String> phoneNumbers = new ArrayList<>();
        if (smsSendRequest.getPhonePrefix() != null && !smsSendRequest.getPhonePrefix().equals("")) {
            for (File userFolder : UsersFolderLocator.getFolder().listFiles()) {
                if (!userFolder.isDirectory()) {
                    continue;
                }
                if (userFolder.getName().startsWith(smsSendRequest.getPhonePrefix())) {
                    phoneNumbers.add(userFolder.getName());
                }
            }
        }
        if (smsSendRequest.getPhones() != null && !smsSendRequest.getPhones().equals("")) {
            phoneNumbers.add(smsSendRequest.getPhone());
        }*/
        Logger.getLogger(SmsSend.class.getName()).log(Level.INFO, "phones size: " + smsSendRequest.getPhones().size());
        if (smsSendRequest.isTesting()) {
            for (String phone : smsSendRequest.getPhones().toArray(new String[smsSendRequest.getPhones().size()])) {
                System.out.println("Sending SMS to: " + phone + " message " + smsSendRequest.getMessage());
            }
        } else {
            //new SMSSender().publish(smsSendRequest.getMessage(), smsSendRequest.getPhones().toArray(new String[smsSendRequest.getPhones().size()]));
        }
        super.response = "OK";
    }

}
