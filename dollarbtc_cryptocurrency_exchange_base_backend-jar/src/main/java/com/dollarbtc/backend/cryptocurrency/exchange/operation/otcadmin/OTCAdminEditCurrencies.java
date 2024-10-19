/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditCurrenciesRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersCurrenciesFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminEditCurrencies extends AbstractOperation<String> {
    
    private final OTCAdminEditCurrenciesRequest otcAdminEditCurrenciesRequest;

    public OTCAdminEditCurrencies(OTCAdminEditCurrenciesRequest otcAdminEditCurrenciesRequest) {
        super(String.class);
        this.otcAdminEditCurrenciesRequest = otcAdminEditCurrenciesRequest;
    }

    @Override
    protected void execute() {
        File usersCurrenciesFile = UsersCurrenciesFolderLocator.getFile(otcAdminEditCurrenciesRequest.getUserName());
        if (!usersCurrenciesFile.isFile()) {
            FileUtil.createFile(otcAdminEditCurrenciesRequest.getCurrencies(), usersCurrenciesFile);
        } else {
            FileUtil.editFile(otcAdminEditCurrenciesRequest.getCurrencies(), usersCurrenciesFile);
        }
        super.response = "OK";
    }
        
}
