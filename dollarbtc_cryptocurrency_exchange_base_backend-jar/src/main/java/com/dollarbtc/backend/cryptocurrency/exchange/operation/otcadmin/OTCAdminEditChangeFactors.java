/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditChangeFactorsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminEditChangeFactors extends AbstractOperation<String> {

    private final OTCAdminEditChangeFactorsRequest otcAdminEditChangeFactorsRequest;

    public OTCAdminEditChangeFactors(OTCAdminEditChangeFactorsRequest otcAdminEditChangeFactorsRequest) {
        super(String.class);
        this.otcAdminEditChangeFactorsRequest = otcAdminEditChangeFactorsRequest;
    }

    @Override
    protected void execute() {
        File otcChangeFactorsFile = OTCFolderLocator.getChangeFactorsFile("MAIN");
        FileUtil.editFile(otcAdminEditChangeFactorsRequest.getChangeFactors(), otcChangeFactorsFile);
        super.response = "OK";
    }
    
}
