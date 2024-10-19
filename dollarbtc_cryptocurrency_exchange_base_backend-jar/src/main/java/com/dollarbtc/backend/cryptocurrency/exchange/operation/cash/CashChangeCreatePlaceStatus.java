/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashChangeCreatePlaceStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashCreatePlaceStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashChangeCreatePlaceStatus extends AbstractOperation<String> {

    private final CashChangeCreatePlaceStatusRequest cashChangeCreatePlaceStatusRequest;

    public CashChangeCreatePlaceStatus(CashChangeCreatePlaceStatusRequest cashChangeCreatePlaceStatusRequest) {
        super(String.class);
        this.cashChangeCreatePlaceStatusRequest = cashChangeCreatePlaceStatusRequest;
    }

    @Override
    public void execute() {
        File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(cashChangeCreatePlaceStatusRequest.getPlaceId());
        if (!cashPlaceConfigFile.isFile()) {
            super.response = "CASH PLACE ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            String id = cashPlaceConfig.get("id").textValue();
            CashCreatePlaceStatus baseCashCreatePlaceStatus = CashCreatePlaceStatus.valueOf(cashPlaceConfig.get("status").textValue());
            String email = cashPlaceConfig.get("email").textValue();
            switch (cashChangeCreatePlaceStatusRequest.getCashCreatePlaceStatus()) {
                case SENDED:
                    super.response = "CAN NOT BE CHANGED TO SENDED";
                    return;
                case ACTIVATED:
                    if (baseCashCreatePlaceStatus.equals(CashCreatePlaceStatus.SENDED)) {
                        super.response = "CAN NOT BE ACTIVATED FROM SENDED";
                        return;
                    }
                    if (baseCashCreatePlaceStatus.equals(CashCreatePlaceStatus.FAILED)) {
                        super.response = "CAN NOT BE ACTIVATED FROM FAILED";
                        return;
                    }
                    ((ObjectNode) cashPlaceConfig).put("active", true);
                case FAILED:
                    if (baseCashCreatePlaceStatus.equals(CashCreatePlaceStatus.SENDED)) {
                        super.response = "CAN NOT BE FAILED FROM SENDED";
                        return;
                    }
                    ((ObjectNode) cashPlaceConfig).put("active", false);
            }
            ((ObjectNode) cashPlaceConfig).put("status", cashChangeCreatePlaceStatusRequest.getCashCreatePlaceStatus().name());
            FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
            if (!cashChangeCreatePlaceStatusRequest.getCashCreatePlaceStatus().equals(CashCreatePlaceStatus.ACTIVATED)) {
                super.response = "OK";
                return;
            }
            try {
                Set<String> recipients = new HashSet<>();
                recipients.add(email);
                File cashPlaceQRCodeFile = CashFolderLocator.getPlaceQRCodeFile(cashChangeCreatePlaceStatusRequest.getPlaceId());
                createQRCode(getBarCode(cashChangeCreatePlaceStatusRequest.getPlaceId()), cashPlaceQRCodeFile.getAbsolutePath(), 300, 300);
                String subject = "Your Cash Place ID MoneyClick";
                String message = "Your Cash Place ID MoneyClick is attached to this email. Please download MoneyClick Cash Admin and scan QR core to register you cash place.";
                new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("support@moneyclick.com__MONEYCLICK", subject, message, recipients, cashPlaceQRCodeFile);
                super.response = "OK";
                return;
            } catch (MailException | WriterException | IOException ex) {
                Logger.getLogger(CashChangeCreatePlaceStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(CashChangeCreatePlaceStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
    private static void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try ( FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

    private static String getBarCode(String id) {
        try {
            return "dollarbtc://retail/"
                    + URLEncoder.encode(id, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}
