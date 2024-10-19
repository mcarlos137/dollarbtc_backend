/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.main;

import com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.operation.GetDashboardOperation;

/**
 *
 * @author CarlosDaniel
 */
public class GetDashboardMain {

    public static void main(String[] args) {
        GetDashboardOperation localBitcoinsGetDashboardOperation = new GetDashboardOperation("d9f1fda6bdb54e1f63f4edd89af94349", "0bd63b86f0ea2f4a243c733724501d3575c67e74faca6ce38bedad86a74b78dd");
        System.out.println("" + localBitcoinsGetDashboardOperation.getResponse());
    }

}
