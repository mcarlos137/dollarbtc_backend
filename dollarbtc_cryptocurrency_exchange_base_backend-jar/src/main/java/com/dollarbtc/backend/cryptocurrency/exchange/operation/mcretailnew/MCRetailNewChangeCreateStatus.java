/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewChangeCreateStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailCreateStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailNewChangeCreateStatus extends AbstractOperation<String> {

    private final MCRetailNewChangeCreateStatusRequest mcRetailNewChangeCreateStatusRequest;

    public MCRetailNewChangeCreateStatus(MCRetailNewChangeCreateStatusRequest mcRetailNewChangeCreateStatusRequest) {
        super(String.class);
        this.mcRetailNewChangeCreateStatusRequest = mcRetailNewChangeCreateStatusRequest;
    }

    @Override
    public void execute() {
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(mcRetailNewChangeCreateStatusRequest.getId());
        if (!moneyclickRetailConfigFile.isFile()) {
            super.response = "RETAIL ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            String id = moneyclickRetailConfig.get("id").textValue();
            MCRetailCreateStatus baseMCRetailCreateStatus = MCRetailCreateStatus.valueOf(moneyclickRetailConfig.get("mcRetailCreateStatus").textValue());
            String email = moneyclickRetailConfig.get("email").textValue();
            switch (mcRetailNewChangeCreateStatusRequest.getMcRetailCreateStatus()) {
                case SENDED:
                    super.response = "CAN NOT BE CHANGED TO SENDED";
                    return;
                case ACTIVATED:
                    if (baseMCRetailCreateStatus.equals(MCRetailCreateStatus.SENDED)) {
                        super.response = "CAN NOT BE ACTIVATED FROM SENDED";
                        return;
                    }
                    if (baseMCRetailCreateStatus.equals(MCRetailCreateStatus.FAILED)) {
                        super.response = "CAN NOT BE ACTIVATED FROM FAILED";
                        return;
                    }
                    ((ObjectNode) moneyclickRetailConfig).put("active", true);
                case FAILED:
                    if (baseMCRetailCreateStatus.equals(MCRetailCreateStatus.SENDED)) {
                        super.response = "CAN NOT BE FAILED FROM SENDED";
                        return;
                    }
                    ((ObjectNode) moneyclickRetailConfig).put("active", false);
            }
            ((ObjectNode) moneyclickRetailConfig).put("mcRetailCreateStatus", mcRetailNewChangeCreateStatusRequest.getMcRetailCreateStatus().name());
            FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
            if (!mcRetailNewChangeCreateStatusRequest.getMcRetailCreateStatus().equals(MCRetailCreateStatus.ACTIVATED)) {
                super.response = "OK";
                return;
            }
            try {
                Set<String> recipients = new HashSet<>();
                recipients.add(email);
                File retailQRCodeFile = MoneyclickFolderLocator.getRetailQRCodeFile(mcRetailNewChangeCreateStatusRequest.getId());
                createQRCode(getBarCode(mcRetailNewChangeCreateStatusRequest.getId()), retailQRCodeFile.getAbsolutePath(), 300, 300);
                String subject = "Your retail ID MoneyClick";
                String message = "Your retail ID MoneyClick is attached to this email. Please download MoneyClick Retail Admin and scan QR core to register you Retail.";
                new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("support@moneyclick.com__MONEYCLICK", subject, message, recipients, retailQRCodeFile);
                super.response = "OK";
                return;
            } catch (MailException | WriterException | IOException ex) {
                Logger.getLogger(MCRetailNewChangeCreateStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewChangeCreateStatus.class.getName()).log(Level.SEVERE, null, ex);
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
