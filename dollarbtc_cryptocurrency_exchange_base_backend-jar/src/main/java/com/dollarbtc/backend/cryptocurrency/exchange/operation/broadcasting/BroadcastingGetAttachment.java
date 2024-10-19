/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BroadcastingGetAttachment extends AbstractOperation<File> {

    private final String fileName;

    public BroadcastingGetAttachment(String fileName) {
        super(File.class);
        this.fileName = fileName;
    }

    @Override
    protected void execute() {
        super.response = new File(BroadcastingFolderLocator.getAttachmentsFolder(), fileName);
    }

}
