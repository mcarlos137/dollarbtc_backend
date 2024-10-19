/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardActivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AttachmentsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MailFolderLocator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class GiftCardSend extends AbstractOperation<String> {

    private final GiftCardSendRequest giftCardSendRequest;

    public GiftCardSend(GiftCardSendRequest giftCardSendRequest) {
        super(String.class);
        this.giftCardSendRequest = giftCardSendRequest;
    }

    @Override
    protected void execute() {
        String id = BaseOperation.getId();
        if (giftCardSendRequest.getLanguage() == null) {
            giftCardSendRequest.setLanguage("EN");
        }
        String result = new GiftCardActivate(new GiftCardActivateRequest(
                id,
                giftCardSendRequest.getUserName(),
                giftCardSendRequest.getCurrency(),
                giftCardSendRequest.getAmount(),
                giftCardSendRequest.getLanguage(),
                giftCardSendRequest.getEmail(),
                giftCardSendRequest.getSource(),
                giftCardSendRequest.isUpfrontCommission()
        )).getResponse();
        if (!result.equals("OK__SEND")) {
            super.response = result;
            return;
        }
        Set<String> recipients = new HashSet<>();
        recipients.add(giftCardSendRequest.getEmail());
        String qrUrl = getQRUrl(id);
        if (qrUrl == null) {
            super.response = "FAIL";
            return;
        }
        String timestamp = DateUtil.getCurrentDate();
        File mailFile = new File(MailFolderLocator.getFolder(), DateUtil.getFileDate(timestamp) + ".json");
        ObjectNode mail = mapper.createObjectNode();        
        mail.put("timestamp", timestamp);
        mail.put("userName", giftCardSendRequest.getUserName());
        mail.put("currency", giftCardSendRequest.getCurrency());
        mail.put("source", giftCardSendRequest.getSource());
        mail.put("language", giftCardSendRequest.getLanguage());
        mail.put("email", giftCardSendRequest.getEmail());
        mail.put("qrId", id);
        mail.put("qrUrl", qrUrl);
        mail.put("amount", giftCardSendRequest.getAmount());
        FileUtil.createFile(mail, mailFile);
        super.response = "OK";
    }

    private String getQRUrl(String id) {
        ObjectNode qr = mapper.createObjectNode();
        qr.put("id", id);
        qr.put("currency", giftCardSendRequest.getCurrency());
        qr.put("amount", giftCardSendRequest.getAmount());
        qr.put("source", giftCardSendRequest.getSource());
        try {
            File qrFile = new File(FileUtil.createFolderIfNoExist(AttachmentsFolderLocator.getGiftCardQRCodesForMailFolder(), "Q-UERTD4_" +  id), id + ".png");
            createQRCode(qr.toString(), qrFile.getAbsolutePath(), 300, 300);
            return "https://giftcardqrattachmentsformail.moneyclick.com/" + "Q-UERTD4_" + id + "/" + id + ".png";
        } catch (WriterException | IOException ex) {
            Logger.getLogger(GiftCardSend.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try ( FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

}
