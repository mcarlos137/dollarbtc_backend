/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerRemoveEscrowRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerRemoveEscrow extends AbstractOperation<String> {
    
    private final BankerRemoveEscrowRequest bankerRemoveEscrowRequest;

    public BankerRemoveEscrow(BankerRemoveEscrowRequest bankerRemoveEscrowRequest) {
        super(String.class);
        this.bankerRemoveEscrowRequest = bankerRemoveEscrowRequest;
    }

    @Override
    protected void execute() {
        File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(bankerRemoveEscrowRequest.getUserName());
        if(!userMCBalanceFolder.isDirectory()){
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File bankerEscrowFolder = BankersFolderLocator.getEscrowFolder(bankerRemoveEscrowRequest.getUserName());
        if(!bankerEscrowFolder.isDirectory()){
            super.response = "BANKER DOES NOT EXIST";
            return;
        }
        String substract = BaseOperation.substractToBalance(
                bankerEscrowFolder, 
                bankerRemoveEscrowRequest.getCurrency(), 
                bankerRemoveEscrowRequest.getAmount(), 
                BalanceOperationType.BANKER_REMOVE_BALANCE, 
                BalanceOperationStatus.OK, 
                null, 
                null, 
                false, 
                null, 
                false, 
                null
        );
        if(!substract.equals("OK")){
            super.response = substract;
            return;
        }
        BaseOperation.addToBalance(
                userMCBalanceFolder,
                bankerRemoveEscrowRequest.getCurrency(), 
                bankerRemoveEscrowRequest.getAmount(), 
                BalanceOperationType.BANKER_REMOVE_BALANCE, 
                BalanceOperationStatus.OK,
                null, 
                null, 
                null, 
                false, 
                null
        );
        super.response = "OK";
    }
        
}
