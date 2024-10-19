/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class ShortsGetAttachment extends AbstractOperation<File> {

    private final String fileName;

    public ShortsGetAttachment(String fileName) {
        super(File.class);
        this.fileName = fileName;
    }

    @Override
    protected void execute() {
        super.response = new File(ShortsFolderLocator.getAttachmentsFolder(), fileName);
    }

}
