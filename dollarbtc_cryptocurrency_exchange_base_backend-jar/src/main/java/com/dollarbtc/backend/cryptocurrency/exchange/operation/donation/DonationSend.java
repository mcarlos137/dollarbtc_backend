/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.donation;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.donation.DonationSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class DonationSend extends AbstractOperation<String> {

    private final DonationSendRequest donationSendRequest;

    public DonationSend(DonationSendRequest donationSendRequest) {
        super(String.class);
        this.donationSendRequest = donationSendRequest;
    }

    @Override
    protected void execute() {
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(donationSendRequest.getBaseUserName()),
                "USD",
                donationSendRequest.getAmount(),
                BalanceOperationType.DONATION,
                BalanceOperationStatus.OK,
                "",
                null,
                false,
                null,
                false,
                null
        );
        if (!substractToBalance.equals("OK")) {
            super.response = substractToBalance;
            return;
        }
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(donationSendRequest.getTargetUserName()),
                "USD",
                donationSendRequest.getAmount(),
                BalanceOperationType.DONATION,
                BalanceOperationStatus.OK,
                "",
                null,
                null,
                false,
                null
        );
        switch (donationSendRequest.getContentType()) {
            case "SHORTS":
                try {
                File shortsCommentsFile = ShortsFolderLocator.getCommentsFile(donationSendRequest.getContentId(), 0);
                ArrayNode shortsComments = mapper.createArrayNode();
                if (shortsCommentsFile.isFile()) {
                    shortsComments = (ArrayNode) mapper.readTree(shortsCommentsFile);
                }
                String timestamp = DateUtil.getCurrentDate();
                ObjectNode shortsComment = mapper.createObjectNode();
                shortsComment.put("id", BaseOperation.getId());
                shortsComment.put("timestamp", timestamp);
                shortsComment.put("userName", donationSendRequest.getBaseUserName());
                shortsComment.put("name", donationSendRequest.getBaseName());
                shortsComment.put("comment", donationSendRequest.getComment());
                shortsComment.put("donationAmountUSD", donationSendRequest.getAmount());
                shortsComment.put("private", donationSendRequest.isPrivateComment());
                ((ObjectNode) shortsComment).set("reactions", mapper.createObjectNode());
                shortsComments.add(shortsComment);
                FileUtil.editFile(shortsComments, shortsCommentsFile);
                File shortsFile = ShortsFolderLocator.getFile(donationSendRequest.getContentId());
                JsonNode shorts = mapper.readTree(shortsFile);
                ((ObjectNode) shorts).put("commentsCount", shorts.get("commentsCount").intValue() + 1);
                ((ObjectNode) shorts).put("donationsAmountUSD", shorts.get("donationsAmountUSD").doubleValue() + donationSendRequest.getAmount());
                FileUtil.editFile(shorts, shortsFile);
            } catch (IOException ex) {
                Logger.getLogger(DonationSend.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
            case "USER":
                new NotificationSendMessageByUserName(donationSendRequest.getTargetUserName(), "DONATION RECEIVED", "You have received a donation of " + donationSendRequest.getAmount() + " USD from " + donationSendRequest.getBaseName()).getResponse();
                break;
            default:
                throw new AssertionError();
        }
        super.response = "OK";
    }

}
