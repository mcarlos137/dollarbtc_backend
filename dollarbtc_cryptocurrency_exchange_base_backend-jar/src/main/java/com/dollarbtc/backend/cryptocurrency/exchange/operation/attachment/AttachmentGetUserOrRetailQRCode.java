/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.EMPTY_IMAGE_FILE;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class AttachmentGetUserOrRetailQRCode extends AbstractOperation<File> {

    private final String userNameOrRetailId;

    public AttachmentGetUserOrRetailQRCode(String userNameOrRetailId) {
        super(File.class);
        this.userNameOrRetailId = userNameOrRetailId;
    }

    @Override
    protected void execute() {
        File gaQRCodeFile = UsersFolderLocator.getGAQRCodeFile(userNameOrRetailId);
        if (gaQRCodeFile.isFile()) {
            super.response = gaQRCodeFile;
            return;
        }
        gaQRCodeFile = MoneyclickFolderLocator.getRetailQRCodeFile(userNameOrRetailId);
        if (gaQRCodeFile.isFile()) {
            super.response = gaQRCodeFile;
            return;
        }
        super.response = EMPTY_IMAGE_FILE;
    }

}
