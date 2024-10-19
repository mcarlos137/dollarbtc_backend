/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetMessageAttachment extends AbstractOperation<File> {

    private final String userName, chatRoom, attachmentFileName;

    public MCUserGetMessageAttachment(String userName, String chatRoom, String attachmentFileName) {
        super(File.class);
        this.userName = userName;
        this.chatRoom = chatRoom;
        this.attachmentFileName = attachmentFileName;
    }

    @Override
    protected void execute() {
        File[] userMCMessagesAttachmentsFolders = new File[]{UsersFolderLocator.getMCMessagesAttachmentsFolder(userName, chatRoom), UsersFolderLocator.getMCMessagesAttachmentsFolder(chatRoom, userName)};
        for (File userMCMessagesAttachmentsFolder : userMCMessagesAttachmentsFolders) {
            for (File userMCMessagesAttachmentFile : userMCMessagesAttachmentsFolder.listFiles()) {
                if (!userMCMessagesAttachmentFile.isFile()) {
                    continue;
                }
                if (userMCMessagesAttachmentFile.getName().startsWith(attachmentFileName)) {
                    super.response = userMCMessagesAttachmentFile;
                    return;
                }
            }
        }
    }

}
