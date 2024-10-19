/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountEditAutomaticRulesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountEditAutomaticRules extends AbstractOperation<String> {
    
    private final MasterAccountEditAutomaticRulesRequest masterAccountEditAutomaticRulesRequest;

    public MasterAccountEditAutomaticRules(MasterAccountEditAutomaticRulesRequest masterAccountEditAutomaticRulesRequest) {
        super(String.class);
        this.masterAccountEditAutomaticRulesRequest = masterAccountEditAutomaticRulesRequest;
    }    
    
    @Override
    public void execute() {
        FileUtil.editFile(masterAccountEditAutomaticRulesRequest.toArrayNode(), MasterAccountFolderLocator.getAutomaticRulesFile(null));
        super.response = "OK";
    }
    
}
