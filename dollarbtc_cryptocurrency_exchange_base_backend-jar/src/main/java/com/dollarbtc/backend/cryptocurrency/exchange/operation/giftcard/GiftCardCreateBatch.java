/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardActivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardCreateBatchRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AttachmentsFolderLocator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class GiftCardCreateBatch extends AbstractOperation<String> {

    private final GiftCardCreateBatchRequest giftCardCreateBatchRequest;

    public GiftCardCreateBatch(GiftCardCreateBatchRequest giftCardCreateBatchRequest) {
        super(String.class);
        this.giftCardCreateBatchRequest = giftCardCreateBatchRequest;
    }

    @Override
    protected void execute() {
        File attachmentsGiftCardQRCodesBatchFolder = new File(AttachmentsFolderLocator.getGiftCardQRCodesFolder(), giftCardCreateBatchRequest.getBatchName());
        if (attachmentsGiftCardQRCodesBatchFolder.isDirectory()) {
            super.response = "BATCH NAME ALREADY EXIST";
            return;
        }
        FileUtil.createFolderIfNoExist(attachmentsGiftCardQRCodesBatchFolder);
        Double amount = 0.0;
        Map<String, Double> giftCardsCreated = new HashMap<>();
        for (Double value : giftCardCreateBatchRequest.getValuesAndQuantities().keySet()) {
            int i = 0;
            while (i < giftCardCreateBatchRequest.getValuesAndQuantities().get(value)) {
                if (amount + value > giftCardCreateBatchRequest.getMaxAmount()) {
                    break;
                }
                String id = BaseOperation.getId();
                String result = getQRUrl(id, value, attachmentsGiftCardQRCodesBatchFolder);
                Logger.getLogger(GiftCardCreateBatch.class.getName()).log(Level.INFO, "CREATED GIFT CARD CODE AT{0}", result);
                amount = amount + value;
                giftCardsCreated.put(id, value);
                i++;
            }
        }
        if (!giftCardsCreated.isEmpty()) {
            if (giftCardCreateBatchRequest.getUserNameToActivate() != null
                    && !giftCardCreateBatchRequest.getUserNameToActivate().equals("")) {
                for (String id : giftCardsCreated.keySet()) {
                    new GiftCardActivate(new GiftCardActivateRequest(
                            id,
                            giftCardCreateBatchRequest.getUserNameToActivate(),
                            giftCardCreateBatchRequest.getCurrency(),
                            giftCardsCreated.get(id),
                            giftCardCreateBatchRequest.getSource(),
                            giftCardCreateBatchRequest.isUpfrontCommission()
                    )
                    ).getResponse();
                }
            }
            try {
                createCSV(giftCardsCreated, attachmentsGiftCardQRCodesBatchFolder);
            } catch (IOException ex) {
                Logger.getLogger(GiftCardCreateBatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = "OK";
    }

    private String getQRUrl(String id, Double amount, File attachmentsGiftCardQRCodesBatchFolder) {
        ObjectNode qr = mapper.createObjectNode();
        qr.put("id", id);
        qr.put("currency", giftCardCreateBatchRequest.getCurrency());
        qr.put("amount", amount);
        qr.put("source", giftCardCreateBatchRequest.getSource());
        qr.put("upfrontCommission", giftCardCreateBatchRequest.isUpfrontCommission());
        try {
            String batchName = attachmentsGiftCardQRCodesBatchFolder.getName();
            createQRCode(qr.toString(), new File(attachmentsGiftCardQRCodesBatchFolder, id + ".png").getAbsolutePath(), 300, 300);
            return "https://giftcardqrattachments.moneyclick.com/" + batchName + "/" + id + ".png";
        } catch (WriterException | IOException ex) {
            Logger.getLogger(GiftCardCreateBatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try ( FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }
    
    private void createCSV(Map<String, Double> giftCardsCreated, File folder) throws IOException {
        File csvFile = new File(folder, "batch.csv");
        for(String id : giftCardsCreated.keySet()){
            String line = id + "," + "C:\\MoneyClick\\GiftCards\\" + folder.getName() + "\\" + id + ".png";
            FileUtil.writeInFile(csvFile, line);
        }
    }

}
