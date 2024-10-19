/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingGetAttachment extends AbstractOperation<File> {

    private final String fileName;

    public LiveStreamingGetAttachment(String fileName) {
        super(File.class);
        this.fileName = fileName;
    }

    @Override
    protected void execute() {
        super.response = new File(LiveStreamingsFolderLocator.getAttachmentsFolder(), fileName);
    }

}
