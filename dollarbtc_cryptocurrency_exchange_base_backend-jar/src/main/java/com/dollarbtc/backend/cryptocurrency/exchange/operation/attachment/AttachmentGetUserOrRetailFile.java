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
public class AttachmentGetUserOrRetailFile extends AbstractOperation<File> {

    private final String userNameOrRetailId, fileName;

    public AttachmentGetUserOrRetailFile(String userNameOrRetailId, String fileName) {
        super(File.class);
        this.userNameOrRetailId = userNameOrRetailId;
        this.fileName = fileName;
    }

    @Override
    protected void execute() {
        File attachmentFile = UsersFolderLocator.getAttachmentFile(userNameOrRetailId, fileName);
        if (attachmentFile.isFile()) {
            super.response = attachmentFile;
            return;
        }
        attachmentFile = MoneyclickFolderLocator.getRetailAttachmentFile(userNameOrRetailId, fileName);
        if (attachmentFile.isFile()) {
            super.response = attachmentFile;
            return;
        }
        super.response = EMPTY_IMAGE_FILE;
    }

}
