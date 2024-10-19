/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.attachment;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.EMPTY_IMAGE_FILE;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class AttachmentGetOTCOperationFile extends AbstractOperation<File> {

    private final String otcOperationId, fileName;

    public AttachmentGetOTCOperationFile(String otcOperationId, String fileName) {
        super(File.class);
        this.otcOperationId = otcOperationId;
        this.fileName = fileName;
    }

    @Override
    protected void execute() {
        File otcOperationIdAttachmentFile = OTCFolderLocator.getOperationIdAttachmentFile(null, otcOperationId, fileName);
        if (!otcOperationIdAttachmentFile.isFile()) {
            super.response = EMPTY_IMAGE_FILE; 
            return;
        }
        super.response = otcOperationIdAttachmentFile; 
    }

}
