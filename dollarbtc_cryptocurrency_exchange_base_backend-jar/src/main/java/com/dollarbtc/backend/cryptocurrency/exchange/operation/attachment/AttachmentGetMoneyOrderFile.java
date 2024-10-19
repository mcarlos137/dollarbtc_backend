/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.EMPTY_IMAGE_FILE;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyOrdersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class AttachmentGetMoneyOrderFile extends AbstractOperation<File> {

    private final String fileName;

    public AttachmentGetMoneyOrderFile(String fileName) {
        super(File.class);
        this.fileName = fileName;
    }

    @Override
    protected void execute() {
        File imageFile = new File(MoneyOrdersFolderLocator.getAttachmentsFolder(), fileName);
        if (imageFile.isFile()) {
            super.response = imageFile;
            return;
        }
        super.response = EMPTY_IMAGE_FILE;
    }

}
