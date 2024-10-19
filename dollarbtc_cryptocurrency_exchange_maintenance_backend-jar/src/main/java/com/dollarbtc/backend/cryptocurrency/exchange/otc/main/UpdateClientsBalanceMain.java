/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminUpdateClientsBalance;

/**
 *
 * @author CarlosDaniel
 */
public class UpdateClientsBalanceMain {

    public static void main(String[] args) {
        // MAIN OPERATOR
//        args = new String[]{"+MBB", "+CBB", "-CB", "+PB", "+MB", "-BB"};
        // OTHER OPERATORS
        // args = new String[]{"+OBB", "+PB", "+MB", "-BB"};
        new OTCAdminUpdateClientsBalance(args, 100).getResponse();
    }  
    
}
